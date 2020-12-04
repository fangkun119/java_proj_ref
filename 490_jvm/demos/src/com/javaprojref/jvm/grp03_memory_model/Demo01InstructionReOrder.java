package com.javaprojref.jvm.grp03_memory_model;

/**
 * 证明会发生指令重排
 */
public class Demo01InstructionReOrder {
    private static int x = 0, y = 0; // 给线程1使用的变量
    private static int a = 0, b = 0; // 给线程2使用的变量

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        for(;;) {
            i++;
            x = 0; y = 0;
            a = 0; b = 0;

            // 线程1的变量x、会受线程2的影响
            Thread one = new Thread(new Runnable() {
                public void run() {
                    //由于线程one先启动，下面这句话让它等一等线程two. 读着可根据自己电脑的实际性能适当调整等待时间.
                    shortWait(100000);
                    a = 1;
                    x = b; // b由另一个线程（线程2）来设置
                }
            });

            // 线程2的变量y、会受线程1的影响
            Thread other = new Thread(new Runnable() {
                public void run() {
                    b = 1;
                    y = a; // a由另一个线程（线程1）来 设置
                }
            });

            one.start(); other.start();
            one.join(); other.join();

            /**
             * 如果没有指令重排，是不应当输出<0,0>组合的，因为
             * (1) `shortWait`来让`a=1; x=b（此时值为0）`最先执行
             * (2) 从线程2第一次执行到`y=a`时，y的值就一定是1
             * 因为输出的组合不应当包括<0,0>，然而程序运行，在走到下面`else`分支很多次运行足够长时间后，会忽然出现一次<0,0></0,0>
             */
            if(x == 0 && y == 0) {
                System.err.println("第" + i + "次 (" + x + "," + y + "）");
                break;
            } else {
                //System.out.println(result);
            }
        }
    }

    public static void shortWait(long interval){
        long start = System.nanoTime();
        long end;
        do {
            end = System.nanoTime();
        } while(start + interval >= end);
    }
}