package io.mfedirko.contactme

import io.mfedirko.common.OrderDir
import io.mfedirko.common.util.Logging.logger
import io.mfedirko.contactme.ContactHistory.Status.NOTIFIED
import io.mfedirko.contactme.ContactHistory.Status.UNREAD
import io.mfedirko.email.EmailService
import io.mfedirko.email.MailTemplateService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.toKotlinDuration

@Service
@EnableConfigurationProperties(ContactMeProperties::class)
class ContactNotificationService(
    private val contactMeRepository: ContactMeRepository,
    private val emailService: EmailService,
    private val templateService: MailTemplateService,
    private val contactMeProperties: ContactMeProperties
) {
    private val log = logger()

    @Async
    @Synchronized
    fun notifyOfNewContactRequests() {
        val (unreadContacts, reason) = findContactRequestsToNotify()
        log.info(reason)
        if (unreadContacts.isEmpty()) {
            return
        }

        val html = templateService.unreadContactRequests(unreadContacts)
        emailService.sendHtmlEmail(
            to = arrayOf(contactMeProperties.toEmail),
            subject = "${unreadContacts.size} new contact requests",
            htmlBody = html
        )
        contactMeRepository.update(*unreadContacts.map { it.apply { status = NOTIFIED } }.toTypedArray())
    }

    private fun findContactRequestsToNotify(): Pair<List<ContactHistory>, String> {
        val lastNotification = contactMeRepository.findContactHistory(ContactHistorySpec().apply {
            status = arrayOf(NOTIFIED)
            orderBy = arrayOf(ContactHistorySpec.OrderBy.UPDATE_TIMESTAMP to OrderDir.DESC)
        }, limit = 1).firstOrNull()?.creationTimestamp
            ?: LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.UTC)
        val hasIntervalPassed = lastNotification < LocalDateTime.now().minusSeconds(contactMeProperties.notificationInterval.toKotlinDuration().inWholeSeconds)
        if (!hasIntervalPassed) {
            return emptyList<ContactHistory>() to "Minimum notification interval has not passed (last notification at $lastNotification)"
        }

        val unreadContactHistory = contactMeRepository.findContactHistory(ContactHistorySpec().apply {
            status = arrayOf(UNREAD)
            startDate = lastNotification
        })
        return unreadContactHistory to "${unreadContactHistory.size} unread contact requests found"
    }
}