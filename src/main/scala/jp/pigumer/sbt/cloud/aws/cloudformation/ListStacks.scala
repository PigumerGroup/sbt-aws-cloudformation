package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{ListStacksRequest, StackStatus, StackSummary}
import sbt.Def

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait ListStacks {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  private def listStacks(client: AmazonCloudFormation, settings: AwscfSettings): Try[Stream[StackSummary]] = Try {
    ListStacks.listStacks(client)
  }

  def listStacksTask = Def.task {
    val settings = awscfSettings.value
    val client = awscf.value
    listStacks(client, settings) match {
      case Success(r) ⇒ r
      case Failure(t) ⇒ {
        sys.error(t.toString)
      }
    }
  }
}

object ListStacks {

  import scala.collection.JavaConverters._

  @tailrec
  private def stacks(client: AmazonCloudFormation,
                     request: ListStacksRequest,
                     stackList: Stream[StackSummary]): Stream[StackSummary] = {
    val result = client.listStacks(request)
    val list = stackList ++ result.getStackSummaries.asScala.filterNot { r ⇒
      r.getStackStatus == StackStatus.DELETE_COMPLETE.toString
    }
    Option(result.getNextToken) match {
      case None ⇒ list
      case Some(n) ⇒ {
        request.withNextToken(n)
        stacks(client, request, list)
      }
    }
  }

  def listStacks(client: AmazonCloudFormation): Stream[StackSummary] = {
    val request = new ListStacksRequest()
    stacks(client, request, Stream.empty)
  }

}