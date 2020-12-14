package com.javaprojref.jvm.grp05_gc;

//-XX:+PrintGCDetails -XX:+UseConcMarkSweepGC -XX:+PrintFlagsFinal -XX:+PrintVMOptions -
public class Demo01HelloGC {
    public static void main(String[] args) {

        for(int i=0; i<10000; i++) {
            byte[] b = new byte[1024 * 1024];
        }
    }
}
