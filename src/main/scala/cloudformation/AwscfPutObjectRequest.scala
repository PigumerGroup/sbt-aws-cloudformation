package cloudformation

import com.amazonaws.services.s3.model.PutObjectRequest
import sbt.File

case class AwscfPutObjectRequest(bucketName: String,
                                 key: String,
                                 file: File)

case class AwscfPutObjectRequests(private val values: Seq[AwscfPutObjectRequest]) {
  val requests = values.map { v ⇒
    new PutObjectRequest(v.bucketName, v.key, v.file)
  }
}
