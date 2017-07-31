package jp.pigumer.sbt.cloud.aws.cloudformation

import java.io.File

import cloudformation.{AwscfSettings, AwscfTTLSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{CreateStackRequest, Parameter, Stack, StackStatus}
import sbt.Def._
import sbt.Keys.streams
import sbt._
import sbt.complete.DefaultParsers.spaceDelimited

import scala.util.{Failure, Success, Try}

trait CreateStack {

  import cloudformation.CloudformationPlugin.autoImport._

  val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  protected def url(bucketName: String, stage: String, templates: File, template: String): String

  protected def updateTimeToLive(settings: AwscfSettings, ttl: AwscfTTLSettings): Unit

  protected def waitForCompletion(client: AmazonCloudFormationClient,
                                  stackName: String,
                                  log: Logger): Try[Seq[Stack]]

  private def create(settings: AwscfSettings,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    import scala.collection.JavaConverters._

    val u = url(settings.bucketName, settings.dir, settings.templates, stack.template)
    val params: Seq[Parameter] = stack.parameters.map {
      case (key, value) ⇒ {
        val p: Parameter = new Parameter().withParameterKey(key).withParameterValue(value)
        p
      }
    }.toSeq

    val request = new CreateStackRequest().
      withTemplateURL(u).
      withStackName(stack.stackName).
      withCapabilities(stack.capabilities.asJava).
      withParameters(params.asJava)

    val client = amazonCloudFormation(settings)

    log.info(s"Create ${stack.stackName}")
    client.createStack(settings.roleARN.map(request.withRoleARN(_)).getOrElse(request))
    waitForCompletion(client, stack.stackName, log) match {
      case Failure(t) ⇒ throw t
      case Success(r) ⇒ {
        r.foreach(stack ⇒ log.info(s"${stack.getStackName} ${stack.getStackStatus}"))
        if (!r.forall(_.getStackStatus == StackStatus.CREATE_COMPLETE.toString)) {
          throw CreateStackException()
        }
      }
    }
  }

  def createStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<shortName>").parsed match {
      case Seq(shortName) ⇒ {
        (for {
          stack ← Try(awscfStacks.value.get(shortName).getOrElse(sys.error(s"${shortName} of the stack is not defined")))
          _ ← create(settings, stack, log)
          _ ← Try(stack.ttl.foreach(t ⇒ updateTimeToLive(settings, t)))
        } yield ()) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒ {
            log.trace(t)
            sys.error(t.getMessage)
          }
        }
      }
      case _ ⇒ sys.error("Usage: <shortName>")
    }
  }
}
