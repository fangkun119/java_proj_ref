package com.javaref.springboot04.service;

import com.javaref.springboot04.dao.CityDAO;
import com.javaref.springboot04.domain.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    @Autowired
    CityDAO cityDAO;

    public List<City> findAll() {
        return cityDAO.findAll();
    }

    public String add(City city) {
        try {
            cityDAO.save(city);
            return "保存成功";
        } catch (Exception e) {
            return "保存失败";
        }
    }
}
