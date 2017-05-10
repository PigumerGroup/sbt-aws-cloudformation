package cloudformation

case class CloudFormationStack(stackName: String,
                               template: String,
                               capabilities: Seq[String] = Seq.empty,
                               parameters: Map[String, String] = Map.empty)
