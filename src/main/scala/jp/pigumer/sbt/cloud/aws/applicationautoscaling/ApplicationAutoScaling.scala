package jp.pigumer.sbt.cloud.aws.applicationautoscaling

import jp.pigumer.sbt.cloud.aws.cloudformation.AwscfSettings
import com.amazonaws.services.applicationautoscaling.AWSApplicationAutoScalingClientBuilder

object ApplicationAutoScaling {

  lazy val applicationAutoScaling = (settings: AwscfSettings) ⇒
    AWSApplicationAutoScalingClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build
}
