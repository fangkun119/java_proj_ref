package com.javaprojref.springboot.autocfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UtilProperties.class)
public class TokenConfig {
    @Bean
    public TokenUtil accessToken() {
        return new TokenUtil();
    }
}
