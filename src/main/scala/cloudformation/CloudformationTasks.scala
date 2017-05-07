package cloudformation

import com.amazonaws.services.s3.{AmazonS3Client, AmazonS3ClientBuilder}
import jp.pigumer.sbt.cloud.aws.s3.SyncTemplates

object CloudformationTasks extends SyncTemplates {

  def amazonS3Client(settings: AwsSettings) =
    AmazonS3ClientBuilder.
      standard.
      withCredentials(settings.credentialsProvider).
      withRegion(settings.region).
      build.asInstanceOf[AmazonS3Client]

}
