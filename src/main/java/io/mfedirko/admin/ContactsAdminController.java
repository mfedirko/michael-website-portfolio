package io.mfedirko.admin;

import io.mfedirko.contactme.ContactHistory;
import io.mfedirko.contactme.ContactMeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class ContactsAdminController {
    private final ContactMeRepository contactMeRepository;

    @GetMapping
    public String getContactListPage(@RequestParam("page") int page, ModelMap modelMap) {
        LocalDate startDate = ContactsPagination.getStartofRange(page);
        LocalDate endDate = ContactsPagination.getEndofRange(page);
        List<ContactHistory> contactHistory = contactMeRepository.findContactHistoryByDateRange(startDate, endDate);
        modelMap.addAttribute("history", contactHistory);
        modelMap.addAttribute("startDate", toDate(startDate));
        modelMap.addAttribute("endDate", toDate(endDate));
        modelMap.addAttribute("nextPage", page + 1);
        return "admin/contact-history-table";
    }

    private static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
