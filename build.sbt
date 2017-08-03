lazy val root = (project in file(".")).
  settings(
    organization := "com.pigumer.sbt.cloud",
    name := "sbt-aws-cloudformation",
    version := "4.0.0",
    sbtPlugin := true,
    Dependencies.AwsCloudformationDeps
  )
