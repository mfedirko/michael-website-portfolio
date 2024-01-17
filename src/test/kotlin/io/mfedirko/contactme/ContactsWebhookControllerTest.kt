package io.mfedirko.contactme

import io.mfedirko.common.infra.security.ParseWebhookAuthorizerFilter
import io.mfedirko.contactme.ContactNotificationService
import io.mfedirko.contactme.ContactsWebhookController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebMvcTest(controllers = [ContactsWebhookController::class])
@AutoConfigureMockMvc
internal class ContactsWebhookControllerTest {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var contactNotificationService: ContactNotificationService

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilter<DefaultMockMvcBuilder>(ParseWebhookAuthorizerFilter("testkey"), "/webhooks/*")
            .build()
    }

    @Test
    fun whenInvalidWebhookKey_then403() {
        mockMvc.perform(get("/webhooks/contacts"))
            .andExpect(status().`is`(403))
    }

    @Test
    fun whenValidWebhookKey_then200() {
        mockMvc.perform(get("/webhooks/contacts")
                .header("X-Parse-Webhook-Key", "testkey"))
            .andExpect(status().isOk)
        Mockito.verify(contactNotificationService).notifyOfNewContactRequests()
    }
}