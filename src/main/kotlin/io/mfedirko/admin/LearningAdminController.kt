package io.mfedirko.admin

import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.LearningRepository
import io.mfedirko.learning.UpdateLessonForm
import io.mfedirko.learning.UpdateLessonForm.Companion.fromLesson
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/learning")
class LearningAdminController(
    private val repository: LearningRepository
) {
    @GetMapping("/create-form")
    fun getCreateLessonForm(modelMap: ModelMap): String {
        modelMap.addAttribute("createLessonForm", CreateLessonForm())
        return CREATE_LESSON
    }

    @PostMapping("/create-form")
    fun submitCreateLesson(@Valid createLessonForm: CreateLessonForm, errors: Errors, modelMap: ModelMap): String {
        if (errors.hasErrors()) {
            return CREATE_LESSON
        }
        val id = repository.createLesson(createLessonForm)
        return lessonCard(modelMap, id)
    }

    @GetMapping("/update-form/{id}")
    fun getUpdateLessonForm(@PathVariable("id") id: Long, modelMap: ModelMap): String {
        val lesson = repository.getLesson(id)!!
        modelMap.addAttribute("updateLessonForm", fromLesson(lesson))
        return UPDATE_LESSON
    }

    @PostMapping("/update-form/{id}")
    fun submitUpdateLesson(
        @PathVariable("id") id: Long,
        @Valid form: UpdateLessonForm,
        errors: Errors,
        modelMap: ModelMap
    ): String {
        if (errors.hasErrors()) {
            return UPDATE_LESSON
        }
        repository.updateLesson(form, id)
        return lessonCard(modelMap, id)
    }

    @GetMapping("/update-cancel/{id}")
    fun cancelUpdate(@PathVariable("id") id: Long, modelMap: ModelMap): String {
        return lessonCard(modelMap, id)
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    fun deleteLesson(@PathVariable("id") id: Long): String {
        repository.deleteLesson(id)
        return "Deleted $id"
    }

    private fun lessonCard(modelMap: ModelMap, id: Long): String {
        val lesson = repository.getLesson(id)
        modelMap.addAttribute("lesson", lesson)
        return LESSON_CARD
    }

    companion object {
        const val CREATE_LESSON = "admin/create-lesson"
        const val UPDATE_LESSON = "admin/update-lesson"
        const val LESSON_CARD = "fragments/lesson-card"
    }
}