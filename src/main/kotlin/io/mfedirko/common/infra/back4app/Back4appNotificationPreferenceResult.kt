package io.mfedirko.common.infra.back4app

import io.mfedirko.contactme.notification.NotificationPreference
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.properties.Delegates

class Back4appNotificationPreferenceResult {
    var objectId: String by Delegates.notNull()
    var createdAt: ZonedDateTime by Delegates.notNull()
    var updatedAt: ZonedDateTime by Delegates.notNull()
    var notificationInterval: String by Delegates.notNull()
    var toEmail: String by Delegates.notNull()
    var fromEmail: String by Delegates.notNull()

    fun toNotificationPreference(): NotificationPreference {
        return NotificationPreference(
            notificationInterval = Duration.parse(notificationInterval),
            fromEmail = fromEmail,
            toEmail = toEmail
        )
    }
}
