package io.mfedirko.infra.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oauth-login")
public class OAuthLoginController {
    @GetMapping
    public String getLoginPage() {
        return "oauth-login";
    }
}
