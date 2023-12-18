package io.mfedirko.contactme;

import io.mfedirko.infra.RecaptchaClient;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.Set;

import static io.mfedirko.contactme.fixture.ContactForms.aContactForm;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ContactFormValidationTest {
    @Autowired
    private Validator validator;

    @MockBean
    private RecaptchaClient recaptchaClient;

    @Test
    void valid() {
        mockValidRecaptchaResponse();

        Set<ConstraintViolation<ContactForm>> violations = validator.validate(
                aContactForm().build());

        assertThat(violations).isEmpty();
    }

    @Test
    void missingRecaptcha() {
        Set<ConstraintViolation<ContactForm>> violations = validator.validate(
                aContactForm().recaptcha(null).build());

        assertThat(violations).has(fieldError("recaptcha"));
    }

    @Test
    void invalidRecaptcha() {
        mockInvalidRecaptchaResponse();

        Set<ConstraintViolation<ContactForm>> violations = validator.validate(
                aContactForm().build());

        assertThat(violations).has(fieldError("recaptcha"));
    }

    @Test
    void invalidEmail() {
        mockValidRecaptchaResponse();

        Set<ConstraintViolation<ContactForm>> violations = validator.validate(
                aContactForm().email("noeml").build());

        assertThat(violations).has(fieldError("email"));
    }

    @Test
    void invalidEmptyForm() {
        Set<ConstraintViolation<ContactForm>> violations = validator.validate(
                aContactForm().email("").fullName("").messageBody("").recaptcha(null).build());

        assertThat(violations)
                .has(fieldError("email"))
                .has(fieldError("fullName"))
                .has(fieldError("messageBody"))
                .has(fieldError("recaptcha"));
    }

    private void mockValidRecaptchaResponse() {
        Mockito.when(recaptchaClient.isValidCaptcha(Mockito.anyString()))
                .thenReturn(true);
    }

    private void mockInvalidRecaptchaResponse() {
        Mockito.when(recaptchaClient.isValidCaptcha(Mockito.anyString()))
                .thenReturn(false);
    }

    private static Condition<Collection<? extends ConstraintViolation<?>>> fieldError(String field) {
        return new Condition<>(el -> el.stream().anyMatch(
                cv -> field.equals(cv.getPropertyPath().toString())),
                "Validation error on field: " + field);
    }
}
