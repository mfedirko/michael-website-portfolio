package com.helloworld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GreetingController {
    @RequestMapping("/greet")
    public String greet(String value, ModelMap modelMap) {
        modelMap.addAttribute("name", value);
        return "greeting";
    }
}
