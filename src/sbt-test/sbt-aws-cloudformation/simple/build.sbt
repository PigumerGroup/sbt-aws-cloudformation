import cloudformation._

lazy val root = (project in file(".")).
  enablePlugins(CloudformationPlugin).
  settings(
    version := "0.1",
    scalaVersion := "2.12.2",
    awsSettings := AwsSettings(
      region = "ap-northeast-1",
      bucketName = "jp-pigumer-test",
      templates = file("cloudformation")
    )
  )
