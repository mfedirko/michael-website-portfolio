package io.mfedirko.common.infra.back4app

import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.Lesson
import io.mfedirko.learning.UpdateLessonForm

class Back4appLessonForm
    constructor(form: CreateLessonForm, author: String) {
    constructor(form: UpdateLessonForm, lesson: Lesson)
            : this(toCreateLessonForm(form, lesson), lesson.author)

    val title: String
    val author: String
    val category: String
    val description: String

    init {
        this.author = author
        this.title = form.title!!
        this.category = form.category!!
        this.description = form.description!!
    }

    companion object {
        fun toCreateLessonForm(form: UpdateLessonForm, lesson: Lesson): CreateLessonForm {
            return CreateLessonForm().apply {
                this.category = form.category ?: lesson.category
                this.description = form.description ?: lesson.description
                this.title = form.title ?: lesson.title
            }
        }
    }
}