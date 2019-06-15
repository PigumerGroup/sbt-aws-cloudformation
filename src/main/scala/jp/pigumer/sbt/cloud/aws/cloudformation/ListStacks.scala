package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{ListStacksRequest, StackStatus, StackSummary}
import sbt.Def

import scala.annotation.tailrec

object ListStacks {

  import scala.collection.JavaConverters._

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

  def listStacks(client: AmazonCloudFormation, maybeInterval: Option[Int]): Stream[StackSummary] = {
    val request = new ListStacksRequest()
    stacks(client, request, maybeInterval, Stream.empty)
  }

}

trait ListStacks {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  def listStacksTask = Def.task {
    val settings = awscfSettings.value
    val client = awscf.value
    val maybeInterval = awscfInterval.?.value
    ListStacks.listStacks(client, maybeInterval)
  }
}

