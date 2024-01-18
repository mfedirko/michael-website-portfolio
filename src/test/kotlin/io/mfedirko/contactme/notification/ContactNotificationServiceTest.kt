package io.mfedirko.contactme.notification

import io.mfedirko.contactme.notification.ContactNotificationBatcher.NotificationBatch
import io.mfedirko.email.EmailService
import io.mfedirko.email.MailTemplateService
import io.mfedirko.fixture.ContactForms
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(classes = [
    ContactNotificationService::class
])
@ExtendWith(MockitoExtension::class)
internal class ContactNotificationServiceTest {
    @Autowired
    private lateinit var contactNotificationService: ContactNotificationService

    @MockBean
    private lateinit var notificationBatcher: ContactNotificationBatcher

    @MockBean
    private lateinit var emailTemplateService: MailTemplateService

    @MockBean
    private lateinit var emailService: EmailService

    @Test
    fun whenNoUnread_thenNoEmail() {
        Mockito.`when`(notificationBatcher.nextBatchToNotify())
            .thenReturn(NotificationBatch(emptyList(), "No requests found"))

        contactNotificationService.notifyOfNewContactRequests()

        Mockito.verifyNoInteractions(emailService, emailTemplateService)
    }

    @Test
    fun whenHasUnread_thenSendEmail() {
        Mockito.`when`(notificationBatcher.nextBatchToNotify())
            .thenReturn(NotificationBatch(listOf(
                ContactForms.aContactForm().toContactHistory()
            ), "Success"))
        val expectedHtml = mockTemplateEngine()

        contactNotificationService.notifyOfNewContactRequests()

        Mockito.verify(emailService).sendHtmlEmail(
            subject = expectedSubject(1),
            htmlBody = expectedHtml
        )
    }

    private fun mockTemplateEngine(): String {
        val expectedHtml = "<html><body>My html</body></html>"
        Mockito.`when`(emailTemplateService.unreadContactRequests(Mockito.anyList()))
            .thenReturn(expectedHtml)
        return expectedHtml
    }

    private fun expectedSubject(countUnread: Int) = "$countUnread new contact requests"
}