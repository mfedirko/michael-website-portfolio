package io.mfedirko.common.util;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.util.Date;

@UtilityClass
public class DateHelper {
    public static final ZoneId TZ_LOCAL = ZoneId.of("America/Chicago");
    public static final ZoneId TZ_UTC = ZoneId.of("UTC");

    public static LocalDate toLocalDatePageByDay(int page /* 1-indexed */, Clock clock) {
        return LocalDate.now(clock).minusDays(page - 1);
    }

    public static Year toLocalYearByPage(int page /* 1-indexed */, Clock clock) {
        return Year.now(clock).minusYears(page - 1);
    }

    public static LocalDateTime toUtcStartOfDay(LocalDate date) {
        return date.atStartOfDay(TZ_LOCAL)
                .withZoneSameInstant(TZ_UTC).toLocalDateTime();
    }

    public static LocalDateTime toUtcEndOfDay(LocalDate date) {
        return date.atTime(23,59,59).atZone(TZ_LOCAL)
                .withZoneSameInstant(TZ_UTC).toLocalDateTime();
    }

    public static LocalDateTime toUtcStartOfYear(Year year) {
        return year.atMonthDay(MonthDay.of(Month.JANUARY, 1))
                .atStartOfDay()
                .withMonth(1).withDayOfMonth(1)
                .atZone(TZ_LOCAL)
                .withZoneSameInstant(TZ_UTC).toLocalDateTime();
    }

    public static LocalDateTime toUtcEndOfYear(Year year) {
        return year.atMonthDay(MonthDay.of(Month.DECEMBER, 31))
                .atTime(23,59,59)
                .withMonth(12).withDayOfMonth(31)
                .atZone(TZ_LOCAL)
                .withZoneSameInstant(TZ_UTC).toLocalDateTime();
    }

    public static LocalDate inLocalTimeZone(LocalDate date) {
        return date.atStartOfDay(TZ_LOCAL).toLocalDate();
    }

    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(TZ_LOCAL).toInstant());
    }

    public static LocalDateTime unixMillisToLocalDateTime(Long creationTimestampMillis) {
        return creationTimestampMillis == null
                ? null
                : Instant.ofEpochMilli(creationTimestampMillis).atZone(TZ_LOCAL).toLocalDateTime();
    }
}
