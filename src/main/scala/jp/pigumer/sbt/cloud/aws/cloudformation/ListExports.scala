package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfExport, AwscfSettings}
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{Export, ListExportsRequest, StackSummary}
import sbt.Def

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait ListExports {

  import cloudformation.CloudformationPlugin.autoImport._

  private def listExports(client: AmazonCloudFormation, settings: AwscfSettings): Try[Seq[AwscfExport]] = Try {
    val request = new ListExportsRequest()
    ListExports.listExports(client)
  }

  def listExportsTask = Def.task {
    val settings = awscfSettings.value
    val client = awscf.value
    listExports(client, settings) match {
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
                      exportList: mutable.MutableList[AwscfExport]): Unit = {
    val result = client.listExports(request)
    val list: Seq[Export] = result.getExports.asScala
    list.foreach(r ⇒
      exportList += AwscfExport(exportingStackId = r.getExportingStackId,
        stackName = stacks.get(r.getExportingStackId).
          map(_.getStackName).
          getOrElse(throw new RuntimeException(s"${r.getExportingStackId} is unknown stack")),
        name = r.getName,
        value = r.getValue)
    )
    Option(result.getNextToken) match {
      case Some(n) ⇒ {
        request.withNextToken(n)
        exports(client, request, stacks, exportList)
      }
      case None ⇒ None
    }
  }

  def listExports(client: AmazonCloudFormation): Seq[AwscfExport] = {
    val request = new ListExportsRequest()

    val result = mutable.MutableList[AwscfExport]()
    val stacks = ListStacks.listStacks(client).map(s ⇒ (s.getStackId, s)).toMap
    exports(client, request, stacks, result)
    Seq(result: _*)
  }

}