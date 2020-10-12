package com.javaref.springboot.controller;

import java.util.List;

import com.javaref.springboot.mapper.Account;
import com.javaref.springboot.mapper.Menu;
import com.javaref.springboot.service.AccountService;
import com.javaref.springboot.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	@Autowired
    AccountService accSrv;

	@Autowired
    MenuService menuSrv;

	// 测试url：http://localhost:8080/account
	@RequestMapping("/account")
	@ResponseBody
	public Object listAccount () {
		List<Account>  accounts = accSrv.findAll();
		return accounts;
	}

	// 测试url：http://localhost:8080/addAccount
	@RequestMapping("/addAccount")
	@ResponseBody
	public Object add () {
		accSrv.add();
		return "ok";
	}

	// 测试url：http://localhost:8080/menu
	@RequestMapping("/menu")
	@ResponseBody
	public Object listMenus () {
		List<Menu> menus =  menuSrv.findAll();
		return menus;
	}

	// 测试url：http://localhost:8080/addMenu
	@RequestMapping("/addMenu")
	@ResponseBody
	public Object addMenu () {
		menuSrv.add();
		return "ok";
	}

	// 测试url：http://localhost:8080/menuQuery?id=2
	@RequestMapping("/menuQuery")
	@ResponseBody
	public Object menuQuery (@RequestParam Integer id) {
		Menu menu = menuSrv.findById(id);
		return menu;
	}

	// 测试url：http://localhost:8080/page?pageNum=2&pageSize=2
	@RequestMapping("/page")
	@ResponseBody
	public Object page (
			// @RequestParam(required = false)：url中没有该参数时，传入null
			@RequestParam(required = false) Integer pageNum,
			@RequestParam(required = false) Integer pageSize
	) {
		List<Menu> menus = menuSrv.findByPage(pageNum,pageSize);
		return menus;
	}
}

