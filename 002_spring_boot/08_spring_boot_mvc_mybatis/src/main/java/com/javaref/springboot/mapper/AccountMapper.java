package com.javaref.springboot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//因为SpringBootMvc04Application被@MapperScan(value = "com.javaref.springboot.mapper")注解
//因此AccountMapper也可以不使用@Mapper注解
//@Mapper
public interface AccountMapper {
	List<Account> findAllByXMLBinding();

	@Select("select * from account")
	List<Account> findAllByAnnotation();

	void add(Account account);
}
