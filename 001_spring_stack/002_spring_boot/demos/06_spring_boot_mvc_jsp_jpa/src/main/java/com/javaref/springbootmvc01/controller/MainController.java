package com.javaref.springbootmvc01.controller;

import com.javaref.springbootmvc01.entity.City;
import com.javaref.springbootmvc01.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/city")
public class MainController {
    @Autowired
    CityService citySrv;

    // 建表SQL：resources/sql/dbname1.city.sql

    // 测试url：localhost:8080/city/10
    @RequestMapping("{id}")
    public String getOne(@PathVariable("id") Integer id, Model map) {
        City city = citySrv.findOne(id);
        map.addAttribute("city", city);

        // 如果使用thymeleaf（在pom.xml中引入spring-boot-starter-thymeleaf）
        // 返回"one"，Spring Boot会用src/main/resourcestemplates/one.html作为模板来生成返回页面
        // return "one";

        // 如果使用JSP
        // 1. 在pom.xml中不引入spring-boot-starter-thymeleaf，它会去templates下查找thymeleaf模板
        // 2. 在pom.xml中引入jstl, tomcat-embed-jasper，开启对JSP的支持
        // 3. 在application.properties中配置
        //      spring.mvc.view.prefix=/WEB-INF/jsp/
        //      spring.mvc.view.suffix=.jsp
        return "one_jsp"; //Spring Boot会用src/main/web-app/WEB-INF/jsp/one_jsp.jsp来生成返回页面
    }

    // 测试url：localhost:8080/list
    @RequestMapping("list")
    public String list(Model map) {
        List<City> list = citySrv.findAll();
        map.addAttribute("list", list);
        return "list_jsp";
    }
}
