package jp.pigumer.sbt.cloud.aws.ecr

import cloudformation.{AwscfECRCredential, AwscfSettings}
import com.amazonaws.services.ecr.{AmazonECR, AmazonECRClientBuilder}
import sbt.Def

trait Ecr {

  import cloudformation.CloudformationPlugin.autoImport._

  lazy val amazonECR: (AwscfSettings) => AmazonECR = { settings: AwscfSettings ⇒
    AmazonECRClientBuilder.standard.withCredentials(settings.credentialsProvider).withRegion(settings.region).build
  }

  def getECRAuthorizationToken = Def.task {
    awscfECRAuthorizationTokenRequest.value match {
      case Some(r) ⇒ {
        val result = amazonECR(awscfSettings.value).getAuthorizationToken(r)
        AwscfECRCredential(result)
      }
      case _ ⇒ sys.error("required awscfECRAuthorizationTokenRequest")
    }
  }
}
