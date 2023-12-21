package io.mfedirko.learning;

import io.mfedirko.contactme.ContactForm;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LearningControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    LearningRepository repository;


    @Test
    void pageLoads() throws Exception {
        mockMvc.perform(get("/learning?page=1"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("learning"))
                        .andExpect(model().attributeExists("lessons"))
                        .andExpect(model().attribute("nextPage", 2));
    }

    @Test
    void nextPage() throws Exception {
        mockMvc.perform(get("/learning?page=3"))
                .andExpect(status().isOk())
                .andExpect(view().name("learning"))
                .andExpect(model().attributeExists("lessons"))
                .andExpect(model().attribute("nextPage", 4));
    }


}