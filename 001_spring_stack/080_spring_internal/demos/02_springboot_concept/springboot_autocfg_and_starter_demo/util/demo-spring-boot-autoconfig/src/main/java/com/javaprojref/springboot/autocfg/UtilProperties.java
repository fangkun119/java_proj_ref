package com.javaprojref.springboot.autocfg;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "util.date")
public class UtilProperties {
    private Double longitude = 120d;
    private String pattern = "yyyy-MM-dd HH:mm:ss";
    private String token = "d810-tqw8-29dp-bnp8";
    private String serverName = "server-1012";

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
