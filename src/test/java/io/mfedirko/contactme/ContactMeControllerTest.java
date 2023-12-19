package io.mfedirko.contactme;

import io.mfedirko.infra.RecaptchaClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static io.mfedirko.contactme.fixture.ContactForms.aContactForm;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactMeControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ContactMeRepository repository;

    @MockBean
    RecaptchaClient recaptchaClient;

    @Test
    void contactPageLoads() throws Exception {
        mockMvc.perform(get("/contact"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("contactme"))
                        .andExpect(model().attributeExists("contactForm"));
    }

    @Test
    void validContactFormSubmit() throws Exception {
        ContactForm contactForm = aContactForm().build();
        Mockito.when(recaptchaClient.isValidCaptcha(Mockito.anyString()))
                        .thenReturn(true);

        mockMvc.perform(post("/contact")
                        .param("g-recaptcha-response", contactForm.getRecaptcha())
                        .param("fullName", contactForm.getFullName())
                        .param("email", contactForm.getEmail())
                        .param("messageBody", contactForm.getMessageBody()))
                .andExpect(status().isOk())
                .andExpect(view().name("contactme-received"))
                .andExpect(model().hasNoErrors());

    }

    @Test
    void invalidEmptyForm() throws Exception {
        mockMvc.perform(post("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("contactme"))
                .andExpect(model().attributeHasFieldErrors("contactForm", "email", "fullName"));
    }
}