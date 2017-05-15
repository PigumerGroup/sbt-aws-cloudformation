package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{CreateStackRequest, Parameter}
import sbt.Def._
import sbt.Keys.streams
import sbt._
import sbt.complete.DefaultParsers.spaceDelimited

import scala.util.{Failure, Success, Try}

trait CreateStack {

  import cloudformation.CloudformationPlugin.autoImport._

  val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  def url(awsSettings: AwscfSettings, stage: String, template: String): String

  def waitForCompletion(amazonCloudFormation: AmazonCloudFormationClient, stackName: String, log: Logger): Unit

  private def create(settings: AwscfSettings,
                     stage: String,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    import scala.collection.JavaConverters._

    val u = url(settings, stage, stack.template)
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
    client.createStack(settings.roleARN.map(request.withRoleARN(_)).getOrElse(request))
    waitForCompletion(client, stack.stackName, log)
  }

  def createStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<stage> <shortName>").parsed match {
      case Seq(stage, shortName) ⇒ {
        (for {
          stack ← Try(awscfStacks.value.get(shortName).getOrElse(sys.error(s"${shortName} of the stack is not defined")))
          _ ← create(settings, stage, stack, log)
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
