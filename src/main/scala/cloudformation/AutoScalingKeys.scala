package cloudformation

import com.amazonaws.services.autoscaling.AmazonAutoScaling
import sbt._

trait AutoScalingKeys {

  lazy val awsAutoScaling = taskKey[AmazonAutoScaling]("AWS AutoScaling tasks")

}
