package io.mfedirko.email

interface EmailService {
    fun sendHtmlEmail(to: Array<String>, subject: String, htmlBody: String, cc: Array<String> = arrayOf())
}