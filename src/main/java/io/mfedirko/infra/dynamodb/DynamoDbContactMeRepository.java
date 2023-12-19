package io.mfedirko.infra.dynamodb;

import io.mfedirko.contactme.ContactForm;
import io.mfedirko.contactme.ContactHistory;
import io.mfedirko.contactme.ContactMeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static io.mfedirko.infra.dynamodb.DynamoContactRequest.*;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBetween;

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
    public List<ContactHistory> findAllContactHistory() {
        PageIterable<DynamoContactRequest> result = getTable().scan();
        return result.items().stream()
                .map(DynamoContactRequest::toContactHistory)
                .toList();
    }

    @Override
    public List<ContactHistory> findContactHistoryByTimestampRange(Instant from, Instant to) {
        Objects.requireNonNull(from, "From timestamp must be non-null");
        Objects.requireNonNull(to, "To timestamp must be non-null");

        PageIterable<DynamoContactRequest> result = getTable().scan(k -> k.filterExpression(Expression.builder()
                .expression("#id BETWEEN :from AND :to")
                .putExpressionName("#id", ID)
                .putExpressionValue(":from", AttributeValue.fromN(String.valueOf(toPartitionKey(from))))
                .putExpressionValue(":to", AttributeValue.fromN(String.valueOf(toPartitionKey(to))))
                .build()));
        if (result == null) {
            log.warn("DynamoDB returned null for findContactHistoryByTimestampRange {}-{}", from, to);
            return Collections.emptyList();
        }
        return result.items().stream().map(DynamoContactRequest::toContactHistory)
                .sorted(Comparator.comparing(ContactHistory::getCreationTimestamp).reversed())
                .toList();
    }
    public DynamoDbTable<DynamoContactRequest> getTable() {
        return enhancedClient.table(DynamoContactRequest.TABLE, TableSchema.fromBean(DynamoContactRequest.class));
    }
}
