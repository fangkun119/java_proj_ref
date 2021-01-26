package com.javaref.springboot.controller;

import com.github.pagehelper.PageInfo;
import com.javaref.springboot.RespStat;
import com.javaref.springboot.entity.Account;
import com.javaref.springboot.entity.Config;
import com.javaref.springboot.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountSrv;

    @Autowired
    Config config;

    @RequestMapping("login")
    public String login(Model model) {
        model.addAttribute("config", config);
        return "account/login";
    }

    @RequestMapping("validataAccount")
    @ResponseBody
    public String validataAccount(String loginName, String password, HttpServletRequest request) {
        Account account = accountSrv.findByLoginNameAndPassword(loginName, password);
        if (account == null) {
            return "failed";
        } else {
            request.getSession().setAttribute("account", account);
            return "success";
        }
    }

    @RequestMapping("/logOut")
    public String logOut(HttpServletRequest request) {
        request.getSession().removeAttribute("account");
        return "index";
    }

    @RequestMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
        PageInfo<Account> page = accountSrv.findByPage(pageNum, pageSize);
        model.addAttribute("page", page);
        return "/account/list";
    }

    @RequestMapping("/deleteById")
    @ResponseBody
    public RespStat deleteById(int id) {
        RespStat stat = accountSrv.deleteById(id);
        return stat;
    }

    // FastDFS
    @RequestMapping("/profile")
    public String profile() {
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            File upload = new File(path.getAbsolutePath(), "static/upload/");
            System.out.println(upload.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "account/profile";
    }

    @RequestMapping("/fileUploadController")
    public String fileUpload(MultipartFile filename, String password) {
        System.out.println("password:" + password);
        System.out.println("file:" + filename.getOriginalFilename());
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            File upload = new File(path.getAbsolutePath(), "static/upload/");
            System.out.println("upload:" + upload);
            filename.transferTo(new File(upload + "/" + filename.getOriginalFilename()));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "profile";
    }
}
