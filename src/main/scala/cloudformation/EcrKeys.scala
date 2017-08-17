package cloudformation

import com.amazonaws.services.ecr.AmazonECR
import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest
import sbt._

trait EcrKeys {

  lazy val awsecr = taskKey[AmazonECR]("AWS ECR tasks")

  val awsecrGetAuthorizationTokenRequest = taskKey[GetAuthorizationTokenRequest]("getAuthorizationTokenRequest")
  val awsecrCredential = taskKey[AwsecrCredential]("getAuthorizationToken")
  val awsecrDomain = taskKey[String]("Get ECR domain")
}
