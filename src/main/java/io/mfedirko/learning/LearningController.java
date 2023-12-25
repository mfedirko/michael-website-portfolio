package io.mfedirko.learning;

import io.mfedirko.common.util.DateHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Clock;
import java.time.Year;
import java.util.List;

@Controller
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {
    private final LearningRepository learningRepository;

    @GetMapping
    public String getLearningPage(@RequestParam("page") int page, ModelMap modelMap) {
        Year year = DateHelper.toLocalYearByPage(page, Clock.systemDefaultZone());
        List<Lesson> lessons = learningRepository.findLessons(year);
        modelMap.addAttribute("lessons", lessons);
        modelMap.addAttribute("nextPage", page + 1);
        return "learning";
    }

}
