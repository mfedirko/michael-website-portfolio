package io.mfedirko.common.validation

import io.mfedirko.common.infra.RecaptchaClient
import io.mfedirko.common.util.Strings.trimToNull
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.beans.factory.annotation.Autowired

class ReCaptchaValidator : ConstraintValidator<ValidReCaptcha, String?> {
    @Autowired
    private lateinit var recaptchaClient: RecaptchaClient

    override fun isValid(recaptcha: String?, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return recaptcha.trimToNull()
            ?.let { recaptchaClient.isValidCaptcha(it) }
            ?: false
    }
}