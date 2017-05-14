package cloudformation

import java.io.File

import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}

case class AwscfSettings(region: String = "us-east-1",
                         bucketName: String,
                         templates: File,
                         credentialsProvider: AWSCredentialsProviderChain = new DefaultAWSCredentialsProviderChain(),
                         roleARN: Option[String] = None)
