lazy val root = (project in file(".")).
  settings(
    organization := "com.pigumer.sbt.cloud",
    name := "sbt-aws-cloudformation",
    version := "3.3.0",
    sbtPlugin := true,
    Dependencies.AwsCloudformationDeps
  )
