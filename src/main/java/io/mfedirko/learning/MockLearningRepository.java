package io.mfedirko.learning;

import com.github.rjeschke.txtmark.Processor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("mock")
public class MockLearningRepository implements LearningRepository {
    private final List<Lesson> lessons = new ArrayList<>();

    @Override
    public List<Lesson> findLessons(Year date) {
        LocalDateTime startOfYear = date.atMonthDay(MonthDay.of(Month.JANUARY,1)).atStartOfDay();
        return lessons.stream()
                .filter(l -> l.getCreationTimestamp().isAfter(startOfYear))
                .sorted(Comparator.comparing(Lesson::getCreationTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public Lesson getLesson(long creationTimeMillis) {
        return lessons.stream()
                .filter(l -> l.getCreationTimestampMillis() == creationTimeMillis)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found!"));
    }

    @Override
    public long createLesson(CreateLessonForm lesson) {
        Lesson newLesson = Lesson.builder()
                .parsedDescription(Processor.process(lesson.getDescription()))
                .description(lesson.getDescription())
                .category(lesson.getCategory())
                .title(lesson.getTitle())
                .author("Michael Fedirko")
                .creationTimestamp(LocalDateTime.now())
                .creationTimestampMillis(Instant.now().toEpochMilli())
                .build();
        lessons.add(newLesson);
        return newLesson.getCreationTimestampMillis();
    }

    @Override
    public void updateLesson(UpdateLessonForm lesson, long id) {
        Lesson oldLesson = getLesson(id);
        Lesson newLesson = Lesson.builder()
                .parsedDescription(Processor.process(lesson.getDescription()))
                .description(lesson.getDescription())
                .category(lesson.getCategory())
                .title(lesson.getTitle())
                .author(oldLesson.getAuthor())
                .creationTimestamp(LocalDateTime.now())
                .creationTimestampMillis(Instant.now().toEpochMilli())
                .build();
        deleteLesson(id);
        lessons.add(newLesson);
    }

    @Override
    public void deleteLesson(long creationTimeMillis) {
        lessons.removeIf(l -> l.getCreationTimestampMillis() == creationTimeMillis);
    }
}
