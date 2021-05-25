package com.javaprojref.springbootl.autocfg_starter_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.javaprojref.springboot.autocfg.DateUtil;

@RestController
public class HelloController {
    @Autowired
    public DateUtil util;

    @GetMapping("/test")
    public String test() {
        return util.getLocalTime();
    }
}