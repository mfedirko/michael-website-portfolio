package io.mfedirko.contactme

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "contactme")
data class ContactMeProperties
@ConstructorBinding
constructor(
    val notificationInterval: java.time.Duration,
    val toEmail: String,
    val fromEmail: String
)