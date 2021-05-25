package com.javaprojref.springboot.autocfg;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Java元注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// 使用`NamingUtilImport implements ImportSelector`来经由一组@Configuration类来批量加载Bean
@Import(NamingUtilImport.class)
public @interface EnableNamingUtil {
}
