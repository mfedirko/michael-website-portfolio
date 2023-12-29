package io.mfedirko.contactme

import io.mfedirko.common.infra.RecaptchaClient
import io.mfedirko.fixture.ContactForms.aContactForm
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class ContactFormValidationTest {
    @Autowired
    private lateinit var validator: Validator

    @MockBean
    private lateinit var recaptchaClient: RecaptchaClient
    
    @Test
    fun valid() {
        mockValidRecaptchaResponse()
        val violations = validator.validate(
            aContactForm()
        )
        Assertions.assertThat(violations).isEmpty()
    }

    @Test
    fun missingRecaptcha() {
        val violations = validator.validate(aContactForm().apply { recaptcha = "" })
        Assertions.assertThat(violations).has(fieldError("recaptcha"))
    }

    @Test
    fun invalidRecaptcha() {
        mockInvalidRecaptchaResponse()
        val violations = validator.validate(
            aContactForm()
        )
        Assertions.assertThat(violations).has(fieldError("recaptcha"))
    }

    @Test
    fun invalidEmail() {
        mockValidRecaptchaResponse()
        val violations = validator.validate(
            aContactForm().apply { email = "noeml" }
        )
        Assertions.assertThat(violations).has(fieldError("email"))
    }

    @Test
    fun invalidEmptyForm() {
        val violations: Set<ConstraintViolation<ContactForm>> = validator.validate(
            aContactForm().apply { email = ""; fullName = ""; messageBody = ""; recaptcha = "" }
        )
        Assertions.assertThat(violations)
            .has(fieldErrors("email", "fullName", "messageBody", "recaptcha"))
    }

    private fun mockValidRecaptchaResponse() {
        Mockito.`when`(recaptchaClient.isValidCaptcha(Mockito.anyString()))
            .thenReturn(true)
    }

    private fun mockInvalidRecaptchaResponse() {
        Mockito.`when`(recaptchaClient.isValidCaptcha(Mockito.anyString()))
            .thenReturn(false)
    }

    companion object {
        private fun fieldErrors(vararg fields: String): Condition<in (MutableCollection<out ConstraintViolation<*>>)> {
            return Condition(
                { violations -> fields.all {
                        field -> violations.any { it.propertyPath.toString() == field }
                    }
                },
                "Validation error on fields: $fields"
            )
        }

        private fun fieldError(field: String): Condition<in (MutableCollection<out ConstraintViolation<*>>)> {
            return Condition(
                { violations -> violations.any {
                        it.propertyPath.toString() == field
                    }
                },
                "Validation error on field: $field"
            )
        }
    }
}