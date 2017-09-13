package jp.pigumer.sbt.cloud.aws.cloudformation

import java.io.File

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.ValidateTemplateRequest
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.io.Source
import scala.util.{Failure, Success, Try}

trait ValidateTemplate {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  private def validateTemplate(client: AmazonCloudFormation,
                               settings: AwscfSettings,
                               templateName: String,
                               log: Logger) = Try {

    val path = new File(settings.templates.get, templateName)
    val templateBody = Source.fromFile(path).mkString

    val request = new ValidateTemplateRequest().
      withTemplateBody(templateBody)
    client.validateTemplate(request)
  }

  def validateTemplateTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    val client = awscf.value
    spaceDelimited("<templateName>").parsed match {
      case Seq(templateName) ⇒
        (for {
          _ ← validateTemplate(client, settings, templateName, log)
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
