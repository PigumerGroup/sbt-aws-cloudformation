lazy val root = (project in file(".")).
  settings(
    organization := "jp.pigumer.sbt.cloud",
    name := "sbt-aws-cloudformation",
    version := "0.0.1-SNAPSHOT",
    sbtPlugin := true,
    Dependencies.AwsCloudformationDeps
  )
