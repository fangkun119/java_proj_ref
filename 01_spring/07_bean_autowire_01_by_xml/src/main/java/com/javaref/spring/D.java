package com.javaref.spring;

public class D {
    public D() {
        super();
        System.out.println("D Init.");
    }

    public E getE() {
        return e;
    }

    public void setE(E e) {
        this.e = e;
    }

    private E e;
}
