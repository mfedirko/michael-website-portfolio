package io.mfedirko.common;

import io.mfedirko.learning.LearningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.oneOf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mock")
public class WebSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @ParameterizedTest
    @CsvSource(value = {
            "GET,/",
            "GET,/oauth-login",
            "GET,/css/application.css",
            "GET,/docs/Michael_Fedirko_Resume.pdf",
            "GET,/images/github-mark-white.svg",
    })
    void whenPublicPage_thenAnyUserCanAccess(HttpMethod httpMethod, String endpoint) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint))
                .andExpect(status().is(oneOf(200, 404)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "GET,/admin",
            "GET,/admin/contacts?page=1",
            "GET,/admin/learning/create-form",
            "POST,/admin/learning/create-form",
            "GET,/admin/learning/update-form/123",
            "POST,/admin/learning/update-form/123"
    })
    @WithMockUser(authorities = {"ADMIN"})
    void whenLoggedInUser_andValidRole_andSecureEndpoint_then200(HttpMethod httpMethod, String endpoint) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint))
                .andExpect(status().is(200));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "GET,/admin",
            "GET,/admin/contacts?page=1",
            "GET,/admin/learning/create-form",
            "POST,/admin/learning/create-form",
            "GET,/admin/learning/update-form/123",
            "POST,/admin/learning/update-form/123",
            "GET,/admin/any/other/endpoint"
    })
    void whenUnauthenticatedUser_andSecureEndpoint_thenRedirectLogin(HttpMethod httpMethod, String endpoint) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("http://localhost/oauth-login"));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "GET,/admin",
            "GET,/admin/contacts?page=1",
            "GET,/admin/learning/create-form",
            "POST,/admin/learning/create-form",
            "GET,/admin/learning/update-form/123",
            "POST,/admin/learning/update-form/123",
            "GET,/admin/any/other/endpoint"
    })
    @WithMockUser(authorities = {"OAUTH_USER"})
    void whenLoggedInUser_andWrongRole_andSecureEndpoint_thenRedirectError(HttpMethod httpMethod, String endpoint) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/error"));
    }
}
