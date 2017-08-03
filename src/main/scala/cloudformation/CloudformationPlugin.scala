package cloudformation

import sbt.{Def, _}

object CloudformationPlugin extends AutoPlugin {

  object autoImport extends CloudformationKeys

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    awscfSettings := awscfSettings.value,
    awscfStacks := awscfStacks.?.value.getOrElse(Map.empty[String, CloudformationStack]),

    awscfPutObjectRequests := awscfPutObjectRequests.?.value.getOrElse(AwscfPutObjectRequests(Seq.empty)),

    awscfUploadTemplates := CloudformationTasks.uploadTemplatesTask.value,

    awscfCreateStack := CloudformationTasks.createStackTask.evaluated,
    awscfDeleteStack := CloudformationTasks.deleteStackTask.evaluated,
    awscfUpdateStack := CloudformationTasks.updateStackTask.evaluated,

    awscfValidateTemplate := CloudformationTasks.validateTemplateTask.evaluated,

    awscfListStacks := CloudformationTasks.listStacksTask.value,
    awscfListExports := CloudformationTasks.listExportsTask.value,

    awscfUpload := CloudformationTasks.uploadTask.evaluated,

    awscfPutObjects := CloudformationTasks.putObjectsTask.value,

    awscfCreateBucket := CloudformationTasks.createBucketTask.evaluated
  )
}
