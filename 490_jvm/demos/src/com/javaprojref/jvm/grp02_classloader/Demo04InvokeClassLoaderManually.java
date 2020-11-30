package com.javaprojref.jvm.grp02_classloader;

public class Demo04InvokeClassLoaderManually {
    public static void main(String[] args) throws ClassNotFoundException {
        Class clazz = Demo04InvokeClassLoaderManually.class.getClassLoader().loadClass(
                "com.javaprojref.jvm.grp02_classloader.Demo04InvokeClassLoaderManually");
        System.out.println(clazz.getName());
        // 输出：
        // com.javaprojref.jvm.grp02_classloader.Demo04InvokeClassLoaderManually
    }
}
