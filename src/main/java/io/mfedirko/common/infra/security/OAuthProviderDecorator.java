package io.mfedirko.common.infra.security;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

interface OAuthProviderDecorator {
    boolean supports(OAuth2UserRequest request);

    OAuth2User decorate(OAuth2User user);
}
