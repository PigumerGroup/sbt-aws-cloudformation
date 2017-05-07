package cloudformation

import java.io.File

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

case class AwsSettings(region: String = "us-east-1",
                       bucketName: String,
                       templates: File) {
  lazy val credentialsProvider = new DefaultAWSCredentialsProviderChain()
}
