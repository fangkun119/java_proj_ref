package com.javaref.springboot.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.javaref.springboot.RespStat;
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
import com.javaref.springboot.entity.Account;

/**
 * 用户账户相关
 */
@Controller
@RequestMapping("/account")
public class AccountController {
	@Autowired
    AccountService accountSrv;
	
	@Autowired
    Config config;

	// 登录页面
	@RequestMapping("login")
	public String login(Model model) {
		model.addAttribute("config", config);
		return "account/login";
	}

	// 用户登录异步校验
	@RequestMapping("validataAccount")
	@ResponseBody
	public String validataAccount(String loginName,String password,HttpServletRequest request) {
		System.out.println("loginName:" + loginName);
		System.out.println("password:" + password);

		// 1. 直接返回是否登录成功的结果
		// 2. 在Seesion中加入Account对象

		// 至于这段代码是放在Controller中，还是放在Service中，根据公司里统一写法来做
		Account account = accountSrv.findByLoginNameAndPassword(loginName, password);
		if (account == null) {
			return "登录失败";
		}else {
			// 登录成功时，在Session里加上登录对象，好处是不同的controller或者前端页面上都能使用
			request.getSession().setAttribute("account", account);
			return "success";
		}
	}

	// 登出：从Session中删除Account对象即可以，AccountFilter会根据Session中是否有Account对象来进行拦截
	@RequestMapping("/logOut")
	public String logOut(HttpServletRequest request) {
		request.getSession().removeAttribute("account");
		return "index";
	}

	@RequestMapping("/list")
	public String list(
			@RequestParam(defaultValue = "1")  int pageNum,
			@RequestParam(defaultValue = "5" ) int pageSize,
			Model model) {
		// 返回一个分页器
		PageInfo<Account>page = accountSrv.findByPage(pageNum,pageSize);
		model.addAttribute("page", page);
		return "/account/list";
	}
	
	@RequestMapping("/deleteById")
	@ResponseBody
	public RespStat deleteById(int id) {
		// 告知前端、是否删除成功
		RespStat stat = accountSrv.deleteById(id);
		return stat;
	}

	// 下面的头像图片上传功能，暂时先上传到jar包启动后的临时目录中，无法持久存储
	// 后续再改为存储到外部DFS中
	@RequestMapping("/profile")
	public String profile () {
		try {
			  File path = new File(ResourceUtils.getURL("classpath:").getPath());
		        File upload = new File(path.getAbsolutePath(), "static/upload/");
		        System.out.println(upload.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		};
		return "account/profile";
	}

	@RequestMapping("/fileUploadController")
	public String fileUpload (MultipartFile filename,String password) {
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

