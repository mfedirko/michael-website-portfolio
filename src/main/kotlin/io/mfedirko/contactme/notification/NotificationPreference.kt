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
}

