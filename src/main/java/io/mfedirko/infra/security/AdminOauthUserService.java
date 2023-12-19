package io.mfedirko.infra.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class AdminOauthUserService extends DefaultOAuth2UserService {
    // only one site admin
    public static final String SITE_ADMIN_LOGIN = "mfedirko";
    public static final String SITE_ADMIN_HTML_URL = "https://github.com/mfedirko";
    public static final String ADMIN_ROLE = "ADMIN";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("User logged in with OAuth: {}", (Object)oAuth2User.getAttribute("login"));
        return new DefaultOAuth2User(getAuthorities(oAuth2User), oAuth2User.getAttributes(), "login");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(OAuth2User oAuth2User) {
        Collection<GrantedAuthority> authorities = new ArrayList<>(oAuth2User.getAuthorities());
        if (isSiteAdmin(oAuth2User)) {
            authorities.add(new SimpleGrantedAuthority(ADMIN_ROLE));
            log.info("Site admin access granted to user: {}", oAuth2User);
        }

        return authorities;
    }

    private boolean isSiteAdmin(OAuth2User oAuth2User) {
        return SITE_ADMIN_LOGIN.equals(oAuth2User.getAttribute("login"))
                && SITE_ADMIN_HTML_URL.equals(oAuth2User.getAttribute("html_url"));
    }
}
