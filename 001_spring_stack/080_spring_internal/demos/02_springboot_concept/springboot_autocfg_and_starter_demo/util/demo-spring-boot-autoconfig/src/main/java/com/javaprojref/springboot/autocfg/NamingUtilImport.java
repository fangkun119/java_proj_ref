package com.javaprojref.springboot.autocfg;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class NamingUtilImport implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {
            // 在数组中填入用来加载Bean的@Configuration类的全路径，可以有多个
            NamingUtil.class.getName()
        };
    }
}
