package io.mfedirko.admin;

import io.mfedirko.learning.CreateLessonForm;
import io.mfedirko.learning.LearningRepository;
import io.mfedirko.learning.Lesson;
import io.mfedirko.learning.UpdateLessonForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/learning")
@RequiredArgsConstructor
public class LearningAdminController {
    public static final String CREATE_LESSON = "admin/create-lesson";
    public static final String UPDATE_LESSON = "admin/update-lesson";
    public static final String LESSON_CARD = "fragments/lesson-card";
    private final LearningRepository repository;

    @GetMapping("/create-form")
    public String getCreateLessonForm(ModelMap modelMap) {
        modelMap.addAttribute("createLessonForm", CreateLessonForm.builder().build());
        return CREATE_LESSON;
    }

    @PostMapping("/create-form")
    public String submitCreateLesson(@Valid CreateLessonForm createLessonForm, Errors errors, ModelMap modelMap) {
        if (errors.hasErrors()) {
            return CREATE_LESSON;
        }
        long id = repository.createLesson(createLessonForm);
        return lessonCard(modelMap, id);
    }

    @GetMapping("/update-form/{id}")
    public String getUpdateLessonForm(@PathVariable("id") long id, ModelMap modelMap) {
        Lesson lesson = repository.getLesson(id);
        modelMap.addAttribute("updateLessonForm", UpdateLessonForm.fromLesson(lesson));
        return UPDATE_LESSON;
    }

    @PostMapping("/update-form/{id}")
    public String submitUpdateLesson(@PathVariable("id") long id, @Valid UpdateLessonForm form, Errors errors, ModelMap modelMap) {
        if (errors.hasErrors()) {
            return UPDATE_LESSON;
        }
        repository.updateLesson(form, id);
        return lessonCard(modelMap, id);
    }

    @GetMapping("/update-cancel/{id}")
    public String cancelUpdate(@PathVariable("id") long id, ModelMap modelMap) {
        return lessonCard(modelMap, id);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteLesson(@PathVariable("id") long id) {
        repository.deleteLesson(id);
        return "Deleted " + id;
    }

    private String lessonCard(ModelMap modelMap, long id) {
        Lesson lesson = repository.getLesson(id);
        modelMap.addAttribute("lesson", lesson);
        return LESSON_CARD;
    }
}
