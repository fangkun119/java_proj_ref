package com.javaref.springboot.mapper;

import com.javaref.springboot.entity.Account;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface AccountMapper extends MyBatisBaseDao<Account, Integer, AccountExample> {
	// 在AccountMapper中创建selectByPermission方法
	// 然后再到AccountMapper.xml中配置这个方法所有对应的SQL，由MyBatis来生成具体实现，包括
	// 		<selectByPermissionResultMap>
	//		<select id="selectByPermission" resultMap="selectByPermissionResultMap">
	List<Account> selectByPermission();
}