package com.javaref.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.javaref.springboot.mapper.Menu;
import com.javaref.springboot.mapper.MenuExample;
import com.javaref.springboot.mapper.MenuMapper;

@Service
public class MenuService {

	@Autowired
	MenuMapper menuMapper;
	
	public List<Menu> findAll() {
		// MenuExample是MyBatisGenerator(GUI)生成的类、用来封装复杂查询的查询条件
		// 不添加任何条件，直接扫全表
		MenuExample example = new MenuExample();
		return menuMapper.selectByExample(example);
	}

	public Menu findById(Integer id) {
		// MenuExample是MyBatisGenerator(GUI)生成的类、用来封装复杂查询的查询条件
		// 添加查询条件，查询特定数据
		MenuExample example = new MenuExample();
		example.createCriteria().andIdEqualTo(id);

		List<Menu> list = menuMapper.selectByExample(example);
		return list.size() == 1?list.get(0) : null;

		// 由于主键查询是常用的查询，MyBatisGenerator(GUI)生成的MenuMapper类中提供selectByPrimaryKey方法，可以直接使用
		// Menu menu = menuMapper.selectByPrimaryKey(id);
		// return menu;
	}

	public void add() {
		// insert
		Menu menu = new Menu();
		menu.setIndex("0");
		menu.setName("首页");
		menu.setRoles("all");

		// MyBatisGenerator(GUI)生成的MenuMapper类中提供insert方法，可以直接使用
		menuMapper.insert(menu);
	}


	public List<Menu> findByPage(Integer pageNum, Integer pageSize) {
		// 引入
		//     <groupId>com.github.pagehelper</groupId>
		//     <artifactId>pagehelper-spring-boot-starter</artifactId>
		// 之后
		// 函数头部的 PageHelper.startPage(pageNum, pageSize) 代码
		// 可以影响函数底部 menuMapper.selectByExample(example) 生成的SQL
		// 这是pagehelper通过AOP来实现的

		PageHelper.startPage(pageNum, pageSize);
		MenuExample example = new MenuExample();
		// AOP
		return menuMapper.selectByExample(example);
	}
}
