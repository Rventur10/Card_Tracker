package com.example.Card_Tracker.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {

    @RequestMapping("/")
    public String index() {
        return "index";  // No .html extension needed
    }
}