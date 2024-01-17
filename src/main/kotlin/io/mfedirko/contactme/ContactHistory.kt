package io.mfedirko.contactme

import java.time.LocalDateTime
import kotlin.properties.Delegates

class ContactHistory {
    var creationTimestamp: LocalDateTime by Delegates.notNull()
    var id: Any by Delegates.notNull()
    var fullName: String by Delegates.notNull()
    var email: String by Delegates.notNull()
    var messageBody: String by Delegates.notNull()
    var status: Status by Delegates.notNull()

    enum class Status {
        /**
         * Initial status after submitting contact form
         */
        UNREAD,

        /**
         * Notification has been sent for this contact request
         */
        NOTIFIED
    }
}