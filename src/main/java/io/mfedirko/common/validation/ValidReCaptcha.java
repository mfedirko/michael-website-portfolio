package io.mfedirko.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReCaptchaValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReCaptcha {
    String message() default "Please complete the reCaptcha";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
