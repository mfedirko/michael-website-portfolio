package io.mfedirko.admin;

import io.mfedirko.contactme.ContactHistory;
import io.mfedirko.contactme.ContactMeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static io.mfedirko.common.util.DateHelper.*;

@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class ContactsAdminController {
    private final ContactMeRepository contactMeRepository;

    @GetMapping
    public String getContactListPage(@RequestParam("page") int page, ModelMap modelMap) {
        LocalDate date = toLocalDate(page, Clock.systemDefaultZone());
        LocalDate endDate = date.plusDays(1);
        List<ContactHistory> contactHistory = contactMeRepository.findContactHistoryByDate(date);
        modelMap.addAttribute("history", contactHistory);
        modelMap.addAttribute("startDate", toDate(date));
        modelMap.addAttribute("endDate", toDate(endDate));
        modelMap.addAttribute("nextPage", page + 1);
        return "admin/contact-history-table";
    }


}
