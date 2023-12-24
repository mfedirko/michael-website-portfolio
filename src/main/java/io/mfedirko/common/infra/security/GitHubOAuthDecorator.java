package io.mfedirko.common.infra.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
class GitHubOAuthDecorator implements OAuthProviderDecorator {
    public static final String SITE_ADMIN_LOGIN = "mfedirko";
    public static final String SITE_ADMIN_HTML_URL = "https://github.com/mfedirko";
    public static final int SITE_ADMIN_ID = 29769908;
    public static final String GITHUB_CLIENT_REGISTRATION_ID = "github";

    @Override
    public boolean supports(OAuth2UserRequest request) {
        return GITHUB_CLIENT_REGISTRATION_ID.equals(request.getClientRegistration().getRegistrationId());
    }

    @Override
    public OAuth2User decorate(OAuth2User user) {
        return new DefaultOAuth2User(getAuthorities(user), user.getAttributes(), "login");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(OAuth2User oAuth2User) {
        Collection<GrantedAuthority> authorities = new ArrayList<>(oAuth2User.getAuthorities());
        if (isSiteAdmin(oAuth2User)) {
            authorities.add(new SimpleGrantedAuthority(AdminOAuthUserService.ADMIN_ROLE));
            log.info("Site admin access granted to GitHub user {}: {}", oAuth2User.getAttribute("name"), oAuth2User.getAuthorities());
        }

        return authorities;
    }

    private boolean isSiteAdmin(OAuth2User oAuth2User) {
        return SITE_ADMIN_ID == (int)oAuth2User.getAttribute("id") &&
                SITE_ADMIN_LOGIN.equals(oAuth2User.getAttribute("login"))
                && SITE_ADMIN_HTML_URL.equals(oAuth2User.getAttribute("html_url"));
    }
}
