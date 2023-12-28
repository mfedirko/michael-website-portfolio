package io.mfedirko.contactme

import io.mfedirko.common.validation.ValidReCaptcha
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length
import java.beans.ConstructorProperties

class ContactForm
@ConstructorProperties("g-recaptcha-response", "fullName", "email", "messageBody")
constructor(
    @field:ValidReCaptcha
    var recaptcha: String?,

    @field:NotEmpty
    @field:Length(min = 5, max = 50)
    var fullName: String?,

    @field:NotEmpty
    @field:Length(max = 50)
    @field:Email
    var email: String?,

    @field:NotEmpty
    @field:Length(max = 300)
    var messageBody: String?
)