package io.mfedirko.common.infra.dynamodb

import io.mfedirko.common.util.Dates
import io.mfedirko.common.util.Dates.toUtcEndOfYear
import io.mfedirko.common.util.Dates.toUtcStartOfYear
import io.mfedirko.common.util.Logging.logger
import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.LearningRepository
import io.mfedirko.learning.Lesson
import io.mfedirko.learning.UpdateLessonForm
import org.springframework.cache.annotation.CacheConfig
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.time.*
import java.util.*

@Repository
@Profile("aws")
@CacheConfig(cacheNames = ["lessons"])
class DynamoDbLearningRepository(
    private val enhancedClient: DynamoDbEnhancedClient
) : LearningRepository {
    private val log = logger()

    private val table: DynamoDbTable<DynamoLesson>
        get() = enhancedClient.table(DynamoLesson.TABLE, TableSchema.fromBean(DynamoLesson::class.java))

    override fun findLessons(year: Year): List<Lesson> {
        log.debug("Called findLessons for {}", year)
        val result: PageIterable<DynamoLesson>? = table.query { k: QueryEnhancedRequest.Builder ->
            k.scanIndexForward(false)
                .queryConditional(
                    QueryConditional.sortBetween(
                        toKey(year.toUtcStartOfYear()),
                        toKey(year.toUtcEndOfYear())
                    )
                )
                .build()
        }
        return result?.items()
            ?.map { DynamoLessonMapper.toLesson(it) }
            ?.toList()
            ?: emptyList()
    }

    override fun getLesson(id: Any): Lesson {
        id as Long
        return table.getItem(toKey(id))
            ?.let { DynamoLessonMapper.toLesson(it) }
            ?: throw IllegalArgumentException("No lesson exists with creationTimestampMillis $id")
    }

    override fun createLesson(lesson: CreateLessonForm): Long {
        val item = DynamoLesson.fromCreateRequest(lesson)
        table.putItem(item)
        return item.creationTimestampMillis
    }

    override fun updateLesson(lesson: UpdateLessonForm, id: Any) {
        id as Long
        val original = getLesson(id)
        table.updateItem(DynamoLesson.fromUpdateRequest(lesson, original, id))
    }

    override fun deleteLesson(id: Any) {
        id as Long
        table.deleteItem(toKey(id))
    }


    companion object {
        private fun toKey(date: LocalDateTime): Key {
            return Key.builder()
                .partitionValue(DynamoLesson.toPartitionKey(date.toLocalDate()))
                .sortValue(toSortKey(date))
                .build()
        }

        private fun toKey(timeMillis: Long): Key {
            val date = LocalDate.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault())
            return Key.builder()
                .partitionValue(DynamoLesson.toPartitionKey(date))
                .sortValue(timeMillis)
                .build()
        }

        private fun toSortKey(date: LocalDateTime): Long {
            val instant = date.atZone(Dates.TZ_UTC).toInstant()
            return DynamoLesson.toSortKey(instant)
        }
    }
}