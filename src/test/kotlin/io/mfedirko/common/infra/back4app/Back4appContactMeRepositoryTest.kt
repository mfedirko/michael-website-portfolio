package io.mfedirko.common.infra.back4app

import io.mfedirko.fixture.ContactForms
import io.mfedirko.learning.CreateLessonForm
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
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

}