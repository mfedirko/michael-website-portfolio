package io.mfedirko.common.infra.back4app

import io.mfedirko.contactme.ContactForm
import io.mfedirko.contactme.ContactHistory

class Back4appContactForm(form: ContactHistory) {
    val fullName: String
    val email: String
    val messageBody: String
    val status: ContactHistory.Status

    init {
        fullName = form.fullName
        email = form.email
        messageBody = form.messageBody
        status = form.status
    }
    constructor(form: ContactForm) : this(form.toContactHistory())
}