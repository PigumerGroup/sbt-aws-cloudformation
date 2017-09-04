package jp.pigumer.sbt.cloud.aws.s3

import cloudformation.AwscfSettings
import com.amazonaws.services.s3.AmazonS3ClientBuilder

trait S3Provider {

  lazy val amazonS3 = (settings: AwscfSettings) ⇒
    AmazonS3ClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build

  protected def key(dir: String, fileName: String): String =
    if (dir.isEmpty) {
      fileName
    } else {
      s"$dir/$fileName"
    }
  
  protected def url(bucketName: String, key: String): String =
    s"https://s3.amazonaws.com/$bucketName/$key"

  protected def url(bucketName: String, dir: String, fileName: String): String =
    url(bucketName, key(dir, fileName))


}
