package io.mfedirko.contactme.notification

import io.mfedirko.contactme.ContactHistory

interface ContactNotificationBatcher {
    fun nextBatchToNotify(): NotificationBatch

    data class NotificationBatch(
        val requests: List<ContactHistory>,
        val statusReason: String
    )
}