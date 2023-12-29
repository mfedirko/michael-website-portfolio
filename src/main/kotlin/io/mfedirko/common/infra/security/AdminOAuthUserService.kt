package io.mfedirko.common.infra.security

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

/**
 * Custom OAuth 2.0 user details service delegating to <tt>OAuthProviderAuthorizer</tt>
 */
@Service
class AdminOAuthUserService(private val providerAuthorizers: List<OAuthProviderAuthorizer>) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = super.loadUser(userRequest)
        return providerAuthorizers.stream()
            .filter { dec: OAuthProviderAuthorizer -> dec.supports(userRequest) }
            .findFirst().map { dec: OAuthProviderAuthorizer -> dec.decorate(user) }
            .orElse(user)
    }

    companion object {
        const val ADMIN_ROLE = "ADMIN"
    }
}