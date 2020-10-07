package com.javaref.springbootmvc01.dao;

import com.javaref.springbootmvc01.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

// 使用JPA，
//  DAO接口需要extends JpaRepository<DataType, Key>
//  继承JpaRepository之后，再回到CityService调用这个DAO
//  就会发现，它已经有了很多常用的方法
//
// 不需要加类似@Repository的注解
// 因为JpaRepository有@NoRepositoryBean注解，该注解一层层点上去，最终可以找到@Repository注解
public interface CityRepository extends JpaRepository<City, Integer> {
}
