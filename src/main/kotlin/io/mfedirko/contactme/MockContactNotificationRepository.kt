package io.mfedirko.contactme

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
@Profile("mock")
class MockContactNotificationRepository : ContactNotificationRepository {
    private var lastNotificationTime: LocalDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)

    override fun findLastNotificationTime(): LocalDateTime {
        return lastNotificationTime
    }

    override fun updateLastNotificationTime() {
        lastNotificationTime = LocalDateTime.now()
    }
}