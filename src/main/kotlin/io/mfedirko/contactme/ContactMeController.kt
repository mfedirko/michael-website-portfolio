package io.mfedirko.contactme

import io.mfedirko.common.util.logger
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/contact")
class ContactMeController(
    private val repository: ContactMeRepository
){
    private val log = logger()

    @GetMapping
    fun getPage(modelMap: ModelMap): String {
        modelMap.addAttribute("contactForm", ContactForm("", "", "", ""))
        return CONTACTME_FORM
    }

    @PostMapping
    fun submitForm(@Valid form: ContactForm?, errors: Errors, modelMap: ModelMap): String {
        if (errors.hasErrors()) {
            return CONTACTME_FORM
        }
        log.info("Received contact request from {}<{}>", form!!.fullName, form.email)
        repository.save(form)
        modelMap.addAttribute("name", form.fullName)
        return CONTACTME_RECEIVED
    }

    companion object {
        private const val CONTACTME_FORM = "contactme"
        private const val CONTACTME_RECEIVED = "contactme-received"
    }
}