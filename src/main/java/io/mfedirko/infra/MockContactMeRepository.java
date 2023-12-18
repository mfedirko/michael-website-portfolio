package io.mfedirko.infra;

import io.mfedirko.contactme.ContactForm;
import io.mfedirko.contactme.ContactHistory;
import io.mfedirko.contactme.ContactMeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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

    @Override
    public List<ContactHistory> findContactHistoryByTimestampRange(Instant from, Instant to) {
        return requests.stream().map(form -> ContactHistory.builder()
                    .fullName(form.getFullName())
                    .email(form.getEmail())
                    .messageBody(form.getMessageBody())
                    .build())
                .collect(Collectors.toList());
    }
}
