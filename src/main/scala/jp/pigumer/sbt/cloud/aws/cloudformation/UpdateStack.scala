package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfSettings, AwscfTTLSettings, CloudformationStack}
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{Parameter, Stack, StackStatus, UpdateStackRequest}
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait UpdateStack {

  import cloudformation.CloudformationPlugin.autoImport._

  protected val cloudFormation: AwscfSettings ⇒ AmazonCloudFormation

  protected def updateTimeToLive(settings: AwscfSettings, ttl: AwscfTTLSettings): Unit

  protected def url(bucketName: String, dir: String, fileName: String): String

  protected def waitForCompletion(client: AmazonCloudFormation,
                                  stackName: String,
                                  log: Logger): Try[Seq[Stack]]

  private def update(settings: AwscfSettings,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    import scala.collection.JavaConverters._

    val u = url(settings.bucketName, settings.baseDir, stack.template)
    val params: Seq[Parameter] = stack.parameters.map {
      case (key, value) ⇒
        val p: Parameter = new Parameter().withParameterKey(key).withParameterValue(value)
        p
    }.toSeq

    val request = new UpdateStackRequest().
      withTemplateURL(u).
      withStackName(stack.stackName).
      withCapabilities(stack.capabilities.asJava).
      withParameters(params.asJava)

    log.info(s"Update ${stack.stackName}")

    val client = cloudFormation(settings)
    client.updateStack(settings.roleARN.map(r ⇒ request.withRoleARN(r)).getOrElse(request))
    waitForCompletion(client, stack.stackName, log) match {
      case Failure(t) ⇒ throw t
      case Success(r) ⇒ {
        r.foreach(stack ⇒ log.info(s"${stack.getStackName} ${stack.getStackStatus}"))
        if (!r.forall(_.getStackStatus == StackStatus.UPDATE_COMPLETE.toString)) {
          throw UpdateStackException()
        }
      }
    }
  }

  def updateStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    spaceDelimited("<shortName>").parsed match {
      case Seq(shortName) ⇒
        (for {
          stack ← Try(awscfStacks.value.getOrElse(shortName, sys.error(s"$shortName of the stack is not defined")))
          _ ← update(settings, stack, log)
          _ ← Try(stack.ttl.foreach(t ⇒ updateTimeToLive(settings, t)))
        } yield ()) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒
            log.trace(t)
            sys.error(t.getMessage)
        }
      case _ ⇒ sys.error("Usage: <shortName>")
    }
  }
}
