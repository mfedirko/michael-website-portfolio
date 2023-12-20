package io.mfedirko.admin;

import lombok.experimental.UtilityClass;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@UtilityClass
public class DateHelper {
    public static final ZoneId TZ_LOCAL = ZoneId.of("America/Chicago");
    public static final ZoneId TZ_UTC = ZoneId.of("UTC");

    public static LocalDate toLocalDate(int page /* 1-indexed */, Clock clock) {
        return LocalDate.now(clock).minusDays(page - 1);
    }

    public static LocalDateTime toUtcStartRange(LocalDate date) {
        return date.atStartOfDay(TZ_LOCAL)
                .withZoneSameInstant(TZ_UTC).toLocalDateTime();
    }

    public static LocalDateTime toUtcEndRange(LocalDate date) {
        return date.atTime(23,59,59).atZone(TZ_LOCAL)
                .withZoneSameInstant(TZ_UTC).toLocalDateTime();
    }

    public static LocalDate inLocalTimeZone(LocalDate date) {
        return date.atStartOfDay(TZ_LOCAL).toLocalDate();
    }

    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(TZ_LOCAL).toInstant());
    }
}
