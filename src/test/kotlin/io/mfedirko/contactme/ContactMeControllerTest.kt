package io.mfedirko.contactme

import io.mfedirko.common.infra.RecaptchaClient
import io.mfedirko.common.infra.security.WebSecurityConfig
import io.mfedirko.fixture.ContactForms.aContactForm
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [ContactMeController::class])
@Import(WebSecurityConfig::class)
@AutoConfigureMockMvc
internal class ContactMeControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var repository: ContactMeRepository

    @MockBean
    private lateinit var recaptchaClient: RecaptchaClient
    
    @Test
    @Throws(Exception::class)
    fun contactPageLoads() {
        mockMvc.perform(MockMvcRequestBuilders.get("/contact"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("contactme"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("contactForm"))
    }

    @Test
    @Throws(Exception::class)
    fun validContactFormSubmit() {
        val contactForm: ContactForm = aContactForm()
        Mockito.`when`(recaptchaClient.isValidCaptcha(Mockito.anyString()))
            .thenReturn(true)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/contact")
                .param("g-recaptcha-response", contactForm.recaptcha)
                .param("fullName", contactForm.fullName)
                .param("email", contactForm.email)
                .param("messageBody", contactForm.messageBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("contactme-received"))
            .andExpect(MockMvcResultMatchers.model().hasNoErrors())
    }

    @Test
    @Throws(Exception::class)
    fun invalidEmptyForm() {
        mockMvc.perform(MockMvcRequestBuilders.post("/contact"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("contactme"))
            .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("contactForm", "email", "fullName"))
    }
}