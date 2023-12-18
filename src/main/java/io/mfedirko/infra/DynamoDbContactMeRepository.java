package io.mfedirko.infra;

import io.mfedirko.contactme.ContactForm;
import io.mfedirko.contactme.ContactMeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("!local")
public class DynamoDbContactMeRepository implements ContactMeRepository {
    @Override
    public void save(ContactForm form) {
        //TODO
    }
}
