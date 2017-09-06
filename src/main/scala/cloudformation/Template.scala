package cloudformation

sealed trait CloudformationTemplate

case class Template(value: String) extends CloudformationTemplate

case class TemplateBody(value: String) extends CloudformationTemplate
