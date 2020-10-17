package com.javaref.springboot.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.javaref.springboot.entity.Account;
import com.javaref.springboot.entity.Permission;
import com.javaref.springboot.entity.Role;
import com.javaref.springboot.service.AccountService;
import com.javaref.springboot.service.PermissionService;
import com.javaref.springboot.service.RoleService;

// 用户账户相关
@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    AccountService accountSrv;

    @Autowired
    PermissionService permissionSrv;

    @Autowired
    RoleService roleSrv;

    @RequestMapping("accountList")
    public String accountList(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
        PageInfo<Account> page = accountSrv.findByPage(pageNum, pageSize);
        model.addAttribute("page", page);
        return "manager/accountList";
    }

    @RequestMapping("permissionList")
    public String permissionList(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
        PageInfo<Permission> page = permissionSrv.findByPage(pageNum, pageSize);
        model.addAttribute("page", page);
        return "manager/permissionList";
    }

    @RequestMapping("permissionModify")
    public String permissionModify(@RequestParam int id, Model model) {
        Permission permission = permissionSrv.findById(id);
        model.addAttribute("p", permission);
        return "manager/permissionModify";
    }

    @RequestMapping("permissionAdd")
    public String permissionAdd(Model model) {
        return "manager/permissionModify";
    }

    @RequestMapping("roleList")
    public String roleList(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
        PageInfo<Role> page = roleSrv.findByPage(pageNum, pageSize);
        model.addAttribute("page", page);
        return "manager/roleList";
    }

    // 为角色添加、修改权限
    @RequestMapping("rolePermission/{id}")
    public String rolePermission(
            @PathVariable int id,
            Model model, HttpServletRequest request) {
        // debug
        // Object attribute = request.getSession().getAttribute("account");
        // System.out.println("attribute:" + ToStringBuilder.reflectionToString(attribute));

        // 当前角色
        Role role = roleSrv.findById(id);
        System.out.println("role:" + ToStringBuilder.reflectionToString(role));
        // 当前角色的权限
        List<Permission> pList = permissionSrv.findAll();
        // 返回模板路径供thymeleaf生成前端页面，并提供Role和List<Premission>给前端页面
        model.addAttribute("pList", pList);
        model.addAttribute("role", role);
        return "manager/rolePermission";
    }
}
