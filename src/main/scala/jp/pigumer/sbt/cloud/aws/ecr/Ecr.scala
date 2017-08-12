package jp.pigumer.sbt.cloud.aws.ecr

import cloudformation.AwscfSettings
import com.amazonaws.services.ecr.{AmazonECR, AmazonECRClientBuilder}

trait Ecr {

  lazy val amazonECR: (AwscfSettings) => AmazonECR = { settings: AwscfSettings ⇒
    AmazonECRClientBuilder.standard.withCredentials(settings.credentialsProvider).withRegion(settings.region).build
  }
}
