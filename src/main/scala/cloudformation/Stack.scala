package cloudformation

class CloudformationStack(val stackName: StackName,
                          val template: Template,
                          val notificationARNs: () ⇒ NotificationARNs = () ⇒ NotificationARNs.empty,
                          val capabilities: Capabilities = Capabilities.empty,
                          val parameters: () ⇒ Parameters = () ⇒ Parameters.empty,
                          val ttl: TTLSettings = TTLSettings.empty)

object CloudformationStack {

  def apply(stackName: String,
            template: String,
            notificationARNs: () ⇒ Seq[String] = () ⇒ Seq.empty,
            capabilities: Seq[String] = Seq.empty,
            parameters: () ⇒ Map[String, String] = () ⇒ Map.empty,
            ttl: Seq[TTLSetting] = Seq.empty): CloudformationStack =
    new CloudformationStack(stackName = StackName(stackName),
      template = Template(template),
      notificationARNs = { () ⇒
        new NotificationARNs(notificationARNs().map(NotificationARN))
      },
      capabilities = new Capabilities(capabilities.map(Capability)),
      parameters = () ⇒ new Parameters(parameters()),
      ttl = TTLSettings(ttl)
    )
}

case class Stacks(values: Map[String, CloudformationStack])

object Stacks {

  val empty: Stacks = Stacks(Map.empty[String, CloudformationStack])

  def apply(elems: (String, CloudformationStack)*): Stacks = new Stacks(elems.toMap)
}