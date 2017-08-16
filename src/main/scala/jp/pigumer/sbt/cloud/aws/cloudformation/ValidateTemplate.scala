package jp.pigumer.sbt.cloud.aws.cloudformation

import java.io.File

import cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.ValidateTemplateRequest
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.io.Source
import scala.util.{Failure, Success, Try}

trait ValidateTemplate {

  import cloudformation.CloudformationPlugin.autoImport._

  protected val cloudFormation: AwscfSettings ⇒ AmazonCloudFormation

  private def validateTemplate(settings: AwscfSettings,
                               templateName: String,
                               log: Logger) = Try {

    val path = new File(settings.templates, templateName)
    val templateBody = Source.fromFile(path).mkString

    val request = new ValidateTemplateRequest().
      withTemplateBody(templateBody)
    cloudFormation(settings).validateTemplate(request)
  }

  def validateTemplateTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<templateName>").parsed match {
      case Seq(templateName) ⇒
        (for {
          _ ← validateTemplate(settings, templateName, log)
        } yield ()) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒
            log.trace(t)
            sys.error(t.toString)
        }
      case _ ⇒ sys.error("Usage: <templateName>")
    }
  }
}
