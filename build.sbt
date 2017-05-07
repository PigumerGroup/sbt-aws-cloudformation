lazy val root = (project in file(".")).
  settings(
    organization := "jp.pigumer.sbt.cloud.aws",
    name := "sbt-aws-cloudformation",
    version := "0.0.1",
    sbtPlugin := true,
    Dependencies.AwsCloudformationDeps
  )
