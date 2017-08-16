package jp.pigumer.sbt.cloud.aws.sts

import cloudformation.AwscfSettings
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder

trait Sts {

  lazy val sts = (settings: AwscfSettings) ⇒
    AWSSecurityTokenServiceClientBuilder.standard.withCredentials(settings.credentialsProvider).withRegion(settings.region).build
}
