package io.mfedirko.common.infra.dynamodb

import com.github.rjeschke.txtmark.Processor
import io.mfedirko.common.util.Dates
import io.mfedirko.learning.Lesson

object DynamoLessonMapper {
    fun toLesson(dynamoLesson: DynamoLesson): Lesson {
        val rawDescription = dynamoLesson.description
        val mdParsedDescription = Processor.process(rawDescription)
        return Lesson().apply {
            parsedDescription = mdParsedDescription
            description = rawDescription!!
            title = dynamoLesson.title!!
            category = dynamoLesson.category!!
            author = dynamoLesson.author!!
            creationTimestampMillis = dynamoLesson.creationTimestampMillis!!
            creationTimestamp = Dates.unixMillisToLocalDateTime(dynamoLesson.creationTimestampMillis!!)
        }
    }
}