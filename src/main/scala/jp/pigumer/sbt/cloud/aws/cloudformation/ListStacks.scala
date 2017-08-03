package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{ListStacksRequest, StackStatus, StackSummary}
import sbt.Def
import sbt.Keys.streams

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait ListStacks {

  import cloudformation.CloudformationPlugin.autoImport._

  protected val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormation

  private def listStacks(settings: AwscfSettings): Try[Seq[StackSummary]] = Try {
    ListStacks.listStacks(amazonCloudFormation(settings))
  }

  def listStacksTask = Def.task {
    val log = streams.value.log
    val settings = awscfSettings.value
    listStacks(settings) match {
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
                     stackList: mutable.MutableList[StackSummary]): Unit = {
    val result = client.listStacks(request)
    val list = result.getStackSummaries.asScala
    list.filterNot(r ⇒
      r.getStackStatus == StackStatus.DELETE_COMPLETE.toString
    ).foreach(r ⇒ stackList += r)
    Option(result.getNextToken) match {
      case None ⇒ ()
      case Some(n) ⇒ {
        request.withNextToken(n)
        stacks(client, request, stackList)
      }
    }
  }

  def listStacks(client: AmazonCloudFormation): Seq[StackSummary] = {
    val result = mutable.MutableList[StackSummary]()
    val request = new ListStacksRequest()
    stacks(client, request, result)
    Seq(result: _*)
  }

}