package io.mfedirko.contactme

import io.mfedirko.common.infra.back4app.Back4appContactMeRepository
import io.mfedirko.common.infra.back4app.Back4appContactNotificationRepository
import io.mfedirko.common.infra.back4app.Back4appTestConfiguration
import io.mfedirko.email.EmailService
import io.mfedirko.email.MailTemplateService
import io.mfedirko.fixture.ContactForms
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.time.ZoneOffset

@SpringBootTest(classes = [
    Back4appTestConfiguration::class,
    Back4appContactMeRepository::class,
    Back4appContactNotificationRepository::class,
    ContactNotificationService::class
])
@ActiveProfiles("back4app")
@ExtendWith(MockitoExtension::class)
internal class ContactNotificationServiceTest {
    @Autowired
    private lateinit var contactNotificationService: ContactNotificationService

    @Autowired
    private lateinit var contactMeRepository: ContactMeRepository

    @Autowired
    private lateinit var contactNotificationRepository: ContactNotificationRepository

    @MockBean
    private lateinit var emailTemplateService: MailTemplateService

    @MockBean
    private lateinit var emailService: EmailService

    @Autowired
    @Qualifier("back4appTemplate")
    private lateinit var restTemplateBuilder: RestTemplateBuilder

    @AfterEach
    fun cleanup() {
        deleteAllContactHistory()
    }

    private fun deleteAllContactHistory() {
        val history = contactMeRepository.findContactHistory(ContactHistorySpec())
        val restTemplate = restTemplateBuilder.build()
        history.forEach { hist ->
            restTemplate.delete("/classes/ContactRequest/${hist.id}")
        }
    }

    @Test
    fun whenNoUnread_thenNoEmail() {
        contactNotificationService.notifyOfNewContactRequests()

        Mockito.verifyNoInteractions(emailService, emailTemplateService)
    }

    @Test
    fun whenHasUnread_andWithinIntervalSinceLastNotification_thenNoEmail() {
        contactMeRepository.save(ContactForms.aContactForm())
        contactNotificationRepository.updateLastNotificationTime()
        contactMeRepository.save(ContactForms.aContactForm())

        contactNotificationService.notifyOfNewContactRequests()

        Mockito.verifyNoInteractions(emailService, emailTemplateService)
    }

    @Test
    fun whenHasUnread_andPastIntervalSinceLastNotification_thenEmails() {
        contactNotificationRepository.updateLastNotificationTime()
        contactMeRepository.save(ContactForms.aContactForm())
        Thread.sleep(2500)
        val expectedHtml = mockTemplateEngine()

        contactNotificationService.notifyOfNewContactRequests()

        Mockito.verify(emailService).sendHtmlEmail(
            to = expectedToEmails(),
            subject = expectedSubject(1),
            htmlBody = expectedHtml
        )
    }

    @Test
    fun whenEmails_thenMarksContactHistoryAsNotified() {
        whenHasUnread_andPastIntervalSinceLastNotification_thenEmails()

        val lastNotif = contactNotificationRepository.findLastNotificationTime()

        Assertions.assertThat(lastNotif).isAfter(LocalDateTime.now().minusDays(1)) // time zone
    }

    private fun mockTemplateEngine(): String {
        val expectedHtml = "<html><body>My html</body></html>"
        Mockito.`when`(emailTemplateService.unreadContactRequests(Mockito.anyList()))
            .thenReturn(expectedHtml)
        return expectedHtml
    }

    private fun expectedToEmails() = arrayOf("michael@gmail.com")
    private fun expectedSubject(countUnread: Int) = "$countUnread new contact requests"
}