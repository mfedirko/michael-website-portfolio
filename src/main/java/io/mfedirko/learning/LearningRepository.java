package io.mfedirko.learning;

import java.time.LocalDate;
import java.util.List;

public interface LearningRepository {

    List<Lesson> findLessons(LocalDate date);

    void createLesson(CreateLessonForm lesson);

    void updateLesson(UpdateLessonForm lesson);
}
