package jp.pigumer.sbt.cloud.aws.applicationautoscaling

import cloudformation.AwscfSettings
import com.amazonaws.services.applicationautoscaling.AWSApplicationAutoScalingClientBuilder

object ApplicationAutoScaling {

  lazy val applicationAutoScaling = (settings: AwscfSettings) ⇒
    AWSApplicationAutoScalingClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build
}
