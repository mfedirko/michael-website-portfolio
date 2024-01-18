package io.mfedirko.contactme.notification

import io.mfedirko.contactme.ContactHistorySpec
import io.mfedirko.contactme.ContactMeProperties
import io.mfedirko.contactme.ContactMeRepository
import io.mfedirko.contactme.notification.ContactNotificationBatcher.NotificationBatch
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.time.toKotlinDuration

@Component
@EnableConfigurationProperties(ContactMeProperties::class)
class IntervalContactNotificationBatcher(
    private val contactMeRepository: ContactMeRepository,
    private val contactNotificationRepository: ContactNotificationRepository,
    private val contactMeProperties: ContactMeProperties
) : ContactNotificationBatcher {

    @Synchronized
    override fun nextBatchToNotify(): NotificationBatch {
        val lastNotification = contactNotificationRepository.findLastNotificationTime()
        val hasIntervalPassed = lastNotification < LocalDateTime.now().minusSeconds(contactMeProperties.notificationInterval.toKotlinDuration().inWholeSeconds)
        if (!hasIntervalPassed) {
            return NotificationBatch(emptyList(), "Minimum notification interval has not passed (last notification at $lastNotification)")
        }
        contactNotificationRepository.updateLastNotificationTime()

        val unreadContactHistory = contactMeRepository.findContactHistory(ContactHistorySpec().apply {
            startDate = lastNotification
        })
        return NotificationBatch(unreadContactHistory, "${unreadContactHistory.size} unread contact requests found since last notification $lastNotification")
    }
}