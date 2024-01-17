package io.mfedirko.learning

import com.github.rjeschke.txtmark.Processor
import io.mfedirko.common.util.Dates.toUtcStartOfYear
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.*

@Repository
@Profile("mock")
class MockLearningRepository : PaginatedLearningRepository {
    private val lessons: MutableList<Lesson> = ArrayList()

    override fun findLessons(limit: Int, offset: Int): List<Lesson> {
        return lessons
            .sortedWith(compareBy { it.creationTimestamp })
            .drop(offset)
            .take(limit)
            .toList()
    }

    override fun findLessons(year: Year): List<Lesson> {
        val startOfYear = year.toUtcStartOfYear()
        return lessons
            .filter { it.creationTimestamp.isAfter(startOfYear) }
            .sortedWith(compareBy { it.creationTimestamp })
            .toList()
    }

    override fun getLesson(id: Any): Lesson {
        return lessons.first { it.id == id }
    }

    override fun createLesson(lesson: CreateLessonForm): Any {
        val newLesson: Lesson = Lesson().apply {
            description = lesson.description!!
            parsedDescription = Processor.process(lesson.description)
            category = lesson.category!!
            title = lesson.title!!
            author = "Michael Fedirko"
            creationTimestamp = LocalDateTime.now()
            id = Instant.now().toEpochMilli()
        }
        lessons.add(newLesson)
        return newLesson.id
    }

    override fun updateLesson(lesson: UpdateLessonForm, id: Any) {
        val oldLesson = getLesson(id)
        val newLesson: Lesson = Lesson().apply {
            parsedDescription = Processor.process(lesson.description)
            description = lesson.description!!
            category = lesson.category!!
            title = lesson.title!!
            author = oldLesson.author
            creationTimestamp = LocalDateTime.now()
            this.id = Instant.now().toEpochMilli()
        }
        deleteLesson(id)
        lessons.add(newLesson)
    }

    override fun deleteLesson(id: Any) {
        lessons.removeIf { it.id == id }
    }
}