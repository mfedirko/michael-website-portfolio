package io.mfedirko.common.infra.email

import io.mfedirko.contactme.ContactHistory
import io.mfedirko.email.MailTemplateService
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*


@Service
class ThymeleafEmailTemplateService(
    private val templateEngine: TemplateEngine
) : MailTemplateService {
    override fun unreadContactRequests(requests: List<ContactHistory>): String {
        val ctx = Context(Locale.US).apply {
            setVariable("count", requests.size)
            setVariable("history", requests.take(NUM_CONTACT_REQUESTS_SHOWN))
        }
        return templateEngine.process("email/unread-contact-requests.html", ctx)
    }

    companion object {
        private const val NUM_CONTACT_REQUESTS_SHOWN = 5
    }
}