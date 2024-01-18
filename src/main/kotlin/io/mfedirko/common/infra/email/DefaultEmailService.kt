package io.mfedirko.common.infra.email

import io.mfedirko.common.util.Logging.logger
import io.mfedirko.contactme.notification.ContactNotificationRepository
import io.mfedirko.email.EmailService
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service


@Service
class DefaultEmailService(
    private val mailSender: JavaMailSender,
    private val notificationRepository: ContactNotificationRepository
) : EmailService {
    private val log = logger()

    override fun sendHtmlEmail(subject: String, htmlBody: String, cc: Array<String>) {
        log.info("Sending email '{}'", subject)
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val notificationPreference = notificationRepository.getNotificationPreference()
            ?: error("Notification preferences are required in order to send email")
        MimeMessageHelper(mimeMessage, false, "UTF-8").apply {
            setSubject(subject)
            setFrom(notificationPreference.fromEmail)
            setTo(notificationPreference.toEmail)
            setCc(cc)
            setText(htmlBody, true)
        }
        mailSender.send(mimeMessage)
        log.info("Successfully sent email '{}'", subject)
    }
}