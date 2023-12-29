package io.mfedirko.common.infra.dynamodb

import io.mfedirko.common.util.DateHelper
import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.LearningRepository
import io.mfedirko.learning.Lesson
import io.mfedirko.learning.UpdateLessonForm
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.time.*
import java.util.*

@Repository
@Profile("!mock")
@CacheConfig(cacheNames = ["lessons"])
class DynamoDbLearningRepository(
    private val enhancedClient: DynamoDbEnhancedClient,
    private val lessonMapper: DynamoLessonMapper,
) : LearningRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val table: DynamoDbTable<DynamoLesson>
        get() = enhancedClient.table(DynamoLesson.TABLE, TableSchema.fromBean(DynamoLesson::class.java))

    @Cacheable
    override fun findLessons(year: Year): List<Lesson> {
        log.debug("Called findLessons for $year")
        val result = table.query { k: QueryEnhancedRequest.Builder ->
            k.scanIndexForward(false)
                .queryConditional(
                    QueryConditional.sortBetween(
                        toKey(DateHelper.toUtcStartOfYear(year)),
                        toKey(DateHelper.toUtcEndOfYear(year))
                    )
                )
                .build()
        }
        return result.items()
            .map { lessonMapper.toLesson(it) }
            .toList()
    }

    override fun getLesson(creationTimeMillis: Long): Lesson {
        return table.getItem(toKey(creationTimeMillis))
            ?.let { lessonMapper.toLesson(it) }
            ?: throw IllegalArgumentException("No lesson exists with creationTimestampMillis $creationTimeMillis")
    }

    @CacheEvict(allEntries = true)
    override fun createLesson(lesson: CreateLessonForm): Long {
        val item = DynamoLesson.fromCreateRequest(lesson)
        table.putItem(item)
        return item.creationTimestampMillis!!
    }

    @CacheEvict(allEntries = true)
    override fun updateLesson(lesson: UpdateLessonForm, creationTimeMillis: Long) {
        val original = getLesson(creationTimeMillis)
        table.updateItem(DynamoLesson.fromUpdateRequest(lesson, original, creationTimeMillis))
    }

    @CacheEvict(allEntries = true)
    override fun deleteLesson(creationTimeMillis: Long) {
        table.deleteItem(toKey(creationTimeMillis))
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
            val instant = date.atZone(DateHelper.TZ_UTC).toInstant()
            return DynamoLesson.toSortKey(instant)
        }
    }
}