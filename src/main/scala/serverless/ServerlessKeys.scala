package serverless

import sbt._

trait ServerlessKeys {

  lazy val serverless = taskKey[String]("Serverless")

  lazy val serverlessPath = settingKey[String]("Path to the Serverless.")
  lazy val serverlessWorkingDirectory = settingKey[File]("Path to the Serverless working directory")

  lazy val serverlessExec = inputKey[Unit]("Execute serverless.")

  lazy val serverlessEnvironment = taskKey[Environment]("Environment to the Serverless")
}
