package com.javaprojref.springinternal.demo2autowire;

import com.javaprojref.springinternal.demo1reflection.UserService;

public class UserController {
    // 用@Autowired注解的私有成员变量，没有set方法
    @Autowired
    private UserService userService;
    public UserService getUserService() {
        return userService;
    }

    // 没有使用Autowired注解的私有成员变量
    private Integer notAutowired;
}
