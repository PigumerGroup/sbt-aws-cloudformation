package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.DeleteStackRequest
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger, SettingKey}

import scala.util.{Failure, Success, Try}

trait DeleteStack {

  import cloudformation.CloudformationPlugin.autoImport._

  def amazonCloudFormation(settings: AwscfSettings): AmazonCloudFormationClient

  def waitForCompletion(client: AmazonCloudFormationClient, stackName: String, log: Logger): Unit

  private def delete(awsSettings: AwscfSettings, stage: String, stack: CloudformationStack, log: Logger) = Try {
    val request = new DeleteStackRequest().
      withStackName(stack.stackName)
    val client = amazonCloudFormation(awsSettings)
    client.deleteStack(request)
    waitForCompletion(client, stack.stackName, log)
  }

  def deleteStackTask(awscfSettings: SettingKey[AwscfSettings]) = Def.inputTask {
    val log = streams.value.log
    spaceDelimited("<stage>, <shortName>").parsed match {
      case Seq(stage, shortName) => {
        (for {
          stack <- Try(awscfStacks.value.get(shortName).getOrElse(throw new RuntimeException()))
          _ <- delete(awscfSettings.value, stage, stack, log)
        } yield ()) match {
          case Success(_) => ()
          case Failure(t) => {
            sys.error(t.toString)
          }
        }
      }
      case _ => sys.error("error")
    }
  }
}
