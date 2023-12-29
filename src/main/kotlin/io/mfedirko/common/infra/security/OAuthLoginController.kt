package io.mfedirko.common.infra.security

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/oauth-login")
class OAuthLoginController(oauthProviders: OAuth2ClientProperties) {
    private val registeredOauthProviders: Collection<String>

    init {
        registeredOauthProviders = oauthProviders.registration.keys
    }

    @GetMapping
    fun getLoginPage(modelMap: ModelMap): String {
        modelMap.addAttribute("providers", registeredOauthProviders)
        return "oauth-login"
    }
}