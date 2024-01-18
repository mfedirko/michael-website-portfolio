package io.mfedirko.contactme.notification

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "contactme")
class ContactMeProperties
@ConstructorBinding
constructor(
   notificationInterval: java.time.Duration,
   toEmail: String,
   fromEmail: String
) : NotificationPreference(notificationInterval, toEmail, fromEmail)
