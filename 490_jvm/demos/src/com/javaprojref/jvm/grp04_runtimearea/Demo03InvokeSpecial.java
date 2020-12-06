package com.javaprojref.jvm.grp04_runtimearea;

public class Demo03InvokeSpecial {
    public static void main(String[] args) {
        Demo03InvokeSpecial t = new Demo03InvokeSpecial();
        t.m(); // final方法是invokevirtual，不是Invokesepcial
        t.n(); // invokespecal
    }

    public final void m() {}
    private void n() {}
}
