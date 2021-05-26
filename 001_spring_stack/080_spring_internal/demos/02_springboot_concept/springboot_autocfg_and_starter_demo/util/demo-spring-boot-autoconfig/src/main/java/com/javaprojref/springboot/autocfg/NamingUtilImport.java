package com.javaprojref.springboot.autocfg;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class NamingUtilImport implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {
                // 在数组中（使用全路径）填入用来加载成Bean的类
                // 可以用来配置了多个Bean的@Configuration类
                NamingConfig.class.getName()
                // 也可以是不同的类
                // NamingUtil.class.getName()
        };
    }
}
