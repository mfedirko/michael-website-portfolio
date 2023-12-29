package io.mfedirko.fixture

import io.mfedirko.common.infra.dynamodb.DynamoLesson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

object DynamoLessons {
    var DATA: List<DynamoLesson>? = null

    init {
        // convert csv with AI-generated mock data to list
        val reader = BufferedReader(InputStreamReader(DynamoLessons::class.java.getResourceAsStream("/lesson.csv")!!))
        DATA = reader.lines()
            .skip(1)
            .map { s: String ->
                s.split(Pattern.quote("|").toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            }
            .map { s: Array<String> ->
                DynamoLesson().apply {
                    id = s[0].substring(0, 4)
                    creationTimestampMillis = s[1].toLong()
                    author = s[2]
                    title = s[3]
                    category = s[4]
                    description = s[5]
                }
            }
            .toList()
    }
}