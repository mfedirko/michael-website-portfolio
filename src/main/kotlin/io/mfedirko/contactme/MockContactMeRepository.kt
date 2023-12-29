package io.mfedirko.contactme

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
@Profile("mock")
class MockContactMeRepository : ContactMeRepository {
    private val requests: MutableList<ContactForm> = ArrayList()
    override fun save(form: ContactForm) {
        requests.add(form)
    }

    fun findAllContactHistory(): List<ContactHistory> {
        return requests.map {
            ContactHistory().apply {
                fullName = it.fullName
                email = it.email
                messageBody = it.messageBody
            }
        }.toList()
    }

    override fun findContactHistoryByDate(date: LocalDate): List<ContactHistory> {
        return findAllContactHistory()
    }
}