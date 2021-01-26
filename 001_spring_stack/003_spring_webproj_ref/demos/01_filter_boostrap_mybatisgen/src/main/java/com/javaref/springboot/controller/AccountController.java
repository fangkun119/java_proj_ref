package com.javaref.springboot.controller;

import com.javaref.springboot.entity.Account;
import com.javaref.springboot.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountSrv;

    @RequestMapping("login")
    public String login() {
        return "account/login";
    }

    @RequestMapping("/logOut")
    public String logout(HttpServletRequest req) {
        req.getSession().removeAttribute("account");
        return "index";
    }

    @RequestMapping("/list")
    public String list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize,
            Model model) {
        List<Account> accountList = accountSrv.findByPage(pageNum, pageSize);
        model.addAttribute("accountList", accountList);
        return "/account/list";
    }

    @RequestMapping("validateAccount")
    @ResponseBody
    public String validate(String loginName, String password, HttpServletRequest request) {
        // 调试的时候看一下是否能接收到请求
        // System.out.println("loginName: " + loginName);
        // System.out.println("password: " + password);

        // 不应当在Controller中写业务逻辑
        // 但是对于下面非常简单的逻辑要不要迁移到Service类中，主要看项目的代码规范
        Account account = accountSrv.findByLoginNameAndPassword(loginName, password);
        if (null == account) {
            return "fail";
        }  else  {
            // 把Account放到Session中 ，可以做更多事情，接口更加通用
            request.getSession().setAttribute("account", account);
            return "success";
        }
    }
}


