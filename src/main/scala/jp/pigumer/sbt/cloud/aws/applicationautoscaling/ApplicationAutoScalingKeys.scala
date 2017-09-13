package jp.pigumer.sbt.cloud.aws.applicationautoscaling

import com.amazonaws.services.applicationautoscaling.AWSApplicationAutoScaling
import sbt._

trait ApplicationAutoScalingKeys {

  lazy val awsApplicationAutoScaling = taskKey[AWSApplicationAutoScaling]("AWS Application-AutoScaling tasks")

}
