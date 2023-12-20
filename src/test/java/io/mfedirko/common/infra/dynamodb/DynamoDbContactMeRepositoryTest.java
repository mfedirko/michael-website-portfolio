package io.mfedirko.common.infra.dynamodb;

import io.mfedirko.contactme.ContactHistory;
import io.mfedirko.DynamoDbTestConfiguration;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static io.mfedirko.fixture.ContactForms.aContactForm;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({DynamoDbTestConfiguration.class})
class DynamoDbContactMeRepositoryTest {
    @Autowired
    private DynamoDbContactMeRepository repository;

    @Nested
    class SaveContact {
        @Test
        void readAfterWrite() {
            String expectedMsg = "my test msg for SavedContact.readAfterWrite";
            LocalDate now = LocalDate.now();

            repository.save(aContactForm().messageBody(expectedMsg).build());
            List<ContactHistory> history = repository.findContactHistoryByDate(now);

            assertThat(history).anyMatch(h -> h.getMessageBody().equals(expectedMsg));
        }
    }

    @Nested
    class FindContactHistoryByDate {

        private static Stream<Arguments> dates() {
            return Stream.of(
                    Arguments.of(LocalDate.of(2023,5,18)),
                    Arguments.of(LocalDate.of(2023,7,14)),
                    Arguments.of(LocalDate.of(2023,12,4)),
                    Arguments.of(LocalDate.of(2022,12,11))
            );
        }

        @ParameterizedTest
        @MethodSource("dates")
        void withinSingleDateRange(LocalDate from) {
            List<ContactHistory> history = repository.findContactHistoryByDate(from);

            assertThat(history).isNotEmpty().are(withinRange(from));
        }

        @ParameterizedTest
        @MethodSource("dates")
        void sortedDescendingByTimestamp(LocalDate from) {
            List<ContactHistory> history = repository.findContactHistoryByDate(from);

            assertThat(history).isNotEmpty().is(sortedDescending());
        }

        @ParameterizedTest
        @MethodSource("dates")
        void noEmptyFields(LocalDate from) {
            List<ContactHistory> history = repository.findContactHistoryByDate(from);

            assertThat(history).isNotEmpty().allSatisfy(hist -> assertThat(hist).hasNoNullFieldsOrProperties());
        }

        private static Condition<ContactHistory> withinRange(LocalDate date) {
            ZoneId tzChicago = ZoneId.of("America/Chicago");
            LocalDateTime from = date.atStartOfDay(tzChicago).toLocalDateTime();
            LocalDateTime to = date.atTime(23,59,59).atZone(tzChicago).toLocalDateTime();
            return new Condition<>(contactHistory -> contactHistory.getCreationTimestamp().isAfter(from)
                    && contactHistory.getCreationTimestamp().isBefore(to),
                    "ContactHistory should be within range %s - %s.".formatted(from, to));
        }

        private static Condition<? super List<? extends ContactHistory>> sortedDescending() {
            return new Condition<>(history -> {
                List<? extends ContactHistory> sorted = new ArrayList<>(history);
                sorted.sort(Comparator.comparing(ContactHistory::getCreationTimestamp).reversed());
                return sorted.equals(history);
            }, "History list should be sorted descending by timestamp");
        }
    }
}