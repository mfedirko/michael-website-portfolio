package io.mfedirko.common.infra.back4app

import io.mfedirko.fixture.ContactForms
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

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
}