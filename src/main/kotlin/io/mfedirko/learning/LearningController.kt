package io.mfedirko.learning

import io.mfedirko.common.util.Dates
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Clock

@Controller
@RequestMapping("/learning")
class LearningController(
    private val learningRepository: LearningRepository
) {
    @GetMapping
    fun getLearningPage(@RequestParam("page") page: Int, modelMap: ModelMap): String {
        val year = Dates.toLocalYearByPage(page, Clock.systemDefaultZone())
        val lessons = learningRepository.findLessons(year)
        modelMap.addAttribute("lessons", lessons)
        modelMap.addAttribute("nextPage", page + 1)
        return "learning"
    }
}