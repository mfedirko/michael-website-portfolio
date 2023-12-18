package io.mfedirko.contactme;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactMeController.class)
class ContactMeControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ContactMeRepository repository;

    @Test
    void contactPageLoads() throws Exception {
        mockMvc.perform(get("/contact"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("contactme"))
                        .andExpect(model().attributeExists("contactForm"));
    }

    @Test
    void validContactFormSubmit() throws Exception {
        mockMvc.perform(post("/contact")
                        .param("fullName", "John Doe")
                        .param("email", "john.doe@gmail.com")
                        .param("messageBody", "Hello I am John Doe"))
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

    @Test
    void invalidEmail() throws Exception {
        mockMvc.perform(post("/contact")
                .param("fullName", "John Doe")
                .param("email", "notanemali")
                .param("messageBody", "Hello I am John Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("contactme"))
                .andExpect(model().attributeHasFieldErrors("contactForm", "email"));
    }

    @Test
    void invalidInputsTooLong() throws Exception {
        String veryLongStr = new RandomStringGenerator.Builder().build().generate(5000);

        mockMvc.perform(post("/contact")
                        .param("fullName", veryLongStr)
                        .param("email", veryLongStr)
                        .param("messageBody", veryLongStr))
                .andExpect(status().isOk())
                .andExpect(view().name("contactme"))
                .andExpect(model().attributeHasFieldErrors("contactForm", "email", "fullName", "messageBody"));
    }
}