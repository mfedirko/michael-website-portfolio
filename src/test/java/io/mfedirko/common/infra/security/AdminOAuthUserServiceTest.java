package io.mfedirko.common.infra.security;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest
@WireMockTest(httpPort = 8111)
class AdminOAuthUserServiceTest {
    @Autowired
    private AdminOAuthUserService userService;

    @Mock
    private OAuth2AccessToken accessToken;

    @Test
    void loadGithubAdminUser() throws Exception {
        stubFor(get(urlPathMatching("/github/user"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-userinfo-admin.json")
                )
        );

        OAuth2User user = userService.loadUser(getUserRequest("github"));

        Assertions.assertThat(user)
                .has(name("mfedirko"))
                .has(adminAuthority());
    }

    @Test
    void loadGithubNonAdminUser() throws Exception {
        stubFor(get(urlPathMatching("/github/user"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-userinfo-nonadmin.json")
                )
        );

        OAuth2User user = userService.loadUser(getUserRequest("github"));

        Assertions.assertThat(user)
                .has(name("ghotherusr"))
                .doesNotHave(adminAuthority());
    }

    @Test
    void loadUserFromNonGithubProvider() throws Exception {
        stubFor(get(urlPathMatching("/google/oauth2/v3/userinfo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("google-userinfo.json")
                )
        );

        OAuth2User user = userService.loadUser(getUserRequest("google"));

        Assertions.assertThat(user)
                .doesNotHave(adminAuthority());
    }

    private Condition<? super OAuth2User> adminAuthority() {
        return new Condition<>(usr -> usr.getAuthorities().stream().anyMatch(auth -> "ADMIN".equals(auth.getAuthority())),
                "ADMIN authority on OAuth user");
    }

    private Condition<? super OAuth2User> name(String value) {
        return attr("name", value);
    }

    private Condition<? super OAuth2User> attr(String name, String value) {
        return new Condition<>(usr -> value.equals(usr.getAttribute(name)),
                "Attribute '%s' = %s".formatted(name, value));
    }

    private OAuth2UserRequest getUserRequest(String registrationId) {
        return new OAuth2UserRequest(getClientRegistration(registrationId), accessToken);
    }

    private ClientRegistration getClientRegistration(String registrationId) {
        switch (registrationId) {
            case "github":
                return  ClientRegistration.withRegistrationId(registrationId)
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
                        .build();
            case "google":
                return  ClientRegistration.withRegistrationId(registrationId)
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
                        .build();
        }
        throw new IllegalArgumentException("Unknown registrationId: " + registrationId);
    }


}