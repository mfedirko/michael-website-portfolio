package io.mfedirko.common.infra.back4app

import io.mfedirko.common.util.Dates.inLocalTimeZone
import io.mfedirko.contactme.ContactHistory
import java.time.ZonedDateTime
import kotlin.properties.Delegates

class Back4appContactResult {
    var createdAt: ZonedDateTime by Delegates.notNull()
    var updatedAt: ZonedDateTime by Delegates.notNull()
    var fullName: String by Delegates.notNull()
    var email: String by Delegates.notNull()
    var messageBody: String by Delegates.notNull()

    fun toContactHistory(): ContactHistory {
        return ContactHistory().apply {
            this.creationTimestamp = this@Back4appContactResult.createdAt.inLocalTimeZone()
            this.email = this@Back4appContactResult.email
            this.fullName = this@Back4appContactResult.fullName
            this.messageBody = this@Back4appContactResult.messageBody
        }
    }
}