package cloudformation

import sbt._

trait CloudformationKeys {
  val awsSettings = settingKey[AwsSettings]("")
  val syncTemplates = inputKey[Unit]("upload cloudformation templates")
}
