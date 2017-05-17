package cloudformation

import com.amazonaws.services.cloudformation.model.StackSummary
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import sbt._

trait CloudformationKeys {

  val awscfSettings = settingKey[AwscfSettings]("AWS CloudFormation settings")
  val awscfStacks = settingKey[Map[String, CloudformationStack]]("AWS CloudFormation stack settings")

  val awscfUploadTemplates = inputKey[Unit]("Upload templates to AWS S3 Bucket")

  val awscfCreateStack = inputKey[Unit]("Create stack")
  val awscfDeleteStack = inputKey[Unit]("Delete stack")
  val awscfUpdateStack = inputKey[Unit]("Update stack")

  val awscfValidateTemplate = inputKey[Unit]("Validate template")

  val awscfListExports = taskKey[Seq[AwscfExport]]("List exports")
  val awscfListStacks = taskKey[Seq[StackSummary]]("List stacks")

  val awscfUpload = inputKey[Unit]("Upload AWS S3 Bucket")
}
