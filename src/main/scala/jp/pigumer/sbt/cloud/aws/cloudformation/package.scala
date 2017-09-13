package jp.pigumer.sbt.cloud.aws

package object cloudformation {

  case class Alias(val name: String) {
    def ->(stack: ⇒ CloudformationStack): Tuple2[String, () ⇒ CloudformationStack] =
      (name, stack _)
    def →(stack: ⇒ CloudformationStack) = ->(stack)
  }
}
