package cloudformation

import java.nio.charset.StandardCharsets
import java.util.Base64

import com.amazonaws.services.ecr.model.GetAuthorizationTokenResult

case class AwsecrCredential(user: String, password: String)

object AwsecrCredential {
  import scala.collection.JavaConverters._

  def apply(getAuthorizationTokenResult: GetAuthorizationTokenResult): AwsecrCredential = {
    val decode = Base64.getDecoder.decode(getAuthorizationTokenResult.getAuthorizationData.asScala.head.getAuthorizationToken)
    val cred = new String(decode, StandardCharsets.UTF_8).split(":")
    new AwsecrCredential(user = cred(0), password = cred(1))
  }
}
