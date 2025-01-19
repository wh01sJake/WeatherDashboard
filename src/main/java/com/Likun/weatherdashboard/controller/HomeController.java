package com.Likun.weatherdashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home() {
        // This will serve the `index.html` from the resources/templates/ directory
        return "forward:/index.html";
    }
}