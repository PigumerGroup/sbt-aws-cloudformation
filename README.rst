sbt-aws-cloudformation
======================

::

  import cloudformation._

  lazy val root = (project in file(".")).
    enablePlugins(CloudformationPlugin).
    settings(
      version := "0.1",
      scalaVersion := "2.12.2",
      awsSettings := AwsSettings(
        region = <YOUR_REGION_NAME>,
        bucketName = <YOUR_BUCKET_NAME>,
        templates = file(<YOUR_TEMPLATES>)
      ),
      awscfStacks := Map(<SHORT_STACK_NAME> -> CloudformationStack(
        stackName = <STACK_NAME>,
        template = <YOUR_TEMPLATE>))
    )

::

  awscfSyncTemplates <stage>
  awscfCreateStack <stage> <shortName>
  awscfDeleteStack <stage> <shortName>

