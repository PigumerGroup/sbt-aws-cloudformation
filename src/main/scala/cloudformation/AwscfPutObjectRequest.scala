package cloudformation

import com.amazonaws.services.s3.model.PutObjectRequest

case class Awss3PutObjectRequests(values: Seq[PutObjectRequest])