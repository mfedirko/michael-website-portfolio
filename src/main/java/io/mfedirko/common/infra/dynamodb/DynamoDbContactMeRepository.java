package io.mfedirko.common.infra.dynamodb;

import io.mfedirko.common.util.DateHelper;
import io.mfedirko.contactme.ContactForm;
import io.mfedirko.contactme.ContactHistory;
import io.mfedirko.contactme.ContactMeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.mfedirko.common.util.DateHelper.TZ_UTC;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBetween;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortGreaterThanOrEqualTo;

@Repository
@RequiredArgsConstructor
@Profile("!mock")
@Slf4j
public class DynamoDbContactMeRepository implements ContactMeRepository {
    private final DynamoDbEnhancedClient enhancedClient;

    @Override
    public void save(ContactForm form) {
        Objects.requireNonNull(form, "ContactForm must be non-null");

        getTable().putItem(DynamoContactRequest.from(form));
    }

    @Override
    public List<ContactHistory> findContactHistoryByDate(LocalDate date) {
        Objects.requireNonNull(date, "Date must be non-null");

        PageIterable<DynamoContactRequest> result = getTable().query(k -> k.scanIndexForward(false)
                .queryConditional(sortBetween(
                        toKey(DateHelper.toUtcStartOfDay(date)),
                        toKey(DateHelper.toUtcEndOfDay(date))))
                .build());
        if (result == null) {
            log.warn("DynamoDB returned null for date {}", date);
            return Collections.emptyList();
        }
        return result.items().stream()
                .map(DynamoContactRequest::toContactHistory)
                .toList();
    }
    private DynamoDbTable<DynamoContactRequest> getTable() {
        return enhancedClient.table(DynamoContactRequest.TABLE, TableSchema.fromBean(DynamoContactRequest.class));
    }

    private static Key toKey(LocalDateTime date) {
        return Key.builder()
                .partitionValue(DynamoContactRequest.toPartitionKey(date.toLocalDate()))
                .sortValue(toSortKey(date))
                .build();
    }

    private static long toSortKey(LocalDateTime date) {
        Instant instant = date.atZone(TZ_UTC).toInstant();
        return DynamoContactRequest.toSortKey(instant);
    }
}
