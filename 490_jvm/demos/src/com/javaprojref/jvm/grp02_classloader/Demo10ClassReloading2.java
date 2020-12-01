package com.javaprojref.jvm.grp02_classloader;

import java.io.*;

public class Demo10ClassReloading2 {
    private static class MyLoader extends ClassLoader {
        /**
         * @param name: 类的全名例如：com.javaprojref.jvm.Hello
         */
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            // 找到.class文件的存放路径
            // 测试文件存放在：/Users/fangkun/test/com/javaprojref/jvm/Hello.class，是在另一个项目中构建的Hello.class
            File f = new File("/Users/fangkun/test/", name.replace(".", "/").concat(".class"));
            if(!f.exists()) {
                // 当加载Hello的基类java.util.Object时会走到这里
                return super.loadClass(name);
            }

            try (
                FileInputStream fis = new FileInputStream(f);
            ){
                // 从文件加载come.javaprojref.jvm.Hello并返回
                System.out.println("load class from file: " + f.getPath());
                byte[] b = new byte[fis.available()];
                fis.read(b);

                return defineClass(name, b, 0, b.length);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ClassNotFoundException();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MyLoader m = null;

        m = new MyLoader();
        Class clazzOld = m.loadClass("com.javaprojref.jvm.Hello");

        m = new MyLoader(); // 如果注释掉，会报"java.lang.LinkageError"，"attempted  duplicate class definition for name: "com/javaprojref/jvm/Hello""
        Class clazzNew = m.loadClass("com.javaprojref.jvm.Hello");

        // 两次load
        System.out.println("clazzOld.hashCode(): " + clazzOld.hashCode());
        System.out.println("clazzNew.hashCode(): " + clazzNew.hashCode());
        System.out.println("clazzOld==clazzNew? : " + (clazzOld == clazzNew));
    }
}
