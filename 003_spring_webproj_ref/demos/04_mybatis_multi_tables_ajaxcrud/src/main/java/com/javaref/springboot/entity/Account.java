package com.javaref.springboot.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String loginName;
    private String password;
    private String nickName;
    private Integer age;
    private String location;

    // 在MyBatis Generator生成的Account类基础上做修改
    // 添加这个Account的角色，与account_role, role联表得到
    private List<Role> roleList;

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    // 添加这个Account具有的权限，与account_role, role, role_permission, permission联表得到
    private List<Permission> permissionList;

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    // 这个字段可以不用了: 从accout表以及代码中移除
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        Account acct = (Account) that;
        return Objects.equals(this.getId(), acct.getId())
                && Objects.equals(this.getLoginName(), acct.getLoginName())
                && Objects.equals(this.getPassword(), acct.getPassword())
                && Objects.equals(this.getNickName(), acct.getNickName())
                && Objects.equals(this.getAge(), acct.getAge())
                && Objects.equals(this.getLocation(), acct.getLocation())
                && Objects.equals(this.getRole(), acct.getLocation())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLoginName(), getPassword(), getNickName(), getAge(), getLocation(), getRole());
    }
}