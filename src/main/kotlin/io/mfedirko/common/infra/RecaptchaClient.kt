package io.mfedirko.common.infra

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class RecaptchaClient(
    @param:Value("\${recaptcha.secret-key}") private val secretKey: String,
    restTemplate: RestTemplateBuilder
) {
    private val restTemplate: RestTemplate

    init {
        this.restTemplate = restTemplate.build()
    }

    fun isValidCaptcha(captcha: String): Boolean {
        return try {
            val response = restTemplate.postForEntity(
                UriComponentsBuilder.fromHttpUrl("https://www.google.com/recaptcha/api/siteverify")
                    .queryParam("secret", secretKey)
                    .queryParam("response", captcha)
                    .build().toUri(), null, String::class.java
            )
            response.statusCode.is2xxSuccessful
        } catch (ex: Exception) {
            false
        }
    }
}