package io.mfedirko.common.infra.dynamodb

import com.github.rjeschke.txtmark.Processor
import io.mfedirko.common.util.DateHelper
import io.mfedirko.learning.Lesson
import org.springframework.stereotype.Component

@Component
class DynamoLessonMapper {
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
            creationTimestamp = DateHelper.unixMillisToLocalDateTime(dynamoLesson.creationTimestampMillis!!)
        }
    }
}