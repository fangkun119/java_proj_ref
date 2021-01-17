package com.javaref.spring;

import com.javaref.spring.controller.MainController;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	public static void main(String[] args) {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		// MainController用"@Controller("mainController")"注解，
		// 		使得bean id被指定为"mainController"（而不是默认的不可见的值）
		// 		使得这个位置可以通过ctx.getBean("mainController")来获得该bean
		MainController controller = ctx.getBean("mainController", MainController.class);
		controller.login();
	}
}
