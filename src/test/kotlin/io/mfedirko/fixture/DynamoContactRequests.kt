package io.mfedirko.fixture

import io.mfedirko.common.infra.dynamodb.DynamoContactRequest
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

object DynamoContactRequests {
    var DATA: List<DynamoContactRequest>? = null

    init {
        // convert csv with AI-generated mock data to List<DynamoContactRequest>
        val reader =
            BufferedReader(InputStreamReader(DynamoContactRequests::class.java.getResourceAsStream("/contact-request.csv")!!))
        DATA = reader.lines()
            .skip(1)
            .map { s: String ->
                s.split(Pattern.quote("|").toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            }
            .map { s: Array<String> ->
                DynamoContactRequest().apply {
                    id = s[0]
                    creationTimestampMillis = s[1].toLong()
                    fullName = s[2]
                    email = s[3]
                    messageBody = s[4]
                }
            }
            .toList()
    }
}