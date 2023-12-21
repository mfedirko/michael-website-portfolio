package io.mfedirko.learning;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Getter
@Builder
@Value
@Jacksonized
public class UpdateLessonForm {
    @NotNull
    LocalDateTime creationTimestamp;

    @NotNull
    Long creationTimestampMillis;

    @NotEmpty
    String category;

    @NotEmpty
    String title;

    @NotEmpty
    String description;
}
