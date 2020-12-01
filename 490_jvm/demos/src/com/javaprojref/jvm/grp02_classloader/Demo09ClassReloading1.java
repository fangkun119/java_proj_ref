package com.javaprojref.jvm.grp02_classloader;

public class Demo09ClassReloading1 {
    public static void main(String[] args) throws Exception {
        Demo05SelfDefinedClassLoader selfDefinedClassLoader= null;

        // 用自定义加载器加载
        selfDefinedClassLoader = new Demo05SelfDefinedClassLoader();
        Class clazzOld = selfDefinedClassLoader.loadClass("com.javaprojref.jvm.Hello");
        // 输出：
        // load class from file: /Users/fangkun/test/com/javaprojref/jvm/Hello.class

        // 用自定义加载器再次加载
        //      如果解除下面这行的注释，会load出新的Class<Hello>，而之前旧ClassLoader加载的Class，也将会被GC
        //      selfDefinedClassLoader = new Demo05SelfDefinedClassLoader();
        Class clazzNew = selfDefinedClassLoader.loadClass("com.javaprojref.jvm.Hello");
        // 输出：无

        // 因为双亲委派机制，
        // 第二次loadClasss时，会先调用最底层ClassLoader即`selfDefinedClassLoader`的`findLoadedClass`,
        // 此时发现com.javaprojref.jvm.Hello已经被加载，因此直接返回，而不会再次加载
        System.out.println("clazzOld.hashCode(): " + clazzOld.hashCode());
        System.out.println("clazzNew.hashCode(): " + clazzNew.hashCode());
        System.out.println("clazzOld==clazzNew? : " + (clazzOld == clazzNew));
        // 输出：
        // clazzOld.hashCode(): 1580066828
        // clazzNew.hashCode(): 1580066828
        // clazzOld==clazzNew? : true
    }
}
