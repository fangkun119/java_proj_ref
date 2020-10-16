package com.javaref.springboot.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.javaref.springboot.entity.Account;
import com.javaref.springboot.mapper.AccountExample;
import com.javaref.springboot.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountMapper  accountMapper;

    public Account findByLoginNameAndPassword(String loginName, String password) {
        AccountExample example = new AccountExample();
        example.createCriteria()
                .andLoginNameEqualTo(loginName)
                .andPasswordEqualTo(password);

        // 数据库中给loginName设置了unique约束
        List<Account> accounts = accountMapper.selectByExample(example);
        if (null == accounts || 0 == accounts.size()) {
            return null;
        } else {
            return accounts.get(0);
        }
    }

    public List<Account> findAll() {
        AccountExample example = new AccountExample();
        return accountMapper.selectByExample(example);
    }

    public List<Account> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize); //设置AOP
        AccountExample example = new AccountExample();
        return accountMapper.selectByExample(example);
    }
}
