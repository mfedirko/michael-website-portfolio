package io.mfedirko.common.validation

import io.mfedirko.common.infra.RecaptchaClient
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils

class ReCaptchaValidator : ConstraintValidator<ValidReCaptcha, String?> {
    @Autowired
    private lateinit var recaptchaClient: RecaptchaClient

    override fun isValid(recaptcha: String?, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return if (!StringUtils.hasText(recaptcha)) {
            false
        } else recaptchaClient.isValidCaptcha(recaptcha)
    }
}