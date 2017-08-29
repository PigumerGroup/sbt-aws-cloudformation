package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfExport, AwscfSettings}
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{ListExportsRequest, StackSummary}
import sbt.Def

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait ListExports {

  import cloudformation.CloudformationPlugin.autoImport._

  private def listExports(client: AmazonCloudFormation,
                          settings: AwscfSettings,
                          stacks: Stream[StackSummary]): Try[Stream[AwscfExport]] = Try {
    ListExports.listExports(client, stacks)
  }

  def listExportsTask = Def.task {
    val settings = awscfSettings.value
    val client = awscf.value
    val stacks = awscfListStacks.value
    listExports(client, settings, stacks) match {
      case Success(r) ⇒ r
      case Failure(t) ⇒ {
        sys.error(t.toString)
      }
    }
  }
}

object ListExports {

  import scala.collection.JavaConverters._

  @tailrec
  private def exports(client: AmazonCloudFormation,
                      request: ListExportsRequest,
                      stacks: Map[String, StackSummary],
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
        exports(client, request, stacks, list)
      }
      case None ⇒ list
    }
  }

  def listExports(client: AmazonCloudFormation, stacks: Stream[StackSummary]): Stream[AwscfExport] = {
    val request = new ListExportsRequest()

    val map = stacks.map(s ⇒ (s.getStackId, s)).toMap
    exports(client, request, map, Stream.empty)
  }

}
