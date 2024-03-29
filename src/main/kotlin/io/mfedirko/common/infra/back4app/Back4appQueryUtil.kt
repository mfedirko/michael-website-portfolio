package io.mfedirko.common.infra.back4app

import com.fasterxml.jackson.databind.ObjectMapper
import io.mfedirko.common.OrderDir

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

    fun <T> `in`(field: String, values: Array<T>): Map<String, Any> {
        return mapOf(
            field to mapOf(
                "\$in" to values
            )
        )
    }

    fun orderBy(vararg fieldAndDirs: Pair<String, OrderDir>): String {
        return fieldAndDirs.fold("") { acc, pair ->
            val (field, dir) = pair
            if (acc.isBlank()) "${dir.symbol}${field}"
            else "${acc},${dir.symbol}${field}"
        }
    }

    fun <T> equals(field: String, value: T): Map<String, Any> {
        return mapOf(
            field to value as Any
        )
    }

    private fun Map<String, Any>.toJson(): String {
        return objectMapper.writeValueAsString(this)
    }

    private val OrderDir.symbol: String
        get() = when(this) {
            OrderDir.ASC -> "+"
            else -> "-"
        }
}