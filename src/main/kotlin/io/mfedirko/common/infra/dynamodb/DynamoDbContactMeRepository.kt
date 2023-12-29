package io.mfedirko.common.infra.dynamodb

import io.mfedirko.common.util.DateHelper.TZ_UTC
import io.mfedirko.common.util.DateHelper.toUtcEndOfDay
import io.mfedirko.common.util.DateHelper.toUtcStartOfDay
import io.mfedirko.common.util.logger
import io.mfedirko.contactme.ContactForm
import io.mfedirko.contactme.ContactHistory
import io.mfedirko.contactme.ContactMeRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
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
    private val log = logger()

    private val table: DynamoDbTable<DynamoContactRequest>
        get() = enhancedClient.table(
            DynamoContactRequest.TABLE, TableSchema.fromBean(
                DynamoContactRequest::class.java
            )
        )

    override fun save(form: ContactForm) {
        table.putItem(DynamoContactRequest.from(form))
    }

    override fun findContactHistoryByDate(date: LocalDate): List<ContactHistory> {
        val result: PageIterable<DynamoContactRequest>? = table.query { query: QueryEnhancedRequest.Builder ->
            query.scanIndexForward(false)
                .queryConditional(
                    QueryConditional.sortBetween(
                        toKey(date.toUtcStartOfDay()),
                        toKey(date.toUtcEndOfDay())
                    )
                )
                .build()
        }

        return result?.items()
            ?.map { it.toContactHistory() }
            ?.toList()
            ?: emptyList()
    }


    companion object {
        private fun toKey(date: LocalDateTime): Key {
            return Key.builder()
                .partitionValue(DynamoContactRequest.toPartitionKey(date.toLocalDate()))
                .sortValue(toSortKey(date))
                .build()
        }

        private fun toSortKey(date: LocalDateTime): Long {
            val instant = date.atZone(TZ_UTC).toInstant()
            return DynamoContactRequest.toSortKey(instant)
        }
    }
}