package jp.pigumer.sbt.cloud.aws.lambda

import jp.pigumer.sbt.cloud.aws.cloudformation.AwscfSettings
import com.amazonaws.services.lambda.AWSLambdaClientBuilder

trait Lambda {

  lazy val lambda = (settings: AwscfSettings) ⇒
    AWSLambdaClientBuilder.
    standard.
    withCredentials(settings.credentialsProvider).
    withRegion(settings.region).
    build

}
