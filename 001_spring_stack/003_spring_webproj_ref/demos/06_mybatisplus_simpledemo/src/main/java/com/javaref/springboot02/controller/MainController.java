package com.javaref.springboot02.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javaref.springboot02.entity.Account;
import com.javaref.springboot02.mapper.AccountMapper;
import com.javaref.springboot02.service.CityService;
import com.javaref.springboot02.service.IAccountService;

@Controller
@RequestMapping("/city")
public class MainController {
	@Autowired
	CityService citySrv;

	// 简单演示一下，就不写Service类了，直接把Mapper注入进来
	@Autowired
	AccountMapper mapper;
	
	@Autowired
	IAccountService accountSrv;
	
	@RequestMapping("/list")
	public String list(Model map) {
		// 直接调用Mapper
		Account account = mapper.selectById(1);
		System.out.println("account:" + account . getLoginName());

		// 调用Service
		System.out.println("accountSrv.count() : " + accountSrv.count());
		return "list";

		// MyBatis原生的mapper.xml，@注解也都支持
		// 如果用mapper.xml，要在application.properties中配置mapper xml的路径
		// 如果用注解，也一样与使用原生MyBatis相同
	}
}


