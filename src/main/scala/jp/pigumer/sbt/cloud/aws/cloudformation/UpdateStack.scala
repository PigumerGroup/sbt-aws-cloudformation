package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.{AwscfSettings, CloudformationStack, TTLSetting}
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import sbt.Def.spaceDelimited
import sbt.Keys.streams
import sbt.{Def, Logger}

import scala.util.{Failure, Success, Try}

trait UpdateStack {

  import cloudformation.CloudformationPlugin.autoImport._

  def updateTimeToLive(client: AmazonDynamoDB, settings: AwscfSettings, ttl: TTLSetting): Unit
  
  protected def url(bucketName: String, dir: String, fileName: String): String

  def describeStacks(client: AmazonCloudFormation,
                     request: DescribeStacksRequest): Stream[Stack]

  private def update(client: AmazonCloudFormation,
                     settings: AwscfSettings,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    import scala.collection.JavaConverters._

    val u = url(settings.bucketName, settings.baseDir, stack.template.value)
    val params = stack.params.values.map {
      case (key, value) ⇒
        val p: Parameter = new Parameter().withParameterKey(key).withParameterValue(value)
        p
    }.toSeq

    val request = new UpdateStackRequest().
      withTemplateURL(u).
      withStackName(stack.stackName.value).
      withCapabilities(stack.capabilities.values.map(_.value).asJava).
      withParameters(params.asJava).
      withNotificationARNs(stack.notifications.values.map(_.value).asJava)

    log.info(stack.stackName.value)
    client.updateStack(settings.roleARN.map(r ⇒ request.withRoleARN(r)).getOrElse(request))

    val describeStacksRequest = new DescribeStacksRequest().withStackName(stack.stackName.value)
    new CloudFormationWaiter(client, client.waiters.stackUpdateComplete).wait(describeStacksRequest)
    val stacks = describeStacks(client, describeStacksRequest)

    stacks.foreach(s ⇒ log.info(s"${s.getStackName} ${s.getStackStatus}"))

    if (!stacks.forall(_.getStackStatus == StackStatus.UPDATE_COMPLETE.toString)) {
      throw UpdateStackException()
    } else {
      stacks
    }
  }

  def updateStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    val client = awscf.value
    val dynamoDB = awsdynamodb.value
    spaceDelimited("<shortName>").parsed match {
      case Seq(shortName) ⇒
        (for {
          stack ← Try(awscfStacks.value.values.getOrElse(shortName, sys.error(s"$shortName of the stack is not defined")))
          _ ← update(client, settings, stack, log)
          _ ← Try(stack.ttl.values.foreach(t ⇒ updateTimeToLive(dynamoDB, settings, t)))
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
