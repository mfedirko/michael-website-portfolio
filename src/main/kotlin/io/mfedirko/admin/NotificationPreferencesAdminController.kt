package io.mfedirko.admin

import io.mfedirko.common.util.Logging.logger
import io.mfedirko.contactme.notification.NotificationPreferenceForm
import io.mfedirko.contactme.notification.NotificationPreferenceRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.Duration


@Controller
@RequestMapping("/admin/preferences/notification")
class NotificationPreferencesAdminController(
    private val notificationPreferenceRepository: NotificationPreferenceRepository
) {
    private val log = logger()

    @GetMapping
    fun getForm(modelMap: ModelMap): String {
        val form = notificationPreferenceRepository.getNotificationPreference()?.let {
            NotificationPreferenceForm.fromNotificationPreference(it)
        } ?: NotificationPreferenceForm(Duration.ofSeconds(0), "", "")
        modelMap.addAttribute("notificationPreferenceForm", form)
        return NOTIFICATION_PREF_FORM
    }

    @PostMapping
    fun submitForm(@Valid form: NotificationPreferenceForm, errors: Errors, modelMap: ModelMap): String {
        if (errors.hasErrors()) {
            return NOTIFICATION_PREF_FORM
        }
        log.info("Updating notification preferences: {}", form)
        notificationPreferenceRepository.updateNotificationPreference(form.toNotificationPreference())
        modelMap.addAttribute("success", true)
        return NOTIFICATION_PREF_FORM
    }


    companion object {
        private const val NOTIFICATION_PREF_FORM = "admin/notification-preferences.html"
    }
}