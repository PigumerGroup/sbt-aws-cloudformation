package jp.pigumer.sbt.cloud.aws.dynamodb

import cloudformation.{AwscfSettings, AwscfTTLSettings}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.UpdateTimeToLiveRequest

import scala.util.{Failure, Success, Try}

trait UpdateTimeToLive {

  protected val amazonDynamoDB: AwscfSettings ⇒ AmazonDynamoDBClient

  def updateTimeToLive(settings: AwscfSettings, ttl: AwscfTTLSettings): Unit = {
    val client = amazonDynamoDB(settings)
    val request = new UpdateTimeToLiveRequest().
      withTableName(ttl.tableName).
      withTimeToLiveSpecification(ttl.timeToLiveSpecification)
    Try(client.updateTimeToLive(request)) match {
      case Success(_) ⇒ ()
      case Failure(t) ⇒ throw t
    }
  }

}
