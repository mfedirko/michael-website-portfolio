package io.mfedirko.email

interface EmailService {
    fun sendHtmlEmail(subject: String, htmlBody: String, cc: Array<String> = arrayOf())
}