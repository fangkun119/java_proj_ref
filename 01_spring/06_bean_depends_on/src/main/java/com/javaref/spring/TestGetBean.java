package com.javaref.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		A a1 = ctx.getBean("A1",A.class);
		System.out.println(a1);

		A a2 = ctx.getBean("A2",A.class);
		System.out.println(a2);

		// 输出: Spring加载applicationContext.xml后初始化A1（不依赖B1）
		// A depends on:  B.hasInitializedObject = false
		// A init.
		// 输出：Spring加载applicationContext.xml后初始化A2（depends-on="B1")
		// B init.
		// A depends on:  B.hasInitializedObject = true
		// A init.
		// 输出：两个System.out.println
		// com.javaref.spring.A@27c20538
		// com.javaref.spring.A@72d818d1
	}
}
