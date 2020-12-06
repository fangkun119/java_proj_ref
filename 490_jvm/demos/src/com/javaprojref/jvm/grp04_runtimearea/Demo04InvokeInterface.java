package com.javaprojref.jvm.grp04_runtimearea;

import java.util.ArrayList;
import java.util.List;

public class Demo04InvokeInterface {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("hello"); //通过接口调用的方法、会通过invokeinterface

        ArrayList<String> list2 = new ArrayList<>();
        list2.add("hello2"); //直接通过类对象调用的方法、仍然是通过invokevirtual
    }
}
