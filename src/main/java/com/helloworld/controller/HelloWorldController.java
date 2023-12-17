package com.helloworld.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @RequestMapping("/greet")
    public String hello(String value) {
        return "<h1> Welcome %s, you have reached Michael's Portfolio. </h1>".formatted(value);
    }
}
