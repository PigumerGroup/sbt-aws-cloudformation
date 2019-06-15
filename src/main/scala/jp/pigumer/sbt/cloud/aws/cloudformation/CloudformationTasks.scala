package jp.pigumer.sbt.cloud.aws.cloudformation

import jp.pigumer.sbt.cloud.aws.s3.{CreateBucket, S3Provider, UploadTemplates}
import jp.pigumer.sbt.cloud.aws.sts.Sts
import sbt.Def
import sbt.Keys.streams
import sbt.complete.DefaultParsers.spaceDelimited

object CloudformationTasks
  extends S3Provider
    with UploadTemplates

    with CloudFormationProvider
    with CreateOrUpdateStack
    with DeleteStack
    with ValidateTemplate
    with ListExports
    with ListStacks

    with CreateBucket
    with Sts {

  private var exports: Map[String, String] = Map.empty

  def getValueTask = Def.inputTask {
    import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

    val log = streams.value.log
    val cf = awscf.value
    val maybeInterval = awscfInterval.?.value
    spaceDelimited("<key>").parsed match {
      case Seq(key) ⇒
        exports.getOrElse(key, {
          exports = ListExports.listExports(cf, maybeInterval, ListStacks.listStacks(cf, maybeInterval)).map(export ⇒ (export.name, export.value)).toMap
          exports(key)
        })
      case _ ⇒ sys.error("Usage: <key>")
    }
  }
}
