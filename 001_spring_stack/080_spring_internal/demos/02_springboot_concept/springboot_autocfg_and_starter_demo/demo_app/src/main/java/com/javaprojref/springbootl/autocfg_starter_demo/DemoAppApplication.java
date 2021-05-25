package com.javaprojref.springbootl.autocfg_starter_demo;

import com.javaprojref.springboot.autocfg.TokenConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(TokenConfig.class) //启动时加载starter中TokenConfig（未包含在META-INF/spring.factories中）所配置的Bean
public class DemoAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoAppApplication.class, args);
    }
}
