package cloudformation

import java.io.File

import org.specs2.mutable.Specification

class CloudformationTasksSpec extends Specification {

  "CloudformationTasks" should {

    "amazonS3Client" in {
      val client = CloudformationTasks.amazonS3Client(AwscfSettings(bucketName = "", templates = new File(".")))
      client != null must beTrue
    }
  }
}
