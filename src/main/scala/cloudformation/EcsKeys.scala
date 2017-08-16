package cloudformation

import com.amazonaws.services.ecs.AmazonECS
import sbt._

trait EcsKeys {

  lazy val awsecs = taskKey[AmazonECS]("AWS ECS tasks")
}
