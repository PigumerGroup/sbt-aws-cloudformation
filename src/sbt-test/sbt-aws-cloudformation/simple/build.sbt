import cloudformation._

lazy val root = (project in file(".")).
  enablePlugins(CloudformationPlugin).
  settings(
    version := "0.1",
    scalaVersion := "2.12.2",
    awscfSettings := AwscfSettings(
      region = "ap-northeast-1",
      bucketName = "jp-pigumer-test",
      templates = file("cloudformation")
    ),
    awscfStacks := Map(
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
      )
    )
  )
