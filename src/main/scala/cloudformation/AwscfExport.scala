package cloudformation

case class AwscfExport(exportingStackId: String,
                       stackName: String,
                       name: String,
                       value: String)
