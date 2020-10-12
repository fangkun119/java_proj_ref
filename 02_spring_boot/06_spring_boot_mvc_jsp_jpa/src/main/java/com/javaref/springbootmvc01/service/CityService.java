package com.javaref.springbootmvc01.service;

import com.javaref.springbootmvc01.dao.CityRepository;
import com.javaref.springbootmvc01.entity.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    // 使用JPA，这里要指向一个CityRepository接口，而不是类
    @Autowired
    CityRepository cityRepo;

    public List<City> findAll() {
        return cityRepo.findAll();
    }

    public City findOne(Integer id) {
        return cityRepo.getOne(id);
    }
}
