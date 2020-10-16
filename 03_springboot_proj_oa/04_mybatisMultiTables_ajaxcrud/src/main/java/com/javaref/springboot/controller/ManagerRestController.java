package com.javaref.springboot.controller;

import com.javaref.springboot.RespStat;
import com.javaref.springboot.entity.Permission;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javaref.springboot.service.PermissionService;

/**
 * restful风格URI的controller
 * 只和用户交换JSON数据
 */

//用@RestController来注解，Rest的Request路径要加上版本号
@RestController
@RequestMapping("/api/v1/manager/permission")
public class ManagerRestController {
	@Autowired
	PermissionService permissionSrv;

	// 添加
	@RequestMapping("add")
	public RespStat add(
			// 参数用@RequestBody注解时，前端发过来的必须是Json对象，否则会报Unsupported Media Type错误
			@RequestBody Permission perm
	) {
		System.out.println("perm:" + ToStringBuilder.reflectionToString(perm));
		permissionSrv.add(perm);
		return RespStat.build(200);
	}

	// 更新
	@RequestMapping("update")
	public RespStat update(
			// 参数用@RequestBody注解时，前端发过来的必须是Json对象，否则会报Unsupported Media Type错误
			@RequestBody Permission permission
	) {
		System.out.println("permission:" + ToStringBuilder.reflectionToString(permission));
		permissionSrv.update(permission);
		return RespStat.build(200);
	}
}
