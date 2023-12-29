package io.mfedirko.common.infra.dynamodb

import io.mfedirko.fixture.DynamoLessons
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DynamoLessonMapperTest {

    @Test
    fun allFieldsPopulated() {
        val dynamoLesson = DynamoLessons.DATA!![0]
        val lesson = DynamoLessonMapper.toLesson(dynamoLesson)
        Assertions.assertThat(lesson)
            .hasNoNullFieldsOrProperties()
            .usingRecursiveComparison().ignoringFields(
                "id", "creationTimestamp", "parsedDescription"
            ).ignoringFieldsMatchingRegexes(".*delegate")
            .isEqualTo(dynamoLesson)
    }

    @Nested
    internal inner class MarkdownDescription {
        @Test
        fun simpleParagraphNoMarkdown() {
            val rawDescription = "No markdown, paragraph; with punctuation. And spaces!"
            val dynamoLesson: DynamoLesson = aLesson()
                .apply {
                    description = rawDescription
                }
            val lesson = DynamoLessonMapper.toLesson(dynamoLesson)
            Assertions.assertThat(lesson.parsedDescription.trim { it <= ' ' })
                .isEqualTo("<p>$rawDescription</p>")
        }

        @Test
        fun headings() {
            val rawDescription: String = """
                # first
                ## second
                ### third
                #### fourth
            """.trimIndent()
            val dynamoLesson: DynamoLesson = aLesson()
                .apply {
                    description = rawDescription
                }
            val lesson = DynamoLessonMapper.toLesson(dynamoLesson)
            Assertions.assertThat(lesson.parsedDescription.trim { it <= ' ' })
                .containsSubsequence(
                    "<h1>", "first",
                    "<h2>", "second",
                    "<h3>", "third",
                    "<h4>", "fourth"
                )
        }

        @Test
        fun unorderedList() {
            val rawDescription: String = """
                - apple
                - orange
                - grapefruit
            """.trimIndent()
            val dynamoLesson: DynamoLesson = aLesson()
                .apply {
                    description = rawDescription
                }
            val lesson = DynamoLessonMapper.toLesson(dynamoLesson)
            Assertions.assertThat(lesson.parsedDescription.trim { it <= ' ' })
                .containsSubsequence(
                    "<ul>",
                    "<li>", "apple",
                    "<li>", "orange",
                    "<li>", "grapefruit",
                    "</ul>"
                )
        }

        @Test
        fun boldText() {
            val rawDescription: String = """
                **bold text**
            """.trimIndent()
            val dynamoLesson: DynamoLesson = aLesson()
                .apply {
                    description = rawDescription
                }
            val lesson = DynamoLessonMapper.toLesson(dynamoLesson)
            Assertions.assertThat(lesson.parsedDescription.trim { it <= ' ' })
                .containsSubsequence("<strong>", "bold text", "</strong>")
        }

        @Test
        fun inlineCodeBlock() {
            val rawDescription: String = """
                `System.out.println("test");`
            """.trimIndent()
            val dynamoLesson: DynamoLesson = aLesson()
                .apply {
                    description = rawDescription
                }
            val lesson = DynamoLessonMapper.toLesson(dynamoLesson)
            Assertions.assertThat(lesson.parsedDescription.trim { it <= ' ' })
                .containsSubsequence(
                    "<code>",
                    "System.out.println(\"test\");",
                    "</code>"
                )
        }

        @Test
        fun multilineCodeBlock() {
            val rawDescription = """
                    TargetGroupAttributes:
                      - Key: deregistration_delay.timeout_seconds
                        Value: 120
                      - Key: stickiness.enabled
                        Value: true
            """
            val dynamoLesson: DynamoLesson = aLesson().apply {
                description = rawDescription
            }
            val lesson = DynamoLessonMapper.toLesson(dynamoLesson)
            Assertions.assertThat(lesson.parsedDescription.trim { it <= ' ' })
                .containsSubsequence(
                    "<pre>", "TargetGroupAttributes",
                    "- Key: deregistration_delay.timeout_seconds",
                    "Value: 120",
                    "- Key: stickiness.enabled",
                    "Value: true",
                    "</pre>"
                )
        }
    }

    companion object {
        private fun aLesson(): DynamoLesson {
            return DynamoLesson().apply {
                creationTimestampMillis = 1232144L
                author = "author"
                description = "description"
                category = "category"
                title = "title"
                id = "id"
            }
        }
    }
}