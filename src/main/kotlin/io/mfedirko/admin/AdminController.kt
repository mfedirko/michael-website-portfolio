package io.mfedirko.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminController {
    @GetMapping
    fun getAdminDashboard(): String {
        return "admin/landing"
    }
}