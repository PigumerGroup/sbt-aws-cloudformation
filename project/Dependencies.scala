import sbt.Keys._
import sbt._

object Dependencies {

  val AwsSdkVersion = "1.11.343"

  val AwsCloudformationDeps = Seq(
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk-cloudformation" % AwsSdkVersion,
      "com.amazonaws" % "aws-java-sdk-sts" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-s3" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-ecr" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-lambda" % AwsSdkVersion,
      "com.amazonaws" % "aws-java-sdk-api-gateway" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-ecs" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-autoscaling" % AwsSdkVersion,
      "com.amazonaws" % "aws-java-sdk-applicationautoscaling" % AwsSdkVersion,

      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )
}
