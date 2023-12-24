package io.mfedirko.common.infra.security;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OAuthLoginControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void loginPageListsProvidersDynamically() throws Exception {
        mockMvc.perform(get("/oauth-login"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                                containsStringIgnoringCase("login with github"),
                                containsStringIgnoringCase("login with google"),
                                not(containsStringIgnoringCase("secret")))
                        ));
    }
}