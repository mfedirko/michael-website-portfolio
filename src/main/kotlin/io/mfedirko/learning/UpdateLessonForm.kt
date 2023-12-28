package io.mfedirko.learning

import jakarta.validation.constraints.NotEmpty

class UpdateLessonForm {
    @NotEmpty
    var category: String? = null

    @NotEmpty
    var title: String? = null

    @NotEmpty
    var description: String? = null

    companion object {
        @JvmStatic
        fun fromLesson(lesson: Lesson): UpdateLessonForm {
            return UpdateLessonForm().apply {
                title = lesson.title
                category = lesson.category
                description = lesson.description
            }
        }
    }
}