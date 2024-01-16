package io.mfedirko.learning

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import java.time.Year

interface LearningRepository {
    @Cacheable
    fun findLessons(year: Year): List<Lesson>
    fun getLesson(id: Any): Lesson?
    @CacheEvict(allEntries = true)
    fun createLesson(lesson: CreateLessonForm): Any
    @CacheEvict(allEntries = true)
    fun updateLesson(lesson: UpdateLessonForm, id: Any)
    @CacheEvict(allEntries = true)
    fun deleteLesson(id: Any)
}