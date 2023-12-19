package io.mfedirko.contactme;

import java.time.Instant;
import java.util.List;

public interface ContactMeRepository {
    void save(ContactForm form);

    List<ContactHistory> findAllContactHistory();
    List<ContactHistory> findContactHistoryByTimestampRange(Instant from, Instant to);
}
