package com.javaref.springbootmvc.springbootmvc03.service;

public class RespStat {
    private int code;
    private String message;
    private String data;

    public RespStat() {
        super();
    }

    public RespStat(int code, String message, String data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
