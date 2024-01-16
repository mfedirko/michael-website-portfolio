package io.mfedirko.common.infra.back4app

import com.github.rjeschke.txtmark.Processor
import io.mfedirko.common.util.Dates.inLocalTimeZone
import io.mfedirko.contactme.ContactHistory
import io.mfedirko.learning.Lesson
import java.time.ZonedDateTime
import kotlin.properties.Delegates

class Back4appLessonResult {
    var objectId: String by Delegates.notNull()
    var createdAt: ZonedDateTime by Delegates.notNull()
    var updatedAt: ZonedDateTime by Delegates.notNull()
    var title: String by Delegates.notNull()
    var author: String by Delegates.notNull()
    var category: String by Delegates.notNull()
    var description: String by Delegates.notNull()

    fun toLesson(): Lesson {
        return Lesson().apply {
            this.creationTimestamp = this@Back4appLessonResult.createdAt.inLocalTimeZone()
            this.title = this@Back4appLessonResult.title
            this.author = this@Back4appLessonResult.author
            this.category = this@Back4appLessonResult.category
            this.description = this@Back4appLessonResult.description
            this.parsedDescription = Processor.process(description)
            this.id = this@Back4appLessonResult.objectId
        }
    }
}