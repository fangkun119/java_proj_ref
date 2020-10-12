package com.javaref.spring;

public class F {
    public F() {
        super();
        System.out.println("F Init.");
    }

    public D getD() {
        return d;
    }

    public void setD(D d) {
        this.d = d;
    }

    private D d;
}
