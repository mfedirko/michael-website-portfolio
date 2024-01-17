package io.mfedirko.contactme

import java.time.LocalDate
import java.time.LocalDateTime

interface ContactMeRepository {
    fun save(form: ContactForm)
    fun update(vararg contactHistory: ContactHistory)

    @Deprecated(
        message = "Replaced by findContactHistory(ContactHistorySpec)",
        replaceWith = ReplaceWith(
            expression = "findContactHistory(ContactHistorySpec().apply { startDate = date; endDate = date })",
            imports = ["io.mfedirko.contactme.ContactHistorySpec"]
        )
    )
    fun findContactHistoryByDate(date: LocalDate): List<ContactHistory>
    fun findContactHistory(spec: ContactHistorySpec, limit: Int = 10000, offset: Int = 0): List<ContactHistory>
}