package com.javaprojref.jvm.grp02_classloader;

public class Demo11ClassLoadLinkInitSteps {
    public static void main(String[] args) {
        System.out.println("T1.count: " + T1.count);
        // 输出：3
        // 分析：
        //  (1) 访问T1.count，触发T1的class loading，使用AppClassLoader
        //  (2) loading之后，进入linking阶段，为静态变量赋默认值
        //      T1.count值：0
        //      T1.t的值：null，没有调用构造方法
        //  (3) linking之后，进入initialization阶段，为静态变量赋初始值
        //      T1.count值：2
        //      T1.t值：代码`new T()`创建的对象
        System.out.println("T2.count: " + T2.count);
        // 输出：2
        // 分析：
        //  (1) 访问T2.count，触发T2的class loading，使用AppClassLoader
        //  (2) loading之后，进入linking阶段，为静态变量赋默认值
        //      T2.count值：0
        //      T2.t的值：null，没有调用构造方法
        //  (3) linking之后，进入initialization阶段，为静态变量赋初始值
        //      T2.t值：代码`new T()`创建的对象
        //      T2.count值：3，因为上面`new T()`时执行了count++
    }
}

class T1 {
    public static int count = 2;
    public static T1 t = new T1();
    private int m = 8; // 非静态成员变量，new对象的时候才创建，过程是：(1)申请内存（2）赋默认值0，（3）执行构造函数设初始值为8
    private T1() {
        count ++; //这样的代码不规范，对静态变量的初始化操作，应当放在静态代码块中
    }
}

class T2 {
    public static T2 t = new T2();
    public static int count = 2;
    private T2() {
        count ++; //这样的代码不规范，对静态变量的初始化操作，应当放在静态代码块中
    }
}
