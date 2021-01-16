package com.javaref.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		A a1 = ctx.getBean("A1", A.class);
		System.out.println(a1);

		A a2 = ctx.getBean("A2", A.class);
		System.out.println(a2);

		C c1 = ctx.getBean("C1", C.class);
		System.out.println(c1);

		// 输出: Spring加载applicationContext.xml后初始化A1（不依赖B1）
		// A depends on:  B.hasInitializedObject = false //是否被初始化B1取决于Spring的初始化顺序
		// A init.
		// 输出：Spring加载applicationContext.xml后初始化A2（depends-on="B1")
		// B init.
		// A depends on:  B.hasInitializedObject = true  //强制B1在A1之前初始化
		// A init.
		// 输出：前两个System.out.println
		// com.javaref.spring.A@27c20538
		// com.javaref.spring.A@72d818d1
		// 输出: 因为C1设置了lazy-init="true"，因此它延迟到getBean时才初始化
		// C init.
		// com.javaref.spring.C@59494225
	}
}
