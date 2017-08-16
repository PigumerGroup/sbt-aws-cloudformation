package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{DeleteStackRequest, Stack}
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait DeleteStack {

  import cloudformation.CloudformationPlugin.autoImport._

  protected val cloudFormation: AwscfSettings ⇒ AmazonCloudFormation

  protected def waitForCompletion(amazonCloudFormation: AmazonCloudFormation,
                                  stackName: String,
                                  log: Logger): Try[Seq[Stack]]

  private def delete(settings: AwscfSettings,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    val request = new DeleteStackRequest().
      withStackName(stack.stackName)

    log.info(s"Delete ${stack.stackName}")

    val client = cloudFormation(settings)
    client.deleteStack(settings.roleARN.map(r ⇒ request.withRoleARN(r)).getOrElse(request))
    waitForCompletion(client, stack.stackName, log) match {
      case Failure(_) ⇒ ()
      case Success(r) ⇒ r.foreach(stack ⇒ log.info(s"${stack.getStackName} ${stack.getStackStatus}"))
    }
  }

  def deleteStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<shortName>").parsed match {
      case Seq(shortName) =>
        (for {
          stack ← Try(awscfStacks.value.getOrElse(shortName, sys.error(s"$shortName of the stack is not defined")))
          _ ← delete(settings, stack, log)
        } yield ()) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒ {
            log.trace(t)
            sys.error(t.getMessage)
          }
        }
      case _ ⇒ sys.error("Usage: <shortName>")
    }
  }
}
