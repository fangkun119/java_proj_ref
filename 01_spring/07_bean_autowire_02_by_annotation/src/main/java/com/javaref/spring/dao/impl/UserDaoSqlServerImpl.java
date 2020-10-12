package com.javaref.spring.dao.impl;

import com.javaref.spring.dao.UserDao;
import com.javaref.spring.entity.User;
import org.springframework.stereotype.Repository;

@Repository("daoSS")
public class UserDaoSqlServerImpl implements UserDao {
	public User getUserByName(String name) {
		System.out.println("UserDaoSqlServerImpl");
		return null;
	}
}
