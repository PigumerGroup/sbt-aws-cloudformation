package jp.pigumer.sbt.cloud.aws.s3

import java.io.File

import cloudformation.AwscfSettings
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import sbt.Keys._
import sbt._
import sbt.complete.DefaultParsers._

import scala.util.{Failure, Success, Try}

trait UploadTemplates {

  import cloudformation.CloudformationPlugin.autoImport._

  val amazonS3Client: AwscfSettings ⇒ AmazonS3Client

  protected def url(bucketName: String, stage: String, templates: File, template: String): String

  private def put(settings: AwscfSettings,
                  client: AmazonS3Client,
                  log: Logger,
                  key: String,
                  file: java.io.File) {
    if (file.isFile) {
      val u = url(settings.bucketName, key, settings.templates, file.getName)
      log.info(s"upload ${file.getName} to ${u}")
      val request = new PutObjectRequest(settings.bucketName, s"${key}/${file.getName}", file)
      client.putObject(request)
      return
    }
    file.listFiles.foreach(f ⇒ put(settings, client, log, s"${key}/${file.getName}", f))
  }

  private def uploads(settings: AwscfSettings, stage: String, log: Logger) = Try {
    val dir = settings.projectName.map(p ⇒ s"${p}${stage}").getOrElse(stage)
    val client = amazonS3Client(settings)
    put(settings, client, log, dir, settings.templates)
  }

  def uploadTemplatesTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<stage>").parsed match {
      case Seq(stage) ⇒ uploads(settings, stage, log) match {
        case Success(_) ⇒ ()
        case Failure(t) ⇒ {
          sys.error(t.toString)
        }
      }
      case _ ⇒ sys.error("Usage: uploadTemplates <stage>")
    }
  }

  private def upload(settings: AwscfSettings,
                     bucketName: String,
                     dist: String,
                     key: String,
                     log: Logger) = Try {
    val client = amazonS3Client(settings)
    val u = s"https://${bucketName}.s3.amazonaws.com/${key}"

    log.info(s"upload ${dist} to ${u}")
    val request = new PutObjectRequest(settings.bucketName, key, new File(dist))
    client.putObject(request)
  }

  def uploadTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<dist> <bucket> <key>").parsed match {
      case Seq(dist, bucket, key) ⇒ upload(settings, bucket, dist, key, log) match {
        case Success(_) ⇒ ()
        case Failure(t) ⇒ {
          sys.error(t.toString)
        }
      }
      case _ ⇒ sys.error("Usage: upload <dist> <bucket> <key>")
    }
  }
}
