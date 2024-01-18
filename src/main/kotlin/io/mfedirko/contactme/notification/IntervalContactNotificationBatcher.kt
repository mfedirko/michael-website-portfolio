package io.mfedirko.contactme.notification

import io.mfedirko.common.util.Dates.TZ_LOCAL
import io.mfedirko.contactme.ContactHistorySpec
import io.mfedirko.contactme.ContactMeRepository
import io.mfedirko.contactme.notification.ContactNotificationBatcher.NotificationBatch
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.toKotlinDuration

@Component
class IntervalContactNotificationBatcher(
    private val contactMeRepository: ContactMeRepository,
    private val contactNotificationRepository: ContactNotificationRepository,
) : ContactNotificationBatcher {

    @Synchronized
    override fun nextBatchToNotify(): NotificationBatch {
        val lastNotification = contactNotificationRepository.findLastNotificationTime()
        val notificationInterval = contactNotificationRepository.getNotificationPreference()?.notificationInterval
            ?: DEFAULT_NOTIFICATION_INTERVAL
        val hasIntervalPassed = lastNotification < LocalDateTime.now(TZ_LOCAL).minusSeconds(notificationInterval.toKotlinDuration().inWholeSeconds)
        if (!hasIntervalPassed) {
            return NotificationBatch(emptyList(), "Minimum notification interval has not passed (last notification at $lastNotification)")
        }
        contactNotificationRepository.updateLastNotificationTime()

        val unreadContactHistory = contactMeRepository.findContactHistory(ContactHistorySpec().apply {
            startDate = lastNotification
        })
        return NotificationBatch(unreadContactHistory, "${unreadContactHistory.size} unread contact requests found since last notification $lastNotification")
    }

    companion object {
        private val DEFAULT_NOTIFICATION_INTERVAL = Duration.ofHours(2)
    }
}