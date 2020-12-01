package com.javaprojref.jvm.grp02_classloader;

public class Demo08AssignParentClassLoader {
    // 先用一个静态变量存储父加载器
    private static Demo05SelfDefinedClassLoader parent = new Demo05SelfDefinedClassLoader();

    // 编写自定义ClassLoader时，在构造函数中调用`protected ClassLoader(ClassLoader parent)`来指定父加载器
    private static class SelfDefinedLoader extends ClassLoader {
        public SelfDefinedLoader() {
            super(parent);
        }
    }
}
