package io.mfedirko.contactme

import io.mfedirko.common.util.Logging.logger
import io.mfedirko.contactme.ContactNotificationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhooks/contacts")
class ContactsWebhookController(
    private val contactNotificationService: ContactNotificationService
) {
    private val log = logger()

    @GetMapping
    fun onContactRequested(@RequestParam("params", required = false) params: Map<String, Any>?): String {
        log.info("Called /contacts webhook with params: {}", params)
        contactNotificationService.notifyOfNewContactRequests()
        return "Success"
    }
}