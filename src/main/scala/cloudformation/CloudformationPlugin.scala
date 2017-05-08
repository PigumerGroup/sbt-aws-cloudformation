package cloudformation

import sbt.{Def, _}

object CloudformationPlugin extends AutoPlugin {

  object autoImport extends CloudformationKeys {
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    syncTemplates := CloudformationTasks.syncTemplatesTask(awsSettings).evaluated,

    awsSettings := awsSettings.value
  )
}
