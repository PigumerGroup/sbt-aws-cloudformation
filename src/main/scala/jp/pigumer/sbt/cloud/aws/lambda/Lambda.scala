package jp.pigumer.sbt.cloud.aws.lambda

import cloudformation.AwscfSettings
import com.amazonaws.services.lambda.AWSLambdaClientBuilder

trait Lambda {

  lazy val lambda = (settings: AwscfSettings) ⇒
    AWSLambdaClientBuilder.
    standard.
    withCredentials(settings.credentialsProvider).
    withRegion(settings.region).
    build

}
