package io.mfedirko.learning;

import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

public interface LearningRepository {

    List<Lesson> findLessons(Year year);

    Lesson getLesson(long creationTimeMillis);

    long createLesson(CreateLessonForm lesson);

    void updateLesson(UpdateLessonForm lesson, long creationTimeMillis);

    void deleteLesson(long creationTimeMillis);
}
