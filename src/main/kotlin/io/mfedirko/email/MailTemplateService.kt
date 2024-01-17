package io.mfedirko.email

import io.mfedirko.contactme.ContactHistory

interface MailTemplateService {
    fun unreadContactRequests(requests: List<ContactHistory>): String
}