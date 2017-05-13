lazy val root = (project in file(".")).
  settings(
    organization := "com.pigumer.sbt.cloud",
    name := "sbt-aws-cloudformation",
    version := "1.1.0-SNAPSHOT",
    sbtPlugin := true,
    Dependencies.AwsCloudformationDeps
  )
