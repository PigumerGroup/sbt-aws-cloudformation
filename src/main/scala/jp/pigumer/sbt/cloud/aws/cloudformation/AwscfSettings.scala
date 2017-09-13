package jp.pigumer.sbt.cloud.aws.cloudformation

import sbt.File

import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}

case class AwscfSettings(region: String = "us-east-1",
                         bucketName: Option[String] = None,
                         projectName: String = "",
                         templates: Option[File] = None,
                         credentialsProvider: AWSCredentialsProviderChain = new DefaultAWSCredentialsProviderChain(),
                         roleARN: Option[String] = None) {
  val baseDir = if (projectName.isEmpty) {
    templates.map(_.getName())
  } else {
    templates.map(f ⇒ Some(s"$projectName/${f.getName}")).getOrElse(Some(projectName))
  }
}
