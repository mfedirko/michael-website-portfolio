package io.mfedirko.contactme.notification

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.Duration

data class NotificationPreferenceForm
constructor(
    @field:NotNull
    var notificationInterval: Duration?,

    @field:NotEmpty
    @field:Email
    var toEmail: String?,

    @field:NotEmpty
    @field:Email
    var fromEmail: String?,
) {
    fun toNotificationPreference(): NotificationPreference {
        return NotificationPreference(
            notificationInterval!!, toEmail!!, fromEmail!!
        )
    }

    companion object {
        fun fromNotificationPreference(pref: NotificationPreference): NotificationPreferenceForm {
            return NotificationPreferenceForm(
                pref.notificationInterval, pref.toEmail, pref.fromEmail
            )
        }
    }
}