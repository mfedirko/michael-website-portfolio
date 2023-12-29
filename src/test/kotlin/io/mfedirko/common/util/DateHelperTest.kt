package io.mfedirko.common.util

import io.mfedirko.common.util.DateHelper.toLocalDatePageByDay
import io.mfedirko.common.util.DateHelper.toUtcEndOfDay
import io.mfedirko.common.util.DateHelper.toUtcStartOfDay
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.*

internal class DateHelperTest {
    private var MAY_18_2023_CST: Clock = Clock.fixed(Instant.ofEpochMilli(1684462557000L), ZoneId.of("US/Eastern"))

    @ParameterizedTest
    @CsvSource(value = ["1,2023-05-18", "18,2023-05-01", "20,2023-04-29", "366,2022-05-18"])
    fun toLocalDatePage(page: Int, expected: LocalDate) {
        val localDate = toLocalDatePageByDay(page, MAY_18_2023_CST)
        Assertions.assertEquals(expected, localDate)
    }

    @ParameterizedTest
    @CsvSource(
        "2023-05-18,2023-05-18T05:00:00",
        "2023-11-30,2023-11-30T06:00:00",
        "2023-12-25,2023-12-25T06:00:00",
        "2023-01-01,2023-01-01T06:00:00"
    )
    fun toUtcStartRange(input: LocalDate, expected: LocalDateTime) {
        val ldt = toUtcStartOfDay(input)
        Assertions.assertEquals(expected, ldt)
    }

    @ParameterizedTest
    @CsvSource(
        "2023-05-18,2023-05-19T04:59:59",
        "2023-11-30,2023-12-01T05:59:59",
        "2023-12-25,2023-12-26T05:59:59",
        "2023-01-01,2023-01-02T05:59:59"
    )
    fun toUtcEndRange(input: LocalDate, expected: LocalDateTime) {
        val ldt = toUtcEndOfDay(input)
        Assertions.assertEquals(expected, ldt)
    }
}