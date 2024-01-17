package io.mfedirko.common.infra.email

import io.mfedirko.common.util.Logging.logger
import io.mfedirko.contactme.ContactMeProperties
import io.mfedirko.email.EmailService
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service


@Service
class DefaultEmailService(
    private val mailSender: JavaMailSender,
    private val contactMeProperties: ContactMeProperties
) : EmailService {
    private val log = logger()

    override fun sendHtmlEmail(to: Array<String>, subject: String, htmlBody: String, cc: Array<String>) {
        log.info("Sending email '{}'", subject)
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(mimeMessage, false, "UTF-8").apply {
            setSubject(subject)
            setFrom(contactMeProperties.fromEmail)
            setTo(contactMeProperties.toEmail)
            setCc(cc)
            setText(htmlBody, true)
        }
        mailSender.send(mimeMessage)
        log.info("Successfully sent email '{}'", subject)
    }
}