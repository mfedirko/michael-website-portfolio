package io.mfedirko.common.infra.dynamodb

import io.mfedirko.DynamoDbTestConfiguration
import io.mfedirko.RepositoryTestHelpers
import io.mfedirko.contactme.ContactHistory
import io.mfedirko.fixture.ContactForms
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.LocalDate
import java.util.stream.Stream

@SpringBootTest
@Import(DynamoDbTestConfiguration::class)
internal class DynamoDbContactMeRepositoryTest {
    @Autowired
    private lateinit var repository: DynamoDbContactMeRepository

    @Test
    fun readAfterWrite() {
        val expectedMsg = "my test msg for SavedContact.readAfterWrite"
        val now = LocalDate.now()
        repository.save(ContactForms.aContactForm().apply { messageBody = expectedMsg })
        val history = repository.findContactHistoryByDate(now)
        Assertions.assertThat(history).anyMatch { it?.messageBody == expectedMsg }
    }

    @ParameterizedTest
    @MethodSource("dates")
    fun withinSingleDateRange(from: LocalDate) {
        val history = repository.findContactHistoryByDate(from)
        Assertions.assertThat(history).isNotEmpty.are(
            RepositoryTestHelpers.withinDate(from) {
                it.creationTimestamp!!
            }
        )
    }

    @ParameterizedTest
    @MethodSource("dates")
    fun sortedDescendingByTimestamp(from: LocalDate) {
        val history = repository.findContactHistoryByDate(from)
        Assertions.assertThat(history).isNotEmpty.`is`(RepositoryTestHelpers.sortedDescending { it.creationTimestamp!! })
    }

    @ParameterizedTest
    @MethodSource("dates")
    fun noEmptyFields(from: LocalDate) {
        val history = repository.findContactHistoryByDate(from)
        Assertions.assertThat(history).isNotEmpty.allSatisfy { hist: ContactHistory? ->
            Assertions.assertThat(hist).hasNoNullFieldsOrProperties()
        }
    }


    companion object {
        @JvmStatic
        private fun dates(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(LocalDate.of(2023, 5, 18)),
                Arguments.of(LocalDate.of(2023, 7, 14)),
                Arguments.of(LocalDate.of(2023, 12, 4)),
                Arguments.of(LocalDate.of(2022, 12, 11))
            )
        }
    }
}