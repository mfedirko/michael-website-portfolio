package io.mfedirko.contactme;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
@Slf4j
public class ContactMeController {
    private static final String CONTACTME_FORM = "contactme";
    private static final String CONTACTME_RECEIVED = "contactme-received";

    private final ContactMeRepository repository;

    @GetMapping
    public String getPage(ModelMap modelMap) {
        modelMap.addAttribute("contactForm", new ContactForm("", "", ""));
        return CONTACTME_FORM;
    }

    @PostMapping
    public String submitForm(@Valid ContactForm form, Errors errors, ModelMap modelMap) {
        if (errors.hasErrors()) {
            return CONTACTME_FORM;
        }

        log.info("Received contact request from {}<{}>", form.getFullName(), form.getEmail());
        repository.save(form);
        modelMap.addAttribute("name", form.getFullName());
        return CONTACTME_RECEIVED;
    }
}
