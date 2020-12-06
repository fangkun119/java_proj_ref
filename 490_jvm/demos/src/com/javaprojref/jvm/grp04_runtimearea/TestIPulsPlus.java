package com.javaprojref.jvm.grp04_runtimearea;

public class TestIPulsPlus {
    public static void main(String[] args) {
        int i = 8;
        //i = i++;  // 输出：8
        i = ++i;    // 输出：9
        System.out.println(i);
    }
}
