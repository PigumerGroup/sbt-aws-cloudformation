package cloudformation

import sbt.File

import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}

case class AwscfSettings(region: String = "us-east-1",
                         bucketName: String,
                         projectName: String = "",
                         templates: File,
                         credentialsProvider: AWSCredentialsProviderChain = new DefaultAWSCredentialsProviderChain(),
                         roleARN: Option[String] = None) {
  val baseDir = if (projectName.isEmpty) {
    templates.getName
  } else {
    s"$projectName/${templates.getName}"
  }
}
