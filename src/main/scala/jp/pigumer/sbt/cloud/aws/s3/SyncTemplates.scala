package jp.pigumer.sbt.cloud.aws.s3

import cloudformation.AwscfSettings
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import sbt.Keys._
import sbt._
import sbt.complete.DefaultParsers._

import scala.util.{Failure, Success, Try}

trait SyncTemplates {

  import cloudformation.CloudformationPlugin.autoImport._

  def amazonS3Client(settings: AwscfSettings): AmazonS3Client

  private def put(client: AmazonS3Client, log: Logger, bucketName: String, key: String, file: java.io.File) {
    if (file.isFile) {
      val k = s"${key}/${file.getName}"
      log.info(s"upload ${k} to ${bucketName}")
      val request = new PutObjectRequest(bucketName, s"${key}/${file.getName}", file)
      client.putObject(request)
      return
    }
    file.listFiles.foreach(f => put(client, log, bucketName, s"${key}/${file.getName}", f))
  }

  private def uploads(awsSettings: AwscfSettings, stage: String, log: Logger) = Try {
    val client = amazonS3Client(awsSettings)
    put(client, log, awsSettings.bucketName, stage, awsSettings.templates)
  }

  def syncTemplatesTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<stage>").parsed match {
      case Seq(stage) => uploads(settings, stage, log) match {
        case Success(_) => ()
        case Failure(t) => {
          sys.error(t.toString)
        }
      }
      case _ => sys.error("error")
    }
  }
}
