package com.javaprojref.springboot.autocfg;

import org.springframework.beans.factory.annotation.Autowired;

public class NamingUtil {
    @Autowired
    private UtilProperties props;

    public String getServerName() {
        return props.getServerName();
    }
}
