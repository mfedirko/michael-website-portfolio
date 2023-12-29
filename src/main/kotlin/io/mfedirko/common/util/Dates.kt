package io.mfedirko.common.util

import java.time.*
import java.util.*

object Dates {
    val TZ_LOCAL: ZoneId = ZoneId.of("America/Chicago")
    val TZ_UTC: ZoneId = ZoneId.of("UTC")

    fun toLocalDatePageByDay(page: Int /* 1-indexed */, clock: Clock?): LocalDate {
        return LocalDate.now(clock).minusDays((page - 1).toLong())
    }

    fun toLocalYearByPage(page: Int /* 1-indexed */, clock: Clock?): Year {
        return Year.now(clock).minusYears((page - 1).toLong())
    }

    fun LocalDate.toUtcStartOfDay(): LocalDateTime {
        return this.atStartOfDay(TZ_LOCAL)
            .withZoneSameInstant(TZ_UTC).toLocalDateTime()
    }

    fun LocalDate.toUtcEndOfDay(): LocalDateTime {
        return this.atTime(23, 59, 59).atZone(TZ_LOCAL)
            .withZoneSameInstant(TZ_UTC).toLocalDateTime()
    }

    fun Year.toUtcStartOfYear(): LocalDateTime {
        return this.atMonthDay(MonthDay.of(Month.JANUARY, 1))
            .atStartOfDay()
            .withMonth(1).withDayOfMonth(1)
            .atZone(TZ_LOCAL)
            .withZoneSameInstant(TZ_UTC).toLocalDateTime()
    }

    fun Year.toUtcEndOfYear(): LocalDateTime {
        return this.atMonthDay(MonthDay.of(Month.DECEMBER, 31))
            .atTime(23, 59, 59)
            .withMonth(12).withDayOfMonth(31)
            .atZone(TZ_LOCAL)
            .withZoneSameInstant(TZ_UTC).toLocalDateTime()
    }

    fun LocalDate.inLocalTimeZone(): LocalDate = this.atStartOfDay(TZ_LOCAL).toLocalDate()

    fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(TZ_LOCAL).toInstant())

    fun unixMillisToLocalDateTime(creationTimestampMillis: Long): LocalDateTime {
        return Instant.ofEpochMilli(creationTimestampMillis).atZone(TZ_LOCAL).toLocalDateTime()
    }
}