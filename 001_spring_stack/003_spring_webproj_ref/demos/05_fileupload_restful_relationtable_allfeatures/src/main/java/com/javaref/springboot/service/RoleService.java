package com.javaref.springboot.service;

import com.javaref.springboot.entity.Role;
import com.javaref.springboot.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.javaref.springboot.mapper.RoleExample;

@Service
public class RoleService {

    @Autowired
    RoleMapper roleMapper;

    public PageInfo<Role> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        RoleExample example = new RoleExample();
        return new PageInfo<>(roleMapper.selectByExample(example));
    }

    public Role findById(int id) {
        return roleMapper.findById(id);
    }

    public void addPermission(int id, int[] permissions) {
        //	for (int p : permissions) {
        //		roleMapper.addPermission(id,p);
        //	}

        // 对应于RoleMapper.xml中的 <insert id="addPermissions">...</insert>
        // 因为这个操作执行了两个SQL，需要在spring.datasource.url中加上allowMultiQueries=true
        // 例如：
        // spring.datasource.url=jdbc:mysql://localhost:3306/ssm?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
        roleMapper.addPermissions(id, permissions);
    }
}
