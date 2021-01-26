package com.javaref.springboot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

// 因为项目的主类被@MapperScan(value = "com.javaref.springboot.mapper")注解
// Mapper扫描可以扫到这里，因此AccountMapper也可以不使用@Mapper注解
// @Mapper
public interface AccountMapper {
	// SQL配置在AccountMapper.xml中
	List<Account> findAllByXMLBinding();
	void add(Account account);

	// 不需要向AccountMapper.xml中添加配置
	@Select("select * from account")
	List<Account> findAllByAnnotation();
}
