package io.mfedirko.common.infra.back4app

import java.time.ZonedDateTime
import kotlin.properties.Delegates

class Back4appContactNotificationResult {
    var objectId: String by Delegates.notNull()
    var createdAt: ZonedDateTime by Delegates.notNull()
    var updatedAt: ZonedDateTime by Delegates.notNull()
}