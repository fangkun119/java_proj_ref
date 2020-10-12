package com.javaref.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaref.springboot.mapper.Account;
import com.javaref.springboot.mapper.AccountMapper;

@Service
public class AccountService {
	@Autowired
	AccountMapper mapper;

	public List<Account> findAll() {
		return mapper.findAllByXMLBinding();
		//return mapper.findAll();
	}

	public void add() {
		Account account = new Account();
		account.setAge(19);
		account.setLocation("beijing");
		account.setLoginName("xiaoming");
		account.setPassword("123");
		mapper.add(account);
	}
}

