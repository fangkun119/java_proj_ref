package com.javaprojref.springboot.autocfg;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UtilProperties.class)
public class NamingConfig {
    @Bean
    NamingUtil getNamingUtil() {
        return new NamingUtil();
    }
}
