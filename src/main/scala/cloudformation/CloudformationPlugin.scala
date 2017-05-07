package cloudformation

import sbt.{Def, _}

object CloudformationPlugin extends AutoPlugin {

  import CloudformationKeys._
  import CloudformationTasks._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    syncTemplates := syncTemplatesTask(awsSettings),
    awsSettings := awsSettings.value
  )
}
