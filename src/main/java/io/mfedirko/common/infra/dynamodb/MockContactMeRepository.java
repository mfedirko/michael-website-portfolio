package io.mfedirko.common.infra.dynamodb;

import io.mfedirko.contactme.ContactForm;
import io.mfedirko.contactme.ContactHistory;
import io.mfedirko.contactme.ContactMeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("mock")
public class MockContactMeRepository implements ContactMeRepository {
    private final List<ContactForm> requests = new ArrayList<>();

    @Override
    public void save(ContactForm form) {
        requests.add(form);
    }

    public List<ContactHistory> findAllContactHistory() {
        return requests.stream().map(form -> ContactHistory.builder()
                        .fullName(form.getFullName())
                        .email(form.getEmail())
                        .messageBody(form.getMessageBody())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ContactHistory> findContactHistoryByDate(LocalDate from) {
        return findAllContactHistory();
    }
}
