package io.mfedirko.contactme.notification

import io.mfedirko.common.infra.back4app.Back4appContactMeRepository
import io.mfedirko.common.infra.back4app.Back4appContactNotificationRepository
import io.mfedirko.common.infra.back4app.Back4appTestConfiguration
import io.mfedirko.contactme.ContactHistorySpec
import io.mfedirko.contactme.ContactMeRepository
import io.mfedirko.fixture.ContactForms
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

@SpringBootTest(classes = [
    Back4appTestConfiguration::class,
    Back4appContactMeRepository::class,
    Back4appContactNotificationRepository::class,
    IntervalContactNotificationBatcher::class
])
@ActiveProfiles("back4app")
@ExtendWith(MockitoExtension::class)
internal class IntervalContactNotificationBatcherTest {
    @Autowired
    private lateinit var batcher: IntervalContactNotificationBatcher

    @Autowired
    private lateinit var contactMeRepository: ContactMeRepository

    @Autowired
    private lateinit var contactNotificationRepository: ContactNotificationRepository

    @Autowired
    @Qualifier("back4appTemplate")
    private lateinit var restTemplateBuilder: RestTemplateBuilder

    @BeforeEach
    fun setup() {
        contactNotificationRepository.updateNotificationPreference(
            NotificationPreference(
                notificationInterval = Duration.ofSeconds(1),
                toEmail = "test@aa",
                fromEmail = "test2@bb"
            )
        )
    }

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
    fun whenWithinIntervalWindow_thenEmptyBatch() {
        contactNotificationRepository.updateLastNotificationTime()

        val (batch, statusReason) = batcher.nextBatchToNotify()

        assertThat(batch).isEmpty()
        assertThat(statusReason.lowercase()).contains("notification interval")
    }

    @Test
    fun whenPastIntervalWindow_thenNonEmptyBatch() {
        contactNotificationRepository.updateLastNotificationTime()
        Thread.sleep(1500)
        contactMeRepository.save(ContactForms.aContactForm())
        contactMeRepository.save(ContactForms.aContactForm())
        contactMeRepository.save(ContactForms.aContactForm())

        val (batch, _) = batcher.nextBatchToNotify()

        assertThat(batch).hasSize(3)
    }

    @Test
    fun whenSubsequentBatches_thenNoOverlap() {
        Thread.sleep(1500)
        contactMeRepository.save(ContactForms.aContactForm())
        contactMeRepository.save(ContactForms.aContactForm())
        val (batch1, reason1) = batcher.nextBatchToNotify()
        Thread.sleep(1500)
        contactMeRepository.save(ContactForms.aContactForm())
        val (batch2, reason2) = batcher.nextBatchToNotify()

        assertThat(batch1).hasSize(2)
        assertThat(batch2).hasSize(1)
        assertThat(batch1.intersect(batch2)).isEmpty()
    }
}