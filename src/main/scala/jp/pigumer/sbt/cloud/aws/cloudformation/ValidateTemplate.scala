package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.ValidateTemplateRequest
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait ValidateTemplate {

  import cloudformation.CloudformationPlugin.autoImport._

  val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  def url(awscfSettings: AwscfSettings, stage: String, template: String): String

  private def validateTemplate(settings: AwscfSettings,
                               stage: String,
                               templateName: String,
                               log: Logger) = Try {

    val u = url(settings, stage, templateName)

    val request = new ValidateTemplateRequest().
      withTemplateURL(u)
    amazonCloudFormation(settings).validateTemplate(request)
  }

  def validateTemplateTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<stage>, <templateName>").parsed match {
      case Seq(stage, templateName) ⇒ {
        (for {
          _ ← validateTemplate(settings, stage, templateName, log)
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
