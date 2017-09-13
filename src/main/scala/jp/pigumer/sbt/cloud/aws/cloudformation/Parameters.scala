package jp.pigumer.sbt.cloud.aws.cloudformation

abstract class Parameters {
  def values: Map[String, String]
}

object Parameters {

  def apply(elems: Map[String, String]): Parameters =
    new Parameters {
      override def values: Map[String, String] = elems
    }
}