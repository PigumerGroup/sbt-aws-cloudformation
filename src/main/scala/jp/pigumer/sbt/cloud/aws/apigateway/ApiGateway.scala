package jp.pigumer.sbt.cloud.aws.apigateway

import cloudformation.AwscfSettings
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder

trait ApiGateway {

  lazy val apigateway = (settings: AwscfSettings) ⇒
    AmazonApiGatewayClientBuilder.
    standard.
    withCredentials(settings.credentialsProvider).
    withRegion(settings.region).
    build

}
