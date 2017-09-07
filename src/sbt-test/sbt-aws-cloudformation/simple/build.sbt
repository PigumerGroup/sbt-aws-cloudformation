import cloudformation._

val role = sys.env.get("ROLE_ARN")
val BucketName = sys.env("BUCKET_NAME")

val cfListExports = taskKey[Unit]("list exports")

lazy val root = (project in file(".")).
  enablePlugins(CloudformationPlugin).
  settings(
    version := "0.1",
    scalaVersion := "2.12.3",
    awscfSettings := AwscfSettings(
      region = "ap-northeast-1",
      bucketName = BucketName,
      projectName = "example",
      templates = file("cloudformation"),
      roleARN = role
    ),
    awscfStacks := Stacks(
      "test" → CloudformationStack(
        stackName = "test",
        template = "test1.yaml",
        parameters = Map("CIDR" → "10.0.0.0/16")
      ),
      "testu" → CloudformationStack(
        stackName = "test",
        template = "test1u.yaml",
        parameters = Map("CIDR" → "10.0.0.0/16")
      ),
      "fail" → CloudformationStack(
        stackName = "test",
        template = "test1.yaml",
        parameters = Map("IPAddr" → "10.0.0.0/16")
      ),
      "test2" → CloudformationStack(
        stackName = "test2",
        template = "test2.yaml",
        capabilities = Seq("CAPABILITY_NAMED_IAM")
      ),
      "test2u" → CloudformationStack(
        stackName = "test2",
        template = "test2u.yaml",
        capabilities = Seq("CAPABILITY_NAMED_IAM")
      ),
      "fail2" → CloudformationStack(
        stackName = "test2",
        template = "test2.yaml"
      ),
      "dynamodb" → CloudformationStack(
        stackName = "dynamodb",
        template = "dynamodb.yaml",
        ttl = Seq(TTLSetting(tableName = "test",
          attributeName = "expiration",
          enabled = true))
      )
    )
  ).
  settings(
    cfListExports := {
      val log = streams.value.log
      awscfListExports.value.foreach { exp ⇒
        log.info(s"${exp.stackName}/${exp.name}: ${exp.value}")
      }
    }
  )
