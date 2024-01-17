package io.mfedirko.common.infra.email

import io.mfedirko.fixture.ContactForms
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertTrue

@SpringBootTest(classes = [
    ThymeleafAutoConfiguration::class,
    ThymeleafEmailTemplateService::class
])
internal class ThymeleafEmailTemplateServiceTest {
    @Autowired
    private lateinit var service: ThymeleafEmailTemplateService

    @Nested
    internal inner class UnreadContactRequests {
        private fun contactRequests() = (0..20).map { i ->
            ContactForms.aContactForm().toContactHistory().apply {
                email = "$i@email.com"
                fullName = "User $i"
                messageBody = "Message $i"
                creationTimestamp = LocalDateTime.now()
                id = UUID.randomUUID().toString()
            }
        }

        @Test
        fun title() {
            val html = service.unreadContactRequests(contactRequests())

            Assertions.assertThat(html).contains("21 Unread Contact Requests")
        }

        @Test
        fun styleNotInBody() {
            val html = service.unreadContactRequests(contactRequests())

            assertTrue(html.lastIndexOf("<style") < html.indexOf("<body"),
                "<style> should not be inside <body> for HTML emails")
        }

        @Test
        fun tableWithFirst5() {
            val html = service.unreadContactRequests(contactRequests())

            Assertions.assertThat(html).containsSubsequence(
                "<table",
                "<tr",
                "User 0", "0@email.com", "Message 0",
                "</tr>",
                "<tr",
                "User 1", "1@email.com", "Message 1",
                "</tr>",
                "<tr",
                "User 2", "2@email.com", "Message 2",
                "</tr>",
                "<tr",
                "User 3", "3@email.com", "Message 3",
                "</tr>",
                "<tr",
                "User 4", "4@email.com", "Message 4",
                "</tr>",
            )
        }
    }

}