package jp.pigumer.sbt.cloud.aws.cloudformation

case class AwscfExport(exportingStackId: String,
                       stackName: String,
                       name: String,
                       value: String)
