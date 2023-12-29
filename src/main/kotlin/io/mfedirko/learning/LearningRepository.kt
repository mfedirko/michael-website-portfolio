package io.mfedirko.learning

import java.time.Year

interface LearningRepository {
    fun findLessons(year: Year): List<Lesson>
    fun getLesson(creationTimeMillis: Long): Lesson?
    fun createLesson(lesson: CreateLessonForm): Long
    fun updateLesson(lesson: UpdateLessonForm, creationTimeMillis: Long)
    fun deleteLesson(creationTimeMillis: Long)
}