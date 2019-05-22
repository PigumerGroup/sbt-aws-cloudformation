package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model._
import sbt.Logger

import scala.util.Try

trait UpdateStack {

  protected def url(bucketName: String, dir: String, fileName: String): String

  def describeStacks(client: AmazonCloudFormation,
                     request: DescribeStacksRequest): Stream[Stack]

  private [cloudformation]
  def update(client: AmazonCloudFormation,
                     settings: AwscfSettings,
                     stack: CloudformationStack,
                     log: Logger): Try[Stream[Stack]] = Try {
    import scala.collection.JavaConverters._

    val bucketName = settings.bucketName.get
    val baseDir = settings.baseDir.get
    val u = url(bucketName, baseDir, stack.template.value)
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

    try {
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
    } catch {
      case e: AmazonCloudFormationException if e.getMessage.contains("No updates are to be performed.") ⇒
        Stream.empty
    }
  }

}
