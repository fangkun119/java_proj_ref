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
        // CityRepository extends JpaRepository<City, Integer>之后，具有findAll方法
        // JpaRepository<City, Integer>是个接口
        // 该接口的方法由JPA来实现
        return cityRepo.findAll();
    }

    public City findOne(Integer id) {
        return cityRepo.getOne(id);
    }
}
