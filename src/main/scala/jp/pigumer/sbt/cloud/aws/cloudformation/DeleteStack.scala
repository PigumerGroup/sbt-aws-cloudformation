package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{DeleteStackRequest, Stack}
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait DeleteStack {

  import cloudformation.CloudformationPlugin.autoImport._

  protected val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  protected def waitForCompletion(amazonCloudFormation: AmazonCloudFormationClient,
                                  stackName: String,
                                  log: Logger): Try[Seq[Stack]]

  private def delete(settings: AwscfSettings,
                     stage: String,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    val request = new DeleteStackRequest().
      withStackName(stack.stackName)

    log.info(s"Delete ${stack.stackName}")

    val client = amazonCloudFormation(settings)
    client.deleteStack(settings.roleARN.map(request.withRoleARN(_)).getOrElse(request))
    waitForCompletion(client, stack.stackName, log) match {
      case Failure(_) ⇒ ()
      case Success(r) ⇒ r.foreach(stack ⇒ log.info(s"${stack.getStackName} ${stack.getStackStatus}"))
    }
  }

  def deleteStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<stage> <shortName>").parsed match {
      case Seq(stage, shortName) => {
        (for {
          stack ← Try(awscfStacks.value.get(shortName).getOrElse(sys.error(s"${shortName} of the stack is not defined")))
          _ ← delete(settings, stage, stack, log)
        } yield ()) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒ {
            log.trace(t)
            sys.error(t.getMessage)
          }
        }
      }
      case _ ⇒ sys.error("Usage: <stage> <shortName>")
    }
  }
}
