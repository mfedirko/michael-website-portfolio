package io.mfedirko.contactme;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@Value
@Jacksonized
public class ContactForm {
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
}
