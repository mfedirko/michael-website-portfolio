package io.mfedirko.common.infra.dynamodb;

import com.github.rjeschke.txtmark.Processor;
import io.mfedirko.common.util.DateHelper;
import io.mfedirko.learning.Lesson;
import org.springframework.stereotype.Component;

@Component
public class DynamoLessonMapper {
    public Lesson toLesson(DynamoLesson dynamoLesson) {
        String rawDescription = dynamoLesson.getDescription();
        String mdParsedDescription = Processor.process(rawDescription);
        return Lesson.builder()
                .parsedDescription(mdParsedDescription)
                .description(rawDescription)
                .title(dynamoLesson.getTitle())
                .category(dynamoLesson.getCategory())
                .author(dynamoLesson.getAuthor())
                .creationTimestampMillis(dynamoLesson.getCreationTimestampMillis())
                .creationTimestamp(DateHelper.unixMillisToLocalDateTime(dynamoLesson.getCreationTimestampMillis()))
                .build();
    }
}
