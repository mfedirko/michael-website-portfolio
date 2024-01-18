package io.mfedirko.common

import io.mfedirko.common.infra.security.WebSecurityConfig
import io.mfedirko.contactme.ContactMeRepository
import io.mfedirko.contactme.notification.ContactNotificationService
import io.mfedirko.contactme.notification.NotificationPreferenceRepository
import io.mfedirko.learning.PaginatedLearningRepository
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebMvcTest
@Import(WebSecurityConfig::class)
@AutoConfigureMockMvc
@ActiveProfiles("mock")
class WebSecurityTest {
    @Autowired
    private lateinit var context: WebApplicationContext
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var contactMeRepository: ContactMeRepository
    @MockBean
    private lateinit var learningRepository: PaginatedLearningRepository
    @MockBean
    private lateinit var contactNotificationService: ContactNotificationService
    @MockBean
    private lateinit var notificationPreferenceRepository: NotificationPreferenceRepository

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    @ParameterizedTest
    @CsvSource(value = [
        "GET,/",
        "GET,/oauth-login",
        "GET,/css/application.css",
        "GET,/docs/Michael_Fedirko_Resume.pdf",
        "GET,/images/github-mark-white.svg"
    ])
    @Throws(
        Exception::class
    )
    fun whenPublicPage_thenAnyUserCanAccess(httpMethod: HttpMethod, endpoint: String) {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint))
            .andExpect(MockMvcResultMatchers.status().`is`(Matchers.oneOf(200, 404)))
    }

    @ParameterizedTest
    @CsvSource(value = [
        "GET,/admin",
        "GET,/admin/contacts?page=1",
        "GET,/admin/learning/create-form",
        "POST,/admin/learning/create-form",
        "POST,/admin/learning/update-form/123",
        "GET,/admin/preferences",
        "GET,/admin/preferences/notification"
    ])
    @WithMockUser(authorities = ["ADMIN"])
    @Throws(
        Exception::class
    )
    fun whenLoggedInUser_andValidRole_andSecureEndpoint_then200(httpMethod: HttpMethod, endpoint: String) {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
    }

    @ParameterizedTest
    @CsvSource(value = [
        "GET,/admin",
        "GET,/admin/contacts?page=1",
        "GET,/admin/learning/create-form",
        "POST,/admin/learning/create-form",
        "GET,/admin/learning/update-form/123",
        "POST,/admin/learning/update-form/123",
        "GET,/admin/any/other/endpoint",
        "GET,/admin/preferences",
        "GET,/admin/preferences/notification",
        "POST,/admin/preferences/notification"
    ])
    @Throws(
        Exception::class
    )
    fun whenUnauthenticatedUser_andSecureEndpoint_thenRedirectLogin(httpMethod: HttpMethod, endpoint: String) {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint))
            .andExpect(MockMvcResultMatchers.status().`is`(302))
            .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/oauth-login"))
    }

    @ParameterizedTest
    @CsvSource(value = [
        "GET,/admin",
        "GET,/admin/contacts?page=1",
        "GET,/admin/learning/create-form",
        "POST,/admin/learning/create-form",
        "GET,/admin/learning/update-form/123",
        "POST,/admin/learning/update-form/123",
        "GET,/admin/any/other/endpoint",
        "GET,/admin/preferences",
        "GET,/admin/preferences/notification",
        "POST,/admin/preferences/notification"
    ])
    @WithMockUser(authorities = ["OAUTH_USER"])
    @Throws(
        Exception::class
    )
    fun whenLoggedInUser_andWrongRole_andSecureEndpoint_thenRedirectError(httpMethod: HttpMethod, endpoint: String) {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint))
            .andExpect(MockMvcResultMatchers.status().`is`(302))
            .andExpect(MockMvcResultMatchers.redirectedUrl("/error"))
    }
}