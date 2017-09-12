sbt-aws-cloudformation
======================

A sbt plugin for AWS CloudFormation.

* sbt 0.13.16+ or sbt 1.0.0+

Installation
------------

project/plugins.sbt::

  addSbtPlugin("com.pigumer.sbt.cloud" % "sbt-aws-cloudformation" % "5.0.14-SNAPSHOT")


your build.sbt::

  enablePlugins(CloudformationPlugin)


Usage
-----

``awscfCreateBucket <stackName> <bucketName>``

Create a bucket using the AWS CloudFormation Stack.

``awscfUploadTemplates``

Upload templates to the bucket.

``awscfCreateStack <shortName>``

Create a AWS CloudFormation stack.

``awscfUpdateStack <shortName>``

Update a AWS CloudFormation stack.

``awscfDeleteStack <shortName>``

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
      scalaVersion := "2.12.3",
      awscfSettings := AwscfSettings(
        projectName = "example",
        region = <YOUR_REGION_NAME>,
        bucketName = <YOUR_BUCKET_NAME>,
        templates = file(<YOUR_TEMPLATES>),
        roleARN = None
      ),
      awscfStacks := Stacks(
        Alias("<SHORT_STACK_NAME>") → CloudformationStack(
          stackName = "<STACK_NAME>",
          template = "<YOUR_TEMPLATE>",
          parameters = Map("<KEY>" → "<VALUE>"),
          capabilities = Seq("CAPABILITY_NAMED_IAM")),
          notificationARNs = Seq("<NOTIFICATION_ARN>"),
          ttl = Seq(AwscfTTLSetting(
            tableName = "<DYNAMODB TABLE NAME>",
            attributeName = "<TTL ATTRIBUTE NAME>",
            enabled = <TTL ENABLED>)
          )
        )
      )

Snippets
--------

AWS Lambda
^^^^^^^^^^

updateFunctionCode::

    val lambdaUpdateFunctionCode = taskKey[Unit]("update lambda function code")

    lambdaUpdateFunctionCode := {
      val updateFunctionCodeRequest = new UpdateFunctionCodeRequest().
        withFunctionName(name.value).
        withS3Bucket(BucketName).
        withS3Key((assemblyOutputPath in assembly).value.getName)
      awslambda.value.updateFunctionCode(updateFunctionCodeRequest)
    }

ECR
^^^^

login::

    awsecr::awsecrLogin

tagging and push::

    val ecrPush = taskKey[Unit]("push")

    ecrPush := {
      val docker = (awsecrDockerPath in awsecr).value
      val domain = (awsecrDomain in awsecr).value

      val imageName = (packageName in Docker).value
      val imageVersion = (version in Docker).value
      val repositoryName = "YOUR-ECR-REPOSITORY"

      val source = s"$imageName:$imageVersion"
      val target = s"$domain/$repositoryName:$imageVersion"

      AwsecrCommands.tag(docker, source, target)
      AwsecrCommands.push(docker, target)
      ()
    }

ECS
^^^^

updateService::

    val ecsUpdateService = taskKey[Unit]("update service")

    ecsUpdateService := {
      val ecs = awsecs.value

      val cluster = "YOUR ECS CLUSTER"

      val taskDefinitionArn = awscfGetValue.toTask(" YOUR-TASK-DEFINITION-ARN-KEY").value
      val service = awscfGetValue.toTask(" YOUR-SERVICE-KEY").value

      val describeTaskDefinitionRequest = new DescribeTaskDefinitionRequest().
        withTaskDefinition(taskDefinitionArn)
      val describeTaskDefinitionResult = ecs.describeTaskDefinition(describeTaskDefinitionRequest)

      val registerTaskDefinitionRequest = new RegisterTaskDefinitionRequest().
        withFamily(describeTaskDefinitionResult.getTaskDefinition.getFamily).
        withContainerDefinitions(describeTaskDefinitionResult.getTaskDefinition.getContainerDefinitions)

      val registerTaskDefinitionResult = ecs.registerTaskDefinition(registerTaskDefinitionRequest)

      val updateServiceRequest = new UpdateServiceRequest().
        withCluster(cluster).
        withService(service).
        withTaskDefinition(registerTaskDefinitionResult.getTaskDefinition.getTaskDefinitionArn)

      ecs.updateService(updateServiceRequest)
    }
