package io.mfedirko.contactme

import java.time.LocalDate

interface ContactMeRepository {
    fun save(form: ContactForm)
    fun findContactHistoryByDate(date: LocalDate): List<ContactHistory>
}