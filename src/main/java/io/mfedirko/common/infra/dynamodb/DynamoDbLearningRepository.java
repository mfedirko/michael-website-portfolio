package io.mfedirko.common.infra.dynamodb;

import io.mfedirko.common.util.DateHelper;
import io.mfedirko.learning.CreateLessonForm;
import io.mfedirko.learning.LearningRepository;
import io.mfedirko.learning.Lesson;
import io.mfedirko.learning.UpdateLessonForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static io.mfedirko.common.util.DateHelper.TZ_UTC;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBetween;

@Repository
@RequiredArgsConstructor
@Profile("!mock")
@Slf4j
public class DynamoDbLearningRepository implements LearningRepository {
    private final DynamoDbEnhancedClient enhancedClient;

    @Override
    public List<Lesson> findLessons(LocalDate date) {
        PageIterable<DynamoLesson> result = getTable().query(k -> k.scanIndexForward(false)
                .queryConditional(sortBetween(
                        toKey(DateHelper.toUtcStartOfYear(date)),
                        toKey(DateHelper.toUtcEndOfYear(date))))
                .build());
        return result.items().stream()
                .map(DynamoLesson::toLesson)
                .toList();
    }

    @Override
    public Lesson getLesson(long creationTimeMillis) {
        return Optional.ofNullable(getTable().getItem(toKey(creationTimeMillis)))
                .map(DynamoLesson::toLesson)
                .orElseThrow(() -> new IllegalArgumentException("No lesson exists with creationTimestampMillis " + creationTimeMillis));
    }

    @Override
    public void createLesson(CreateLessonForm req) {
        getTable().putItem(DynamoLesson.fromCreateRequest(req));
    }

    @Override
    public void updateLesson(UpdateLessonForm req) {
        getTable().updateItem(DynamoLesson.fromUpdateRequest(req));
    }

    @Override
    public void deleteLesson(long creationTimeMillis) {
        getTable().deleteItem(toKey(creationTimeMillis));
    }


    private DynamoDbTable<DynamoLesson> getTable() {
        return enhancedClient.table(DynamoLesson.TABLE, TableSchema.fromBean(DynamoLesson.class));
    }

    private static Key toKey(LocalDateTime date) {
        return Key.builder()
                .partitionValue(DynamoLesson.toPartitionKey(date.toLocalDate()))
                .sortValue(toSortKey(date))
                .build();
    }

    private static Key toKey(long timeMillis) {
        LocalDate date = LocalDate.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
        return Key.builder()
                .partitionValue(DynamoLesson.toPartitionKey(date))
                .sortValue(timeMillis)
                .build();
    }

    private static long toSortKey(LocalDateTime date) {
        Instant instant = date.atZone(TZ_UTC).toInstant();
        return DynamoLesson.toSortKey(instant);
    }
}
