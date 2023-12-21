package io.mfedirko;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Condition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class RepositoryTestHelpers {
    public static final ZoneId TZ_CHICAGO = ZoneId.of("America/Chicago");

    public static <T> Condition<? super List<? extends T>> sortedDescending(Function<T, LocalDateTime> timestampGetter) {
        return new Condition<>(items -> {
            List<? extends T> sorted = new ArrayList<>(items);
            sorted.sort(Comparator.comparing(timestampGetter).reversed());
            return sorted.equals(items);
        }, "List should be sorted descending by timestamp");
    }


    public static <T> Condition<T> withinDate(LocalDate date, Function<T, LocalDateTime> timestampGetter) {
        LocalDateTime from = date.atStartOfDay(TZ_CHICAGO).toLocalDateTime();
        LocalDateTime to = date.atTime(23,59,59).atZone(TZ_CHICAGO).toLocalDateTime();
        return withinRange(from, to, timestampGetter);
    }

    public static <T> Condition<T> withinYear(int year, Function<T, LocalDateTime> timestampGetter) {
        LocalDateTime from = LocalDate.of(year, 1, 1).atStartOfDay(TZ_CHICAGO).toLocalDateTime();
        LocalDateTime to = LocalDate.of(year, 12, 31).atTime(23,59,59).atZone(TZ_CHICAGO).toLocalDateTime();
        return withinRange(from, to, timestampGetter);
    }

    public static <T> Condition<T> withinRange(LocalDateTime from, LocalDateTime to, Function<T, LocalDateTime> timestampGetter) {
        return new Condition<>(item -> timestampGetter.apply(item).isAfter(from)
                && timestampGetter.apply(item).isBefore(to),
                "Should be within range %s - %s.".formatted(from, to));
    }
}
