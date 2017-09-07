package cloudformation

import com.amazonaws.services.ecr.AmazonECR
import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest
import sbt._

trait EcrKeys {

  lazy val awsecr = taskKey[AmazonECR]("AWS ECR tasks")

  lazy val awsecrGetAuthorizationTokenRequest = taskKey[GetAuthorizationTokenRequest]("getAuthorizationTokenRequest")
  lazy val awsecrCredential = taskKey[AwsecrCredential]("getAuthorizationToken")
  lazy val awsecrDomain = taskKey[String]("Get ECR domain")

  lazy val awsecrDockerPath = taskKey[String]("Path to the Docker binary.")
  lazy val awsecrLogin = taskKey[String]("AWS ECR Login")
}
