package cloudformation

import jp.pigumer.sbt.cloud.aws.cloudformation._
import jp.pigumer.sbt.cloud.aws.dynamodb.{DynamoDBProvider, UpdateTimeToLive}
import jp.pigumer.sbt.cloud.aws.ecr.Ecr
import jp.pigumer.sbt.cloud.aws.s3.{CreateBucket, S3Provider, UploadTemplates}
import jp.pigumer.sbt.cloud.aws.sts.Sts

object CloudformationTasks
  extends S3Provider
  with UploadTemplates

  with CloudFormationProvider
  with CreateStack
  with DeleteStack
  with UpdateStack
  with ValidateTemplate
  with ListExports
  with ListStacks

  with DynamoDBProvider
  with UpdateTimeToLive
  with CreateBucket

  with Ecr
  with Sts {
}
