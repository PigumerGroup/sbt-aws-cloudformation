package cloudformation

case class NotificationARN(value: String)

class NotificationARNs(val values: Seq[NotificationARN])

object NotificationARNs {

  val empty: NotificationARNs = new NotificationARNs(Seq.empty[NotificationARN])

  def apply(values: Seq[String]): NotificationARNs =
    new NotificationARNs(values.map(NotificationARN))
}