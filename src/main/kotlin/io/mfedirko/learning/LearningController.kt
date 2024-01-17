package io.mfedirko.learning

import io.mfedirko.common.util.Dates.pageToLimitOffset
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/learning")
class LearningController(
    private val learningRepository: PaginatedLearningRepository
) {
    @GetMapping
    fun getLearningPage(@RequestParam("page") page: Int, modelMap: ModelMap): String {
        val (limit, offset) = pageToLimitOffset(page, pageSize = 10)
        val lessons = learningRepository.findLessons(limit, offset)
        with(modelMap) {
            addAttribute("lessons", lessons)
            addAttribute("nextPage", page + 1)
        }
        return "learning"
    }
}