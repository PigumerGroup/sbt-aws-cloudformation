package jp.pigumer.sbt.cloud.aws.autoscaling

import jp.pigumer.sbt.cloud.aws.cloudformation.AwscfSettings
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder

object AutoScaling {

  lazy val autoScaling = (settings: AwscfSettings) ⇒
    AmazonAutoScalingClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build
}
