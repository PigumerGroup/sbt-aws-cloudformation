package cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.StackSummary
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest
import sbt._

trait CloudformationKeys {

  lazy val awscf = taskKey[AmazonCloudFormation]("AWS CloudFormation tasks")
  lazy val awss3 = taskKey[AmazonS3]("AWS S3 tasks")
  lazy val awsdynamodb = taskKey[AmazonDynamoDB]("AWS DynamoDB tasks")

  val awscfSettings = settingKey[AwscfSettings]("AWS CloudFormation settings")

  val awscfGetCallerIdentityRequest = taskKey[GetCallerIdentityRequest]("GetCallerIdentityRequest")
  lazy val awscfAccountId = taskKey[String]("Get account id")

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
  val awss3PutObjectRequests = taskKey[Awss3PutObjectRequests]("AWS S3 put object request")
}
