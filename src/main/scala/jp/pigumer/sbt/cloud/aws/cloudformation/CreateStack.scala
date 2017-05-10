package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwsSettings, CloudFormationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{CreateStackRequest, Parameter}
import sbt.Def._
import sbt.Keys.streams
import sbt._
import sbt.complete.DefaultParsers.spaceDelimited

import scala.util.{Failure, Success, Try}

trait CreateStack {

  import cloudformation.CloudformationPlugin.autoImport._

  def amazonCloudFormation(settings: AwsSettings): AmazonCloudFormationClient

  def url(awsSettings: AwsSettings, stage: String, template: String): String

  def waitForCompletion(client: AmazonCloudFormationClient, stackName: String, log: Logger): Unit

  private def create(awsSettings: AwsSettings,
                     stage: String,
                     stack: CloudFormationStack,
                     log: Logger) = Try {
    import scala.collection.JavaConverters._

    val u = url(awsSettings, stage, stack.template)
    val params: Seq[Parameter] = stack.parameters.map {
      case (key, value) => {
        val p: Parameter = new Parameter().withParameterKey(key).withParameterValue(value)
        p
      }
    }.toSeq

    val request = new CreateStackRequest().
      withTemplateURL(u).
      withStackName(stack.stackName).
      withCapabilities(stack.capabilities.asJava).
      withParameters(params.asJava)
    val client = amazonCloudFormation(awsSettings)
    client.createStack(request)
    waitForCompletion(client, stack.stackName, log)
  }

  def createStackTask(awsSettings: SettingKey[AwsSettings]) = Def.inputTask {
    val log = streams.value.log
    spaceDelimited("<stage>, <shortName>").parsed match {
      case Seq(stage, shortName) => {
        (for {
          stack <- Try(stacks.value.get(shortName).getOrElse(throw new RuntimeException()))
          _ <- create(awsSettings.value, stage, stack, log)
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
