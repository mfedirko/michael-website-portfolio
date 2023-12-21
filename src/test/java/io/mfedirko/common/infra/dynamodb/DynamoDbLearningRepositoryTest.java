package io.mfedirko.common.infra.dynamodb;

import io.mfedirko.DynamoDbTestConfiguration;
import io.mfedirko.learning.CreateLessonForm;
import io.mfedirko.learning.Lesson;
import io.mfedirko.learning.UpdateLessonForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static io.mfedirko.RepositoryTestHelpers.sortedDescending;
import static io.mfedirko.RepositoryTestHelpers.withinYear;

@SpringBootTest
@Import({DynamoDbTestConfiguration.class})
class DynamoDbLearningRepositoryTest {
    @Autowired
    private DynamoDbLearningRepository repository;


    @Nested
    class SaveLessons {
        @Test
        void updateExisting() {
            LocalDate date = LocalDate.of(2023, 1, 1);
            Lesson lesson = repository.findLessons(date)
                    .get(0);
            UpdateLessonForm updateForm = UpdateLessonForm.builder()
                    .category(lesson.getCategory())
                    .description("MY NEW DESCRIPTION OF LESSON!")
                    .title("MY NEW TITLE of lessoin")
                    .creationTimestamp(lesson.getCreationTimestamp())
                    .creationTimestampMillis(lesson.getCreationTimestampMillis())
                    .build();

            repository.updateLesson(updateForm);

            List<Lesson> lessons = repository.findLessons(date);
            Assertions.assertThat(lessons)
                    .filteredOn(l -> l.getCreationTimestamp().equals(lesson.getCreationTimestamp()))
                    .hasSize(1)
                    .allMatch(l -> l.getDescription().equals(updateForm.getDescription())
                            && l.getTitle().equals(updateForm.getTitle()));
        }

        @Test
        void deleteLesson() {
            LocalDate date = LocalDate.of(2023, 1, 1);
            Lesson lesson = repository.findLessons(date)
                    .get(0);

            repository.deleteLesson(lesson.getCreationTimestampMillis());

            List<Lesson> lessons = repository.findLessons(date);
            Assertions.assertThat(lessons)
                    .filteredOn(l -> l.getCreationTimestamp().equals(lesson.getCreationTimestamp()))
                    .isEmpty();
        }

        @Test
        void createNew() {
            CreateLessonForm form = CreateLessonForm.builder()
                    .category("NEW CATEGORY")
                    .description("MY EXPETED DESCRIPTION")
                    .title("MY TITLE FOR NEW LESSON")
                    .build();

            repository.createLesson(form);

            List<Lesson> lessons = repository.findLessons(LocalDate.now());
            Assertions.assertThat(lessons)
                    .filteredOn(l -> l.getCategory().equals(form.getCategory())
                        && l.getDescription().equals(form.getDescription())
                        && l.getTitle().equals(form.getTitle()))
                    .hasSize(1);
        }
    }

    @Nested
    class FindLessons {
        @ParameterizedTest
        @ValueSource(strings = {
                "2023-12-14",
                "2022-05-09"
        })
        void withinRange(LocalDate date) {
            List<Lesson> lessons = repository.findLessons(date);

            Assertions.assertThat(lessons).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "2023-12-14",
                "2022-05-09",
                "2022-11-23",
                "2023-01-01",
        })
        void withinRangeOfYear(LocalDate date) {
            List<Lesson> lessons = repository.findLessons(date);

            Assertions.assertThat(lessons).isNotEmpty().are(withinYear(date.getYear(), Lesson::getCreationTimestamp));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "2023-12-14",
                "2022-05-09",
                "2022-11-23",
                "2023-01-01",
        })
        void sortedDescendingByTimestamp(LocalDate date) {
            List<Lesson> lessons = repository.findLessons(date);

            Assertions.assertThat(lessons).is(sortedDescending(Lesson::getCreationTimestamp));
        }
    }


}