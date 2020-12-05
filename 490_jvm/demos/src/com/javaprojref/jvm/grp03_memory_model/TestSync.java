package com.javaprojref.jvm.grp03_memory_model;

public class TestSync {
    synchronized void m() {

    }

    void n() {
        synchronized (this) {

        }
    }

    public static void main(String[] args) {

    }
}
