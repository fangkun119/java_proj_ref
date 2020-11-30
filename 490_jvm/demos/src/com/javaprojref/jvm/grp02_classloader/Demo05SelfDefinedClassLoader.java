package com.javaprojref.jvm.grp02_classloader;

import com.javaprojref.jvm.Hello;

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
        // 例如com.javaprojref.jvm.Hello，其class文件存放位置为../com/javaprojref/jvm/Hello.class
        File f = new File("./src/", name.replace(".", "/").concat(".class"));
        try (
                // use try-with-resource on AutoClosable and AutoClose objects
                FileInputStream fis = new FileInputStream(f);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ){
            // 从文件读入，写入到二进制输出流(ByteArrayOutputStream)中
            int buf = 0;
            while ((buf=fis.read()) !=0) {
                baos.write(buf);
            }
            // 转换为二进制数组
            byte[] bytes = baos.toByteArray();
            // 将二进制数组转换为Class对象并返回
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
        Hello h = (Hello)clazz.newInstance();
        h.m();
        // 输出：Hello JVM!
        System.out.println(selfDefinedClassLoader.getClass().getClassLoader());
        // 输出：sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println(selfDefinedClassLoader.getParent());
        // 输出：sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println(getSystemClassLoader());
        // 输出：sun.misc.Launcher$AppClassLoader@18b4aac2
    }
}
