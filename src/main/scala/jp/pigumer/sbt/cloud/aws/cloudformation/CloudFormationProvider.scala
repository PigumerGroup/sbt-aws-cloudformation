package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.model.{DescribeStacksRequest, StackStatus}
import com.amazonaws.services.cloudformation.{AmazonCloudFormationClient, AmazonCloudFormationClientBuilder}
import sbt.Logger

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

trait CloudFormationProvider {

  lazy val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient = settings ⇒ {
    val builder = AmazonCloudFormationClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region)
    builder.build.asInstanceOf[AmazonCloudFormationClient]
  }

  def url(awsSettings: AwscfSettings, stage: String, template: String): String =
    s"https://${awsSettings.bucketName}.s3.amazonaws.com/${stage}/${awsSettings.templates.getName}/${template}"

  def waitForCompletion(client: AmazonCloudFormationClient, stackName: String, log: Logger) = {
    val request = new DescribeStacksRequest().
      withStackName(stackName)

    var completed = false
    while (!completed) {
      completed = Try(client.describeStacks(request).getStacks.asScala.toSeq) match {
        case Failure(t) ⇒ true
        case Success(stacks) ⇒ if (stacks.isEmpty) {
          log.warn("Stack has been deleted")
          true
        } else {
          stacks.map(_.getStackStatus).
            forall(s ⇒ {
              s == StackStatus.CREATE_COMPLETE.toString ||
              s == StackStatus.CREATE_FAILED.toString ||
              s == StackStatus.ROLLBACK_FAILED.toString ||
              s == StackStatus.UPDATE_COMPLETE.toString ||
              s == StackStatus.DELETE_FAILED.toString ||
              s == StackStatus.ROLLBACK_COMPLETE.toString
            })
        }
      }
      if (!completed) Thread.sleep(10000)
    }
  }
}
