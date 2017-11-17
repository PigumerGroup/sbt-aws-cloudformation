package jp.pigumer.sbt.cloud.aws.cloudformation

abstract class CloudformationStack(val stackName: StackName,
                          val template: Template,
                          val capabilities: Capabilities = Capabilities.empty) {
  def notifications: NotificationARNs
  def params: Parameters
}

object CloudformationStack {

  def apply(stackName: String,
            template: String,
            notificationARNs: Seq[String] = Seq.empty,
            capabilities: Seq[String] = Seq.empty,
            parameters: Map[String, String] = Map.empty): CloudformationStack =
    new CloudformationStack(stackName = StackName(stackName),
      template = Template(template),
      capabilities = new Capabilities(capabilities.map(Capability))
    ) {
      override def notifications: NotificationARNs = NotificationARNs(notificationARNs)
      override def params: Parameters = Parameters(parameters)
    }
}

case class Stacks(values: Map[String, () ⇒ CloudformationStack])

object Stacks {

  val empty: Stacks = new Stacks(Map.empty)

  def apply(elems: (String, () ⇒ CloudformationStack)*): Stacks = new Stacks(elems.toMap)
}