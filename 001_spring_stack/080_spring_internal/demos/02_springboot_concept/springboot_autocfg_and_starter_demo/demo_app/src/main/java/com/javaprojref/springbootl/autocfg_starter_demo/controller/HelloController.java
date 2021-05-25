package com.javaprojref.springbootl.autocfg_starter_demo.controller;

import com.javaprojref.springboot.autocfg.NamingUtil;
import com.javaprojref.springboot.autocfg.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.javaprojref.springboot.autocfg.DateUtil;

@RestController
public class HelloController {
    // 依赖starter会自动加载META-INF/spring.factories中所有@Configure类所配置的Bean，包括DateUtile
    @Autowired
    protected DateUtil dateUtil;

    // 通过DemoApplication类的@Import(TokenConfig.class)来加载成为Bean
    @Autowired
    protected TokenUtil tokenUtil;

    // 通过DemoApplication类的@EnableNamingUtil来加载成为Bean
    @Autowired
    protected NamingUtil namingUtil;

    @GetMapping("/test")
    public String test() {
        return String.format(
                "Time: %s; Token: %s; Server Name: %s",
                dateUtil.getLocalTime(), tokenUtil.getToken(), namingUtil.getServerName());
    }
}