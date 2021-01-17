package com.javaref.springbootmvc.springbootmvc03.repository;

import com.javaref.springbootmvc.springbootmvc03.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    // 自定义方法: JPA根据方法声明约定，自动生成Hibernate QL以及对应的方法实现
    List<Account> findByIdBetween(int min, int max);
    Optional<Account> findByLoginNameAndPassword(String loginName, String password);

    // 自定义 Hibernate QL
    // 面向对象的select，因此不是select * 而是select一个Account，放在变量acc中，存入返回列表
    @Query("select acc from Account acc where acc.id=1 ")
    List<Account> queryWithHQL1();
    @Query("select acc from Account acc where acc.id=?1 ")
    List<Account> queryWithHQL2(int id);

    // 除了HQL，JPA也支持动态拼接SQL，SQL；
    // 太复杂的查询也放在这一层，但是会使用ES，Solr等
    // 更多JPA相关内容，见附加的JPA文档

    // Spring JPA在跨表查询时，问题会非常多，并且需要深入学习JPA及Hibernate底层的知识
    // 1. 如果使用Spring JPA，尽量在设计上避免在DAO层跨表查询
    // 2. 这也是Spring JPA在企业中使用较少的原因，并且很多公司禁止使用JPA跨表查询
    // 3. N+1问题：
    //  (1) ORM: 一个实体类 <-> 一张表；一个实体类对象 <-> 一张表中的一行数据
    //  (2) 表关联时（例如1:N映射）
    //      如果代码写不好、会解析成N条SQL查询，改起来比较复杂，需要覆盖一些JPA/Hibernate的方法
    //      在team人员经验不齐时，难以保证质量

    // 对比JPA、使用MyBatis会更加灵活和容易掌握；对比JDBC、使用MyBatis更加简洁高效
}
