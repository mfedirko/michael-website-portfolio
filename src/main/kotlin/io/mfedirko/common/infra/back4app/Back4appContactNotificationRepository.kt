package io.mfedirko.common.infra.back4app

import io.mfedirko.common.OrderDir
import io.mfedirko.common.infra.back4app.Back4appContactResult.Companion.CREATED_AT
import io.mfedirko.common.infra.back4app.Back4appContactResult.Companion.UPDATED_AT
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.between
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.orderBy
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.where
import io.mfedirko.common.util.Dates.inLocalTimeZone
import io.mfedirko.common.util.Dates.inUtcTimeZone
import io.mfedirko.common.util.Dates.toUtcEndOfDay
import io.mfedirko.common.util.Dates.toUtcStartOfDay
import io.mfedirko.contactme.*
import io.mfedirko.contactme.ContactHistorySpec.OrderBy.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
@Profile("back4app")
class Back4appContactNotificationRepository(
    @Qualifier("back4appTemplate") restTemplateBuilder: RestTemplateBuilder,
) : ContactNotificationRepository {
    private val restTemplate: RestTemplate
    init {
        restTemplate = restTemplateBuilder.build()
    }

    override fun findLastNotificationTime(): LocalDateTime {
        return findLastContactNotification()?.updatedAt?.inLocalTimeZone()
            ?: LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)
    }

    private fun findLastContactNotification() = restTemplate.getForEntity(
            "/classes/ContactNotification",
            ContactNotificationResults::class.java
        ).body?.results?.firstOrNull()

    override fun updateLastNotificationTime() {
        val lastNotification = findLastContactNotification()
        if (lastNotification == null) {
            restTemplate.postForLocation(
                "/classes/ContactNotification",
                Back4appContactNotification()
            )
        } else {
            restTemplate.put(
                "/classes/ContactNotification/{id}",
                Back4appContactNotification(),
                lastNotification.objectId
            )
        }
    }

    internal class ContactNotificationResults: Back4appResults<Back4appContactNotificationResult>()
}