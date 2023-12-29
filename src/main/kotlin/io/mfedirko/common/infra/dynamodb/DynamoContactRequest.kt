package io.mfedirko.common.infra.dynamodb

import io.mfedirko.common.util.DateHelper
import io.mfedirko.common.util.DateHelper.inLocalTimeZone
import io.mfedirko.contactme.ContactForm
import io.mfedirko.contactme.ContactHistory
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@DynamoDbBean
class DynamoContactRequest {
    @get:DynamoDbAttribute(ID)
    @get:DynamoDbPartitionKey
    var id: String? = null

    @get:DynamoDbAttribute(CREATION_TIMESTAMP)
    @get:DynamoDbSortKey
    var creationTimestampMillis // unix epoch timestamp in milliseconds
            : Long? = null

    @get:DynamoDbAttribute(FULL_NAME)
    var fullName: String? = null

    @get:DynamoDbAttribute(EMAIL)
    var email: String? = null

    @get:DynamoDbAttribute(MESSAGE_BODY)
    var messageBody: String? = null

    fun toContactHistory(): ContactHistory {
        return ContactHistory().apply {
            fullName = this@DynamoContactRequest.fullName
            email = this@DynamoContactRequest.email
            messageBody = this@DynamoContactRequest.messageBody
            creationTimestamp = DateHelper.unixMillisToLocalDateTime(this@DynamoContactRequest.creationTimestampMillis!!)

        }
    }

    companion object {
        const val TABLE = "Contact-Request"
        const val ID = "id"
        const val CREATION_TIMESTAMP = "creation_timestamp"
        const val FULL_NAME = "full_name"
        const val EMAIL = "email"
        const val MESSAGE_BODY = "message_body"

        fun from(form: ContactForm): DynamoContactRequest {
            return DynamoContactRequest().apply {
                fullName = form.fullName
                email = form.email
                messageBody = form.messageBody
                id = toPartitionKey(LocalDate.now())
                creationTimestampMillis = toSortKey(Instant.now())
            }
        }

        fun toSortKey(now: Instant): Long { // sort by timestamp millis
            return now.toEpochMilli()
        }

        fun toPartitionKey(now: LocalDate): String { // partition by day (in local time zone)
            return now.inLocalTimeZone().format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }
}