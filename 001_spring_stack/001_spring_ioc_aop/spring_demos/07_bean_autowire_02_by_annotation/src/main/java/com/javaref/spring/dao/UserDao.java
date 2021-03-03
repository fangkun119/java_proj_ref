package com.javaref.spring.dao;

import com.javaref.spring.entity.User;

public interface UserDao {

	public User getUserByName(String name);
}
