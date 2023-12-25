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

import java.time.Year;
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
            Year year = Year.of(2023);
            Lesson lesson = repository.findLessons(year)
                    .get(0);
            UpdateLessonForm updateForm = UpdateLessonForm.builder()
                    .category(lesson.getCategory())
                    .description("MY NEW DESCRIPTION OF LESSON!")
                    .title("MY NEW TITLE of lessoin")
                    .build();

            repository.updateLesson(updateForm, lesson.getCreationTimestampMillis());

            List<Lesson> lessons = repository.findLessons(year);
            Assertions.assertThat(lessons)
                    .filteredOn(l -> l.getCreationTimestamp().equals(lesson.getCreationTimestamp()))
                    .hasSize(1)
                    .allMatch(l -> l.getDescription().equals(updateForm.getDescription())
                            && l.getTitle().equals(updateForm.getTitle()));
        }

        @Test
        void updateMergesOldFields() {
            Year year = Year.of(2023);
            Lesson lesson = repository.findLessons(year)
                    .get(0);
            Assertions.assertThat(lesson.getAuthor()).isNotEmpty();

            UpdateLessonForm updateForm = UpdateLessonForm.builder()
                    .title("MY NEW TITLE of lessoin")
                    .build();

            repository.updateLesson(updateForm, lesson.getCreationTimestampMillis());

            List<Lesson> lessons = repository.findLessons(year);
            Assertions.assertThat(lessons)
                    .filteredOn(l -> l.getCreationTimestamp().equals(lesson.getCreationTimestamp()))
                    .hasSize(1)
                    .allSatisfy(l -> Assertions.assertThat(l).hasNoNullFieldsOrProperties()
                            .hasFieldOrPropertyWithValue("title", updateForm.getTitle()));
        }


        @Test
        void deleteLesson() {
            Year year = Year.of(2023);
            Lesson lesson = repository.findLessons(year)
                    .get(0);

            repository.deleteLesson(lesson.getCreationTimestampMillis());

            List<Lesson> lessons = repository.findLessons(year);
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

            List<Lesson> lessons = repository.findLessons(Year.now());
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
                "2023",
                "2022"
        })
        void withinRange(Year year) {
            List<Lesson> lessons = repository.findLessons(year);

            Assertions.assertThat(lessons).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "2023",
                "2022"
        })
        void withinRangeOfYear(Year year) {
            List<Lesson> lessons = repository.findLessons(year);

            Assertions.assertThat(lessons).isNotEmpty().are(withinYear(year.getValue(), Lesson::getCreationTimestamp));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "2023",
                "2022"
        })
        void sortedDescendingByTimestamp(Year year) {
            List<Lesson> lessons = repository.findLessons(year);

            Assertions.assertThat(lessons).is(sortedDescending(Lesson::getCreationTimestamp));
        }
    }


}