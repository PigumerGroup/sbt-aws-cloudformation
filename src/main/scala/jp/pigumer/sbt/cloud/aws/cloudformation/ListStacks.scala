package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{ListStacksRequest, StackStatus, StackSummary}
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait ListStacks {

  import cloudformation.CloudformationPlugin.autoImport._

  protected val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  private def output(stack: StackSummary, log: Logger): Unit = {
    val s = s"${stack.getStackName} ${stack.getStackStatus}"
    log.info(s)
  }

  private def listStacks(settings: AwscfSettings,
                         log: Logger) = Try {
    val r = ListStacks.listStacks(amazonCloudFormation(settings))
    r.foreach(output(_, log))
    r
  }

  def listStacksTask = Def.task {
    val log = streams.value.log
    val settings = awscfSettings.value
    listStacks(settings, log) match {
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
  private def stacks(client: AmazonCloudFormationClient,
                     request: ListStacksRequest,
                     stackList: mutable.MutableList[StackSummary]): Unit = {
    val result = client.listStacks(request)
    val list = result.getStackSummaries.asScala.toSeq
    list.filterNot(r ⇒
      r.getStackStatus == StackStatus.DELETE_COMPLETE.toString
    ).foreach(r ⇒ stackList += r)
    if (null == result.getNextToken)
      return ()
    val r = new ListStacksRequest().withNextToken(result.getNextToken)
    stacks(client, r, stackList)
  }

  def listStacks(client: AmazonCloudFormationClient): Seq[StackSummary] = {
    val result = mutable.MutableList[StackSummary]()
    val request = new ListStacksRequest()
    stacks(client, request, result)
    Seq(result: _*)
  }

}