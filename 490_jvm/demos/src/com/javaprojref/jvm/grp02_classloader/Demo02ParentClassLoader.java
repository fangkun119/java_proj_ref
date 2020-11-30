package com.javaprojref.jvm.grp02_classloader;

public class Demo02ParentClassLoader {
    public static void main(String[] args) {
        System.out.println(Demo02ParentClassLoader.class.getClassLoader());
        // 加载器：sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println(Demo02ParentClassLoader.class.getClassLoader().getClass().getClassLoader());
        // 加载器的加载器：null (Bootstrap类加载器)
        System.out.println(Demo02ParentClassLoader.class.getClassLoader().getParent());
        // 父加载器：sun.misc.Launcher$ExtClassLoader@610455d6
        System.out.println(Demo02ParentClassLoader.class.getClassLoader().getParent().getParent());
        // 父加载器的父加载器：null (Bootstrap类加载器）

        // System.out.println(T004_ParentAndChild.class.getClassLoader().getParent().getParent().getParent());
        // 没有更上一层的父加载器了
    }
}
