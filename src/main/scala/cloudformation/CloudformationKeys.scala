package cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.StackSummary
import com.amazonaws.services.s3.AmazonS3
import sbt._

trait CloudformationKeys {

  val awscf = taskKey[AmazonCloudFormation]("AWS CloudFormation tasks")
  val awss3 = taskKey[AmazonS3]("AWS S3 tasks")

  val awscfSettings = settingKey[AwscfSettings]("AWS CloudFormation settings")
  val awscfAccountId = taskKey[String]("Get account id")

  val awscfStacks = taskKey[Map[String, CloudformationStack]]("AWS CloudFormation stack settings")

  val awscfUploadTemplates = taskKey[Unit]("Upload templates to AWS S3 Bucket")

  val awscfCreateStack = inputKey[Unit]("Create stack")
  val awscfDeleteStack = inputKey[Unit]("Delete stack")
  val awscfUpdateStack = inputKey[Unit]("Update stack")

  val awscfValidateTemplate = inputKey[Unit]("Validate template")

  val awscfListExports = taskKey[Seq[AwscfExport]]("List exports")
  val awscfListStacks = taskKey[Seq[StackSummary]]("List stacks")

  val awscfCreateBucket = inputKey[Unit]("Create AWS S3 Bucket")


  val awss3Upload = inputKey[Unit]("Upload AWS S3 Bucket")
  val awss3PutObjects = taskKey[Unit]("Put object AWS S3 Bucket")
  val awss3PutObjectRequests = taskKey[AwscfPutObjectRequests]("AWS S3 put object request")
}
