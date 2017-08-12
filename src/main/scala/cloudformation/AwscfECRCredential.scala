package cloudformation

import java.nio.charset.StandardCharsets
import java.util.Base64

import com.amazonaws.services.ecr.model.GetAuthorizationTokenResult

class AwscfECRCredential(val user: String, val password: String)

object AwscfECRCredential {
  import scala.collection.JavaConverters._

  def apply(getAuthorizationTokenResult: GetAuthorizationTokenResult): AwscfECRCredential = {
    val decode = Base64.getDecoder.decode(getAuthorizationTokenResult.getAuthorizationData.asScala.head.getAuthorizationToken)
    val cred = new String(decode, StandardCharsets.UTF_8).split(":")
    new AwscfECRCredential(user = cred(0), password = cred(1))
  }
}
