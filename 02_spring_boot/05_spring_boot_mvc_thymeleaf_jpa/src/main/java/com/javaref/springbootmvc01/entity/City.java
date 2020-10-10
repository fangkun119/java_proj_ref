package com.javaref.springbootmvc01.entity;

import javax.persistence.*;

// 想让JPA能够识别并处理City类，需要增加相关的javax.persistence.*注解
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

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
