package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{Parameter, UpdateStackRequest}
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait UpdateStack {

  import cloudformation.CloudformationPlugin.autoImport._

  val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  def url(awscfSettings: AwscfSettings, stage: String, template: String): String

  def waitForCompletion(amazonCloudFormation: AmazonCloudFormationClient, stackName: String, log: Logger): Unit

  private def update(settings: AwscfSettings,
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

    val request = new UpdateStackRequest().
      withTemplateURL(u).
      withStackName(stack.stackName).
      withCapabilities(stack.capabilities.asJava).
      withParameters(params.asJava)

    val client = amazonCloudFormation(settings)
    client.updateStack(request)
    waitForCompletion(client, stack.stackName, log)
  }

  def updateStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<stage>, <shortName>").parsed match {
      case Seq(stage, shortName) ⇒ {
        (for {
          stack ← Try(awscfStacks.value.get(shortName).getOrElse(throw new RuntimeException()))
          _ ← update(settings, stage, stack, log)
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
