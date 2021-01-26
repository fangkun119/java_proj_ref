package com.javaref.springboot.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.javaref.springboot.RespStat;
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
        // 设置DB查询条件
        AccountExample example = new AccountExample();
        example.createCriteria()
                .andLoginNameEqualTo(loginName)
                .andPasswordEqualTo(password);
        // 调用DAO进行查询
        List<Account> accounts = accountMapper.selectByExample(example);
        if (null == accounts || 0 == accounts.size()) {
            // 数据库中给loginName设置了unique约束
            return null;
        } else {
            return accounts.get(0);
        }
    }

    public List<Account> findAll() {
        AccountExample example = new AccountExample();
        return accountMapper.selectByExample(example);
    }

    public PageInfo<Account> findByPage(int pageNum, int pageSize) {
        // 设置AOP，它会影响底层数据库访问的操作
        PageHelper.startPage(pageNum, pageSize);

        // 调用DAO层返回数据
        AccountExample example = new AccountExample();
        List<Account> accountList = accountMapper.selectByExample(example);

        // 构造PageInfo时，设置分页器只显示临近的5页（默认是8页）
        return new PageInfo<>(accountList, 5);

        // 封装在PageInfo中的数据（也可以看出分页器API的设计思路）
        // pageNum, pageSize, size, startRow, endRow, pages
        // prePage, nextPage, isFirstPage, isLastPage,
        // hasPreviousPage，hasNextPage，navigatePages，
        // navigatepageNums, navigateFirstPage, pnavigateLastPage
    }

    public RespStat deleteById(int id) {
        int deletedRowNum = accountMapper.deleteByPrimaryKey(id);
        if (1 == deletedRowNum) {
            return RespStat.build(200, "删除成功");
        } else {
            return RespStat.build(500, "删除出错");
        }
    }
}
