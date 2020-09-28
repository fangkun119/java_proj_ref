package com.javaref.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		Person person1 = ctx.getBean("person",Person.class);
		System.out.println(ToStringBuilder.reflectionToString(person1));

		Food food2 = ctx.getBean("food2", Food.class);
		System.out.println(ToStringBuilder.reflectionToString(food2));
	}
}
