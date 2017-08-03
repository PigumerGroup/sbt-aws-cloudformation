package jp.pigumer.sbt.cloud.aws.dynamodb

import cloudformation.AwscfSettings
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}

trait DynamoDBProvider {

  protected lazy val amazonDynamoDB: AwscfSettings ⇒ AmazonDynamoDB = { settings ⇒
    AmazonDynamoDBClientBuilder.
      standard().
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build
  }

}
