package jp.pigumer.sbt.cloud.aws.s3

import cloudformation.AwscfSettings
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}

trait S3Provider {

  lazy val amazonS3Client: AwscfSettings ⇒ AmazonS3 = { settings ⇒
    AmazonS3ClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build
  }
}
