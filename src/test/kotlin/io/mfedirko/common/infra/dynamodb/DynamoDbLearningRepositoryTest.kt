package io.mfedirko.common.infra.dynamodb

import io.mfedirko.DynamoDbTestConfiguration
import io.mfedirko.RepositoryTestHelpers
import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.Lesson
import io.mfedirko.learning.UpdateLessonForm
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.Year

@SpringBootTest
@Import(DynamoDbTestConfiguration::class)
internal class DynamoDbLearningRepositoryTest {
    @Autowired
    private lateinit var repository: DynamoDbLearningRepository

    @Nested
    internal inner class SaveLessons {
        @Test
        fun updateExisting() {
            val year = Year.of(2023)
            val lesson = repository.findLessons(year)[0]
            val updateForm = UpdateLessonForm().apply {
                category = lesson.category
                description = "MY NEW DESCRIPTION OF LESSON!"
                title = "MY NEW TITLE of lessoin"
            }
            repository.updateLesson(updateForm, lesson.creationTimestampMillis)
            val lessons = repository.findLessons(year)
            Assertions.assertThat(lessons)
                .filteredOn { l: Lesson -> l.creationTimestamp == lesson.creationTimestamp }
                .hasSize(1)
                .allMatch { l: Lesson -> l.description == updateForm.description && l.title == updateForm.title }
        }

        @Test
        fun updateMergesOldFields() {
            val year = Year.of(2023)
            val lesson = repository.findLessons(year)[0]
            Assertions.assertThat(lesson.author).isNotEmpty
            val updateForm = UpdateLessonForm.fromLesson(lesson).apply {
                title = "MY NEW TITLE of lessoin"
            }
            repository.updateLesson(updateForm, lesson.creationTimestampMillis)
            val lessons = repository.findLessons(year)
            Assertions.assertThat(lessons)
                .filteredOn { l: Lesson -> l.creationTimestamp == lesson.creationTimestamp }
                .hasSize(1)
                .allSatisfy { l: Lesson ->
                    Assertions.assertThat(l).hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("title", updateForm.title)
                }
        }

        @Test
        fun deleteLesson() {
            val year = Year.of(2023)
            val lesson = repository.findLessons(year)[0]
            repository.deleteLesson(lesson.creationTimestampMillis)
            val lessons = repository.findLessons(year)
            Assertions.assertThat(lessons)
                .filteredOn { l: Lesson -> l.creationTimestamp == lesson.creationTimestamp }
                .isEmpty()
        }

        @Test
        fun createNew() {
            val form = CreateLessonForm().apply {
                category = "NEW CATEGORY"
                description = "MY EXPETED DESCRIPTION"
                title = "MY TITLE FOR NEW LESSON"
            }
            repository.createLesson(form)
            val lessons = repository.findLessons(Year.now())
            Assertions.assertThat(lessons)
                .filteredOn { l: Lesson -> l.category == form.category && l.description == form.description && l.title == form.title }
                .hasSize(1)
        }
    }

    @Nested
    internal inner class FindLessons {
        @ParameterizedTest
        @ValueSource(strings = ["2023", "2022"])
        fun withinRange(year: Year) {
            val lessons = repository.findLessons(year)
            Assertions.assertThat(lessons).isNotEmpty
        }

        @ParameterizedTest
        @ValueSource(strings = ["2023", "2022"])
        fun withinRangeOfYear(year: Year) {
            val lessons = repository.findLessons(year)
            Assertions.assertThat(lessons).isNotEmpty.are(RepositoryTestHelpers.withinYear(year.value) { obj: Lesson -> obj.creationTimestamp })
        }

        @ParameterizedTest
        @ValueSource(strings = ["2023", "2022"])
        fun sortedDescendingByTimestamp(year: Year) {
            val lessons = repository.findLessons(year)
            Assertions.assertThat(lessons)
                .`is`(RepositoryTestHelpers.sortedDescending { obj: Lesson -> obj.creationTimestamp })
        }
    }
}