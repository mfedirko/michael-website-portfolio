package io.mfedirko.common.infra.back4app

import io.mfedirko.common.OrderDir
import io.mfedirko.contactme.ContactHistory
import io.mfedirko.contactme.ContactHistorySpec
import io.mfedirko.fixture.ContactForms
import io.mfedirko.learning.CreateLessonForm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.Year

@SpringBootTest(classes = [
    Back4appTestConfiguration::class,
    Back4appContactMeRepository::class,
])
@ActiveProfiles("back4app")
internal class Back4appContactMeRepositoryTest {
    @Autowired
    private lateinit var repository: Back4appContactMeRepository

    @Test
    fun readAfterWrite() {
        repository.save(ContactForms.aContactForm())

        val results = repository.findContactHistoryByDate(LocalDate.now())

        assertEquals(1, results.size)
    }

    @Test
    fun sortedDescByTimeCreated() {
        val first = ContactForms.aContactForm().apply { email = "first@email.com" }
        val second = ContactForms.aContactForm().apply { email = "second@email.com" }
        repository.save(first)
        repository.save(second)

        val results = repository.findContactHistoryByDate(LocalDate.now())

        assertEquals(listOf("second@email.com", "first@email.com"), results.map { it.email }.take(2))
    }

    @Nested
    internal inner class Update {
        @Test
        fun updatesFields() {
            repository.save(ContactForms.aContactForm())
            val results = repository.findContactHistoryByDate(LocalDate.now())

            repository.update(*results.map { it.apply {
                fullName = "test full name"
                email = "testing@email.edu"
            } }.toTypedArray())

            val updated = repository.findContactHistoryByDate(LocalDate.now())
            assertAll(updated.map { hist ->
                Executable {
                    assertEquals("testing@email.edu", hist.email)
                    assertEquals("test full name", hist.fullName)
                }
            })
        }

        @Test
        fun manyUpdates() {
            repeat(50) { repository.save(ContactForms.aContactForm()) }
            updatesFields()
        }
    }

    @Nested
    internal inner class FindBySpec {
        @Test
        fun startDate() {

        }

        @Test
        fun endDate() {

        }


        @Test
        fun orderBy() {
            val first = ContactForms.aContactForm().apply { email = "yyy@email.com"; fullName = "H User" }
            val second = ContactForms.aContactForm().apply { email = "zzz@email.com"; fullName = "D User" }
            val third = ContactForms.aContactForm().apply { email = "yyy@email.com"; fullName = "B User" }
            repository.save(first)
            repository.save(second)
            repository.save(third)

            val spec = ContactHistorySpec().apply {
                orderBy = arrayOf(
                    ContactHistorySpec.OrderBy.EMAIL to OrderDir.DESC,
                    ContactHistorySpec.OrderBy.FULL_NAME to OrderDir.DESC
                )
            }
            val results = repository.findContactHistory(spec)

            assertEquals(listOf("D User", "H User", "B User"), results.map { it.fullName }.take(3))
        }
    }
}