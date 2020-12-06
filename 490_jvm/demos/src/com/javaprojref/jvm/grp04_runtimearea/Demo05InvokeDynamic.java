package com.javaprojref.jvm.grp04_runtimearea;

public class Demo05InvokeDynamic {
    public static void main(String[] args) {
        I i1 = C::n; // lambda表达式，相当于 I i1 = () -> {C.n();}
        I i2 = C::n;
        I i3 = C::n;
        System.out.println(i1.getClass());
        System.out.println(i2.getClass());
        System.out.println(i3.getClass());
        // 输出如下、都是由lambda动态产生的内部类：
        // class com.javaprojref.jvm.grp04_runtimearea.Demo05InvokeDynamic$$Lambda$1/764977973
        // class com.javaprojref.jvm.grp04_runtimearea.Demo05InvokeDynamic$$Lambda$2/668386784
        // class com.javaprojref.jvm.grp04_runtimearea.Demo05InvokeDynamic$$Lambda$3/1329552164

        //for(;;) {I j = C::n;} //MethodArea <1.8 Perm Space (FGC不回收)
    }

    // 是一个Functional Interface，任何一个具体的方法，都可以认为是这个interface的一个具体对象
    @FunctionalInterface
    public interface I {
        void m();
    }

    public static class C {
        static void n() {
            System.out.println("hello");
        }
    }
}
