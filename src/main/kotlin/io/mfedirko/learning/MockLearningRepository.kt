package io.mfedirko.learning

import com.github.rjeschke.txtmark.Processor
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.*
import java.util.stream.Collectors

@Repository
@Profile("mock")
class MockLearningRepository : LearningRepository {
    private val lessons: MutableList<Lesson> = ArrayList()
    override fun findLessons(year: Year): List<Lesson> {
        val startOfYear = year.atMonthDay(MonthDay.of(Month.JANUARY, 1)).atStartOfDay()
        return lessons.stream()
            .filter { l: Lesson -> l.creationTimestamp.isAfter(startOfYear) }
            .sorted(Comparator.comparing(Lesson::creationTimestamp))
            .collect(Collectors.toList())
    }

    override fun getLesson(creationTimeMillis: Long): Lesson {
        return lessons.stream()
            .filter { l: Lesson -> l.creationTimestampMillis == creationTimeMillis }
            .findFirst()
            .orElseThrow { IllegalArgumentException("Lesson not found!") }
    }

    override fun createLesson(lesson: CreateLessonForm): Long {
        val newLesson: Lesson = Lesson().apply {
            description = lesson.description!!
            parsedDescription = Processor.process(lesson.description)
            category = lesson.category!!
            title = lesson.title!!
            author = "Michael Fedirko"
            creationTimestamp = LocalDateTime.now()
            creationTimestampMillis = Instant.now().toEpochMilli()
        }
        lessons.add(newLesson)
        return newLesson.creationTimestampMillis
    }

    override fun updateLesson(lesson: UpdateLessonForm, creationTimeMillis: Long) {
        val oldLesson = getLesson(creationTimeMillis)
        val newLesson: Lesson = Lesson().apply {
            parsedDescription = Processor.process(lesson.description)
            description = lesson.description!!
            category = lesson.category!!
            title = lesson.title!!
            author = oldLesson.author
            creationTimestamp = LocalDateTime.now()
            creationTimestampMillis = Instant.now().toEpochMilli()
        }
        deleteLesson(creationTimeMillis)
        lessons.add(newLesson)
    }

    override fun deleteLesson(creationTimeMillis: Long) {
        lessons.removeIf { l: Lesson -> l.creationTimestampMillis == creationTimeMillis }
    }
}