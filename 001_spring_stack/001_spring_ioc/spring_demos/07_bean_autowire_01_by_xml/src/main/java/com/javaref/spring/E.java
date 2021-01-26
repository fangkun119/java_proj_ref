package com.javaref.spring;

public class E {
    public E() {
        super();
        System.out.println("E Init.");
    }

    public F getF() {
        return f;
    }

    public void setF(F f) {
        this.f = f;
    }

    private F f;
}
