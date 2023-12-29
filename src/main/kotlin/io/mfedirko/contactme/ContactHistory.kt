package io.mfedirko.contactme

import java.time.LocalDateTime
import kotlin.properties.Delegates

class ContactHistory {
    var creationTimestamp: LocalDateTime by Delegates.notNull()
    var fullName: String by Delegates.notNull()
    var email: String by Delegates.notNull()
    var messageBody: String by Delegates.notNull()
}