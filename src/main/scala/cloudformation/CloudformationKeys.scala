package cloudformation

import sbt._

trait CloudformationKeys {
  val awsSettings = settingKey[AwsSettings]("")

  val syncTemplates = inputKey[Unit]("upload cloudformation templates")

  val createStack = inputKey[Unit]("")
  val deleteStack = inputKey[Unit]("")
  val updateStack = inputKey[Unit]("")
}
