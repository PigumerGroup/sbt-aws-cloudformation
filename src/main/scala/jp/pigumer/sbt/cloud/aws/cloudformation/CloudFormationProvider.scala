package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.model.{DescribeStacksRequest, Stack, StackStatus}
import com.amazonaws.services.cloudformation.{AmazonCloudFormation, AmazonCloudFormationClientBuilder}
import sbt.Logger

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.util.Try

trait CloudFormationProvider {

  lazy val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormation = { settings ⇒
    AmazonCloudFormationClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build
  }

  protected def key(dir: String, fileName: String): String =
    if (dir.isEmpty) {
      fileName
    } else {
      s"$dir/$fileName"
    }

  protected def url(bucketName: String, key: String): String =
    s"https://$bucketName.s3.amazonaws.com/$key"

  protected def url(bucketName: String, dir: String, fileName: String): String =
    url(bucketName, key(dir, fileName))

  @tailrec
  private def describeStacks(client: AmazonCloudFormation,
                     request: DescribeStacksRequest,
                     list: Stream[Stack]): Stream[Stack] = {
    val result = client.describeStacks(request)
    val l = list ++ result.getStacks.asScala
    Option(result.getNextToken) match {
      case Some(n) ⇒ {
        request.withNextToken(n)
        describeStacks(client, request, l)
      }
      case None ⇒ l
    }
  }

  @tailrec
  private def allCompletion(client: AmazonCloudFormation,
                            request: DescribeStacksRequest,
                            stacks: Stream[Stack],
                            log: Logger): Stream[Stack] = {
    val stackStatus = stacks.map(_.getStackStatus)
    val completed = stackStatus.forall(
      s ⇒ {
        s == StackStatus.CREATE_COMPLETE.toString ||
        s == StackStatus.CREATE_FAILED.toString ||
        s == StackStatus.ROLLBACK_FAILED.toString ||
        s == StackStatus.UPDATE_COMPLETE.toString ||
        s == StackStatus.DELETE_FAILED.toString ||
        s == StackStatus.ROLLBACK_COMPLETE.toString ||
        s == StackStatus.UPDATE_ROLLBACK_FAILED.toString
      }
    )
    if (completed) {
      stacks.foreach(s ⇒ log.debug(s"${s.getStackName} ${s.getStackStatus} ${s.getStackStatusReason}"))
      stacks
    } else {
      Thread.sleep(10000)

      val s = describeStacks(client, request, Stream.empty)
      allCompletion(client, request, s, log)
    }
  }

  protected def waitForCompletion(client: AmazonCloudFormation,
                                  stackName: String,
                                  log: Logger): Try[Seq[Stack]] = {
    val request = new DescribeStacksRequest().
      withStackName(stackName)
    Try(describeStacks(client, request, Stream.empty)) map {
      l ⇒ allCompletion(client, request, l, log)
    }
  }

}

case class CreateStackException(message: String = "Can't CREATE_STACK") extends RuntimeException
case class UpdateStackException(message: String = "Can't UPDATE_STACK") extends RuntimeException
