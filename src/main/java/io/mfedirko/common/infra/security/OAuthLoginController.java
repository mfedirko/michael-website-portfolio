package io.mfedirko.common.infra.security;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
@RequestMapping("/oauth-login")
public class OAuthLoginController {
    private final Collection<String> registeredOauthProviders;
    public OAuthLoginController(OAuth2ClientProperties oauthProviders) {
        this.registeredOauthProviders = oauthProviders.getRegistration().keySet();
    }

    @GetMapping
    public String getLoginPage(ModelMap modelMap) {
        modelMap.addAttribute("providers", registeredOauthProviders);
        return "oauth-login";
    }
}
