package cloudformation

import sbt.{Def, _}

object CloudformationPlugin extends AutoPlugin {

  object autoImport extends CloudformationKeys {
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    awscfSettings := awscfSettings.value,
    awscfStacks := awscfStacks.value,

    awscfSyncTemplates := CloudformationTasks.syncTemplatesTask(awscfSettings).evaluated,

    awscfCreateStack := CloudformationTasks.createStackTask(awscfSettings).evaluated,
    awscfDeleteStack := CloudformationTasks.deleteStackTask(awscfSettings).evaluated,
    awscfUpdateStack := CloudformationTasks.updateStackTask(awscfSettings).evaluated
  )
}
