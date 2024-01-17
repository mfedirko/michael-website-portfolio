package io.mfedirko.contactme

import io.mfedirko.common.OrderDir
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

class ContactHistorySpec {
    var orderBy: Array<Pair<OrderBy, OrderDir>> = emptyArray()
    var startDate: LocalDateTime? = null
    var endDate: LocalDateTime? = null

    enum class OrderBy {
        CREATION_TIMESTAMP,
        UPDATE_TIMESTAMP,
        FULL_NAME,
        EMAIL,
    }
}