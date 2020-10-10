package com.javaref.springbootmvc.springbootmvc03.service;

import com.javaref.springbootmvc.springbootmvc03.entity.Account;
import com.javaref.springbootmvc.springbootmvc03.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    AccountRepository accRep;

    public RespStat save(Account account) {
        try {
            Account entity = accRep.save(account);
        } catch (Exception e) {
            return new RespStat(500, "error", e.getMessage());
        }
        return new RespStat(200, "ok", "save success");
    }

    public List<Account> findAll() {
        // 下面的findAll()是JpaRepository<Account, Integer> 接口自带方法
        return accRep.findAll();
    }

    public Optional<Account> findById(int id)  {
        // 下面的findById()是JpaRepository<Account, Integer> 接口自带方法
        return accRep.findById(id);
    }

    public List<Account> findByIdBetween(int max, int min) {
        // 下面的findByIdBetween 是自定义方法
        // 在接口AccountRepository中添加方法声明，JPA根据格式约定生成对应的HQL (Hibernate QL)
        //      Hibernate: select account0_.id as id1_0_, account0_.age as age2_0_, account0_.location as location3_0_, account0_.login_name as login_na4_0_, account0_.nick_name as nick_nam5_0_, account0_.password as password6_0_ from account account0_ where account0_.id between ? and ?
        // 方法声明与HQL对应关系：http://www.voidcn.com/article/p-rjsyvrwl-boh.html
        return accRep.findByIdBetween(max, min);
    }

    public Optional<Account> findByLoginNameAndPassword(String loginName, String password) {
        // 下面的findByLoginNameAndPassword 是自定义方法
        // 在接口中AccountRepository中添加方法声明，JPA根据格式约定生成对应的HQL
        return accRep.findByLoginNameAndPassword(loginName, password);
    }

    public List<Account> queryWithHQL1() {
        // 下面的 queryWithHQL1 是自定义方法
        // 不是通过命名约定生成HQL（Hibernate QL），而是通过在注解中传入HQL来实现查询
        return accRep.queryWithHQL1();
    }

    public List<Account> queryWithHQL2(int id) {
        // 下面的 queryWithHQL2 是自定义方法
        // 不是通过命名约定生成HQL（Hibernate QL），而是通过在注解中传入HQL来实现查询
        return accRep.queryWithHQL2(id);
    }
}
