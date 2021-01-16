package com.javaref.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	/**
	 * 创建了3个类，A依赖B，B依赖C，C依赖A，构成循环依赖，但scope都是Singleton也就意味着都只会初始化一次
	 * @param args
	 */
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		// 输出
		// A Init.
		// B Init.
		// C Init.
		// D Init.
		// E Init.
		// F Init.

		A singletonA = ctx.getBean("singletonA", A.class);
		System.out.println(ToStringBuilder.reflectionToString(singletonA));
		System.out.println(ToStringBuilder.reflectionToString(singletonA.getSingletonB()));
		System.out.println(ToStringBuilder.reflectionToString(singletonA.getSingletonB().getSingletonC()));
		// 输出：
		// com.javaref.spring.A@71423665[singletonB=com.javaref.spring.B@5abca1e0]
		// com.javaref.spring.B@5abca1e0[singletonC=com.javaref.spring.C@2286778]
		// com.javaref.spring.C@2286778[singletonA=com.javaref.spring.A@71423665]

		B singletonB = ctx.getBean("singletonB", B.class);
		System.out.println(ToStringBuilder.reflectionToString(singletonB));
		System.out.println(ToStringBuilder.reflectionToString(singletonB.getSingletonC()));
		System.out.println(ToStringBuilder.reflectionToString(singletonB.getSingletonC().getSingletonA()));
		// 输出
		// com.javaref.spring.B@5abca1e0[singletonC=com.javaref.spring.C@2286778]
		// com.javaref.spring.C@2286778[singletonA=com.javaref.spring.A@71423665]
		// com.javaref.spring.A@71423665[singletonB=com.javaref.spring.B@5abca1e0]

		E e = ctx.getBean("beanE", E.class);
		System.out.println(ToStringBuilder.reflectionToString(e));
		System.out.println(ToStringBuilder.reflectionToString(e.getF()));
		System.out.println(ToStringBuilder.reflectionToString(e.getF().getD()));
		// 输出
		// com.javaref.spring.E@185d8b6[f=com.javaref.spring.F@67784306]
		// com.javaref.spring.F@67784306[d=com.javaref.spring.D@335eadca]
		// com.javaref.spring.D@335eadca[e=com.javaref.spring.E@185d8b6]

		return;
	}
}
