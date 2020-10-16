package com.javaref.springboot.mapper;

import com.javaref.springboot.entity.Role;
import com.javaref.springboot.mapper.RoleExample;
import org.springframework.stereotype.Repository;

/**
 * 先用Mybatis Generator生成
 *  Permission，PermissionMapper，PermissionMapper.xml, Role, RoleMapper，RoleMapper.xml
 *      只生成entity表，两张relation表不用生成，
 *      另外Menu表晚些时候再处理
 * 生成后PermissionExample, RoleExample要从entity包移到mapper包，方式Mybatis扫包时遇到同名的内部类冲突
 * 把这两个*Example类移到mapper包之后，
 *      resource/mybatis/mapper/PermissionMapper.xml, RoleMapper.xml中
 *      对两个Example的引用，也要确保包名改为了mapper
 */
@Repository
public interface RoleMapper extends MyBatisBaseDao<Role, Integer, RoleExample> {
}