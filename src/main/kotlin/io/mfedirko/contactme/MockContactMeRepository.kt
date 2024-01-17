package io.mfedirko.contactme

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
@Profile("mock")
class MockContactMeRepository : ContactMeRepository {
    private val requests: MutableList<ContactHistory> = ArrayList()
    override fun save(form: ContactForm) {
        requests.add(form.toContactHistory())
    }

    override fun update(vararg contactHistory: ContactHistory) {
        contactHistory.forEach { hist ->
            requests.firstOrNull { it.id == hist.id }
                ?.let {
                    it.fullName = hist.fullName
                    it.email = hist.email
                    it.messageBody = hist.messageBody
                }
                ?: requests.add(hist)
        }
    }

    fun findAllContactHistory(): List<ContactHistory> {
        return requests.toList()
    }

    override fun findContactHistoryByDate(date: LocalDate): List<ContactHistory> {
        return findAllContactHistory()
    }

    override fun findContactHistory(spec: ContactHistorySpec, limit: Int, offset: Int): List<ContactHistory> {
        return findAllContactHistory()
    }

}