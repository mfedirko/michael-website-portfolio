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
    public static final String CREATE_LESSON = "create-lesson";
    public static final String CREATE_LESSON_SUCCESS = "create-lesson-success";
    public static final String UPDATE_LESSON = "update-lesson";
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
        repository.createLesson(createLessonForm);
        return CREATE_LESSON_SUCCESS;
    }

    @GetMapping("/update-form/{id}")
    public String getUpdateLessonForm(@PathVariable("id") long id, ModelMap modelMap) {
        Lesson lesson = repository.getLesson(id);
        modelMap.addAttribute("updateLessonForm", UpdateLessonForm.fromLesson(lesson));
        return UPDATE_LESSON;
    }

    @PostMapping("/update-form/{id}")
    public String submitUpdateLesson(@PathVariable("id") long id, @Valid UpdateLessonForm form, Errors errors, ModelMap modelMap) {
        form = form.withCreationTimestampMillis(id);
        if (errors.hasErrors()) {
            return UPDATE_LESSON;
        }
        repository.updateLesson(form);
        Lesson lesson = repository.getLesson(id);
        modelMap.addAttribute("lesson", lesson);
        return LESSON_CARD;
    }
}
