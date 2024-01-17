package io.mfedirko.common.infra.back4app

import com.fasterxml.jackson.databind.ObjectMapper
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.OrderDir
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.between
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.orderBy
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.where
import io.mfedirko.common.util.Dates.toUtcEndOfDay
import io.mfedirko.common.util.Dates.toUtcStartOfDay
import io.mfedirko.contactme.ContactForm
import io.mfedirko.contactme.ContactHistory
import io.mfedirko.contactme.ContactMeRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

@Repository
@Profile("back4app")
class Back4appContactMeRepository(
    @Qualifier("back4appTemplate") restTemplateBuilder: RestTemplateBuilder
) : ContactMeRepository {
    private val restTemplate: RestTemplate
    init {
        restTemplate = restTemplateBuilder.build()
    }

    override fun save(form: ContactForm) {
        restTemplate.postForEntity("/classes/ContactRequest", Back4appContactForm(form), String::class.java)
    }

    override fun findContactHistoryByDate(date: LocalDate): List<ContactHistory> {
        val resp = restTemplate.getForEntity(
            "/classes/ContactRequest?where={where}&order={order}",
            ContactResults::class.java,
            where(
                between("createdAt", date.toUtcStartOfDay(), date.toUtcEndOfDay())
            ),
            orderBy("updatedAt" to OrderDir.DESC)
        )
        return resp.body?.results?.map {
            it.toContactHistory()
        } ?: listOf()

    }


    internal class ContactResults: Back4appResults<Back4appContactResult>()

}