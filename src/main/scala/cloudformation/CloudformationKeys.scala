package cloudformation

import com.amazonaws.services.cloudformation.model.StackSummary
import sbt._

trait CloudformationKeys {
  val awscfSettings = settingKey[AwscfSettings]("")
  val awscfStacks = settingKey[Map[String, CloudformationStack]]("")

  val awscfUploadTemplates = inputKey[Unit]("")

  val awscfCreateStack = inputKey[Unit]("")
  val awscfDeleteStack = inputKey[Unit]("")
  val awscfUpdateStack = inputKey[Unit]("")

  val awscfValidateTemplate = inputKey[Unit]("")

  val awscfListExports = taskKey[Seq[AwscfExport]]("")
  val awscfListStacks = taskKey[Seq[StackSummary]]("")
}
