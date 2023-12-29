package io.mfedirko.common.util

object Strings {
    fun String?.trimToNull(): String? = if (this.isNullOrBlank()) null else this.trim()
}
