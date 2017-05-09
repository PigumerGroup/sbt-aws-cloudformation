package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwsSettings
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger, SettingKey}

import scala.util.{Failure, Success, Try}

trait UpdateStack {

  def amazonCloudFormation(settings: AwsSettings): AmazonCloudFormationClient

  private def update(awsSettings: AwsSettings, stage: String, stackName: String, log: Logger) = Try {
    log.info(s"${stage} ${stackName}")
  }

  def updateStackTask(awsSettings: SettingKey[AwsSettings]) = Def.inputTask {
    val log = streams.value.log
    spaceDelimited("<stage>, <stackName>").parsed match {
      case Seq(stage, stackName) => update(awsSettings.value, stage, stackName, log) match {
        case Success(_) => ()
        case Failure(t) => {
          sys.error(t.toString)
        }
      }
      case _ => sys.error("error")
    }
  }
}
