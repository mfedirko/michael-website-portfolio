package io.mfedirko.fixture

import io.mfedirko.contactme.ContactForm

object ContactForms {
    @JvmStatic
    fun aContactForm(): ContactForm {
        return ContactForm(
            fullName = "John Doe",
            email = "johndoe@gmail.com",
            messageBody = "my message",
            recaptcha = "validcaptcha"
        )
    }
}