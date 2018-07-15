lazy val root = (project in file(".")).
  settings(
    organization := "com.pigumer.sbt.cloud",
    name := "sbt-aws-cloudformation",
    version := "5.0.24-SNAPSHOT",
    sbtPlugin := true,
    javacOptions ++= Seq("-target", "1.8"),
    scalaCompilerBridgeSource := {
      val sv = appConfiguration.value.provider.id.version
      ("org.scala-sbt" % "compiler-interface" % sv % "component").sources
    },
    Dependencies.AwsCloudformationDeps
  )
