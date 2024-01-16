package io.mfedirko.common.infra.back4app

import io.mfedirko.contactme.ContactForm

class Back4appContactForm(form: ContactForm) {
    val fullName: String
    val email: String
    val messageBody: String

    init {
        fullName = form.fullName!!
        email = form.email!!
        messageBody = form.messageBody!!
    }
}