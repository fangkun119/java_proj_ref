package com.javaref.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

// ToStringBuilder是很好用的工具类，减少代码长度
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TestGetBean {
	public static void main(String[] args) {
		// 指定生成ClassPathXmlApplicationContext的Spring配置文件
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		// 根据配置注入Bean
		Person person1 = ctx.getBean("person1_alias", Person.class);
		System.out.println(ToStringBuilder.reflectionToString(person1));
		// 输出：
		// com.javaref.spring.Person@6321e813[name=Jerry,age=18,food=com.javaref.spring.Food@3224f60b]

		Person person2 = ctx.getBean("person2", Person.class);
		System.out.println(ToStringBuilder.reflectionToString(person2));
		// 输出：
		// com.javaref.spring.Person@63e2203c[name=Tom,age=21,food=com.javaref.spring.Food@1efed156]

		// 空值注入
		Person personWithEmptyName = ctx.getBean("person_with_empty_name", Person.class);
		System.out.println(ToStringBuilder.reflectionToString(personWithEmptyName));
		// 输出：
		// com.javaref.spring.Person@6737fd8f[name=,age=0,food=<null>]

		// 空指针注入
		Person personWithNullName = ctx.getBean("person_with_null_name", Person.class);
		System.out.println(ToStringBuilder.reflectionToString(personWithNullName));
		// 输出：
		// com.javaref.spring.Person@72b6cbcc[name=<null>,age=0,food=<null>]
	}
}
