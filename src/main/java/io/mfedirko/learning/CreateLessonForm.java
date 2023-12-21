package io.mfedirko.learning;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Value
@Jacksonized
public class CreateLessonForm {
    @NotEmpty
    String category;

    @NotEmpty
    String title;

    @NotEmpty
    String description;
}
