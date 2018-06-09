package jp.pigumer.sbt.cloud.aws.cloudformation

import java.nio.charset.StandardCharsets

import com.amazonaws.services.cloudformation.model._
import com.amazonaws.services.cloudformation.{AmazonCloudFormation, AmazonCloudFormationClientBuilder}

object CloudformationSpec extends App {

  val template = {
    val is = Thread.currentThread.getContextClassLoader.getResourceAsStream("lambda.yaml")
    try {
      Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
    } finally {
      is.close()
    }
  }

  def createStack(request: CreateStackRequest)(implicit client: AmazonCloudFormation) = {
    client.createStack(request)
    val describeStackRequest = new DescribeStacksRequest().withStackName(request.getStackName)
    new CloudFormationWaiter(client,
      client.waiters().stackCreateComplete)
      .wait(describeStackRequest)

    client.describeStacks(describeStackRequest)
  }

  def updateStack(request: UpdateStackRequest)(implicit client: AmazonCloudFormation) = {
    client.updateStack(request)
    val describeStackRequest = new DescribeStacksRequest().withStackName(request.getStackName)
    new CloudFormationWaiter(client,
      client.waiters().stackCreateComplete)
      .wait(describeStackRequest)

    client.describeStacks(describeStackRequest)
  }

  val stackName = "test-lambda"

  implicit val client: AmazonCloudFormation = AmazonCloudFormationClientBuilder.standard.build
  val updateStackRequest = new UpdateStackRequest()
    .withTemplateBody(new String(template, StandardCharsets.UTF_8))
    .withStackName("test-lambda")
    .withCapabilities("CAPABILITY_IAM")
  try {
    updateStack(updateStackRequest)
  } catch {
    case e: AmazonCloudFormationException if e.getMessage.contains("No updates are to be performed.") ⇒
      e.printStackTrace(System.out)
  }
  sys.exit()
}
