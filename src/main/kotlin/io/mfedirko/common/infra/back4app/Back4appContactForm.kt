package io.mfedirko.common.infra.back4app

import io.mfedirko.contactme.ContactForm
import io.mfedirko.contactme.ContactHistory

class Back4appContactForm(form: ContactHistory) {
    val fullName: String
    val email: String
    val messageBody: String

    init {
        fullName = form.fullName
        email = form.email
        messageBody = form.messageBody
    }
    constructor(form: ContactForm) : this(form.toContactHistory())
}