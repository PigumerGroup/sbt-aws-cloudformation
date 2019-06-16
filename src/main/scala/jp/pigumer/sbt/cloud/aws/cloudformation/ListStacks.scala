package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{ListStacksRequest, StackStatus, StackSummary}
import sbt.Def

import scala.annotation.tailrec

object ListStacks {

  import scala.collection.JavaConverters._

  @tailrec
  private def retry(count: Int, maybeInterval: Option[Int])(stacks: ⇒ Stream[StackSummary]): Stream[StackSummary] =
    try {
      stacks
    } catch {
      case e: Throwable ⇒
        if (count == 0) {
          throw e
        } else {
          maybeInterval.foreach(Thread.sleep(_))
          retry(count - 1, maybeInterval)(stacks)
        }
    }

  @tailrec
  private def stacks(client: AmazonCloudFormation,
                     request: ListStacksRequest,
                     maybeInterval: Option[Int],
                     stackList: Stream[StackSummary]): Stream[StackSummary] = {
    val result = client.listStacks(request)
    val list = stackList ++ result.getStackSummaries.asScala.filterNot { r ⇒
      r.getStackStatus == StackStatus.DELETE_COMPLETE.toString
    }
    Option(result.getNextToken) match {
      case None ⇒ list
      case Some(n) ⇒ {
        request.withNextToken(n)
        maybeInterval.foreach(Thread.sleep(_))
        stacks(client, request, maybeInterval, list)
      }
    }
  }

  def listStacks(client: AmazonCloudFormation,
                 retryCount: Int,
                 maybeRetryInterval: Option[Int],
                 maybeInterval: Option[Int]): Stream[StackSummary] = {
    val request = new ListStacksRequest()
    retry(retryCount, maybeRetryInterval) {
      stacks(client, request, maybeInterval, Stream.empty)
    }
  }

}

trait ListStacks {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  def listStacksTask = Def.task {
    val client = awscf.value
    val retryCount = awscfRetryCount.?.value.getOrElse(0)
    val maybeRetryInterval = awscfRetryInterval.?.value
    val maybeInterval = awscfInterval.?.value
    ListStacks.listStacks(client, retryCount, maybeRetryInterval, maybeInterval)
  }
}

