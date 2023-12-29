package io.mfedirko.common.validation

import jakarta.validation.Constraint
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ReCaptchaValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidReCaptcha(
    val message: String = "Please complete the reCaptcha",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)