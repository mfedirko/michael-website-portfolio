package io.mfedirko.contactme

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.stream.Collectors

@Repository
@Profile("mock")
class MockContactMeRepository : ContactMeRepository {
    private val requests: MutableList<ContactForm> = ArrayList()
    override fun save(form: ContactForm) {
        requests.add(form)
    }

    fun findAllContactHistory(): List<ContactHistory> {
        return requests.stream().map { form: ContactForm ->
            ContactHistory().apply {
                fullName = form.fullName
                email = form.email
                messageBody = form.messageBody
            }
        }.collect(Collectors.toList())
    }

    override fun findContactHistoryByDate(date: LocalDate): List<ContactHistory> {
        return findAllContactHistory()
    }
}