package io.mfedirko.common.validation;

import io.mfedirko.infra.RecaptchaClient;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class ReCaptchaValidator implements ConstraintValidator<ValidReCaptcha, String> {
    @Autowired
    private RecaptchaClient recaptchaClient;

    @Override
    public boolean isValid(String recaptcha, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasText(recaptcha)) {
            return false;
        }
        return recaptchaClient.isValidCaptcha(recaptcha);
    }
}
