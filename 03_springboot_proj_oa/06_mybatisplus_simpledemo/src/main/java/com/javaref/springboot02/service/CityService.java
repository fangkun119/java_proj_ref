package com.javaref.springboot02.service;

import java.util.List;

import com.javaref.springboot02.dao.CityRepository;
import com.javaref.springboot02.entity.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityService {

	@Autowired
	// Repository == Dao
	CityRepository cityRepo;
	
	public List<City> findAll() {

		List<City> findAll = cityRepo.findAll();
		return findAll;
	}

}
