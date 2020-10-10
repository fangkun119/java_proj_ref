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

    // 测试url：localhost:8080/list/10
    @RequestMapping("list/{id}")
    public String getOne(@PathVariable("id") Integer id, Model map) {
        City city = citySrv.findOne(id);
        map.addAttribute("city", city);
        return "one";
    }

    // 测试url：localhost:8080/list/
    @RequestMapping("/list")
    public String list(Model map) {
        List<City> cities = citySrv.findAll();
        map.addAttribute("cities", cities);
        return "list";
    }
}
