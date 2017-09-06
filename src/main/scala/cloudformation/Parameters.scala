package cloudformation

case class Parameters(values: Map[String, String])

object Parameters {

  val empty: Parameters = Parameters(Map.empty[String, String])
}