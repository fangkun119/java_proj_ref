package com.javaref.spring;

import com.javaref.spring.controller.MainController;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
	
		MainController controller = ctx.getBean("mainController", MainController.class);
		controller.login();
	}
}
