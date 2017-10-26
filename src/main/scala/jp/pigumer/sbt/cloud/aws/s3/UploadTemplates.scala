package jp.pigumer.sbt.cloud.aws.s3

import jp.pigumer.sbt.cloud.aws.cloudformation.AwscfSettings
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import sbt.Keys._
import sbt._
import sbt.complete.DefaultParsers._

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait UploadTemplates {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  protected def key(dir: String, fileName: String): String

  protected def url(bucketName: String, key: String): String

  protected def url(bucketName: String, dir: String, fileName: String): String

  private def put(dir: String, file: File, uploads: Seq[String])(implicit settings: AwscfSettings,
                                                                 client: AmazonS3,
                                                                 log: Logger): Seq[String] = {
    val bucketName = settings.bucketName.get
    val k = key(dir, file.getName)
    if (file.isFile) {
      val u = url(bucketName, dir, file.getName)
      log.info(s"putObject $u")
      val request = new PutObjectRequest(bucketName, k, file)
      client.putObject(request)
      uploads :+ u
    } else {
      putFiles(k, file.listFiles, uploads)
    }
  }

  @tailrec
  private def putFiles(dir: String,
                       files: Seq[File],
                       uploads: Seq[String])(implicit settings: AwscfSettings,
                                             client: AmazonS3,
                                             log: Logger): Seq[String] = {
    files match {
      case head +: tails ⇒ {
        val res = put(dir, head, uploads)
        putFiles(dir, tails, res)
      }
      case Nil ⇒ uploads
    }
  }

  private def uploads(client: AmazonS3, settings: AwscfSettings, log: Logger) = Try {
    implicit val s = settings
    implicit val l = log
    implicit val s3 = client
    put(settings.projectName, settings.templates.get, Seq.empty)
  }

  def uploadTemplatesTask = Def.task {
    val log = streams.value.log
    val settings = awscfSettings.value
    val client = awss3.value
    uploads(client, settings, log) match {
      case Success(r) ⇒ r
      case Failure(t) ⇒ {
        log.trace(t)
        sys.error(t.toString)
      }
    }
  }

  def putObjectsTask = Def.task {
    val log = streams.value.log
    val client = awss3.value

    val requests = (awss3PutObjectRequests in awss3).value
    val responses = requests.values.map { request ⇒
      Try {
        client.putObject(request)
        val u = url(request.getBucketName, request.getKey)
        log.info(s"putObject $u")
        u
      }
    }.foldLeft(Try(Seq.empty[String])) { (r, url) ⇒
      for {
        x ← r
        u ← url
      } yield x :+ u
    }
    responses match {
      case Success(r) ⇒ r
      case Failure(t) ⇒
        log.trace(t)
        sys.error("putObject failed")
    }
  }

  private def upload(client: AmazonS3,
                     settings: AwscfSettings,
                     bucketName: String,
                     dist: String,
                     key: String,
                     log: Logger) = Try {
    val request = new PutObjectRequest(bucketName, key, new File(dist))
    client.putObject(request)
    val u = url(bucketName, key)
    log.info(s"putObject $u")
    u
  }

  def uploadTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    val client = awss3.value
    spaceDelimited("<dist> <bucket> <key>").parsed match {
      case Seq(dist, bucket, key) ⇒ upload(client, settings, bucket, dist, key, log) match {
        case Success(url) ⇒ url
        case Failure(t) ⇒ {
          log.trace(t)
          sys.error(t.toString)
        }
      }
      case _ ⇒ sys.error("Usage: upload <dist> <bucket> <key>")
    }
  }
}
