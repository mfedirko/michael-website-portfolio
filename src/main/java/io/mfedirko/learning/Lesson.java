package io.mfedirko.learning;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Getter
@Builder
@Value
@Jacksonized
public class Lesson {
    LocalDateTime creationTimestamp;
    long creationTimestampMillis;
    String author;
    String category;
    String title;
    String description;
    String parsedDescription;
}
