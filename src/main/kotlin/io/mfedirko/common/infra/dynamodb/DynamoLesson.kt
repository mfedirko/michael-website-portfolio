package io.mfedirko.common.infra.dynamodb

import io.mfedirko.common.util.Dates
import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.Lesson
import io.mfedirko.learning.UpdateLessonForm
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.Instant
import java.time.LocalDate
import kotlin.properties.Delegates

@DynamoDbBean
class DynamoLesson {
    @get:DynamoDbAttribute(ID)
    @get:DynamoDbPartitionKey
    var id: String by Delegates.notNull()

    @get:DynamoDbAttribute(CREATION_TIMESTAMP)
    @get:DynamoDbSortKey
    var creationTimestampMillis // unix epoch timestamp in milliseconds
            : Long by Delegates.notNull()

    @get:DynamoDbAttribute(AUTHOR)
    var author: String by Delegates.notNull()

    @get:DynamoDbAttribute(CATEGORY)
    var category: String by Delegates.notNull()

    @get:DynamoDbAttribute(TITLE)
    var title: String by Delegates.notNull()

    @get:DynamoDbAttribute(DESCRIPTION)
    var description: String by Delegates.notNull()

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
                Dates.TZ_UTC
            )
            return DynamoLesson().apply {
                author = original.author
                description = update.description!!
                title = update.title!!
                category = update.category!!
                id = toPartitionKey(localDate)
                this.creationTimestampMillis = creationTimestampMillis
            }
        }

        fun fromCreateRequest(lesson: CreateLessonForm): DynamoLesson {
            return DynamoLesson().apply {
                description = lesson.description!!
                title = lesson.title!!
                category = lesson.category!!
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