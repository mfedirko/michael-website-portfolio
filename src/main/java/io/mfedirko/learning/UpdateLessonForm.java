package io.mfedirko.learning;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Getter
@Builder
@Value
@Jacksonized
public class UpdateLessonForm {
    LocalDateTime creationTimestamp;

    @With
    Long creationTimestampMillis;

    @NotEmpty
    String category;

    @NotEmpty
    String title;

    @NotEmpty
    String description;

    public static UpdateLessonForm fromLesson(Lesson lesson) {
        return UpdateLessonForm.builder()
                .creationTimestampMillis(lesson.getCreationTimestampMillis())
                .creationTimestamp(lesson.getCreationTimestamp())
                .title(lesson.getTitle())
                .category(lesson.getCategory())
                .description(lesson.getDescription())
                .build();
    }
}
