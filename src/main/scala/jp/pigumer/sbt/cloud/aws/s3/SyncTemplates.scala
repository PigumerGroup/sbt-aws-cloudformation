package jp.pigumer.sbt.cloud.aws.s3

import cloudformation.AwsSettings
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import sbt._
import sbt.complete.DefaultParsers.spaceDelimited

trait SyncTemplates {

  protected def amazonS3Client(settings: AwsSettings): AmazonS3Client

  private def put(client: AmazonS3Client, bucketName: String, stage: String, file: File): File = {
    val key = s"${stage}/${file.name}"
    val request = new PutObjectRequest(bucketName, key, file)
    client.putObject(request)
  }

  def syncTemplatesTask(awsSettings: TaskKey[AwsSettings]) = Def.inputTask {
    val stage = (spaceDelimited("<stage>").parsed match {
      case Seq(a) => Some(a)
      case _ => None
    }).getOrElse(sys.error("Error deploy. useage: deploy <stage>"))

    val settings = awsSettings.value
    val client = amazonS3Client(settings)

    settings.templates.listFiles.filter(f => f.isFile).
      foreach(f => put(client, settings.bucketName, stage, f))
  }
}
