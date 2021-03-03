package com.javaref.spring.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 这个类用来做ORM映射，需要保证线程安全，内部的成员不能被共享
 * 因此用@Component注解时，还要加上@Scope("prototype")使其成为非单例模型
 *
 * 另外一个选择是，直接用pojo，不放入Spring框架
 */

@Component
@Scope("prototype")
public class Pet {
	@Value("可乐")
	private String name;
}
