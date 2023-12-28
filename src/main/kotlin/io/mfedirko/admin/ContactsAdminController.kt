package io.mfedirko.admin

import io.mfedirko.common.util.DateHelper.toDate
import io.mfedirko.common.util.DateHelper.toLocalDatePageByDay
import io.mfedirko.contactme.ContactMeRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Clock

@Controller
@RequestMapping("/admin/contacts")
class ContactsAdminController(
    private val contactMeRepository: ContactMeRepository
) {
    @GetMapping
    fun getContactListPage(@RequestParam("page") page: Int, modelMap: ModelMap): String {
        val date = toLocalDatePageByDay(page, Clock.systemDefaultZone())
        val endDate = date.plusDays(1)
        val contactHistory = contactMeRepository.findContactHistoryByDate(date)
        modelMap.addAttribute("history", contactHistory)
        modelMap.addAttribute("startDate", toDate(date))
        modelMap.addAttribute("endDate", toDate(endDate))
        modelMap.addAttribute("nextPage", page + 1)
        return "admin/contact-history-table"
    }
}