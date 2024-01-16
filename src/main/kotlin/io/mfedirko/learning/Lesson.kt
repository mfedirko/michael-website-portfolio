package io.mfedirko.learning

import java.time.LocalDateTime
import kotlin.properties.Delegates

class Lesson {
    var creationTimestamp: LocalDateTime by Delegates.notNull()
    var id: Any by Delegates.notNull()
    var author: String by Delegates.notNull()
    var category: String by Delegates.notNull()
    var title: String by Delegates.notNull()
    var description: String by Delegates.notNull()
    var parsedDescription: String by Delegates.notNull()
}