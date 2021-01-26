package com.javaref.springboot.controller.rest;

import com.javaref.springboot.RespStat;
import com.javaref.springboot.entity.Permission;
import com.javaref.springboot.service.PermissionService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * restful风格URI的controller
 * 只和用户交换JSON数据
 *
 * @author Administrator
 */

@RestController
@RequestMapping("/api/v1/manager/permission")
public class PermissionManagerRestController {
    @Autowired
    PermissionService permissionSrv;

    // 新建或更新Permission
    @PostMapping("set")
    public RespStat setPermission(@RequestBody Permission permission) {
        System.out.println("permission:" + ToStringBuilder.reflectionToString(permission));
        // 检查是add，还是update
        if (permission.getId() == null) {
            permissionSrv.add(permission);
        } else {
            permissionSrv.update(permission);
        }
        // 返回结果
        return RespStat.build(200);
    }
}
