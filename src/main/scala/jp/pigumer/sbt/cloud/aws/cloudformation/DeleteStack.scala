package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.DeleteStackRequest
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait DeleteStack {

  import cloudformation.CloudformationPlugin.autoImport._

  val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  def waitForCompletion(amazonCloudFormation: AmazonCloudFormationClient, stackName: String, log: Logger): Unit

  private def delete(settings: AwscfSettings,
                     stage: String,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    val request = new DeleteStackRequest().
      withStackName(stack.stackName)

    val client = amazonCloudFormation(settings)
    client.deleteStack(settings.roleARN.map(request.withRoleARN(_)).getOrElse(request))
    waitForCompletion(client, stack.stackName, log)
  }

  def deleteStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<stage>, <shortName>").parsed match {
      case Seq(stage, shortName) => {
        (for {
          stack ← Try(awscfStacks.value.get(shortName).getOrElse(throw new RuntimeException()))
          _ ← delete(settings, stage, stack, log)
        } yield ()) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒ {
            sys.error(t.toString)
          }
        }
      }
      case _ ⇒ sys.error("error")
    }
  }
}
