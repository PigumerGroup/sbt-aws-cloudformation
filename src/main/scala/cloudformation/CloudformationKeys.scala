package cloudformation

import sbt._

trait CloudformationKeys {
  val awscfSettings = settingKey[AwscfSettings]("")
  val awscfStacks = settingKey[Map[String, CloudformationStack]]("")

  val awscfSyncTemplates = inputKey[Unit]("upload cloudformation templates")

  val awscfCreateStack = inputKey[Unit]("")
  val awscfDeleteStack = inputKey[Unit]("")
  val awscfUpdateStack = inputKey[Unit]("")
}
