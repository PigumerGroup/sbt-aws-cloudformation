package cloudformation

import com.amazonaws.services.dynamodbv2.model.TimeToLiveSpecification

case class AwscfTTLSettings(tableName: String,
                            attributeName: String,
                            enabled: Boolean) {
  lazy val timeToLiveSpecification: TimeToLiveSpecification = new TimeToLiveSpecification().
    withAttributeName(attributeName).
    withEnabled(enabled)
}
