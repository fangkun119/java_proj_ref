package com.javaref.spring.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * 这个类用来做ORM映射，需要保证线程安全，内部的成员不能被共享
 *
 * 如果使用Spring框架托管这个bean
 * > 除了用@Component注解
 * > 还要加上@Scope("prototype")使其成为非单例模型
 * 另外一个选择是，直接用pojo，不放入Spring框架
 */

@Component
@Scope("prototype")
public class User {
	@Value("zhangfg")
	private String loginName;
	// 注入简单数据类型
	@Value("123123")
	private String password;
	// 注入对象引用
	// Pet类用@Component @Scope("prototype")注解，因此也是一个非单例的bean
	@Autowired
	private Pet pet;
}

