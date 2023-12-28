package io.mfedirko.learning

import java.time.LocalDateTime
import kotlin.properties.Delegates

class Lesson {
    var creationTimestamp: LocalDateTime by Delegates.notNull()
    var creationTimestampMillis: Long = 0
    var author: String by Delegates.notNull()
    var category: String by Delegates.notNull()
    var title: String by Delegates.notNull()
    var description: String by Delegates.notNull()
    var parsedDescription: String by Delegates.notNull()
}