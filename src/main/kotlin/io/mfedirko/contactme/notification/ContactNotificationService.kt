package io.mfedirko.contactme.notification

import io.mfedirko.common.util.Logging.logger
import io.mfedirko.contactme.ContactMeProperties
import io.mfedirko.email.EmailService
import io.mfedirko.email.MailTemplateService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(ContactMeProperties::class)
class ContactNotificationService(
    private val notificationBatcher: ContactNotificationBatcher,
    private val emailService: EmailService,
    private val templateService: MailTemplateService,
    private val contactMeProperties: ContactMeProperties
) {
    private val log = logger()

    @Async
    @Synchronized
    fun notifyOfNewContactRequests() {
        val (contactBatch, reason) = notificationBatcher.nextBatchToNotify()
        log.info(reason)
        if (contactBatch.isEmpty()) {
            return
        }

        val html = templateService.unreadContactRequests(contactBatch)
        emailService.sendHtmlEmail(
            to = arrayOf(contactMeProperties.toEmail),
            subject = "${contactBatch.size} new contact requests",
            htmlBody = html
        )
    }

}