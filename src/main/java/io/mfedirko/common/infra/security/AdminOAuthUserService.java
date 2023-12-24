package io.mfedirko.common.infra.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom OAuth 2.0 user details service delegating to <tt>OAuthProviderAuthorizer</tt>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminOAuthUserService extends DefaultOAuth2UserService {
    public static final String ADMIN_ROLE = "ADMIN";

    private final List<OAuthProviderAuthorizer> providerAuthorizers;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        return providerAuthorizers.stream()
                .filter(dec -> dec.supports(userRequest))
                .findFirst().map(dec -> dec.decorate(user))
                .orElse(user);
    }

}
