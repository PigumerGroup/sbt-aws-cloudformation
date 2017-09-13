package jp.pigumer.sbt.cloud.aws.s3

import jp.pigumer.sbt.cloud.aws.cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{CreateStackRequest, DescribeStacksRequest, Parameter, Stack}
import jp.pigumer.sbt.cloud.aws.cloudformation.CloudFormationWaiter
import sbt.Keys.streams
import sbt.complete.DefaultParsers.spaceDelimited
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait CreateBucket {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  def describeStacks(client: AmazonCloudFormation,
                     request: DescribeStacksRequest): Stream[Stack]

  private def createStack(client: AmazonCloudFormation,
                          settings: AwscfSettings,
                          stackName: String,
                          bucketName: String,
                          log: Logger): Try[Unit] = Try {
    import scala.collection.JavaConverters._

    val yaml =
      """
        |AWSTemplateFormatVersion: '2010-09-09'
        |Parameters:
        |  BucketName:
        |    Type: String
        |    Description: BucketName
        |Resources:
        |  Bucket:
        |    Type: AWS::S3::Bucket
        |    Properties:
        |      BucketName: !Ref 'BucketName'
      """.stripMargin
    log.debug(yaml)

    val request = new CreateStackRequest().
      withTemplateBody(yaml).
      withStackName(stackName).
      withParameters(Seq(
        new Parameter().
          withParameterKey("BucketName").
          withParameterValue(bucketName)).asJava)

    log.info(s"$stackName $bucketName")
    client.createStack(settings.roleARN.map(r ⇒ request.withRoleARN(r)).getOrElse(request))

    val describeStacksRequest = new DescribeStacksRequest().withStackName(stackName)
    new CloudFormationWaiter(client, client.waiters.stackCreateComplete).wait(describeStacksRequest)

    describeStacks(client, describeStacksRequest).map { s ⇒
      log.info(s"${s.getStackName} ${s.getStackStatus}")
      s
    }
  }

  def createBucketTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    val client = awscf.value
    val bucketName = settings.bucketName.get
    spaceDelimited("<stackName> <bucketName>").parsed match {
      case Seq(stackName, bucketName) ⇒
        createStack(client, settings, stackName, bucketName, log) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒
            log.trace(t)
            sys.error(t.getMessage)
        }
      case Seq(stackName) ⇒
        createStack(client, settings, stackName, bucketName, log) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒
            log.trace(t)
            sys.error(t.getMessage)
        }
      case _ ⇒ sys.error("Usage: createBucket <stackName> <bucketName>")
    }
  }
}
