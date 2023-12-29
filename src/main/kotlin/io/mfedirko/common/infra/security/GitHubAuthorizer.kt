package io.mfedirko.common.infra.security

import io.mfedirko.common.util.logger
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * For a GitHub OAuth 2.0 login, check if the user should be granted additional admin authorities
 */
class GitHubAuthorizer : OAuthProviderAuthorizer {
    private val log = logger()

    override fun supports(request: OAuth2UserRequest): Boolean {
        return GITHUB_CLIENT_REGISTRATION_ID == request.clientRegistration.registrationId
    }

    override fun decorate(user: OAuth2User): OAuth2User {
        return DefaultOAuth2User(getAuthorities(user), user.attributes, "login")
    }

    private fun getAuthorities(oAuth2User: OAuth2User): Collection<GrantedAuthority> {
        val authorities: MutableCollection<GrantedAuthority> = ArrayList(oAuth2User.authorities)
        if (isSiteAdmin(oAuth2User)) {
            authorities.add(SimpleGrantedAuthority(AdminOAuthUserService.ADMIN_ROLE))
            log.info(
                "Site admin access granted to GitHub user {}: {}",
                oAuth2User.getAttribute("name"),
                oAuth2User.authorities
            )
        }
        return authorities
    }

    private fun isSiteAdmin(oAuth2User: OAuth2User): Boolean {
        return SITE_ADMIN_ID == oAuth2User.getAttribute("id")
                && SITE_ADMIN_LOGIN == oAuth2User.getAttribute("login")
                && SITE_ADMIN_HTML_URL == oAuth2User.getAttribute("html_url")
    }

    companion object {
        const val SITE_ADMIN_LOGIN = "mfedirko"
        const val SITE_ADMIN_HTML_URL = "https://github.com/mfedirko"
        const val SITE_ADMIN_ID = 29769908
        const val GITHUB_CLIENT_REGISTRATION_ID = "github"
    }
}