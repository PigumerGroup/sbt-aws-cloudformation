package cloudformation

case class CloudformationStack(stackName: String,
                               template: String,
                               notificationARNs: Seq[String] = Seq.empty,
                               capabilities: Seq[String] = Seq.empty,
                               parameters: Map[String, String] = Map.empty,
                               ttl: Seq[AwscfTTLSettings] = Seq.empty)
