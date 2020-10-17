package com.javaref.springboot02.dao;

import com.javaref.springboot02.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {

}
