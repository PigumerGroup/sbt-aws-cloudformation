package jp.pigumer.sbt.cloud.aws.cloudformation

case class NotificationARN(value: String)

abstract class NotificationARNs {
  def values: Seq[NotificationARN]
}

object NotificationARNs {

  def apply(elems: Seq[String]): NotificationARNs =
    new NotificationARNs {
      override def values = elems.map(NotificationARN)
    }
}