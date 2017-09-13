package jp.pigumer.sbt.cloud.aws.apigateway

import com.amazonaws.services.apigateway.AmazonApiGateway
import sbt._

trait ApiGatewayKeys {

  lazy val awsapigateway = taskKey[AmazonApiGateway]("AWS ApiGateway tasks")
}
