package cloudformation

import com.amazonaws.services.dynamodbv2.model.TimeToLiveSpecification

case class TTLSetting(tableName: String,
                           attributeName: String,
                           enabled: Boolean) {
  lazy val timeToLiveSpecification: TimeToLiveSpecification = new TimeToLiveSpecification().
    withAttributeName(attributeName).
    withEnabled(enabled)
}

case class TTLSettings(values: Seq[TTLSetting])

object TTLSettings {
  val empty: TTLSettings = TTLSettings(Seq.empty)
}