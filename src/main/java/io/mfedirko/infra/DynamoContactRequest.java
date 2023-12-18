package io.mfedirko.infra;


import io.mfedirko.contactme.ContactForm;
import io.mfedirko.contactme.ContactHistory;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.time.ZoneId;

@DynamoDbBean
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamoContactRequest {
    public static final String TABLE = "Contact-Requests";
    public static final String ID = "id";
    public static final String CREATION_TIMESTAMP = "creation_timestamp";
    public static final String FULL_NAME = "full_name";
    public static final String EMAIL = "email";
    public static final String MESSAGE_BODY = "message_body";

    private Long id;
    private Long creationTimestampMillis; // unix epoch timestamp in milliseconds
    private String fullName;
    private String email;
    private String messageBody;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(ID)
    public Long getId() {
        return id;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(CREATION_TIMESTAMP)
    public Long getCreationTimestampMillis() {
        return creationTimestampMillis;
    }

    @DynamoDbAttribute(FULL_NAME)
    public String getFullName() {
        return fullName;
    }

    @DynamoDbAttribute(EMAIL)
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute(MESSAGE_BODY)
    public String getMessageBody() {
        return messageBody;
    }

    public static DynamoContactRequest from(ContactForm form) {
        final Instant now = Instant.now();
        return DynamoContactRequest.builder()
                .fullName(form.getFullName())
                .email(form.getEmail())
                .messageBody(form.getMessageBody())
                .id(toPartitionKey(now))
                .creationTimestampMillis(toSortKey(now))
                .build();
    }

    public static long toSortKey(Instant now) { // sort by timestamp millis
        return now.toEpochMilli();
    }

    public static long toPartitionKey(Instant now) { // partition by day
        return now.getEpochSecond() / (60 * 60 * 24);
    }

    public ContactHistory toContactHistory() {
        return ContactHistory.builder()
                .fullName(fullName)
                .email(email)
                .messageBody(messageBody)
                .creationTimestamp(creationTimestampMillis == null
                        ? null
                        : Instant.ofEpochMilli(creationTimestampMillis).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }
}
