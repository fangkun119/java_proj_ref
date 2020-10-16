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


    public PageInfo<Account> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize); //设置AOP

        AccountExample example = new AccountExample();
        List<Account> accountList = accountMapper.selectByExample(example);

        // 构造PageInfo时，设置分页器只显示临近的5页（默认是8页）
        return new PageInfo<>(accountList, 5);

        // 点到PageInfo的代码中，
        // 可以看到PageInfo给前端的分页控件交互时，会用到哪些信息，这可以帮助我们理解还如何实现分页的前后端交互
        // 不过PageHelper已经提供了该功能，就不需要自己实现了
        /*
        pageNum, pageSize, size, startRow, endRow, pages
        prePage, nextPage, isFirstPage, isLastPage,
        hasPreviousPage，hasNextPage，navigatePages，
        navigatepageNums, navigateFirstPage, pnavigateLastPage
        */
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
