package cloudformation

import com.amazonaws.services.lambda.AWSLambda
import sbt._

trait LambdaKeys {

  lazy val awslambda = taskKey[AWSLambda]("AWS Lambda tasks")
}
