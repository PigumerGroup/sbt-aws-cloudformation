package cloudformation

import sbt.{Def, _}

object CloudformationPlugin extends AutoPlugin {

  object autoImport extends CloudformationKeys {
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    awscfSettings := awscfSettings.value,
    awscfStacks := awscfStacks.value,

    awscfSyncTemplates := CloudformationTasks.syncTemplatesTask.evaluated,

    awscfCreateStack := CloudformationTasks.createStackTask.evaluated,
    awscfDeleteStack := CloudformationTasks.deleteStackTask.evaluated,
    awscfUpdateStack := CloudformationTasks.updateStackTask.evaluated,

    awscfValidateTemplate := CloudformationTasks.validateTemplateTask.evaluated
  )
}
