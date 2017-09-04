package jp.pigumer.sbt.cloud.aws.cloudformation

import cloudformation.AwscfSettings
import com.amazonaws.services.cloudformation.model.{DescribeStacksRequest, Stack}
import com.amazonaws.services.cloudformation.{AmazonCloudFormation, AmazonCloudFormationClientBuilder}

import scala.annotation.tailrec

trait CloudFormationProvider {

  lazy val cloudFormation = (settings: AwscfSettings) ⇒
    AmazonCloudFormationClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build

  @tailrec
  private def describeStacks(client: AmazonCloudFormation,
                             request: DescribeStacksRequest,
                             list: Stream[Stack]): Stream[Stack] = {
    import scala.collection.JavaConverters._

    val result = client.describeStacks(request)
    val l = list ++ result.getStacks.asScala
    Option(result.getNextToken) match {
      case Some(n) ⇒ {
        request.withNextToken(n)
        describeStacks(client, request, l)
      }
      case None ⇒ l
    }
  }

  def describeStacks(client: AmazonCloudFormation,
                     request: DescribeStacksRequest): Stream[Stack] =
    describeStacks(client, request, Stream.empty)

}

case class CreateStackException(message: String = "Can't CREATE_STACK") extends RuntimeException
case class UpdateStackException(message: String = "Can't UPDATE_STACK") extends RuntimeException
