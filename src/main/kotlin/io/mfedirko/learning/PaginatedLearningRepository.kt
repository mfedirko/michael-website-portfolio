package io.mfedirko.learning

import org.springframework.cache.annotation.Cacheable

interface PaginatedLearningRepository : LearningRepository {
    @Cacheable
    fun findLessons(limit: Int, offset: Int = 0): List<Lesson>
}