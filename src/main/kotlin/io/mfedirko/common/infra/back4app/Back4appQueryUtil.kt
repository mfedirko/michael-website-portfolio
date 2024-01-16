package io.mfedirko.common.infra.back4app

import com.fasterxml.jackson.databind.ObjectMapper

object Back4appQueryUtil {
    private val objectMapper = ObjectMapper()

    fun where(vararg args: Map<String, Any>): String {
        return args.fold(mapOf<String, Any>()) { acc, map -> acc + map }.toJson()
    }

    fun <T> between(field: String, start: T, end: T): Map<String, Any> {
        return mapOf(
            field to mapOf(
                "\$gte" to start.toString(),
                "\$lte" to end.toString()
            )
        )
    }

    fun <T> equals(field: String, value: T): Map<String, Any> {
        return mapOf(
            field to value as Any
        )
    }

    private fun Map<String, Any>.toJson(): String {
        return objectMapper.writeValueAsString(this)
    }
}