package com.mashibing.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
// Note: ToStringBuilder是很好用的工具类，减少代码长度
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TestGetBean {
	public static void main(String[] args) {
		// Note: 指定生成ClassPathXmlApplicationContext的Spring配置文件
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		// Note: 根据配置注入Bean
		Person person1 = ctx.getBean("person1_alias", Person.class);
		System.out.println(ToStringBuilder.reflectionToString(person1));

		Person person2 = ctx.getBean("person2", Person.class);
		System.out.println(ToStringBuilder.reflectionToString(person2));
	}
}
