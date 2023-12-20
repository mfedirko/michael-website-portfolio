package io.mfedirko.fixture;

import io.mfedirko.common.infra.dynamodb.DynamoContactRequest;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class DynamoContactRequests {
    public static final List<DynamoContactRequest> CONTACT_REQUESTS_DATA;

    static {
        // convert csv with AI-generated mock data to List<DynamoContactRequest>
        BufferedReader reader = new BufferedReader(new InputStreamReader(DynamoContactRequests.class.getResourceAsStream("/contact-request.csv")));
        CONTACT_REQUESTS_DATA = reader.lines()
                .skip(1)
                .map(s -> s.split(Pattern.quote("|")))
                .map(s -> DynamoContactRequest.builder()
                        .id(s[0])
                        .creationTimestampMillis(Long.parseLong(s[1]))
                        .fullName(s[2])
                        .email(s[3])
                        .messageBody(s[4])
                        .build())
                .toList();
    }
}
