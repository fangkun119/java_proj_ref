package com.javaref.springboot.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

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

import com.github.pagehelper.PageInfo;

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
            return "fail";
        } else {
            // 登录成功，将Account写到Session里，在不同的controller或者前端页面上都能使用
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
    public String list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize,
            Model model) {
        PageInfo<Account> page = accountSrv.findByPage(pageNum, pageSize);
        model.addAttribute("page", page);
        return "/account/list";
    }

    @RequestMapping("/deleteById")
    @ResponseBody
    public RespStat deleteById(int id) {
        // 在stat中标记是否删除成功，并返回给前端
        // TODO: 另外要处理用户删自己的情况
        RespStat stat = accountSrv.deleteById(id);
        return stat;
    }

    // FastDFS
    @RequestMapping("/profile")
    public String profile() {
        return "account/profile";
    }

    @RequestMapping("/fileUploadController")
    public String fileUpload(MultipartFile filename, String password, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("account");
        try {
            // 存放在当前项目的目录下：
            // 缺点是用jar包启动时这是个临时目录，另外路径中如果出现中文路径会转义成url encoding字符串导致无法上传
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            File upload = new File(path.getAbsolutePath(), "static/uploads/" + account.getLoginName());

            // 存放在系统指定的目录中：需要在配置文件的 spring.resources.static-locations 一项中配文件静态目录映射
            // 会有文件备份，维护等问题，也不适合用在虚拟机上
            // File upload = new File(config.getFileBaseDir() + "/uploads/" + account.getLoginName());

            // 后续项目中改成用外部的文件系统来存储

            // 上传
            filename.transferTo(upload);
            account.setPassword(password);
            account.setLocation("/uploads/" + account.getLoginName());
            accountSrv.update(account);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "account/profile";
    }
}

