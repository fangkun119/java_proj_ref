package com.javaref.springboot04.domain;

public class City {
    private Integer id;
    private String name;

    public static City create(Integer id, String name) {
        City city = new City();
        city.setId(id);
        city.setName(name);
        return city;
    }

    // 需要由get方法，thymeleaf解析 "<td th:text="city.id"></td>" 时要用
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
