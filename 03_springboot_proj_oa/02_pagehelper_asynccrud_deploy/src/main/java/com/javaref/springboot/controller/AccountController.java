package com.javaref.springboot.controller;

import com.github.pagehelper.PageInfo;
import com.javaref.springboot.RespStat;
import com.javaref.springboot.entity.Account;
import com.javaref.springboot.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        PageInfo<Account> page = accountSrv.findByPage(pageNum, pageSize);
        model.addAttribute("page", page);
        return "/account/list";
    }

    @RequestMapping("deleteById")
    @ResponseBody
    public RespStat deleteById(int id) {
        return accountSrv.deleteById(id);
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

    // 这里目前还是一个不成熟的实现，
    // 实际应用中，不应当上传到虚拟机内部的目录，而是应当上传到外部的DFS中
    @RequestMapping("/profile")
    public String profile () {
        try {
            // 打印出上传文件的存放路径，可以看到目前上传代码的问题
            // 1. 如果路径中有"中文"，会被转义成%XX格式，导致找不到上传目录
            // 2. 目录指向的是jar包运行时生成的临时目录，该目录无法长久保存上传文件
            // 解决办法是：
            // 1. 在application.conf中增加spring.resources.static-locations配置
            //    除了默认路径：spring.resources.static-locations=classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/
            //    增加绝对路径：file:/Users/fangkun/Downloads/uploads/
            // 2. 上传和下载路径都指向这个绝对路径
            // 后续提供更完善的实现，上传到外部的DFS上

            // "classpath:" 指向了jar包运行时，生成的临时目录中的target/classes/目录
            File classPath  = new File(ResourceUtils.getURL("classpath:").getPath());
            // 下面的uploadPath，指向了target/classes/static/upload/目录
            File uploadPath = new File(classPath.getAbsolutePath(), "static/upload/");
            System.out.println(uploadPath.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        };
        return "account/profile";
    }

    @RequestMapping("/fileUploadController")
    public String fileUpload (MultipartFile filename, String password) {
        System.out.println("password:" + password);
        System.out.println("file:" + filename.getOriginalFilename());
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            File upload = new File(path.getAbsolutePath(), "static/upload/");
            System.out.println("upload:" + upload);
            filename.transferTo(new File(upload+"/"+filename.getOriginalFilename()));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "profile";
    }
}


