package io.mfedirko.contactme;

import io.mfedirko.infra.RecaptchaClient;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static io.mfedirko.contactme.fixture.ContactForms.aContactForm;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactMeController.class)
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