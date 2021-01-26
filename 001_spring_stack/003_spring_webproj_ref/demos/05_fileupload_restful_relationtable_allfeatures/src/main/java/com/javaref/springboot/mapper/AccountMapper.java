package com.javaref.springboot.mapper;

import com.javaref.springboot.entity.Account;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * AccountMapper继承基类
 */
@Repository
public interface AccountMapper extends MyBatisBaseDao<Account, Integer, AccountExample> {

    List<Account> selectByPermission();

    Account findByLoginNameAndPassword(String loginName, String password);
}