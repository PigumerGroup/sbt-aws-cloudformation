package jp.pigumer.sbt.cloud.aws.ecs

import jp.pigumer.sbt.cloud.aws.cloudformation.AwscfSettings
import com.amazonaws.services.ecs.AmazonECSClientBuilder

trait Ecs {

  lazy val ecs = (settings: AwscfSettings) ⇒
    AmazonECSClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build

}
