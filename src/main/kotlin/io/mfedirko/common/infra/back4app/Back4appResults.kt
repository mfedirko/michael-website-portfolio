package io.mfedirko.common.infra.back4app

import kotlin.properties.Delegates

internal open class Back4appResults<T> {
    var results: List<T> by Delegates.notNull()
}