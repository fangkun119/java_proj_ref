package com.javaref.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class MainController {
    @RequestMapping("")
    public String index() {
        return "index";
    }

    @RequestMapping("index")
    public String index1() {
        return "index";
    }

    @RequestMapping("errorPage")
    public String errorPage(Model model) {
        return "errorPage";
    }
}
