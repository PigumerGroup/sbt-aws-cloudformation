package jp.pigumer.sbt.cloud.aws.s3

import cloudformation.AwscfSettings
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import sbt.Keys._
import sbt._
import sbt.complete.DefaultParsers._

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait UploadTemplates {

  import cloudformation.CloudformationPlugin.autoImport._

  val amazonS3Client: AwscfSettings ⇒ AmazonS3

  protected def key(dir: String, fileName: String): String

  protected def url(bucketName: String, key: String): String

  protected def url(bucketName: String, dir: String, fileName: String): String

  private def put(dir: String, file: File)(implicit settings: AwscfSettings,
                    client: AmazonS3,
                    log: Logger): Unit = {
    val k = key(dir, file.getName)
    if (file.isFile) {
      val u = url(settings.bucketName, dir, file.getName)
      log.info(s"upload ${u}")
      val request = new PutObjectRequest(settings.bucketName, k, file)
      client.putObject(request)
    } else {
      putFiles(k, file.listFiles)
    }
  }

  @tailrec
  private def putFiles(dir: String,
                  files: Seq[File])(implicit settings: AwscfSettings,
                                    client: AmazonS3,
                                    log: Logger): Unit = {
    files match {
      case head +: Nil ⇒ {
        put(dir, head)
      }
      case head +: tails ⇒ {
        put(dir, head)
        putFiles(dir, tails)
      }
      case _ ⇒ ()
    }
  }

  private def uploads(settings: AwscfSettings, log: Logger) = Try {
    implicit val s = settings
    implicit val l = log
    implicit val client = amazonS3Client(settings)
    put(settings.projectName, settings.templates)
  }

  def uploadTemplatesTask = Def.task {
    val log = streams.value.log
    val settings = awscfSettings.value
    uploads(settings, log) match {
      case Success(_) ⇒ ()
      case Failure(t) ⇒ {
        sys.error(t.toString)
      }
    }
  }

  def putObjectsTask = Def.task {
    val log = streams.value.log
    val settings = awscfSettings.value
    awscfPutObjectRequests.value.requests.foreach { request ⇒
      Try {
        val client = amazonS3Client(settings)
        val u = url(request.getBucketName, request.getKey)
        client.putObject(request)
        log.info(s"putObject $u")
      } match {
        case Success(_) ⇒ ()
        case Failure(t) ⇒ sys.error(t.toString)
      }
    }
  }

  private def upload(settings: AwscfSettings,
                     bucketName: String,
                     dist: String,
                     key: String,
                     log: Logger) = Try {
    val client = amazonS3Client(settings)
    val u = url(bucketName, key)

    log.info(s"upload ${u}")
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
