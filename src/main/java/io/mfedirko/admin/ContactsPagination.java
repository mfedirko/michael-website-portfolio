package io.mfedirko.admin;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class ContactsPagination {
    LocalDate getEndofRange(int page /* 1-indexed */) {
        return LocalDate.now().minusDays(page - 1);
    }

    LocalDate getStartofRange(int page /* 1-indexed */) {
        return LocalDate.now().minusDays(page);
    }
}
