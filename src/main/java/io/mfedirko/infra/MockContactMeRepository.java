package io.mfedirko.infra;

import io.mfedirko.contactme.ContactForm;
import io.mfedirko.contactme.ContactMeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("local")
public class MockContactMeRepository implements ContactMeRepository {
    private final List<ContactForm> requests = new ArrayList<>();

    @Override
    public void save(ContactForm form) {
        requests.add(form);
    }
}
