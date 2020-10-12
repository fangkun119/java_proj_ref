package com.javaref.springbootmvc.springbootmvc03.controller;

import com.javaref.springbootmvc.springbootmvc03.entity.Account;
import com.javaref.springbootmvc.springbootmvc03.service.AccountService;
import com.javaref.springbootmvc.springbootmvc03.service.RespStat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

// application.properties中有 server.servlet.context-path=/account 配置
// 因此下面所有方法、以及static目录下的静态文件对应的url，都要加上/account标签

@Controller
public class MainController {
    @Autowired
    AccountService accSrv;

    // 测试url：http://localhost:8080/account/list
    @GetMapping("list") //相当于 @RequestMapping(value="register", method=RequestMethod.GET)
    public String list() {
        List<Account> accounts = accSrv.findAll();
        System.out.println(ToStringBuilder.reflectionToString(accounts));
        return "list";
    }

    // 测试url: http://localhost:8080/account/register
    @PostMapping("register")
    public String registerPost(HttpServletRequest request, Account account) {
        // 获取表单数据，可以通过HttpServletRequest，也可以通过注入@Entity对象account注入来获取
        // 处于演示需要，这里两种方式一起使用
        String loginName = (String)request.getAttribute("loginName");
        System.out.println("============= Post ============");
        System.out.println("loginName=" + loginName);
        System.out.println("account: " + ToStringBuilder.reflectionToString(account));

        // 注册用户，结果存入context
        RespStat stat = accSrv.save(account);
        request.setAttribute("stat", stat);

        // 返回结果用"register.html"模板来渲染
        return "register";
    }

    // 获取表单数据方法1：从HttpServletRequest中提取
    /*
    @PostMapping("register")
    public String registerPost(HttpServletRequest request) {
        String loginName = (String)request.getAttribute("loginName");
        System.out.println("post: loginName=" + loginName);
        return "register";
    }

    // 获取表单数据方法2：容器根据表单中attribute的"name"，以及@Entity Account对象的属性，自动填充并传入
    @PostMapping("register")
    public String registerPost(Account account) {
        System.out.println("account: " + ToStringBuilder.reflectionToString(account));
        return "register";
    }
     */

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    // 测试url: http://localhost:8080/account/testjpa1
    // 可以先用@RepsonseBody注解，return类型临时改为Object：这样可以直接返回原始数据，在不写前端页面的情况下先调试好后端
    @GetMapping("testjpa1")
    @ResponseBody
    public Object testjpa1() {
        List<Account> accounts = accSrv.findByIdBetween(1, 5);
        return accounts;
    }

    @GetMapping("testjpa2")
    @ResponseBody
    public Optional<Account> testjpa2() {
        return accSrv.findByLoginNameAndPassword("aaa","123");
    }

    @GetMapping("testjpa3")
    @ResponseBody
    public Optional<Account> testjpa3() {
        // 如果执行下面的findById方法，日志会打印H-SQL（Hibernate QL）
        // Optional<Account> acc = accSrv.findById(1);
        return accSrv.findById(1);
    }

    @GetMapping("testjpa4")
    @ResponseBody
    public List<Account> testjpa4() {
        return accSrv.queryWithHQL1();
    }

    @GetMapping("testjpa5")
    @ResponseBody
    public List<Account> testjpa5() {
        return accSrv.queryWithHQL2(1);
    }
}
