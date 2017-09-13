package jp.pigumer.sbt.cloud.aws.cloudformation

case class Capability(value: String)

class Capabilities(val values: Seq[Capability])

object Capabilities {

  val empty: Capabilities = new Capabilities(Seq.empty[Capability])

  def apply(values: Seq[String]): Capabilities =
    new Capabilities(values.map(Capability))
}