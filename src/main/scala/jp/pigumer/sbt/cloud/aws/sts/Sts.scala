package jp.pigumer.sbt.cloud.aws.sts

import cloudformation.AwscfSettings
import com.amazonaws.services.securitytoken.{AWSSecurityTokenService, AWSSecurityTokenServiceClientBuilder}

trait Sts {

  val sts: (AwscfSettings) => AWSSecurityTokenService = settings ⇒
    AWSSecurityTokenServiceClientBuilder.standard.withCredentials(settings.credentialsProvider).withRegion(settings.region).build
}
