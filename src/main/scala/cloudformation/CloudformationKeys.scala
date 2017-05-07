package cloudformation

import sbt._

object CloudformationKeys {
  val awsSettings = taskKey[AwsSettings]("")
  val syncTemplates = inputKey[Unit]("")
}
