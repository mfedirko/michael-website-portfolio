package io.mfedirko.common.infra.back4app

import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.UpdateLessonForm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.test.context.ActiveProfiles
import java.time.Year

private const val MOCK_AUTHOR = "Author Name"

@SpringBootTest(classes = [
    Back4appTestConfiguration::class,
    Back4appLearningRepository::class,
])
@ActiveProfiles("back4app")
@ExtendWith(MockitoExtension::class)
internal class Back4appLearningRepositoryTest {
    @Autowired
    private lateinit var repository: Back4appLearningRepository

    @Mock
    private lateinit var auth: Authentication


    private fun mockSecurityContext() {
        Mockito.`when`(auth.name).thenReturn(MOCK_AUTHOR)
        SecurityContextHolder.setContext(SecurityContextImpl(auth))
    }

    internal open inner class BaseTest {
        val createForm = CreateLessonForm().apply { category = "cat"; title = "title"; description = "my test desc" }
        lateinit var id: Any

        @BeforeEach
        fun setup() {
            mockSecurityContext()
            id = repository.createLesson(createForm)
        }

        @AfterEach
        fun cleanup() {
            repository.deleteLesson(id)
        }
    }



    @Nested
    internal inner class FindCreatedLesson : BaseTest() {
        @Test
        fun findByYear() {
            val results = repository.findLessons(Year.now())

            assertEquals(1, results.size)
        }

        @Test
        fun findById() {
            val results = repository.getLesson(id)

            assertNotNull(results)
        }

        @Test
        fun fieldsMatch() {
            val results = repository.getLesson(id)!!

            assertEquals(MOCK_AUTHOR, results.author)
            assertEquals(createForm.category, results.category)
            assertEquals(createForm.title, results.title)
            assertEquals(createForm.description, results.description)
        }

        @Test
        fun sortedDescByTimeCreated() {
            val second = CreateLessonForm().apply { category = "cat"; title = "second"; description = "my test desc 2" }
            val third = CreateLessonForm().apply { category = "cat"; title = "third"; description = "my test desc 3" }
            repository.createLesson(second)
            repository.createLesson(third)

            val results = repository.findLessons(Year.now())

            assertEquals(listOf("third", "second"), results.map { it.title }.take(2))
        }
    }

    @Nested
    internal inner class UpdateLesson : BaseTest() {
        @Test
        fun updatesField() {
            val updateLessonForm = UpdateLessonForm().apply { category = "new category"; title = "new title" }

            repository.updateLesson(updateLessonForm, id)

            val updated = repository.getLesson(id)!!
            assertEquals("new category", updated.category)
            assertEquals("new title", updated.title)
        }

        @Test
        fun doesNotUpdateUnchangedFields() {
            val updateLessonForm = UpdateLessonForm().apply { category = "new category" }
            val original = repository.getLesson(id)!!

            repository.updateLesson(updateLessonForm, id)

            val updated = repository.getLesson(id)!!
            assertEquals(original.author, updated.author)
            assertEquals(original.title, updated.title)
            assertEquals(original.description, updated.description)
            assertEquals(original.parsedDescription, updated.parsedDescription)
        }
    }

    @Nested
    internal inner class DeleteLesson : BaseTest() {
        @Test
        fun deletesLesson() {
            assertNotNull(repository.getLesson(id))

            repository.deleteLesson(id)

            assertNull(repository.getLesson(id))
        }
    }
}