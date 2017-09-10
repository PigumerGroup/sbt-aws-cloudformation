package cloudformation

import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest
import jp.pigumer.sbt.cloud.aws.apigateway.ApiGateway
import jp.pigumer.sbt.cloud.aws.applicationautoscaling.ApplicationAutoScaling
import jp.pigumer.sbt.cloud.aws.autoscaling.AutoScaling
import jp.pigumer.sbt.cloud.aws.ecr.Ecr
import jp.pigumer.sbt.cloud.aws.ecs.Ecs
import jp.pigumer.sbt.cloud.aws.lambda.Lambda
import sbt.{Def, _}
import serverless.{Serverless, ServerlessKeys}

object CloudformationPlugin extends AutoPlugin {

  object autoImport
    extends CloudformationKeys
      with EcrKeys
      with EcsKeys
      with LambdaKeys
      with ApiGatewayKeys
      with ApplicationAutoScalingKeys
      with AutoScalingKeys
      with ServerlessKeys

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    awscfSettings := awscfSettings.value,

    awscf := {
      import CloudformationTasks._
      cloudFormation(awscfSettings.value)
    },

    awss3 := {
      import CloudformationTasks._
      amazonS3(awscfSettings.value)
    },

    awsdynamodb := {
      import CloudformationTasks._
      dynamoDB(awscfSettings.value)
    },

    awscfGetCallerIdentityRequest :=
      awscfGetCallerIdentityRequest.?.value.getOrElse(new GetCallerIdentityRequest()),
    awscfAccountId := {
      import CloudformationTasks._
      sts(awscfSettings.value).getCallerIdentity(awscfGetCallerIdentityRequest.value).getAccount
    },

    awscfStacks := awscfStacks.?.value.getOrElse(Stacks.empty),

    awscfUploadTemplates := CloudformationTasks.uploadTemplatesTask.value,

    awscfCreateStack := CloudformationTasks.createStackTask.evaluated,
    awscfDeleteStack := CloudformationTasks.deleteStackTask.evaluated,
    awscfUpdateStack := CloudformationTasks.updateStackTask.evaluated,

    awscfValidateTemplate := CloudformationTasks.validateTemplateTask.evaluated,

    awscfListStacks := CloudformationTasks.listStacksTask.value,
    awscfListExports := CloudformationTasks.listExportsTask.value,

    awscfGetValue := CloudformationTasks.getValueTask.evaluated,

    awscfCreateBucket := CloudformationTasks.createBucketTask.evaluated,


    awss3Upload in awss3 := CloudformationTasks.uploadTask.evaluated,

    awss3PutObjectRequests in awss3 := awss3PutObjectRequests.?.value.getOrElse(Awss3PutObjectRequests(Seq.empty)),
    awss3PutObjects in awss3 := CloudformationTasks.putObjectsTask.value,

    
    awsecr := {
      new Ecr {}.ecr(awscfSettings.value)
    },
    awsecrDockerPath in awsecr :=
      sys.env.get("DOCKER").filter(_.nonEmpty).getOrElse("docker"),
    awsecrGetAuthorizationTokenRequest in awsecr :=
      awsecrGetAuthorizationTokenRequest.?.value.getOrElse(new GetAuthorizationTokenRequest()),
    awsecrCredential in awsecr :=
      AwsecrCredential(awsecr.value.getAuthorizationToken((awsecrGetAuthorizationTokenRequest in awsecr).value))
    ,
    awsecrDomain in awsecr := {
      s"${awscfAccountId.value}.dkr.ecr.${awscfSettings.value.region}.amazonaws.com"
    },
    awsecrLogin in awsecr := {
      val dockerPath = (awsecrDockerPath in awsecr).value
      val credential = (awsecrCredential in awsecr).value
      val domain = (awsecrDomain in awsecr).value

      val loginCommand = dockerPath :: "login" :: "-u" :: credential.user :: "-p" :: credential.password :: s"https://$domain" :: Nil
      val cmd = loginCommand.mkString(" ")
      sys.process.Process(cmd)! match {
        case 0 ⇒ cmd
        case _ ⇒ sys.error(s"Login failed. Command: $cmd")
      }
    },


    awsecs := {
      new Ecs {}.ecs(awscfSettings.value)
    },


    awslambda := {
      new Lambda {}.lambda(awscfSettings.value)
    },


    awsapigateway := {
      new ApiGateway {}.apigateway(awscfSettings.value)
    },

    awsApplicationAutoScaling := {
      ApplicationAutoScaling.applicationAutoScaling(awscfSettings.value)
    },

    awsAutoScaling := {
      AutoScaling.autoScaling(awscfSettings.value)
    },


    serverlessPath := serverlessPath.?.value.getOrElse("serverless"),
    serverlessWorkingDirectory := serverlessWorkingDirectory.?.value.getOrElse(file(".")),

    serverless := serverlessPath.value,

    serverlessExec in serverless := Serverless.serverlessExec.evaluated
  )
}
