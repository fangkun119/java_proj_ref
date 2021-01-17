package com.javaref.springboot.service;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.javaref.springboot.RespStat;
import com.javaref.springboot.entity.Account;
import com.javaref.springboot.mapper.AccountExample;
import com.javaref.springboot.mapper.AccountMapper;

@Service
public class AccountService {
	@Autowired
	AccountMapper accMapper;

	public Account findByLoginNameAndPassword(String loginName, String password) {
		AccountExample example = new AccountExample();
		example.createCriteria()
			.andLoginNameEqualTo(loginName)
			.andPasswordEqualTo(password);
		// loginName的unique约束保证最多返回一个
		List<Account> list = accMapper.selectByExample(example );
		return list.size() == 0? null:list.get(0);
	}

	public List<Account> findAll() {
		AccountExample example = new AccountExample();
		return accMapper.selectByExample(example );
	}

	public PageInfo<Account> findByPage(int pageNum, int pageSize) {
		/*
		// 打印日志，查看AccountMapper.xml中配置的联表查询是否生效
		List<Account> alist = accMapper.selectByPermission();
		if (alist.size() > 0) {
			Account account = alist.get(0);
			System.out.println("account getPermissionList:" + account.getPermissionList().size());
			System.out.println("account getRoleList:" + account.getRoleList().size());
			System.out.println("alist.size() + " + alist.size());
			System.out.println(ToStringBuilder.reflectionToString(alist.get(0)));
		}
		*/

		PageHelper.startPage(pageNum, pageSize);
		AccountExample example = new AccountExample();
		List<Account> list = accMapper.selectByExample(example );
		return new PageInfo<>(list,5);
	}

	public RespStat deleteById(int id) {
		// 1. 要提示用户 
		// 2. 通过删除标记 数据永远删不掉    / update 只做增，而不是直接改表内容  // 历史数据 表（数据库）  -> 写文本log
		int row = accMapper.deleteByPrimaryKey(id);
		if(row == 1) {
			return RespStat.build(200);
		}else {
			return RespStat.build(500,"删除出错");
		}
	}
}
