package io.mfedirko.contactme.notification

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration

open class NotificationPreference
@JsonCreator
constructor(
    /**
     * Limits how often notifications are sent regarding contact requests
     */
    @JsonProperty("notificationInterval")
    val notificationInterval: Duration,

    /**
     * Email address to which notification emails are sent
     */
    @JsonProperty("toEmail")
    val toEmail: String,

    /**
     * Email address which will appear as the sender/from address in notification emails
     */
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

