package io.mfedirko.fixture;

import io.mfedirko.contactme.ContactForm;

public class ContactForms {
    public static ContactForm.ContactFormBuilder aContactForm() {
        return ContactForm.builder()
                .fullName("John Doe")
                .email("johndoe@gmail.com")
                .messageBody("my message")
                .recaptcha("validcaptcha");
    }
}
