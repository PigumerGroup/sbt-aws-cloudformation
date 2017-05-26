lazy val root = (project in file(".")).
  settings(
    organization := "com.pigumer.sbt.cloud",
    name := "sbt-aws-cloudformation",
    version := "2.2.1-SNAPSHOT",
    sbtPlugin := true,
    Dependencies.AwsCloudformationDeps
  )
