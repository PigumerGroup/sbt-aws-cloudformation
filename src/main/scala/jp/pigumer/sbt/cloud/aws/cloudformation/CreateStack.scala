package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model._
import sbt._

import scala.util.Try

trait CreateStack {

  protected def url(bucketName: String, dir: String, fileName: String): String

  def describeStacks(client: AmazonCloudFormation,
                     request: DescribeStacksRequest): Stream[Stack]

  private [cloudformation]
  def create(client: AmazonCloudFormation,
                     settings: AwscfSettings,
                     stack: CloudformationStack,
                     log: Logger) = Try {
    import scala.collection.JavaConverters._

    val bucketName = settings.bucketName.get
    val baseDir = settings.baseDir.get
    val u = url(bucketName, baseDir, stack.template.value)
    val params: Seq[Parameter] = stack.params.values.map {
      case (key, value) ⇒ {
        val p: Parameter = new Parameter().withParameterKey(key).withParameterValue(value)
        p
      }
    }.toSeq

    val request = new CreateStackRequest().
      withTemplateURL(u).
      withStackName(stack.stackName.value).
      withCapabilities(stack.capabilities.values.map(_.value).asJava).
      withParameters(params.asJava).
      withNotificationARNs(stack.notifications.values.map(_.value).asJava)

    log.info(stack.stackName.value)
    client.createStack(settings.roleARN.map(r ⇒ request.withRoleARN(r)).getOrElse(request))

    val describeStackRequest = new DescribeStacksRequest().withStackName(stack.stackName.value)
    new CloudFormationWaiter(client,
      client.waiters.stackCreateComplete).wait(describeStackRequest)
    describeStacks(client, describeStackRequest)
  }

}
