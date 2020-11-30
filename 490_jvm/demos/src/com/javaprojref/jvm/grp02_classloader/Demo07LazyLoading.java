package com.javaprojref.jvm.grp02_classloader;

// 严格讲应该叫lazy initialzing，
// 因为java虚拟机规范并没有严格规定什么时候必须loading, 但严格规定了什么时候initialzing
public class Demo07LazyLoading {
    public static void main(String[] args) throws Exception {
        // 输入参数1，2，3，……，来运行各个实验
        runTest(5);
    }

    private static void runTest(int testNo) throws ClassNotFoundException {
        switch (testNo) {
            case 1:
                // 没有触发任何加载条件
                P p;
                // 无输出
                break;
            case 2:
                // 子类加载时、父类必须先加载
                X x = new X();
                // 输出
                // P has been loaded
                // X has been loaded
                break;
            case 3:
                // 访问final static成员变量不会触发加载
                System.out.println(P.staticFinalFeild);
                // 输出
                // 8
                break;
            case 4:
                // 访问static变量（non final）会触发加载
                System.out.println(P.staticField);
                // 输出：
                // P has been loaded
                // 9
                break;
            case 5:
                Class.forName("com.javaprojref.jvm.grp02_classloader.Demo07LazyLoading$P"); //$用来表示P是内部类
                // 输出：
                // P has been loaded
                break;
            default:
                break;
        }
        return;
    }

    public static class P {
        final static int staticFinalFeild = 8;
        static int staticField = 9;
        static {
            // 执行到这一句时，说明class P被加载过了
            System.out.println("P has been loaded");
        }
    }

    public static class X extends P {
        static {
            // 执行到这一句时，说明class X被加载过了
            System.out.println("X has been loaded");
        }
    }
}
