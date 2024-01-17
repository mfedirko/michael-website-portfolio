package io.mfedirko.common.infra.security

import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [OAuthLoginController::class])
@Import(WebSecurityConfig::class)
@AutoConfigureMockMvc
internal class OAuthLoginControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Throws(Exception::class)
    fun loginPageListsProvidersDynamically() {
        mockMvc.perform(MockMvcRequestBuilders.get("/oauth-login"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().string(
                    Matchers.allOf(
                        Matchers.containsStringIgnoringCase("login with github"),
                        Matchers.containsStringIgnoringCase("login with google"),
                        Matchers.not(Matchers.containsStringIgnoringCase("secret"))
                    )
                )
            )
    }
}