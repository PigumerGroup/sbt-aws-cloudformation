package jp.pigumer.sbt.cloud.aws.dynamodb

import cloudformation.{AwscfSettings, AwscfTTLSettings}
import com.amazonaws.services.dynamodbv2.model.UpdateTimeToLiveRequest
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}

import scala.util.{Failure, Success, Try}

trait DynamoDBProvider {

  lazy val dynamoDB = (settings: AwscfSettings) ⇒
    AmazonDynamoDBClientBuilder.
      standard().
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build

  def updateTimeToLive(client: AmazonDynamoDB, settings: AwscfSettings, ttl: AwscfTTLSettings): Unit = {
    val request = new UpdateTimeToLiveRequest().
      withTableName(ttl.tableName).
      withTimeToLiveSpecification(ttl.timeToLiveSpecification)
    Try(client.updateTimeToLive(request)) match {
      case Success(_) ⇒ ()
      case Failure(t) ⇒ throw t
    }
  }

}
