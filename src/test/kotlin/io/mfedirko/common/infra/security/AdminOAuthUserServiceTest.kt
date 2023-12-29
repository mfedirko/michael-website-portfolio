package io.mfedirko.common.infra.security

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User

@SpringBootTest
@WireMockTest(httpPort = 8111)
internal class AdminOAuthUserServiceTest {
    @Autowired
    private lateinit var userService: AdminOAuthUserService

    @Mock
    private lateinit var accessToken: OAuth2AccessToken
    
    @Test
    @Throws(Exception::class)
    fun loadGithubAdminUser() {
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/github/user"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-userinfo-admin.json")
                )
        )
        val user = userService.loadUser(getUserRequest("github"))
        Assertions.assertThat(user)
            .has(name("mfedirko"))
            .has(adminAuthority())
    }

    @Test
    @Throws(Exception::class)
    fun loadGithubNonAdminUser() {
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/github/user"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-userinfo-nonadmin.json")
                )
        )
        val user = userService.loadUser(getUserRequest("github"))
        Assertions.assertThat(user)
            .has(name("ghotherusr"))
            .doesNotHave(adminAuthority())
    }

    @Test
    @Throws(Exception::class)
    fun loadUserFromNonGithubProvider() {
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/google/oauth2/v3/userinfo"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("google-userinfo.json")
                )
        )
        val user = userService.loadUser(getUserRequest("google"))
        Assertions.assertThat(user)
            .doesNotHave(adminAuthority())
    }

    private fun adminAuthority(): Condition<in OAuth2User> {
        return Condition(
            { usr: OAuth2User ->
                usr.authorities.stream().anyMatch { auth: GrantedAuthority? -> "ADMIN" == auth!!.authority }
            },
            "ADMIN authority on OAuth user"
        )
    }

    private fun name(value: String): Condition<in OAuth2User> {
        return attr("name", value)
    }

    private fun attr(name: String, value: String): Condition<in OAuth2User> {
        return Condition(
            { usr: OAuth2User -> value == usr.getAttribute(name) },
            "Attribute '$name' = $value"
        )
    }

    private fun getUserRequest(registrationId: String): OAuth2UserRequest {
        return OAuth2UserRequest(getClientRegistration(registrationId), accessToken)
    }

    private fun getClientRegistration(registrationId: String): ClientRegistration {
        when (registrationId) {
            "github" -> return ClientRegistration.withRegistrationId(registrationId)
                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("user:read", "ADMIN")
                .authorizationUri("http://localhost:8111/github/login/oauth/authorize")
                .tokenUri("http://localhost:8111/github/login/oauth/access_token")
                .userInfoUri("http://localhost:8111/github/user")
                .userNameAttributeName("id")
                .clientName("GitHub")
                .clientSecret("mysecret")
                .clientId("myid")
                .build()
            "google" -> return ClientRegistration.withRegistrationId(registrationId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("openid", "profile", "email", "ADMIN")
                .authorizationUri("http://localhost:8111/google/o/oauth2/v2/auth")
                .tokenUri("http://localhost:8111/google/oauth2/v4/token")
                .jwkSetUri("http://localhost:8111/google/oauth2/v3/certs")
                .issuerUri("http://localhost:8111/google/issuer")
                .userInfoUri("http://localhost:8111/google/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .clientSecret("mysecret")
                .clientId("myid")
                .build()
        }
        throw IllegalArgumentException("Unknown registrationId: $registrationId")
    }
}