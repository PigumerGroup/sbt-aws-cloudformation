package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{ListExportsRequest, StackSummary}
import sbt.Def

import scala.annotation.tailrec

object ListExports {

  import scala.collection.JavaConverters._

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

  def listExports(client: AmazonCloudFormation, maybeInterval: Option[Int], stacks: Stream[StackSummary]): Stream[AwscfExport] = {
    val request = new ListExportsRequest()

    val map = stacks.map(s ⇒ (s.getStackId, s)).toMap
    exports(client, request, map, maybeInterval, Stream.empty)
  }

}

trait ListExports {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  def listExportsTask = Def.task {
    val client = awscf.value
    val maybeInterval = awscfInterval.?.value
    ListExports.listExports(client, maybeInterval, ListStacks.listStacks(client, maybeInterval))
  }
}

