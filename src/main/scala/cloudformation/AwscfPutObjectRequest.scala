package cloudformation

import java.io.File

import com.amazonaws.services.s3.model.PutObjectRequest

case class AwscfPutObjectRequest(bucketName: String,
                                 key: String,
                                 file: File) {
  val putObjectRequest: PutObjectRequest =
    new PutObjectRequest(bucketName, key, file)
}
