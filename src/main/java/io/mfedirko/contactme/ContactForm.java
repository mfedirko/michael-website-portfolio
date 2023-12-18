package io.mfedirko.contactme;

import io.mfedirko.common.validation.ValidReCaptcha;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import java.beans.ConstructorProperties;

@Getter
@Builder
@Value
@Jacksonized
public class ContactForm {
    @ValidReCaptcha
    String recaptcha;

    @NotEmpty
    @Length(min = 5, max = 50)
    String fullName;

    @Length(max = 50)
    @NotEmpty
    @Email
    String email;

    @NotEmpty
    @Length(max = 300)
    String messageBody;

    @ConstructorProperties({"g-recaptcha-response", "fullName", "email", "messageBody"})
    public ContactForm(String recaptcha, String fullName, String email, String messageBody) {
        this.recaptcha = recaptcha;
        this.fullName = fullName;
        this.email = email;
        this.messageBody = messageBody;
    }
}
