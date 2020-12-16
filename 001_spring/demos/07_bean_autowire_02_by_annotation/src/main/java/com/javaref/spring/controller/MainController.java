package com.javaref.spring.controller;

import com.javaref.spring.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.javaref.spring.entity.User;

// 1. 	默认的@Component, 以byType的方式自动注入到被@autowire注解的位置，而bean的id不可见
// 		如果想使用TestGetBean.java中"ctx.getBean("mainController", MainController.class)"这样的方法来获取bean，需要为它传入bean id
// 2.	被@Controller("mainController")注解的类，与@Component("mainController")一样可以被自动装配
//		除了@Controller、类似的注解还有@Repository,@Service
@Controller("mainController")
public class MainController {
	/**
	 * Controller层负责逻辑跳转，在Web环境下，由Controller先接入请求
	 */

	// 使用基于xml的自动装配，注入依赖的Service类，需要为属性srv编写get，set方法，还要在xml中写配置文件
	//
	// 使用基于注解的自动装配
	// 1. 首先需要在main函数中所指定的applicationContext.xml中，指定component-scan的package (框架会以byType的方式开启自动装配)
	// 2. 在依赖的类上添加@Component注解（或其他让bean可以自动装配的注解、例如@Controller等）
	// 3. 在要注入的位置，用@Autowired注解标记 (不需要写get,set方法，@Autowired是使用AOP实现的）
	//
	// 基于注解的自动装配，使用的是byType的装配方式，每个被自动装配的Class最多只能有一个bean

	@Autowired
	private MainService srv;
	
	public String login() {
		String loginName = "zhangfg";
		String password = "123456";
		User user = srv.login(loginName,password);
		if(user == null) {
			return "login fail";
		}else {
			return "login success";
		}
	}
}
