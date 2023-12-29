package io.mfedirko.common.util

import java.time.*
import java.util.*

object DateHelper {
    @JvmField
    val TZ_LOCAL: ZoneId = ZoneId.of("America/Chicago")

    @JvmField
    val TZ_UTC: ZoneId = ZoneId.of("UTC")

    @JvmStatic
    fun toLocalDatePageByDay(page: Int /* 1-indexed */, clock: Clock?): LocalDate {
        return LocalDate.now(clock).minusDays((page - 1).toLong())
    }

    fun toLocalYearByPage(page: Int /* 1-indexed */, clock: Clock?): Year {
        return Year.now(clock).minusYears((page - 1).toLong())
    }

    @JvmStatic
    fun toUtcStartOfDay(date: LocalDate): LocalDateTime {
        return date.atStartOfDay(TZ_LOCAL)
            .withZoneSameInstant(TZ_UTC).toLocalDateTime()
    }

    @JvmStatic
    fun toUtcEndOfDay(date: LocalDate): LocalDateTime {
        return date.atTime(23, 59, 59).atZone(TZ_LOCAL)
            .withZoneSameInstant(TZ_UTC).toLocalDateTime()
    }

    fun toUtcStartOfYear(year: Year): LocalDateTime {
        return year.atMonthDay(MonthDay.of(Month.JANUARY, 1))
            .atStartOfDay()
            .withMonth(1).withDayOfMonth(1)
            .atZone(TZ_LOCAL)
            .withZoneSameInstant(TZ_UTC).toLocalDateTime()
    }

    fun toUtcEndOfYear(year: Year): LocalDateTime {
        return year.atMonthDay(MonthDay.of(Month.DECEMBER, 31))
            .atTime(23, 59, 59)
            .withMonth(12).withDayOfMonth(31)
            .atZone(TZ_LOCAL)
            .withZoneSameInstant(TZ_UTC).toLocalDateTime()
    }

    fun inLocalTimeZone(date: LocalDate): LocalDate {
        return date.atStartOfDay(TZ_LOCAL).toLocalDate()
    }

    @JvmStatic
    fun toDate(date: LocalDate): Date {
        return Date.from(date.atStartOfDay(TZ_LOCAL).toInstant())
    }

    fun unixMillisToLocalDateTime(creationTimestampMillis: Long): LocalDateTime {
        return Instant.ofEpochMilli(creationTimestampMillis).atZone(
            TZ_LOCAL
        ).toLocalDateTime()
    }
}