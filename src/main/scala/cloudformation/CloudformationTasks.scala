package cloudformation

import jp.pigumer.sbt.cloud.aws.cloudformation._
import jp.pigumer.sbt.cloud.aws.s3.{S3Provider, SyncTemplates}

object CloudformationTasks
  extends S3Provider
  with SyncTemplates
  with CloudFormationProvider
  with CreateStack
  with DeleteStack
  with UpdateStack
  with ValidateTemplate {
}
