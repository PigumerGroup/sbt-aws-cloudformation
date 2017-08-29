package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.{DescribeStacksRequest, Stack}
import com.amazonaws.waiters.{Waiter, WaiterParameters}

import scala.annotation.tailrec

class CloudFormationWaiter(cloudformation: AmazonCloudFormation,
                           waiter: Waiter[DescribeStacksRequest]) {
  import scala.collection.JavaConverters._

  def wait(request: DescribeStacksRequest) =
    waiter.run(new WaiterParameters[DescribeStacksRequest](request))


}
