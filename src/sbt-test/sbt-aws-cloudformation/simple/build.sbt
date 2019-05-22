import jp.pigumer.sbt.cloud.aws.cloudformation._

val role = sys.env.get("ROLE_ARN")
val BucketName = sys.env.get("BUCKET_NAME")

val cfListExports = taskKey[Unit]("list exports")

lazy val root = (project in file(".")).
  enablePlugins(CloudformationPlugin).
  settings(
    version := "0.1",
    scalaVersion := "2.12.8",
    awscfSettings := AwscfSettings(
      region = "ap-northeast-1",
      bucketName = BucketName,
      projectName = "example",
      templates = Some(file("cloudformation")),
      roleARN = role
    ),
    awscfStacks := Stacks(
      Alias("test") → CloudformationStack(
        stackName = "test",
        template = "test1.yaml",
        parameters = Map("CIDR" → "10.0.0.0/16")
      ),
      Alias("testu") → CloudformationStack(
        stackName = "test",
        template = "test1u.yaml",
        parameters = Map("CIDR" → "10.0.0.0/16")
      ),
      Alias("fail") → CloudformationStack(
        stackName = "test",
        template = "test1.yaml",
        parameters = Map("IPAddr" → "10.0.0.0/16")
      ),
      Alias("test2") → CloudformationStack(
        stackName = "test2",
        template = "test2.yaml",
        capabilities = Seq("CAPABILITY_NAMED_IAM")
      ),
      Alias("test2u") → CloudformationStack(
        stackName = "test2",
        template = "test2u.yaml",
        capabilities = Seq("CAPABILITY_NAMED_IAM")
      ),
      Alias("fail2") → CloudformationStack(
        stackName = "test2",
        template = "test2.yaml"
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
