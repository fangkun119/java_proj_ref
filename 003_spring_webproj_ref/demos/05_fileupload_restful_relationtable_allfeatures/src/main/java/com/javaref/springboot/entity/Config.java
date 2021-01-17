package com.javaref.springboot.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 系统配置相关
 *
 * @author Administrator
 */
@Component
public class Config {

    @Value(value = "${config.systemName}")
    private String systemName;

    @Value(value = "${config.fileBaseDir}")
    private String fileBaseDir;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getFileBaseDir() {
        return fileBaseDir;
    }

    public void setFileBaseDir(String fileBaseDir) {
        this.fileBaseDir = fileBaseDir;
    }
}
