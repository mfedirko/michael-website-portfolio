package io.mfedirko.contactme

import java.time.LocalDateTime
import kotlin.properties.Delegates

class ContactHistory {
    var creationTimestamp: LocalDateTime by Delegates.notNull()
    var fullName: String? = null
    var email: String? = null
    var messageBody: String? = null
}