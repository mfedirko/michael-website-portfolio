package io.mfedirko.learning

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
internal class LearningControllerTest {
    @Autowired
    var mockMvc: MockMvc? = null

    @MockBean
    var repository: LearningRepository? = null

    @Test
    @Throws(Exception::class)
    fun pageLoads() {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/learning?page=1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("learning"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
            .andExpect(MockMvcResultMatchers.model().attribute("nextPage", 2))
    }

    @Test
    @Throws(Exception::class)
    fun nextPage() {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/learning?page=3"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("learning"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
            .andExpect(MockMvcResultMatchers.model().attribute("nextPage", 4))
    }
}