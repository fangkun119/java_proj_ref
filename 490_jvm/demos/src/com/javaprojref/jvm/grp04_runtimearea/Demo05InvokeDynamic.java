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

        // 用lambda表达式时要注意以下问题
        // 下面代码会在MethodArea产生大量的对象，造成性能浪费（指1.8及之后的版本、它们将方法区是现在Meta Space中；而1.8之前方法区是现在Perm Space中并且Full GC时不会回收会直接导致OOM）
        // for(;;) {I j = C::n;}
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
