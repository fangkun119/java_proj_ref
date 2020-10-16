package com.javaref.springboot.service;

import java.util.List;

import com.javaref.springboot.entity.Permission;
import com.javaref.springboot.mapper.PermissionExample;
import com.javaref.springboot.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class PermissionService {

	@Autowired
    PermissionMapper pMapper;
	
	public PageInfo<Permission> findByPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		PermissionExample example = new PermissionExample();
		List<Permission> list = pMapper.selectByExample(example);

		return new PageInfo<>(list);
	}

	public Permission findById(int id) {
		return pMapper.selectByPrimaryKey(id);
	}

	public void update(Permission permission) {
		/*
			PermissionExample example = new PermissionExample();
			example.createCriteria().andIdEqualTo(permission.getId());
			pMapper.updateByExample(permission, example);
		 */
		// Selective表示只更新有值的
		pMapper.updateByPrimaryKeySelective(permission);
	}

	public void add(Permission permission) {
		pMapper.insert(permission);
	}
}

