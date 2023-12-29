package io.mfedirko.common.infra.dynamodb

import io.mfedirko.common.infra.dynamodb.DynamoContactRequest.Companion.from
import io.mfedirko.common.infra.dynamodb.DynamoContactRequest.Companion.toPartitionKey
import io.mfedirko.common.infra.dynamodb.DynamoContactRequest.Companion.toSortKey
import io.mfedirko.common.util.DateHelper.TZ_UTC
import io.mfedirko.common.util.DateHelper.toUtcEndOfDay
import io.mfedirko.common.util.DateHelper.toUtcStartOfDay
import io.mfedirko.contactme.ContactForm
import io.mfedirko.contactme.ContactHistory
import io.mfedirko.contactme.ContactMeRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Repository
@Profile("!mock")
class DynamoDbContactMeRepository(
    private val enhancedClient: DynamoDbEnhancedClient

) : ContactMeRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun save(form: ContactForm) {
        table.putItem(from(form))
    }

    override fun findContactHistoryByDate(date: LocalDate): List<ContactHistory> {
        val result = table.query { k: QueryEnhancedRequest.Builder ->
            k.scanIndexForward(false)
                .queryConditional(
                    QueryConditional.sortBetween(
                        toKey(toUtcStartOfDay(date)),
                        toKey(toUtcEndOfDay(date))
                    )
                )
                .build()
        }
        if (result == null) {
            log.warn("DynamoDB returned null for date $date")
            return emptyList()
        }
        return result.items().stream()
            .map { obj: DynamoContactRequest -> obj.toContactHistory() }
            .toList()
    }

    private val table: DynamoDbTable<DynamoContactRequest>
        get() = enhancedClient.table(
            DynamoContactRequest.TABLE, TableSchema.fromBean(
                DynamoContactRequest::class.java
            )
        )

    companion object {
        private fun toKey(date: LocalDateTime): Key {
            return Key.builder()
                .partitionValue(toPartitionKey(date.toLocalDate()))
                .sortValue(toSortKey(date))
                .build()
        }

        private fun toSortKey(date: LocalDateTime): Long {
            val instant = date.atZone(TZ_UTC).toInstant()
            return toSortKey(instant)
        }
    }
}