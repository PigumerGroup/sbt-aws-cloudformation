package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfExport, AwscfSettings}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{Export, ListExportsRequest, StackSummary}
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait ListExports {

  import cloudformation.CloudformationPlugin.autoImport._

  protected val amazonCloudFormation: AwscfSettings ⇒ AmazonCloudFormationClient

  protected def url(awscfSettings: AwscfSettings, stage: String, template: String): String

  private def output(export: AwscfExport, log: Logger): Unit = {
    val s = s"${export.stackName} ${export.name} ${export.value}"
    log.info(s)
  }

  private def listExports(settings: AwscfSettings,
                          log: Logger) = Try {
    val request = new ListExportsRequest()
    val client = amazonCloudFormation(settings)
    val list = ListExports.listExports(client)
    list.foreach(output(_, log))
    list
  }

  def listExportsTask = Def.task {
    val log = streams.value.log
    val settings = awscfSettings.value
    listExports(settings, log) match {
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
  private def exports(client: AmazonCloudFormationClient,
                      request: ListExportsRequest,
                      stacks: Map[String, StackSummary],
                      exportList: mutable.MutableList[AwscfExport]): Unit = {
    val result = client.listExports(request)
    val list: Seq[Export] = result.getExports.asScala.toSeq
    list.foreach(r ⇒
      exportList += AwscfExport(exportingStackId = r.getExportingStackId,
        stackName = stacks.get(r.getExportingStackId).map(_.getStackName).getOrElse(throw new RuntimeException(s"${r.getExportingStackId} is unknown stack")),
        name = r.getName,
        value = r.getValue)
    )
    if (result.getNextToken == null)
      return ()
    val r = new ListExportsRequest().withNextToken(result.getNextToken)
    exports(client, r, stacks, exportList)
  }

  def listExports(client: AmazonCloudFormationClient): Seq[AwscfExport] = {
    val request = new ListExportsRequest()

    val result = mutable.MutableList[AwscfExport]()
    val stacks = ListStacks.listStacks(client).map(s ⇒ (s.getStackId, s)).toMap
    exports(client, request, stacks, result)
    Seq(result: _*)
  }

}