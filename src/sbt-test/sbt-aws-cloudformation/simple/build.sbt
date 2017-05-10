import cloudformation._

lazy val root = (project in file(".")).
  enablePlugins(CloudformationPlugin).
  settings(
    version := "0.1",
    scalaVersion := "2.12.2",
    awsSettings := AwsSettings(
      region = "ap-northeast-1",
      bucketName = "jp-pigumer-test",
      templates = file("cloudformation")
    ),
    stacks := Map(
      "test" -> CloudFormationStack(
        stackName = "test",
        template = "test1.yaml",
        parameters = Map("CIDR" -> "10.0.0.0/16")
      ),
      "fail" -> CloudFormationStack(
        stackName = "test",
        template = "test1.yaml",
        parameters = Map("IPAddr" -> "10.0.0.0/16")
      ),
      "test2" -> CloudFormationStack(
        stackName = "test2",
        template = "test2.yaml",
        capabilities = Seq("CAPABILITY_NAMED_IAM")
      ),
      "fail2" -> CloudFormationStack(
        stackName = "test2",
        template = "test2.yaml"
      )
    )
  )
