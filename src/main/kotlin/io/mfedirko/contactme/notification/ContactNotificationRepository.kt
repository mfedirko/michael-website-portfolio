package io.mfedirko.contactme.notification

import java.time.LocalDateTime

interface ContactNotificationRepository : NotificationPreferenceRepository {
    fun findLastNotificationTime(): LocalDateTime
    fun updateLastNotificationTime()
}