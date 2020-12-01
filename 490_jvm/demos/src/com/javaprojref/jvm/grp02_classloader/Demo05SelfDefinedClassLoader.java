package com.javaprojref.jvm.grp02_classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class Demo05SelfDefinedClassLoader extends ClassLoader {

    /**
     * @param name: 类的全名例如：com.javaprojref.jvm.Hello
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 找到.class文件的存放路径
        // 测试文件存放在：/Users/fangkun/test/com/javaprojref/jvm/Hello.class，是在另一个项目中构建的Hello.class
        File f = new File("/Users/fangkun/test/", name.replace(".", "/").concat(".class"));
        try (
            FileInputStream fis = new FileInputStream(f);
        ){
            // 从文件加载come.javaprojref.jvm.Hello并返回
            System.out.println("load class from file: " + f.getPath());
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.findClass(name); //throws ClassNotFoundException
    }

    public static void main(String[] args) throws Exception {
        // 加载
        ClassLoader selfDefinedClassLoader = new Demo05SelfDefinedClassLoader();
        Class clazz   = selfDefinedClassLoader.loadClass("com.javaprojref.jvm.Hello");
        Class clazz1  = selfDefinedClassLoader.loadClass("com.javaprojref.jvm.Hello");
        // 测试
        System.out.println(clazz == clazz1);
        // 输出：true
        // Hello h = (Hello)clazz.newInstance();
        // h.m();
        // 输出：Hello JVM!
        System.out.println(selfDefinedClassLoader.getClass().getClassLoader());
        // 输出：sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println(selfDefinedClassLoader.getParent());
        // 输出：sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println(getSystemClassLoader());
        // 输出：sun.misc.Launcher$AppClassLoader@18b4aac2
    }
}
