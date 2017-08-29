package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.waiters.{Waiter, WaiterParameters}

class CloudFormationWaiter(cloudformation: AmazonCloudFormation,
                           waiter: Waiter[DescribeStacksRequest]) {

  def wait(request: DescribeStacksRequest) =
    waiter.run(new WaiterParameters[DescribeStacksRequest](request))

}
