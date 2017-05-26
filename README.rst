sbt-aws-cloudformation
======================

A sbt plugin for AWS CloudFormation.

Installation
------------

project/plugins.sbt::

  addSbtPlugin("com.pigumer.sbt.cloud" % "sbt-aws-cloudformation" % "2.2.1")


your build.sbt::

  enablePlugins(CloudformationPlugin)


Usage
-----

``awscfCreateBucket <stackName>``

Create a bucket using the AWS CloudFormation Stack.

``awscfUploadTemplates <stage>``

Upload templates to the bucket.

``awscfCreateStack <stage> <shortName>``

Create a AWS CloudFormation stack.

``awscfUpdateStack <stage> <shortName>``

Update a AWS CloudFormation stack.

``awscfDeleteStack <stage> <shortName>``

Delete a AWS CloudFormation stack.

``awscfValidateTemplate <templateName>``

Validate a AWS CloudFormation template.

``awscfListStacks``

``awscfListExports``

ex build.sbt::

  import cloudformation._

  lazy val root = (project in file(".")).
    enablePlugins(CloudformationPlugin).
    settings(
      version := "0.1",
      scalaVersion := "2.12.2",
      awscfSettings := AwscfSettings(
        projectName = Some("example/"),
        region = <YOUR_REGION_NAME>,
        bucketName = <YOUR_BUCKET_NAME>,
        templates = file(<YOUR_TEMPLATES>),
        roleARN = None
      ),
      awscfStacks := Map(<SHORT_STACK_NAME> → CloudformationStack(
        stackName = <STACK_NAME>,
        template = <YOUR_TEMPLATE>,
        parameters = Map(<KEY> → <VALUE>),
        capabilities = Seq("CAPABILITY_NAMED_IAM")),
        ttl = Seq(AwscfTTLSettings(
          tableName = <DYNAMODB TABLE NAME>,
          attributeName = <TTL ATTRIBUTE NAME>,
          enabled = <TTL ENABLED>))
    )

