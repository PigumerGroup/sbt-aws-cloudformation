package jp.pigumer.sbt.cloud.aws.dynamodb

import cloudformation.AwscfSettings
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDBClient, AmazonDynamoDBClientBuilder}

trait DynamoDBProvider {

  import cloudformation.CloudformationPlugin.autoImport._

  protected lazy val amazonDynamoDB: AwscfSettings ⇒ AmazonDynamoDBClient = settings ⇒ {
    val builder = AmazonDynamoDBClientBuilder.
      standard().
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region)
    builder.build().asInstanceOf[AmazonDynamoDBClient]
  }

}
