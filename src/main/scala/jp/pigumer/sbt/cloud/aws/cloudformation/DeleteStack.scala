package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwsSettings
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.DeleteStackRequest
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger, SettingKey}

import scala.util.{Failure, Success, Try}

trait DeleteStack {

  def amazonCloudFormation(settings: AwsSettings): AmazonCloudFormationClient

  def waitForCompletion(client: AmazonCloudFormationClient, stackName: String, log: Logger): Unit

  private def delete(awsSettings: AwsSettings, stage: String, stackName: String, log: Logger) = Try {
    val request = new DeleteStackRequest().
      withStackName(stackName)
    val client = amazonCloudFormation(awsSettings)
    client.deleteStack(request)
    waitForCompletion(client, stackName, log)
  }

  def deleteStackTask(awsSettings: SettingKey[AwsSettings]) = Def.inputTask {
    val log = streams.value.log
    spaceDelimited("<stage>, <stackName>").parsed match {
      case Seq(stage, stackName) => delete(awsSettings.value, stage, stackName, log) match {
        case Success(_) => ()
        case Failure(t) => {
          sys.error(t.toString)
        }
      }
      case _ => sys.error("error")
    }
  }
}
