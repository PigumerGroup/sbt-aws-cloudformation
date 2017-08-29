lazy val root = (project in file(".")).
  settings(
    organization := "com.pigumer.sbt.cloud",
    name := "sbt-aws-cloudformation",
    version := "5.0.4",
    sbtPlugin := true,
    Dependencies.AwsCloudformationDeps
  )
