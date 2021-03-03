package com.javaref.spring.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.javaref.spring.dao.UserDao;
import com.javaref.spring.entity.User;


/**
 * 可能遇到这样的场景：一个线程创建连接、一个关闭连接，但是都在同一个Connection上执行
 * 需要用ThreadLocal来线程隔离
 *
 * 因此用@Component注解时，还要加上@Scope("prototype")使其成为非单例模型
 */

//@Repository与@Component一样，让类可以被自动装配
@Repository("daoMysql")
public class UserDaoMysqlImpl implements UserDao {
	@Autowired()
	User user;
	
	public User getUserByName(String name) {
		System.out.println(this.getClass().getCanonicalName() + ": get user by name");
		return user;
	}

}
