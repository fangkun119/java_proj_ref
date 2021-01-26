package com.javaref.springboot.mapper;

import org.springframework.stereotype.Repository;

/**
 * 用 https://github.com/zouzg/mybatis-generator-gui 自动生成（勾选Query by Example）
 * 类：Menu，MenuExample, MenuMapper, MyBatisBaseDap
 * 配置：AccountMapper.xml
 */
@Repository
public interface MenuMapper extends MyBatisBaseDao<Menu, Integer, MenuExample> {
}