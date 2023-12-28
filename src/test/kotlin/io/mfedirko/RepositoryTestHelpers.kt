package io.mfedirko

import org.assertj.core.api.Condition
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.function.Function

object RepositoryTestHelpers {
    val TZ_CHICAGO = ZoneId.of("America/Chicago")

    fun <T> sortedDescending(timestampGetter: (T) -> LocalDateTime): Condition<in List<T>> {
        return Condition({ items: List<T> ->
            val sorted: List<T> = ArrayList(items).sortedWith(compareByDescending(timestampGetter))
            sorted == items
        }, "List should be sorted descending by timestamp")
    }

    fun <T> withinDate(date: LocalDate, timestampGetter: (T) -> LocalDateTime): Condition<T> {
        val from = date.atStartOfDay(TZ_CHICAGO).toLocalDateTime()
        val to = date.atTime(23, 59, 59).atZone(TZ_CHICAGO).toLocalDateTime()
        return withinRange(from, to, timestampGetter)
    }

    fun <T> withinYear(year: Int, timestampGetter: (T) -> LocalDateTime): Condition<T> {
        val from = LocalDate.of(year, 1, 1).atStartOfDay(TZ_CHICAGO).toLocalDateTime()
        val to = LocalDate.of(year, 12, 31).atTime(23, 59, 59).atZone(TZ_CHICAGO).toLocalDateTime()
        return withinRange(from, to, timestampGetter)
    }

    fun <T> withinRange(
        from: LocalDateTime?,
        to: LocalDateTime?,
        timestampGetter: Function<T, LocalDateTime>
    ): Condition<T> {
        return Condition(
            { item: T ->
                (timestampGetter.apply(item).isAfter(from)
                        && timestampGetter.apply(item).isBefore(to))
            },
            "Should be within range $from - $to."
        )
    }
}