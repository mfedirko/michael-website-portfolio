package io.mfedirko.contactme;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public interface ContactMeRepository {
    void save(ContactForm form);
    List<ContactHistory> findContactHistoryByDate(LocalDate date);
}
