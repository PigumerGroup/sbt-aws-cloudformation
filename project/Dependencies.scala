import sbt.Keys._
import sbt._

object Dependencies {

  val AwsSdkVersion = "1.11.175"

  val AwsCloudformationDeps = Seq(
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk-cloudformation" % AwsSdkVersion,
      "com.amazonaws" % "aws-java-sdk-sts" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-s3" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-dynamodb" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-ecr" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-lambda" % AwsSdkVersion,
      "com.amazonaws" % "aws-java-sdk-api-gateway" % AwsSdkVersion,

      "com.amazonaws" % "aws-java-sdk-ecs" % AwsSdkVersion,

      "org.specs2" %% "specs2-core" % "3.8.6" % Test,
      "org.specs2" %% "specs2-mock" % "3.8.6" % Test,
      "org.specs2" %% "specs2-junit" % "3.8.6" % Test
    )
  )
}
