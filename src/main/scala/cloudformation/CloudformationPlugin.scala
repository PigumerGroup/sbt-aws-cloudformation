package cloudformation

import sbt.{Def, _}

object CloudformationPlugin extends AutoPlugin {

  object autoImport extends CloudformationKeys {
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    awsSettings := awsSettings.value,
    stacks := stacks.value,

    syncTemplates := CloudformationTasks.syncTemplatesTask(awsSettings).evaluated,

    createStack := CloudformationTasks.createStackTask(awsSettings).evaluated,
    deleteStack := CloudformationTasks.deleteStackTask(awsSettings).evaluated,
    updateStack := CloudformationTasks.updateStackTask(awsSettings).evaluated
  )
}
