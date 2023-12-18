package io.mfedirko.contactme;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Getter
@Builder
@Value
@Jacksonized
public class ContactHistory {
    LocalDateTime creationTimestamp;
    String fullName;
    String email;
    String messageBody;
}
