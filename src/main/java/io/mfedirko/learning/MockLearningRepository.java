package io.mfedirko.learning;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Repository
@Profile("mock")
public class MockLearningRepository implements LearningRepository {
    @Override
    public List<Lesson> findLessons(LocalDate date) {
        if (date.isBefore(LocalDate.now().minusDays(73))) {
            return Collections.emptyList();
        }
        return List.of(
                Lesson.builder()
                        .author("Michael Fedirko")
                        .category("Lessons from Building this Website")
                        .creationTimestamp(LocalDateTime.now())
                        .title("AWS Challenges")
                        .description("I encountered some challenge")
                        .build(),
                Lesson.builder()
                        .author("Michael Fedirko")
                        .category("Lessons from Building this Website")
                        .creationTimestamp(LocalDateTime.now().minusDays(5))
                        .title("Other Challenges")
                        .description("other")
                        .build(),
                Lesson.builder()
                        .author("Michael Fedirko")
                        .category("Lessons from Building this Website")
                        .creationTimestamp(LocalDateTime.now().minusDays(29))
                        .title("Hello")
                        .description("test 123")
                        .build()
        );
    }

    @Override
    public Lesson getLesson(long creationTimeMillis) {
        return Lesson.builder()
                .author("Michael Fedirko")
                .category("Lessons from Building this Website")
                .creationTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(creationTimeMillis), ZoneId.systemDefault()))
                .title("AWS Challenges")
                .description("I encountered some challenge")
                .build();
    }

    @Override
    public void createLesson(CreateLessonForm lesson) {

    }

    @Override
    public void updateLesson(UpdateLessonForm lesson, long id) {

    }

    @Override
    public void deleteLesson(long creationTimeMillis) {

    }
}
