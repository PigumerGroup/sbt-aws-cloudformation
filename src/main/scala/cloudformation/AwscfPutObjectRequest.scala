package cloudformation

import sbt.File

import com.amazonaws.services.s3.model.PutObjectRequest

case class AwscfPutObjectRequest(bucketName: String,
                                 key: String,
                                 file: File)

case class AwscfPutObjectRequests(values: Seq[AwscfPutObjectRequest]) {
  val requests = values.map(v ⇒ new PutObjectRequest(v.bucketName, v.key, v.file))
}
