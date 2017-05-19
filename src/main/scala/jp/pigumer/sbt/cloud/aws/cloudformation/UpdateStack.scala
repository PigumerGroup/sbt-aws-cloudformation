package jp.pigumer.sbt.cloud.aws.cloudformation

import java.io.File

import cloudformation.{AwscfSettings, AwscfTTLSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{Parameter, Stack, UpdateStackRequest}
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait UpdateStack {

  import cloudformation.CloudformationPlugin.autoImport._

  protected val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  protected def updateTimeToLive(settings: AwscfSettings, ttl: AwscfTTLSettings): Unit

  protected def url(bucketName: String, stage: String, templates: File, template: String): String

  protected def waitForCompletion(client: AmazonCloudFormationClient,
                                  stackName: String,
                                  log: Logger): Try[Seq[Stack]]

  private def update(settings: AwscfSettings,
                     stage: String,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    import scala.collection.JavaConverters._

    val dir = settings.projectName.map(p ⇒ s"${p}${stage}").getOrElse(stage)
    val u = url(settings.bucketName, dir, settings.templates, stack.template)
    val params: Seq[Parameter] = stack.parameters.map {
      case (key, value) ⇒ {
        val p: Parameter = new Parameter().withParameterKey(key).withParameterValue(value)
        p
      }
    }.toSeq

    val request = new UpdateStackRequest().
      withTemplateURL(u).
      withStackName(stack.stackName).
      withCapabilities(stack.capabilities.asJava).
      withParameters(params.asJava)

    log.info(s"Update ${stack.stackName}")

    val client = amazonCloudFormation(settings)
    client.updateStack(settings.roleARN.map(request.withRoleARN(_)).getOrElse(request))
    waitForCompletion(client, stack.stackName, log) match {
      case Failure(t) ⇒ throw t
      case Success(r) ⇒ r.foreach(stack ⇒ log.info(s"${stack.getStackName} ${stack.getStackStatus}"))
    }
  }

  def updateStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<stage> <shortName>").parsed match {
      case Seq(stage, shortName) ⇒ {
        (for {
          stack ← Try(awscfStacks.value.get(shortName).getOrElse(sys.error(s"${shortName} of the stack is not defined")))
          _ ← update(settings, stage, stack, log)
          _ ← Try(stack.ttl.foreach(t ⇒ updateTimeToLive(settings, t)))
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
