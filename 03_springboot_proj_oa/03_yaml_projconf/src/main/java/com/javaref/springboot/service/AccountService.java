package com.javaref.springboot.service;

import java.util.List;

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
		
		// accountName列的unique索引保证只有一条
		List<Account> list = accMapper.selectByExample(example );
		return list.size() == 0? null:list.get(0);
	}

	public List<Account> findAll() {
		AccountExample example = new AccountExample();
		return accMapper.selectByExample(example );
	}

	public PageInfo<Account> findByPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		AccountExample example = new AccountExample();
		List<Account> list = accMapper.selectByExample(example );
		// 翻页器默认翻到最近8页，可以指定页数
		return new PageInfo<>(list,5);
	}

	public RespStat deleteById(int id) {
		// 关于删除功能：
		// 1. 要提示用户避免误删
		// 2. 软删除：通过删除标记 数据永远删不掉
		// 		update 只做增，而不是直接改表内容
		// 		历史数据 表（数据库）  -> 写文本log

		// 这里的实现比较简单，只做了提示用户避免误删的功能
		int row = accMapper.deleteByPrimaryKey(id);
		if(row == 1) {
			return RespStat.build(200);
		}else {
			return RespStat.build(500,"删除出错");
		}
	}
}
