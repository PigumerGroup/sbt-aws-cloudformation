package jp.pigumer.sbt.cloud.aws.cloudformation

import java.io.File

import cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.model.{DescribeStacksRequest, Stack, StackStatus}
import com.amazonaws.services.cloudformation.{AmazonCloudFormationClient, AmazonCloudFormationClientBuilder}
import sbt.Logger

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

trait CloudFormationProvider {

  lazy val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient = settings ⇒ {
    val builder = AmazonCloudFormationClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region)
    builder.build.asInstanceOf[AmazonCloudFormationClient]
  }

  protected def url(bucketName: String, stage: String, templates: File, template: String): String =
    s"https://${bucketName}.s3.amazonaws.com/${stage}/${templates.getName}/${template}"

  @tailrec
  private def describeStacks(client: AmazonCloudFormationClient,
                     request: DescribeStacksRequest,
                     list: mutable.MutableList[Stack]): Unit = {
    val result = client.describeStacks(request)
    list ++= result.getStacks.asScala
    if (result.getNextToken == null) {
      return ()
    }
    describeStacks(client, request, list)
  }

  @tailrec
  private def allCompletion(client: AmazonCloudFormationClient,
                            request: DescribeStacksRequest,
                            stacks: Seq[Stack],
                            log: Logger): Seq[Stack] = {
    val completed = stacks.map(_.getStackStatus).forall(
      s ⇒ {
        s == StackStatus.CREATE_COMPLETE.toString ||
        s == StackStatus.CREATE_FAILED.toString ||
        s == StackStatus.ROLLBACK_FAILED.toString ||
        s == StackStatus.UPDATE_COMPLETE.toString ||
        s == StackStatus.DELETE_FAILED.toString ||
        s == StackStatus.ROLLBACK_COMPLETE.toString
      }
    )
    if (completed) {
      stacks.foreach(s ⇒ log.debug(s"${s.getStackName} ${s.getStackStatus} ${s.getStackStatusReason}"))
      return stacks
    }

    Thread.sleep(10000)

    val list = mutable.MutableList[Stack]()
    describeStacks(client, request, list)
    allCompletion(client, request, list, log)
  }

  protected def waitForCompletion(client: AmazonCloudFormationClient,
                                  stackName: String,
                                  log: Logger): Try[Seq[Stack]] = Try {
    val request = new DescribeStacksRequest().
      withStackName(stackName)
    val list = mutable.MutableList[Stack]()
    Try(describeStacks(client, request, list)).map(
      _ ⇒ allCompletion(client, request, list, log)
    ).getOrElse(Seq.empty[Stack])
  }

}
