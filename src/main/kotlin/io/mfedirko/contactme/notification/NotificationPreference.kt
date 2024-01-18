package io.mfedirko.contactme.notification

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration

open class NotificationPreference
@JsonCreator
constructor(
    @JsonProperty("notificationInterval")
    val notificationInterval: Duration,

    @JsonProperty("toEmail")
    val toEmail: String,

    @JsonProperty("fromEmail")
    val fromEmail: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NotificationPreference) return false

        if (notificationInterval != other.notificationInterval) return false
        if (toEmail != other.toEmail) return false
        if (fromEmail != other.fromEmail) return false

        return true
    }

    override fun hashCode(): Int {
        var result = notificationInterval.hashCode()
        result = 31 * result + toEmail.hashCode()
        result = 31 * result + fromEmail.hashCode()
        return result
    }
}

