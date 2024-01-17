package io.mfedirko.contactme

import java.time.LocalDateTime

interface ContactNotificationRepository {
    fun findLastNotificationTime(): LocalDateTime
    fun updateLastNotificationTime()
}