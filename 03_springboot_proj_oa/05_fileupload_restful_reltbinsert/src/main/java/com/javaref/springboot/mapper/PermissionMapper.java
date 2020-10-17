package com.javaref.springboot.mapper;

import com.javaref.springboot.entity.Permission;

import org.springframework.stereotype.Repository;

/**
 * PermissionMapper继承基类
 */
@Repository
public interface PermissionMapper extends MyBatisBaseDao<Permission, Integer, PermissionExample> {
}