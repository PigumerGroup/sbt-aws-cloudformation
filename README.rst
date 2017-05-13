sbt-aws-cloudformation
======================

::

  addSbtPlugin("com.pigumer.sbt.cloud" % "sbt-aws-cloudformation" % "1.0.0")

::

  import cloudformation._

  lazy val root = (project in file(".")).
    enablePlugins(CloudformationPlugin).
    settings(
      version := "0.1",
      scalaVersion := "2.12.2",
      awscfSettings := AwscfSettings(
        region = <YOUR_REGION_NAME>,
        bucketName = <YOUR_BUCKET_NAME>,
        templates = file(<YOUR_TEMPLATES>)
      ),
      awscfStacks := Map(<SHORT_STACK_NAME> → CloudformationStack(
        stackName = <STACK_NAME>,
        template = <YOUR_TEMPLATE>,
        parameters = Map(<KEY> → <VALUE>),
        capabilities = Seq("CAPABILITY_NAMED_IAM")))
    )

::

  awscfSyncTemplates <stage>

  awscfCreateStack <stage> <shortName>
  awscfUpdateStack <stage> <shortName>
  awscfDeleteStack <stage> <shortName>

  awscfValidateTemplate <stage> <templateName>

  awscfListStacks
  awscfListExports

