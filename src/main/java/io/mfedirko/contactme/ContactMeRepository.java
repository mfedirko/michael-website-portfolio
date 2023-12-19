package io.mfedirko.contactme;

import java.time.LocalDate;
import java.util.List;

public interface ContactMeRepository {
    void save(ContactForm form);
    List<ContactHistory> findContactHistoryByDateRange(LocalDate from, LocalDate to);
}
