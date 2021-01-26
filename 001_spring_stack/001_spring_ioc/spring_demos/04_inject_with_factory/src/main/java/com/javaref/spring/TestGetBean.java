package com.javaref.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		/**************************************************************
		* 动态工厂
		**/
		// 如果不使用Spring，代码是这样写的
		// try { Car car = (new CarFactory()).getCar("audi");}catch (Exception e) { e.printStackTrace();}
		Car car1 = ctx.getBean("car1",Car.class);
		System.out.println("bean car1: " + car1.getName() + " @" + car1.hashCode());

		/**************************************************************
		 * 静态工厂
		 **/
		// 如果不使用Spring，代码是这样写的
		// try { Car car = CarFactory().getCarStatic("audi"); }catch (Exception e) { e.printStackTrace();}
		Car car2 = ctx.getBean("car2", Car.class);
		System.out.println("bean car2: " + car2.getName() + " @" + car2.hashCode());
	}
}
