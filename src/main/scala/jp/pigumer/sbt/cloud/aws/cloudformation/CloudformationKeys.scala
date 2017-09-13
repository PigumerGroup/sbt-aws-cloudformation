package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.StackSummary
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest
import jp.pigumer.sbt.cloud.aws.s3.Awss3PutObjectRequests
import sbt._

trait CloudformationKeys {

  lazy val awscf = taskKey[AmazonCloudFormation]("AWS CloudFormation tasks")
  lazy val awss3 = taskKey[AmazonS3]("AWS S3 tasks")
  lazy val awsdynamodb = taskKey[AmazonDynamoDB]("AWS DynamoDB tasks")

  lazy val awscfSettings = settingKey[AwscfSettings]("AWS CloudFormation settings")

  lazy val awscfGetCallerIdentityRequest = taskKey[GetCallerIdentityRequest]("GetCallerIdentityRequest")
  lazy val awscfAccountId = taskKey[String]("Get account id")

  lazy val awscfStacks = taskKey[Stacks]("AWS CloudFormation stack settings")

  lazy val awscfUploadTemplates = taskKey[Seq[String]]("Upload templates to AWS S3 Bucket")

  lazy val awscfCreateStack = inputKey[Unit]("Create stack")
  lazy val awscfDeleteStack = inputKey[Unit]("Delete stack")
  lazy val awscfUpdateStack = inputKey[Unit]("Update stack")

  lazy val awscfValidateTemplate = inputKey[Unit]("Validate template")

  lazy val awscfListExports = taskKey[Stream[AwscfExport]]("List exports")
  lazy val awscfGetValue = inputKey[String]("Get value")

  lazy val awscfListStacks = taskKey[Stream[StackSummary]]("List stacks")

  lazy val awscfCreateBucket = inputKey[Unit]("Create AWS S3 Bucket")

  lazy val awss3Upload = inputKey[String]("Upload AWS S3 Bucket")

  lazy val awss3PutObjects = taskKey[Seq[String]]("Put object AWS S3 Bucket")
  lazy val awss3PutObjectRequests = taskKey[Awss3PutObjectRequests]("AWS S3 put object request")
}
