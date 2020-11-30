package com.javaprojref.jvm.grp02_classloader;


public class Demo01ClassLoaderLevel {
    public static void main(String[] args) {
        /**
         * 查看类是被哪个ClassLoader类加载到内存中的
         */
        System.out.println(String.class.getClassLoader());
        //  输出 null，说明是被bootstrap层的加载器加载
        System.out.println(sun.awt.HKSCS.class.getClassLoader());
        //  输出 null，说明是被bootstrap层的加载器加载
        System.out.println(sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader());
        //  输出 sun.misc.Launcher$ExtClassLoader@6f94fa3e，被Extension层的ClassLoader加载
        System.out.println(Demo01ClassLoaderLevel.class.getClassLoader());
        //  输出 sun.misc.Launcher$AppClassLoader@18b4aac2，被App层的ClassLoader加载

        /**
         * 查看某个类的ClassLoader的ClassLoader
         */
        System.out.println(sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader().getClass().getClassLoader());
        //  输出 null，说明是bootstrap层的加载器
        System.out.println(Demo01ClassLoaderLevel.class.getClassLoader().getClass().getClassLoader());
        //  输出 null，说明是bootstrap层的加载器

        /**
         * 查看一个自定义加载器的父加载器
         */
        System.out.println(new Demo05SelfDefinedClassLoader().getParent());
        //  输出 sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println(ClassLoader.getSystemClassLoader());
        //  输出 sun.misc.Launcher$AppClassLoader@18b4aac2
    }
}

