package io.mfedirko.admin;

import io.mfedirko.contactme.ContactHistory;
import io.mfedirko.contactme.ContactMeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class ContactsAdminController {
    private final ContactMeRepository contactMeRepository;

    @GetMapping
    public String getContactListPage(ModelMap modelMap) {
        List<ContactHistory> contactHistory = contactMeRepository.findContactHistoryByTimestampRange(
                Instant.now().minusSeconds(60 * 60 * 24 * 7), Instant.now());
        modelMap.addAttribute("history", contactHistory);
        return "admin/contact-history";
    }
}
