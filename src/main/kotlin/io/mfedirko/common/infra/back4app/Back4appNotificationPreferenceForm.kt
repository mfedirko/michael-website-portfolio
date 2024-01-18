package io.mfedirko.common.infra.back4app

import io.mfedirko.contactme.notification.NotificationPreference
import java.time.ZonedDateTime
import kotlin.properties.Delegates

class Back4appNotificationPreferenceForm(prefs: NotificationPreference) {
    val notificationInterval: String
    val toEmail: String
    val fromEmail: String

    init {
        notificationInterval = prefs.notificationInterval.toString()
        toEmail = prefs.toEmail
        fromEmail = prefs.fromEmail
    }

}