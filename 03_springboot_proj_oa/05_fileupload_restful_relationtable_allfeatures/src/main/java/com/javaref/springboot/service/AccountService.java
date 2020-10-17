package com.javaref.springboot.service;

import java.util.List;

import com.javaref.springboot.RespStat;
import com.javaref.springboot.entity.Account;
import com.javaref.springboot.mapper.AccountExample;
import com.javaref.springboot.mapper.AccountMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class AccountService {
    @Autowired
    AccountMapper accMapper;

    public Account findByLoginNameAndPassword(String loginName, String password) {
        // 不要使用MybatisGenerator为AccountMapper内置的查询方法，因为这些方法是基于单表的
        // AccountExample example = new AccountExample();
        // example.createCriteria()
        //	    .andLoginNameEqualTo(loginName)
        //	    .andPasswordEqualTo(password);
        // List<Account> accountList = accMapper.selectByExample(example);
        // return null == accountList ? null : accountList.get(0); // loginName unique 索引保证只有一个元素

        // 改用自定义的findByLoginNameAndPassword方法（对应AccountMapper.xml的<select id="findByLoginNameAndPassword"..>标签）
        // 这个方法使用联表来查询，返回的Account中填充了roleList和permissionList;
        Account account = accMapper.findByLoginNameAndPassword(loginName, password);
        return account;
    }

    public List<Account> findAll() {
        AccountExample example = new AccountExample();
        return accMapper.selectByExample(example);
    }

    public PageInfo<Account> findByPage(int pageNum, int pageSize) {
        /*
        List<Account> alist = accMapper.selectByPermission();
        Account account = alist.get(0);

        System.out.println("account getPermissionList:" + account.getPermissionList().size());
        System.out.println("account getRoleList:" + account.getRoleList().size());

        System.out.println("alist.size() + " + alist.size());
        System.out.println(ToStringBuilder.reflectionToString(alist.get(0)));
        PageHelper.startPage(pageNum, pageSize);
         */
        AccountExample example = new AccountExample();
        List<Account> list = accMapper.selectByExample(example);
        return new PageInfo<>(list, 5);
    }

    public RespStat deleteById(int id) {
        // 1. 要提示用户
        // 2. 通过删除标记 数据永远删不掉    / update 只做增，而不是直接改表内容  // 历史数据 表（数据库）  -> 写文本log
        int row = accMapper.deleteByPrimaryKey(id);
        if (row == 1) {
            return RespStat.build(200);
        } else {
            return RespStat.build(500, "删除出错");
        }
    }

    public void update(Account account) {
        // updateByPrimaryKey 会更新所有字段
        // updateByPrimaryKeySelective 只会更新提供的字段
        accMapper.updateByPrimaryKeySelective(account);
    }
}
