package io.mfedirko.learning

import io.mfedirko.common.infra.security.WebSecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [LearningController::class])
@Import(WebSecurityConfig::class)
@AutoConfigureMockMvc
internal class LearningControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var repository: PaginatedLearningRepository

    @Test
    @Throws(Exception::class)
    fun pageLoads() {
        mockMvc.perform(MockMvcRequestBuilders.get("/learning?page=1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("learning"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
            .andExpect(MockMvcResultMatchers.model().attribute("nextPage", 2))
    }

    @Test
    @Throws(Exception::class)
    fun nextPage() {
        mockMvc.perform(MockMvcRequestBuilders.get("/learning?page=3"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("learning"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
            .andExpect(MockMvcResultMatchers.model().attribute("nextPage", 4))
    }
}