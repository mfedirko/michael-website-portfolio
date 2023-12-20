package io.mfedirko.common.util;

import io.mfedirko.common.util.DateHelper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class DateHelperTest {
    Clock MAY_18_2023_CST = Clock.fixed(Instant.ofEpochMilli(1684462557000L), ZoneId.of("US/Eastern"));

    @ParameterizedTest
    @CsvSource(value = {
        "1,2023-05-18",
        "18,2023-05-01",
        "20,2023-04-29",
        "366,2022-05-18"
    })
    void toLocalDatePage(int page, LocalDate expected) {
        LocalDate localDate = DateHelper.toLocalDate(page, MAY_18_2023_CST);

        assertEquals(expected, localDate);
    }

    @ParameterizedTest
    @CsvSource({
            "2023-05-18,2023-05-18T05:00:00",
            "2023-11-30,2023-11-30T06:00:00",
            "2023-12-25,2023-12-25T06:00:00",
            "2023-01-01,2023-01-01T06:00:00"
    })
    void toUtcStartRange(LocalDate input, LocalDateTime expected) {
        LocalDateTime ldt = DateHelper.toUtcStartOfDay(input);

        assertEquals(expected, ldt);
    }

    @ParameterizedTest
    @CsvSource({
            "2023-05-18,2023-05-19T04:59:59",
            "2023-11-30,2023-12-01T05:59:59",
            "2023-12-25,2023-12-26T05:59:59",
            "2023-01-01,2023-01-02T05:59:59"
    })
    void toUtcEndRange(LocalDate input, LocalDateTime expected) {
        LocalDateTime ldt = DateHelper.toUtcEndOfDay(input);

        assertEquals(expected, ldt);
    }
}