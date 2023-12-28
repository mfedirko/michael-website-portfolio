package io.mfedirko.learning

import jakarta.validation.constraints.NotEmpty

class CreateLessonForm {
    @NotEmpty
    var category: String? = null

    @NotEmpty
    var title: String? = null

    @NotEmpty
    var description: String? = null
}