package io.mfedirko.common.infra.back4app

import io.mfedirko.common.OrderDir
import io.mfedirko.common.infra.back4app.Back4appContactResult.Companion.CREATED_AT
import io.mfedirko.common.infra.back4app.Back4appContactResult.Companion.UPDATED_AT
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.between
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.orderBy
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.where
import io.mfedirko.common.util.Dates.TZ_UTC
import io.mfedirko.common.util.Dates.inLocalTimeZone
import io.mfedirko.common.util.Dates.inUtcTimeZone
import io.mfedirko.common.util.Dates.toUtcEndOfDay
import io.mfedirko.common.util.Dates.toUtcStartOfDay
import io.mfedirko.contactme.*
import io.mfedirko.contactme.ContactHistorySpec.OrderBy.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
@Profile("back4app")
class Back4appContactMeRepository(
    @Qualifier("back4appTemplate") restTemplateBuilder: RestTemplateBuilder,
    @Value("\${back4app.rate-limit-per-second}") private val rateLimitPerSec: Int
) : ContactMeRepository {
    private val restTemplate: RestTemplate
    init {
        restTemplate = restTemplateBuilder.build()
    }

    override fun save(form: ContactForm) {
        restTemplate.postForEntity("/classes/ContactRequest", Back4appContactForm(form), String::class.java)
    }

    override fun update(vararg contactHistory: ContactHistory) {
        val batches = contactHistory.toList().chunked(rateLimitPerSec)
        batches.forEachIndexed { i, batch ->
            batch.forEach { hist ->
                restTemplate.put("/classes/ContactRequest/{id}", Back4appContactForm(hist), hist.id)
            }
            if (i < batches.size - 1) Thread.sleep(1100) // rate limit on Parse API
        }
    }

    override fun findContactHistory(spec: ContactHistorySpec, limit: Int, offset: Int): List<ContactHistory> {
        val resp = restTemplate.getForEntity(
            "/classes/ContactRequest?where={where}&order={order}&limit={limit}&skip={skip}",
            ContactResults::class.java,
            spec.toWhere(),
            spec.toOrderBy(),
            limit,
            offset
        )
        return resp.body?.results?.map {
            it.toContactHistory()
        } ?: listOf()
    }


    private fun ContactHistorySpec.toWhere(): String {
        return where(
            between(CREATED_AT,
                (startDate?.inUtcTimeZone() ?: LocalDate.ofEpochDay(0)).toString(),
                (endDate?.inUtcTimeZone() ?: LocalDate.now(TZ_UTC).plusDays(2)).toString())
        )
    }

    private fun ContactHistorySpec.toOrderBy(): String {
        val sortBy = if (this.orderBy.isEmpty())
                        arrayOf(UPDATE_TIMESTAMP to OrderDir.DESC)
                     else this.orderBy
        return orderBy(
            *sortBy.map { (field, dir) ->
                val fieldName = when(field) {
                    CREATION_TIMESTAMP -> CREATED_AT
                    UPDATE_TIMESTAMP -> UPDATED_AT
                    EMAIL -> Back4appContactResult.EMAIL
                    FULL_NAME -> Back4appContactResult.FULL_NAME
                }
                fieldName to dir
            }.toTypedArray()
        )
    }

    override fun findContactHistoryByDate(date: LocalDate): List<ContactHistory> {
        val resp = restTemplate.getForEntity(
            "/classes/ContactRequest?where={where}&order={order}",
            ContactResults::class.java,
            where(
                between(CREATED_AT, date.toUtcStartOfDay(), date.toUtcEndOfDay())
            ),
            orderBy(UPDATED_AT to OrderDir.DESC)
        )
        return resp.body?.results?.map {
            it.toContactHistory()
        } ?: listOf()

    }

    internal class ContactResults: Back4appResults<Back4appContactResult>()
}