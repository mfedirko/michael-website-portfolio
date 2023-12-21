package io.mfedirko.fixture;

import io.mfedirko.common.infra.dynamodb.DynamoContactRequest;
import io.mfedirko.common.infra.dynamodb.DynamoLesson;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class DynamoLessons {
    public static final List<DynamoLesson> DATA;

    static {
        // convert csv with AI-generated mock data to list
        BufferedReader reader = new BufferedReader(new InputStreamReader(DynamoLessons.class.getResourceAsStream("/lesson.csv")));
        DATA = reader.lines()
                .skip(1)
                .map(s -> s.split(Pattern.quote("|")))
                .map(s -> DynamoLesson.builder()
                        .id(s[0].substring(0, 4))
                        .creationTimestampMillis(Long.parseLong(s[1]))
                        .author(s[2])
                        .title(s[3])
                        .category(s[4])
                        .description(s[5])
                        .build())
                .toList();
    }
}
