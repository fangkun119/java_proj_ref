package com.javaref.springboot.controller;

import com.javaref.springboot.entity.Account;
import com.javaref.springboot.entity.Permission;
import com.javaref.springboot.service.AccountService;
import com.javaref.springboot.service.PermissionService;
import com.javaref.springboot.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;


/**
 * 用户账户相关
 */

// 分成几个不同的Controller：AccountController，ManagerController便于分工
@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    AccountService accountSrv;

    // 增加Permission Service
	@Autowired
    PermissionService permissionSrv;

	// 增加Role Service
	@Autowired
    RoleService roleSrv;

    // 添加对应于前端页面3个新增链接的Controller
    // "系统设置" -> "账号列表"
	@RequestMapping("accountList")
    public String accountList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5" ) int pageSize,
            Model model) {
    	PageInfo<Account>page = accountSrv.findByPage(pageNum,pageSize);
		model.addAttribute("page", page);
    	return "manager/accountList";
    }

    // "系统设置" -> "权限管理"
    @RequestMapping("permissionList")
    public String permissionList(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "5" ) int pageSize,Model model) {
    	PageInfo<Permission>page = permissionSrv.findByPage(pageNum,pageSize);
		model.addAttribute("page", page);
    	return "manager/permissionList";
    }

    // "系统设置" -> "权限管理" -> "新建/修改权限"
    @RequestMapping("permissionModify")
    public String permissionModify(@RequestParam int id,Model model) {
    	Permission permission = permissionSrv.findById(id);
    	model.addAttribute("p", permission);
    	return "manager/permissionModify";
    }
    
    @RequestMapping("permissionAdd")
    public String permissionAdd(Model model) {
    	return "manager/permissionModify";
    }
    
    @RequestMapping("roleList")
    public String roleList(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "5" ) int pageSize,Model model) {
    	PageInfo<Account>page = accountSrv.findByPage(pageNum,pageSize);
    	model.addAttribute("page", page);
    	return "manager/roleList";
    }
    
    
	
}
