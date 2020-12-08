package com.javaprojref.jvm.grp05_gc;

// 不设下列run configuration时、运行时间600左右
// 设置下列run configuration时、运行时间1000左右
// -XX:-DoEscapeAnalysis        # 关闭逃逸分析（"-"表示关闭）
// -XX:-EliminateAllocations    # 关闭标量替换优化
// -XX:-UseTLAB                 # 关闭线程专有对象分配
// -Xlog:grp05_gc*

// 栈上分配：
//  * 线程私有小对象
//  * 无逃逸，只在某一段代码中被使用
//  * 支持标量替换，例如class T { int m; int n }可以用两个int来替代

// TLAB：Thread Local Allocation Buffer
//  * 占用eden、默认1%
//  * 多线程的时候不用竞争eden就可以申请空间、以提高效率
//  * 小对象

public class TestTLAB {
    class User {
        int id;
        String name;
        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    void alloc(int i) {
        new User(i, "name " + i); // 创建的对象没有逃逸
    }

    public static void main(String[] args) {
        TestTLAB t = new TestTLAB();
        long start = System.currentTimeMillis();
        for(int i=0; i<1000_0000; i++) t.alloc(i);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
