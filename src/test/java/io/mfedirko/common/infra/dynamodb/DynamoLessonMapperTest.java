package io.mfedirko.common.infra.dynamodb;

import io.mfedirko.fixture.DynamoLessons;
import io.mfedirko.learning.Lesson;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoLessonMapperTest {
    DynamoLessonMapper mapper = new DynamoLessonMapper();

    @Test
    void allFieldsPopulated() {
        DynamoLesson dynamoLesson = DynamoLessons.DATA.get(0);

        Lesson lesson = mapper.toLesson(dynamoLesson);

        assertThat(lesson)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().ignoringFields(
                        "id", "creationTimestamp", "parsedDescription")
                .isEqualTo(dynamoLesson);
    }

    @Nested
    class MarkdownDescription {
        @Test
        void simpleParagraphNoMarkdown() {
            String rawDescription = "No markdown, paragraph; with punctuation. And spaces!";

            DynamoLesson dynamoLesson = aLesson()
                    .description(rawDescription)
                    .build();

            Lesson lesson = mapper.toLesson(dynamoLesson);

            assertThat(lesson.getParsedDescription().trim())
                    .isEqualTo("<p>" + rawDescription + "</p>");
        }

        @Test
        void headings() {
            String rawDescription = """
                    # first
                    ## second
                    ### third
                    #### fourth
                    """;

            DynamoLesson dynamoLesson = aLesson()
                    .description(rawDescription)
                    .build();

            Lesson lesson = mapper.toLesson(dynamoLesson);

            assertThat(lesson.getParsedDescription().trim())
                    .containsSubsequence(
                            "<h1>", "first",
                            "<h2>", "second",
                            "<h3>", "third",
                            "<h4>", "fourth"
                    );
        }

        @Test
        void unorderedList() {
            String rawDescription = """
                    - apple
                    - orange
                    - grapefruit
                    """;

            DynamoLesson dynamoLesson = aLesson()
                    .description(rawDescription)
                    .build();

            Lesson lesson = mapper.toLesson(dynamoLesson);

            assertThat(lesson.getParsedDescription().trim())
                    .containsSubsequence(
                            "<ul>",
                            "<li>", "apple",
                            "<li>", "orange",
                            "<li>", "grapefruit",
                            "</ul>");
        }

        @Test
        void boldText() {
            String rawDescription = """
                   **bold text**
                    """;

            DynamoLesson dynamoLesson = aLesson()
                    .description(rawDescription)
                    .build();

            Lesson lesson = mapper.toLesson(dynamoLesson);

            assertThat(lesson.getParsedDescription().trim())
                    .containsSubsequence("<strong>", "bold text", "</strong>");
        }
    }

    private static DynamoLesson.DynamoLessonBuilder aLesson() {
        return DynamoLesson.builder()
                .creationTimestampMillis(1232144L);
    }

}