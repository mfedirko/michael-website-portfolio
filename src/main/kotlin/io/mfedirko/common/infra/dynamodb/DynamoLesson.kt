package io.mfedirko.common.infra.dynamodb

import io.mfedirko.common.util.DateHelper
import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.Lesson
import io.mfedirko.learning.UpdateLessonForm
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.Instant
import java.time.LocalDate

@DynamoDbBean
class DynamoLesson {
    @get:DynamoDbAttribute(ID)
    @get:DynamoDbPartitionKey
    var id: String? = null

    @get:DynamoDbAttribute(CREATION_TIMESTAMP)
    @get:DynamoDbSortKey
    var creationTimestampMillis // unix epoch timestamp in milliseconds
            : Long? = null

    @get:DynamoDbAttribute(AUTHOR)
    var author: String? = null

    @get:DynamoDbAttribute(CATEGORY)
    var category: String? = null

    @get:DynamoDbAttribute(TITLE)
    var title: String? = null

    @get:DynamoDbAttribute(DESCRIPTION)
    var description: String? = null

    companion object {
        const val TABLE = "Lesson"
        const val ID = "id"
        const val CREATION_TIMESTAMP = "creation_timestamp"
        const val AUTHOR = "author"
        const val CATEGORY = "category"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        private const val DEFAULT_AUTHOR = "Michael Fedirko"
        fun fromUpdateRequest(update: UpdateLessonForm, original: Lesson, creationTimestampMillis: Long): DynamoLesson {
            val localDate = LocalDate.ofInstant(
                Instant.ofEpochMilli(creationTimestampMillis),
                DateHelper.TZ_UTC
            )
            return DynamoLesson().apply {
                author = original.author
                description = update.description
                title = update.title
                category = update.category
                id = toPartitionKey(localDate)
                this.creationTimestampMillis = creationTimestampMillis
            }
        }

        fun fromCreateRequest(lesson: CreateLessonForm): DynamoLesson {
            return DynamoLesson().apply {
                description = lesson.description
                title = lesson.title
                category = lesson.category
                author = DEFAULT_AUTHOR
                id = toPartitionKey(LocalDate.now())
                creationTimestampMillis = toSortKey(Instant.now())
            }
        }

        fun toPartitionKey(date: LocalDate): String {
            return date.year.toString()
        }

        fun toSortKey(instant: Instant): Long {
            return instant.toEpochMilli()
        }
    }
}