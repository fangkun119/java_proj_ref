package com.javaref.spring.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.javaref.spring.dao.UserDao;
import com.javaref.spring.entity.User;

/**
 * Service层负责处理具体业务逻辑，比如校验账号密码等
 */

@Service
public class MainService {
	// UserDao是一个接口，有两个具体实现
	//	UserDaoMysqlImpl	：用@Repository("daoMysql")注解
	//	UserDaoSqlServerImpl：用@Repository("daoSS")注解
	// 除了 @Autowired，还需要加上@qualifier(..) 来指定用哪一个实现来装配
	@Autowired
	@Qualifier("daoMysql")
	UserDao dao;
	
	public User login(String loginName, String password) {
		System.out.println("loginName:" + loginName);
		System.out.println("Service received the request");

		User user = dao.getUserByName(loginName);
		System.out.println(ToStringBuilder.reflectionToString(user));
		return user;
	}
}
