package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwsSettings
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.CreateStackRequest
import sbt.Def._
import sbt.Keys.streams
import sbt._
import sbt.complete.DefaultParsers.spaceDelimited

import scala.util.{Failure, Success, Try}

trait CreateStack {

  def amazonCloudFormation(settings: AwsSettings): AmazonCloudFormationClient

  def url(awsSettings: AwsSettings, stage: String, template: String): String

  def waitForCompletion(client: AmazonCloudFormationClient, stackName: String, log: Logger): Unit

  private def create(awsSettings: AwsSettings,
                     stage: String,
                     stackName: String,
                     template: String,
                     log: Logger) = Try {
    val u = url(awsSettings, stage, template)
    val request = new CreateStackRequest().
      withTemplateURL(u).
      withStackName(stackName)
    val client = amazonCloudFormation(awsSettings)
    client.createStack(request)
    waitForCompletion(client, stackName, log)
  }

  def createStackTask(awsSettings: SettingKey[AwsSettings]) = Def.inputTask {
    val log = streams.value.log
    spaceDelimited("<stage>, <stackName> <template>").parsed match {
      case Seq(stage, stackName, template) => create(awsSettings.value, stage, stackName, template, log) match {
        case Success(_) => ()
        case Failure(t) => {
          sys.error(t.toString)
        }
      }
      case _ => sys.error("error")
    }
  }
}
