package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{ListExportsRequest, StackSummary}
import sbt.Def

import scala.annotation.tailrec

object ListExports {

  import scala.collection.JavaConverters._

  @tailrec
  private def retry(count: Int, maybeInterval: Option[Int])(exports: ⇒ Stream[AwscfExport]): Stream[AwscfExport] =
    try {
      exports
    } catch {
      case e: Throwable ⇒
        if (count == 0) {
          throw e
        } else {
          maybeInterval.foreach(Thread.sleep(_))
          retry(count - 1, maybeInterval)(exports)
        }
    }

  @tailrec
  private def exports(client: AmazonCloudFormation,
                      request: ListExportsRequest,
                      stacks: Map[String, StackSummary],
                      maybeInterval: Option[Int],
                      exportList: Stream[AwscfExport]): Stream[AwscfExport] = {
    val result = client.listExports(request)
    val list = exportList ++ result.getExports.asScala.map { e ⇒
      AwscfExport(exportingStackId = e.getExportingStackId,
        stackName = stacks(e.getExportingStackId).getStackName,
        name = e.getName,
        value = e.getValue)
    }.toStream

    Option(result.getNextToken) match {
      case Some(n) ⇒ {
        request.withNextToken(n)
        maybeInterval.foreach(Thread.sleep(_))
        exports(client, request, stacks, maybeInterval, list)
      }
      case None ⇒ list
    }
  }

  def listExports(client: AmazonCloudFormation,
                  retryCount: Int,
                  maybeRetryInterval: Option[Int],
                  maybeInterval: Option[Int], stacks: Stream[StackSummary]): Stream[AwscfExport] = {
    val request = new ListExportsRequest()

    val map = stacks.map(s ⇒ (s.getStackId, s)).toMap
    retry(retryCount, maybeRetryInterval) {
      exports(client, request, map, maybeInterval, Stream.empty)
    }
  }

}

trait ListExports {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  def listExportsTask = Def.task {
    val client = awscf.value
    val retryCount = awscfRetryCount.?.value.getOrElse(0)
    val maybeRetryInterval = awscfRetryInterval.?.value
    val maybeInterval = awscfInterval.?.value
    ListExports.listExports(client,
      retryCount,
      maybeRetryInterval,
      maybeInterval,
      ListStacks.listStacks(client, retryCount, maybeRetryInterval, maybeInterval))
  }
}

