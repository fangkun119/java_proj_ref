package com.javaprojref.springboot.autocfg;

import org.springframework.beans.factory.annotation.Autowired;

public class TokenUtil {
    @Autowired
    private UtilProperties props;

    public String getToken() {
        return props.getToken();
    }
}
