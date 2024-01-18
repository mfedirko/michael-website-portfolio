package io.mfedirko.contactme.notification

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
@Profile("mock")
class MockContactNotificationRepository : ContactNotificationRepository {
    private var lastNotificationTime: LocalDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)
    private var notificationPreference: NotificationPreference? = null

    override fun findLastNotificationTime(): LocalDateTime {
        return lastNotificationTime
    }

    override fun updateLastNotificationTime() {
        lastNotificationTime = LocalDateTime.now()
    }

    override fun getNotificationPreference(): NotificationPreference? {
        return notificationPreference
    }

    override fun updateNotificationPreference(notificationPreference: NotificationPreference) {
        this.notificationPreference = notificationPreference
    }
}