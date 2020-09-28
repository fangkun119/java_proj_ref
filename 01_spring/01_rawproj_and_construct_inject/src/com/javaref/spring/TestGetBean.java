package com.javaref.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {

	public static void main(String[] args) {

		// 如果不使用Spring，传统的依赖管理方法如下
		//  Person person = (Person)ctx.getBean("person");
		//  Food food = ctx.getBean("food",Food.class);
		//  food.setName("apple");
		//	person.setName("zhangsan");
		//	person.setAge(18);
		//	person.setFood(food);

		// 可以在创建ClassPathXmlApplicationContext时传入多个配置文件，如下：
		// 	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
		// 		"applicationContext.xml","application-service.xml");

		// 也可以只传入一个配置文件，然后在这个文件中通过<import>标签来引用其他的配置文件
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		Person person = (Person)ctx.getBean("person");

		System.out.println(ToStringBuilder.reflectionToString(person,ToStringStyle.MULTI_LINE_STYLE));;
		System.out.println(ToStringBuilder.reflectionToString(ctx,ToStringStyle.MULTI_LINE_STYLE));;
	}
}
