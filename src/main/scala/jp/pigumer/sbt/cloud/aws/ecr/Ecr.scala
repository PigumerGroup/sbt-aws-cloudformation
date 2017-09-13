package jp.pigumer.sbt.cloud.aws.ecr

import jp.pigumer.sbt.cloud.aws.cloudformation.AwscfSettings
import com.amazonaws.services.ecr.{AmazonECR, AmazonECRClientBuilder}

trait Ecr {

  lazy val ecr: (AwscfSettings) => AmazonECR = settings ⇒
    AmazonECRClientBuilder.standard.withCredentials(settings.credentialsProvider).withRegion(settings.region).build

}
