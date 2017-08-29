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

}
