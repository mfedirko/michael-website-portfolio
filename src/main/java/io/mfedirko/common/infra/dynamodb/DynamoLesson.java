package io.mfedirko.common.infra.dynamodb;

import io.mfedirko.common.util.DateHelper;
import io.mfedirko.learning.CreateLessonForm;
import io.mfedirko.learning.Lesson;
import io.mfedirko.learning.UpdateLessonForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static io.mfedirko.common.util.DateHelper.TZ_LOCAL;
import static io.mfedirko.common.util.DateHelper.TZ_UTC;

@DynamoDbBean
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamoLesson {
    public static final String TABLE = "Lesson";
    public static final String ID = "id";
    public static final String CREATION_TIMESTAMP = "creation_timestamp";
    public static final String AUTHOR = "author";
    public static final String CATEGORY = "category";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    private static final String DEFAULT_AUTHOR = "Michael Fedirko";

    private String id;
    private Long creationTimestampMillis; // unix epoch timestamp in milliseconds
    private String author;
    private String category;
    private String title;
    private String description;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(ID)
    public String getId() {
        return id;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(CREATION_TIMESTAMP)
    public Long getCreationTimestampMillis() {
        return creationTimestampMillis;
    }

    @DynamoDbAttribute(AUTHOR)
    public String getAuthor() {
        return author;
    }

    @DynamoDbAttribute(CATEGORY)
    public String getCategory() {
        return category;
    }

    @DynamoDbAttribute(TITLE)
    public String getTitle() {
        return title;
    }

    @DynamoDbAttribute(DESCRIPTION)
    public String getDescription() {
        return description;
    }

    public Lesson toLesson() {
        return Lesson.builder()
                .description(description)
                .title(title)
                .category(category)
                .author(author)
                .creationTimestampMillis(creationTimestampMillis)
                .creationTimestamp(DateHelper.unixMillisToLocalDateTime(creationTimestampMillis))
                .build();
    }

    public static DynamoLesson fromUpdateRequest(UpdateLessonForm lesson) {
        LocalDate localDate = LocalDate.ofInstant(
                Instant.ofEpochMilli(lesson.getCreationTimestampMillis()),
                TZ_UTC);

        return DynamoLesson.builder()
                .description(lesson.getDescription())
                .title(lesson.getTitle())
                .category(lesson.getCategory())
                .id(toPartitionKey(localDate))
                .creationTimestampMillis(lesson.getCreationTimestampMillis())
                .build();
    }

    public static DynamoLesson fromCreateRequest(CreateLessonForm lesson) {
        return DynamoLesson.builder()
                .description(lesson.getDescription())
                .title(lesson.getTitle())
                .category(lesson.getCategory())
                .author(DEFAULT_AUTHOR)
                .id(toPartitionKey(LocalDate.now()))
                .creationTimestampMillis(toSortKey(Instant.now()))
                .build();
    }

    public static String toPartitionKey(LocalDate date) {
        return String.valueOf(date.getYear());
    }

    public static long toSortKey(Instant instant) {
        return instant.toEpochMilli();
    }
}
