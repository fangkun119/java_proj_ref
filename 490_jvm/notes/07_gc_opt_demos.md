# GC日志、调优案例及工具

## 1 准备

临时设置别名`java8`,`javac8`别名，以便在不修改Java Home的情况下方便地使用`java 8`来编译和运行程序

> ~~~bash
> _____________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ alias java8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/java
> $ alias javac8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/javac
> ~~~

也可以将这两个别名加到Mac的`~/.bashprofile`中、使其在新打开的终端窗口时自动生效，以下是添加到文件中的内容

> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ cat ~/.bash_profile | grep java
> alias java8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/java
> alias javac8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/javac
> ~~~

## 2 PS(`Parallel Scavenge`) PO（`Parallel Old`) GC日志

代码：[./GCCmdDemo.java](./GCCmdDemo.java)

> ~~~java
> import java.util.List;
> import java.util.LinkedList;
> 
> public class GCCmdDemo {
>    public static void main(String[] args) {
>        System.out.println("HelloGC");
>        List list = new LinkedList();
>        for (;;) {
>            byte[] b = new byte[1024 * 1024];
>            @unchecked
>            list.add(b);
>        }
>    }
> }
> ~~~

编译

> ~~~bash
> _____________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> javac8 -Xlint:unchecked GCCmdDemo.java
> _____________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ ls
> GCCmdDemo.class GCCmdDemo.java  readme.md
> ~~~

用指定的JVM运行参数运行程序

> java -XX:+PrintCommandLineFlags GCCmdDemo
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ java8 -XX:+PrintCommandLineFlags GCCmdDemo
> -XX:InitialHeapSize=134217728 -XX:MaxHeapSize=2147483648 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
> HelloGC
> Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
> 	at GCCmdDemo.main(GCCmdDemo.java:9)
> ~~~
>
> * `-XX:InitialHeapSize=134217728`：起始堆大小
> * `-XX:MaxHeapSize=2147483648`：最大堆大小
> * `-XX:+PrintCommandLineFlags`：打印传递给虚拟机的显式和隐式参数
> * `-XX:+UseCompressedClassPointers`:压缩位于对象头的类指针、从8字节压缩到4字节
> * `-XX:+UseCompressedOops`:压缩普通对象指针
> * `-XX:+UseParallelGC`:使用`Parallel Scavenge` +` Parallel Old` 垃圾回收器
>

让程序运行时打印JVM日志

> java -Xmn10M -Xms40M -Xmx60M -XX:+PrintCommandLineFlags -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCCause GCCmdDemo
>
> * `-Xmn`,`-Xms`,`-Xmx`：指定新生代大小、堆内存最小值、堆内存最大值；其中`-Xms`和`-Xmx`最好设成相等或接近、否则堆内存会反复扩张收缩浪费内存空间
> * `PrintGC`：打印GC回收内存的信息
> * `PrintGCDetails`：打印GC详细信息
> * `PrintGCTimeStamps`：打印GC时间戳
> * `PrintGCCause`：打印GC被触发的原因
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ java8 -Xmn10M -Xms40M -Xmx60M -XX:+PrintCommandLineFlags -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCCause GCCmdDemo
> -XX:InitialHeapSize=41943040 -XX:MaxHeapSize=62914560 -XX:MaxNewSize=10485760 -XX:NewSize=10485760 -XX:+PrintCommandLineFlags -XX:+PrintGC -XX:+PrintGCCause -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
> HelloGC
> 0.084: [GC (Allocation Failure) [PSYoungGen: 7839K->384K(9216K)] 7839K->7560K(39936K), 0.0055741 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
> 0.090: [GC (Allocation Failure) [PSYoungGen: 7712K->336K(9216K)] 14888K->14680K(39936K), 0.0036271 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
> 0.095: [GC (Allocation Failure) [PSYoungGen: 7657K->336K(9216K)] 22001K->21848K(39936K), 0.0039213 secs] [Times: user=0.00 sys=0.01, real=0.00 secs]
> 0.100: [GC (Allocation Failure) [PSYoungGen: 7659K->368K(9216K)] 29172K->29048K(39936K), 0.0050667 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
> 0.105: [Full GC (Ergonomics) [PSYoungGen: 368K->0K(9216K)] [ParOldGen: 28680K->28940K(45568K)] 29048K->28940K(54784K), [Metaspace: 2515K->2515K(1056768K)], 0.0072645 secs] [Times: user=0.02 sys=0.01, real=0.00 secs]
> 0.113: [GC (Allocation Failure) [PSYoungGen: 7325K->128K(9216K)] 36265K->36236K(54784K), 0.0055329 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
> 0.120: [GC (Allocation Failure) [PSYoungGen: 7446K->128K(9728K)] 43555K->43404K(55296K), 0.0047033 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
> 0.125: [Full GC (Ergonomics) [PSYoungGen: 128K->0K(9728K)] [ParOldGen: 43276K->43277K(51200K)] 43404K->43277K(60928K), [Metaspace: 2515K->2515K(1056768K)], 0.0028382 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
> 0.129: [GC (Allocation Failure) --[PSYoungGen: 8365K->8365K(9728K)] 51642K->58810K(60928K), 0.0043126 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
> 0.134: [Full GC (Ergonomics) [PSYoungGen: 8365K->1024K(9728K)] [ParOldGen: 50445K->50445K(51200K)] 58810K->51469K(60928K), [Metaspace: 2515K->2515K(1056768K)], 0.0029302 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
> 0.138: [Full GC (Ergonomics) [PSYoungGen: 8360K->8192K(9728K)] [ParOldGen: 50445K->50445K(51200K)] 58805K->58637K(60928K), [Metaspace: 2515K->2515K(1056768K)], 0.0031193 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
> 0.141: [Full GC (Allocation Failure) [PSYoungGen: 8192K->8192K(9728K)] [ParOldGen: 50445K->50433K(51200K)] 58637K->58626K(60928K), [Metaspace: 2515K->2515K(1056768K)], 0.0081275 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
> Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
> 	at GCCmdDemo.main(GCCmdDemo.java:9)
> Heap
>  PSYoungGen      total 9728K, used 8533K [0x00000007bf600000, 0x00000007c0000000, 0x00000007c0000000)
>   eden space 9216K, 92% used [0x00000007bf600000,0x00000007bfe55698,0x00000007bff00000)
>   from space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
>   to   space 512K, 0% used [0x00000007bff00000,0x00000007bff00000,0x00000007bff80000)
>  ParOldGen       total 51200K, used 50433K [0x00000007bc400000, 0x00000007bf600000, 0x00000007bf600000)
>   object space 51200K, 98% used [0x00000007bc400000,0x00000007bf540648,0x00000007bf600000)
>  Metaspace       used 2546K, capacity 4486K, committed 4864K, reserved 1056768K
>   class space    used 275K, capacity 386K, committed 512K, reserved 1048576K
> ~~~
>

日志解读：

> `Parallel Scavenge`（Miner GC）日志：
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_ps_gc_log.jpg)
>
> `[GC类型(GC原因)[内存代:回收前该代使用量->回收后该代使用量(该代总大小),该代回收花费时间] 回收前堆的使用量->回收后堆的使用量(堆的总大小), 堆回收花费时间][Times:Linux用户态、内核态、总共时间消耗]`
>
> 内存Dump日志
>
> ~~~bash
> Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
> 	at GCCmdDemo.main(GCCmdDemo.java:9)
> Heap
>  PSYoungGen      total 9728K, used 8533K [0x00000007bf600000, 0x00000007c0000000, 0x00000007c0000000)
>   eden space 9216K, 92% used [0x00000007bf600000,0x00000007bfe55698,0x00000007bff00000)
>   from space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
>   to   space 512K, 0% used [0x00000007bff00000,0x00000007bff00000,0x00000007bff80000)
>  ParOldGen       total 51200K, used 50433K [0x00000007bc400000, 0x00000007bf600000, 0x00000007bf600000)
>   object space 51200K, 98% used [0x00000007bc400000,0x00000007bf540648,0x00000007bf600000)
>  Metaspace       used 2546K, capacity 4486K, committed 4864K, reserved 1056768K
>   class space    used 275K, capacity 386K, committed 512K, reserved 1048576K
> ~~~
>
> * `eden space 9216K, 92% used [0x00000007bf600000,0x00000007bfe55698,0x00000007bff00000)`中`[]`内部的数字依次是：空间起始地址；空间被使用部分结束地址；空间结束地址
> * `used`：当前使用；`capacity`：当前容量； `committed`：当前已使用虚拟内存块的总大小；`reserved`：总共预留的虚拟内存大小
> * `class space`是`Metaspace`中专门用来存储`Class`部分的空间
> * `from`,`to`对应两个`survival`分区，`total = eden + survival * 1`，之所以`*1`是因为只有一个`survival`分区在存储数据，另外一个是给`minor gc`搬运数据时当`buffer`用的
>

备注：

> 上面是PSPO的日志，CMS的日志其实也类似，差别主要在于出错时的日志不同，会报`promotion failure`

## 3 调优案例及工具

### 3.1 总体步骤

> 症状：CPU 100%，说明一定有线程在占用系统资源
>
> 1. 找出哪个进程CPU高：`top`命令
> 2. 找出该进程中哪个线程CPU高：`top -Hp`命令
> 3. （如果是java程序）导出该线程的堆栈、看看它都在调用什么：`jstack`
> 4. 查找哪个方法（栈帧）消耗时间：`jstack`

### 3.2 Demo代码

代码：[./FullGCIssueDemo.java](FullGCIssueDemo)

编译和运行：在JVM参数中指定200M内存，打印GC日志

> ~~~bash
> [root@CentOS share]# cd ~/share/
> [root@CentOS share]# javac -version
> javac 1.8.0_191
> [root@CentOS share]# java -version
> java version "1.8.0_191"
> Java(TM) SE Runtime Environment (build 1.8.0_191-b12)
> Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)
> [root@CentOS share]# javac FullGCIssueDemo.java
> [root@CentOS share]# ls FullGCIssueDemo*
> FullGCIssueDemo$1.class         FullGCIssueDemo.class
> FullGCIssueDemo$CardInfo.class  FullGCIssueDemo.java
> [root@CentOS share]# java  -Xms200M -Xmx200M -XX:+PrintGC FullGCIssueDemo
> [GC (Allocation Failure)  54656K->1105K(198016K), 0.0066771 secs]
> [root@CentOS share]# top
> ~~~
>
> 刚起动时，主要是业务线程均匀占用CPU，每个进程的占用量都不多
>
> ~~~bash
>  PID  USER      PR  NI    VIRT    RES    SHR S %CPU %MEM     TIME+ COMMAND
>  2657 root      20   0 2715936  90724  12284 S  0.7  8.9   0:00.05 pool-1-thread-1
>  2669 root      20   0 2715936  90724  12284 S  0.7  8.9   0:00.04 pool-1-thread-1
>  2678 root      20   0 2715936  90724  12284 S  0.7  8.9   0:00.05 pool-1-thread-2
> …………
> ~~~
>
> 程序运行一段时间后，开始出现频繁Full GC问题

### 3.3 定位问题

#### (1) 找到CPU消耗量高的进程

> ~~~bash
>  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND
>  2647 root      20   0 2715936  90436  12256 S   2.7  8.9   0:00.46 java
> ~~~

#### (2) 查看消耗CPU的线程：

> `top -Hp ${进程ID}`（本例是`top -Hp 2647`）
>
> ~~~bash
> PID USER      PR  NI    VIRT    RES    SHR S %CPU %MEM     TIME+ COMMAND
>  2649 root      20   0 2716964 240060  12520 S 47.8 23.7   0:36.27 VM Thread
>  2689 root      20   0 2716964 240060  12520 S  3.3 23.7   0:03.88 pool-1-thread-3
>  2666 root      20   0 2716964 240060  12520 S  2.3 23.7   0:03.76 pool-1-thread-1
>  2671 root      20   0 2716964 240060  12520 S  2.3 23.7   0:04.04 pool-1-thread-1
>  2682 root      20   0 2716964 240060  12520 S  1.7 23.7   0:03.45 pool-1-thread-2
> ~~~
>
> 但运行一段时间之后，开始频繁Full GC，每次只能回收极小的内存（例如`198015K->198015K`）
>
> ~~~bash
> [root@CentOS share]# java  -Xms200M -Xmx200M -XX:+PrintGC FullGCIssueDemo
> [GC (Allocation Failure)  54656K->1105K(198016K), 0.0026228 secs]
> [GC (Allocation Failure)  55761K->23328K(198016K), 0.0717318 secs]
> [GC (Allocation Failure)  77651K->55678K(198016K), 0.1342062 secs]
> [GC (Allocation Failure)  110334K->93303K(198016K), 0.1824356 secs]
> [Full GC (Allocation Failure)  147959K->128338K(198016K), 0.5614952 secs]
> [Full GC (Allocation Failure)  182994K->165305K(198016K), 0.6120991 secs]
> [Full GC (Allocation Failure)  194726K->186605K(198016K), 0.5741825 secs]
> [Full GC (Allocation Failure)  198015K->191900K(198016K), 0.7115490 secs]
> [Full GC (Allocation Failure)  198015K->196388K(198016K), 0.9043872 secs]
> [Full GC (Allocation Failure)  198015K->197577K(198016K), 0.7095628 secs]
> [Full GC (Allocation Failure)  198015K->197910K(198016K), 0.7214470 secs]
> [Full GC (Allocation Failure)  198015K->197995K(198016K), 0.7496549 secs]
> [Full GC (Allocation Failure)  198015K->198013K(198016K), 0.6774704 secs]
> [Full GC (Allocation Failure)  198015K->198015K(198016K), 0.8540173 secs]
> ~~~

#### (3) 用`jstack`查看线程状态

> * nid=Ox***`是`jstack`用的线程ID
> * 线程名称：
>   * `Reference Handler`：处理`Reference`的JVM内部线程
>   * `Finalizer`：垃圾回收线程
>   * `Sweeper threader`： 垃圾回收使用的线程
> * 自己写代码时也要为线程设置明确含义的线程名称，这样会便于debug
>
> ~~~bash
> [root@CentOS share]# jstack 2647 
> 
> ……
> 
> "DestroyJavaVM" #59 prio=5 os_prio=0 tid=0x00007f8ff004b800 nid=0xa58 waiting on condition [0x0000000000000000]
>    java.lang.Thread.State: RUNNABLE
> 
> "Attach Listener" #58 daemon prio=9 os_prio=0 tid=0x00007f8fc8002800 nid=0xafc waiting on condition [0x0000000000000000]
>    java.lang.Thread.State: RUNNABLE
> 
> "pool-1-thread-50" #57 prio=5 os_prio=0 tid=0x00007f8ff027f000 nid=0xa92 waiting on condition [0x00007f8fccfb1000]
>    java.lang.Thread.State: WAITING (parking)
>         at sun.misc.Unsafe.park(Native Method)
>         - parking to wait for  <0x00000000f8ad6b88> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
> 
> ……
> 
> "pool-1-thread-1" #8 prio=5 os_prio=0 tid=0x00007f8ff021b800 nid=0xa61 waiting on condition [0x00007f8ff5752000]
>    java.lang.Thread.State: WAITING (parking)
>         at sun.misc.Unsafe.park(Native Method)
>         - parking to wait for <0x00000000f8ad6b88> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
>         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
>         at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
>         at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:1088)
>         at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:809)
>         at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
>         at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
>         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
>         at java.lang.Thread.run(Thread.java:748)
> 
> "Service Thread" #7 daemon prio=9 os_prio=0 tid=0x00007f8ff0121800 nid=0xa5f runnable [0x0000000000000000]
>    java.lang.Thread.State: RUNNABLE
> 
> "C1 CompilerThread1" #6 daemon prio=9 os_prio=0 tid=0x00007f8ff0116800 nid=0xa5e waiting on condition [0x0000000000000000]
>    java.lang.Thread.State: RUNNABLE
> 
> "C2 CompilerThread0" #5 daemon prio=9 os_prio=0 tid=0x00007f8ff0109800 nid=0xa5d waiting on condition [0x0000000000000000]
>    java.lang.Thread.State: RUNNABLE
> 
> "Signal Dispatcher" #4 daemon prio=9 os_prio=0 tid=0x00007f8ff0107800 nid=0xa5c runnable [0x0000000000000000]
>    java.lang.Thread.State: RUNNABLE
> 
> "Finalizer" #3 daemon prio=8 os_prio=0 tid=0x00007f8ff00da000 nid=0xa5b in Object.wait() [0x00007f8ff61b5000]
>    java.lang.Thread.State: WAITING (on object monitor)
> 	at java.lang.Object.wait(Native Method)
> 	- waiting on <0x00000000f8adc578> (a java.lang.ref.ReferenceQueue$Lock)
> 	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:144)
> 	- locked <0x00000000f8adc578> (a java.lang.ref.ReferenceQueue$Lock)
> 	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:165)
> 	at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:216)
> 
> "Reference Handler" #2 daemon prio=10 os_prio=0 tid=0x00007f8ff00d5800 nid=0xa5a in Object.wait() [0x00007f8ff62b6000]
>    java.lang.Thread.State: WAITING (on object monitor)
> 	at java.lang.Object.wait(Native Method)
> 	- waiting on <0x00000000f8adc730> (a java.lang.ref.Reference$Lock)
> 	at java.lang.Object.wait(Object.java:502)
> 	at java.lang.ref.Reference.tryHandlePending(Reference.java:191)
> 	- locked <0x00000000f8adc730> (a java.lang.ref.Reference$Lock)
> 	at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)
> ~~~

#### (4) 根据`jstack`输出来debug 

>  需要重点关注`WAITING`状态和`BLOCKED`状态：`java.lang.Thread.State: WAITING (parking)` （代表线程在等待某个互斥锁的释放），关注它在`waiting on`什么（例如本例中的`waiting on <0x00000000f8ad6b88> (a java.lang.ref.ReferenceQueue$Lock)`）
>
> * 例如查找死锁原因：找到wait for的锁，是哪个线程持有的：搜索`jstack`打印的信息，找被wait for的`Ox**`（形如本例中的`<0x00000000f8ad6b88>`）看哪个RUNNABLE的线程也持有这把锁
>
> 查找本例的问题：
>
> * 发现有43个线程都在wait for <0x00000000f8ad6b88>，查看其调用栈和相关业务代码，问题发生在`ThreadPoolExecutor.getTask`，与向`ScheduledThreadPoolExecutor`中大量调度任务之间发生阻塞有关
>
> 另外一个思路是，此时系统已经在频繁GC了，如果找到是哪个类被大量创建对象（使用下面介绍的各种工具），也能找到问题的原因

### 3.3 其他`jvm`自带的工具

> CentOS自带的JDK未必提供了这些工具，有可能需要自行下载和安装

#### (1) 用`jinfo`查看进程虚拟机的详细信息 

> ~~~bash
> [root@CentOS share]# jinfo  2647
> Attaching to process ID 2647, please wait...
> Debugger attached successfully.
> Server compiler detected.
> JVM version is 25.191-b12
> Java System Properties:
> 
> java.runtime.name = Java(TM) SE Runtime Environment
> java.vm.version = 25.191-b12
> sun.boot.library.path = /usr/java/jdk1.8.0_191-amd64/jre/lib/amd64
> java.vendor.url = http://java.oracle.com/
> java.vm.vendor = Oracle Corporation
> path.separator = :
> file.encoding.pkg = sun.io
> java.vm.name = Java HotSpot(TM) 64-Bit Server VM
> sun.os.patch.level = unknown
> sun.java.launcher = SUN_STANDARD
> user.country = CN
> user.dir = /root/share
> java.vm.specification.name = Java Virtual Machine Specification
> java.runtime.version = 1.8.0_191-b12
> java.awt.graphicsenv = sun.awt.X11GraphicsEnvironment
> os.arch = amd64
> java.endorsed.dirs = /usr/java/jdk1.8.0_191-amd64/jre/lib/endorsed
> java.io.tmpdir = /tmp
> line.separator =
> 
> java.vm.specification.vendor = Oracle Corporation
> os.name = Linux
> sun.jnu.encoding = UTF-8
> java.library.path = /usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
> java.specification.name = Java Platform API Specification
> java.class.version = 52.0
> sun.management.compiler = HotSpot 64-Bit Tiered Compilers
> os.version = 3.10.0-1127.19.1.el7.x86_64
> user.home = /root
> user.timezone =
> java.awt.printerjob = sun.print.PSPrinterJob
> file.encoding = UTF-8
> java.specification.version = 1.8
> user.name = root
> java.class.path = .
> java.vm.specification.version = 1.8
> sun.arch.data.model = 64
> sun.java.command = FullGCIssueDemo
> java.home = /usr/java/jdk1.8.0_191-amd64/jre
> user.language = zh
> java.specification.vendor = Oracle Corporation
> awt.toolkit = sun.awt.X11.XToolkit
> java.vm.info = mixed mode
> java.version = 1.8.0_191
> java.ext.dirs = /usr/java/jdk1.8.0_191-amd64/jre/lib/ext:/usr/java/packages/lib/ext
> sun.boot.class.path = /usr/java/jdk1.8.0_191-amd64/jre/lib/resources.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/rt.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/sunrsasign.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/jsse.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/jce.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/charsets.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/jfr.jar:/usr/java/jdk1.8.0_191-amd64/jre/classes
> java.vendor = Oracle Corporation
> file.separator = /
> java.vendor.url.bug = http://bugreport.sun.com/bugreport/
> sun.io.unicode.encoding = UnicodeLittle
> sun.cpu.endian = little
> sun.cpu.isalist =
> 
> VM Flags:
> Non-default VM flags: -XX:CICompilerCount=2 -XX:InitialHeapSize=209715200 -XX:MaxHeapSize=209715200 -XX:MaxNewSize=69861376 -XX:MinHeapDeltaBytes=196608 -XX:NewSize=69861376 -XX:OldSize=139853824 -XX:+PrintGC -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps
> Command line:  -Xms200M -Xmx200M -XX:+PrintGC
> ~~~

#### (2) 用`jstat -gc ${pid} ${interval}` 查看进程的GC信息

> 每隔1000毫秒查看一次进程在各个代的内存占用信息
>
> ~~~bash
> [root@CentOS share]# jstat -gc 18074 1000
>  S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
> 6784.0 6784.0 6784.0  0.0   54656.0  14718.9   136576.0   85478.2   4864.0 3855.7 512.0  421.6       4    0.351   0      0.000    0.351
> 6784.0 6784.0 6784.0  0.0   54656.0  15404.2   136576.0   85478.2   4864.0 3855.7 512.0  421.6       4    0.351   0      0.000    0.351
> 6784.0 6784.0 6784.0  0.0   54656.0  15404.2   136576.0   85478.2   4864.0 3855.7 512.0  421.6       4    0.351   0      0.000    0.351
> 6784.0 6784.0 6784.0  0.0   54656.0  15404.2   136576.0   85478.2   4864.0 3855.7 512.0  421.6       4    0.351   0      0.000    0.351
> ~~~

#### (3) 使用`jconsole`图形化界面查看进程GC的信息（会影响服务器性能、用于压力测试而非线上监控）

> 在Linux机器上，以支持JMX的方式启动程序
>
> ~~~bash
> [root@CentOS share]# /usr/java/jdk1.8.0_191-amd64/bin/java -Djava.rmi.server.hostname=192.168.1.104 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=11111 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false FullGCIssueDemo
> ~~~
>
> 在Mac上，用`Finder`进入JDK存放java bin文件的目录，双击jconsole，启动图形化界面
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_jconsole_conn.jpg)
>
> 填入Linux机器上JMX服务的地址和端口后，可以看到图形化的GC信息
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_jconsole_monitor.jpg)

#### (4) 使用`jvisualvm`图形化界面查看进程GC的信息（会影响服务器性能、用于压力测试而非线上监控）

> 在Linux机器上，以支持JMX的方式启动程序
>
> ~~~bash
> [root@CentOS share]# /usr/java/jdk1.8.0_191-amd64/bin/java -Djava.rmi.server.hostname=192.168.1.104 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=11111 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false FullGCIssueDemo
> ~~~
>
> 在Mac上，用`Finder`进入JDK存放java bin文件的目录，双击jvisualvm，启动图形化界面
>
> 添加JMX连接
>
> ![jvm_jvisualvm_conn](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_jvisualvm_conn.jpg)
>
> 连接JMX可以看到相关的信息
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_jvisualvm_monitor.jpg)
>
> 用`堆抽样器`可以看出来是哪个类占用的内存导致了堆被占满，还可以看来是哪个线程分配了大量的内存
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_jvisualvm_heapsampler.jpg)

#### （5）使用`jmap -histo ${pid} | head -n ${top_line_num}`查看有多少对象产生

> * 可以直接看到是哪些类被大量创建内存占用了堆空间，不需要开启JMX
> * 问题是对于大内存系统，`jmap`执行期间会对进程有一定影响，有可能造成系统卡顿等
>
> ~~~bash
> [root@CentOS share]# jps
> 21670 Jps
> 21607 FullGCIssueDemo
> [root@CentOS share]# jmap -histo 21607 | head -n20
>  num     #instances         #bytes  class name
> ----------------------------------------------
>    1:           342       17703560  [I
>    2:        137600        9907200  java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask
>    3:        137626        5505040  java.math.BigDecimal
>    4:        137600        4403200  FullGCIssueDemo$CardInfo
>    5:        137600        3302400  java.util.Date
>    6:        137600        3302400  java.util.concurrent.Executors$RunnableAdapter
>    7:        137600        2201600  FullGCIssueDemo$$Lambda$2/1826771953
>    8:         50737        1623584  java.util.concurrent.locks.AbstractQueuedSynchronizer$Node
>    9:             2        1183456  [Ljava.util.concurrent.RunnableScheduledFuture;
>   10:          2768         418264  [Ljava.lang.Object;
>   11:          1901         144056  [C
>   12:           722          82872  java.lang.Class
>   13:          1890          45360  java.lang.String
>   14:            39          25952  [B
>   15:            56          21056  java.lang.Thread
>   16:           185          13320  java.lang.reflect.Field
>   17:           147          12936  java.lang.reflect.Method
> ~~~

#### （6） Dump Heap内存（注意有可能对系统性能产生较大的影响）

> 有两种方法，一种是设置JVM参数让系统在OOM时dump内存，一种是使用jmap来dump一个运行中程序的堆内存
>
> ~~~bash
> [root@CentOS share]# /usr/java/jdk1.8.0_191-amd64/bin/java -Xms100M -Xmx100M -XX:+PrintGC -XX:+HeapDumpOnOutOfMemoryError FullGCIssueDemo
> [GC (Allocation Failure)  27328K->981K(99008K), 0.0046298 secs]
> [GC (Allocation Failure)  28309K->11964K(99008K), 0.0340912 secs]
> [GC (Allocation Failure)  39292K->28095K(99008K), 0.0597428 secs]
> [GC (Allocation Failure)  55423K->44588K(99008K), 0.0510157 secs]
> [GC (Allocation Failure)  71916K->62236K(99008K), 0.0570480 secs]
> [Full GC (Allocation Failure)  89564K->80700K(99008K), 0.2843741 secs]
> [Full GC (Allocation Failure)  99008K->93247K(99008K), 0.3196756 secs]
> [Full GC (Allocation Failure)  99007K->97515K(99008K), 0.3184651 secs]
> [Full GC (Allocation Failure)  99007K->98546K(99008K), 0.3559635 secs]
> [Full GC (Allocation Failure)  99007K->98864K(99008K), 0.3318949 secs]
> [Full GC (Allocation Failure)  99007K->98965K(99008K), 0.3338175 secs]
> [Full GC (Allocation Failure)  99007K->99001K(99008K), 0.3407357 secs]
> [Full GC (Allocation Failure)  99007K->99005K(99008K), 0.3327970 secs]
> [Full GC (Allocation Failure)  99007K->99007K(99008K), 0.3299050 secs]
> [Full GC (Allocation Failure)  99007K->99007K(99008K), 0.3295439 secs]
> [Full GC (Allocation Failure)  99007K->99007K(99008K), 0.3297128 secs]
> [Full GC (Allocation Failure)  99007K->99007K(99008K), 0.3293066 secs]
> java.lang.OutOfMemoryError: Java heap space
> Dumping heap to java_pid23617.hprof ...
> [Full GC (Allocation Failure)  99007K->99007K(99008K), 0.3313961 secs]
> ~~~
>
> ~~~bash
> [root@CentOS share]# jmap -dump:format=b,file=java_pid23617.hprof 23617
> Dumping heap to /root/share/java_pid23617.hprof ...
> Heap dump file created
> ~~~
>
> * 用`jhat`等工具可以载入dump文件，并用浏览器查看heap信息（如下面的demo）
> * 也可以使用`MAT`等工具、参考：[https://www.cnblogs.com/baihuitestsoftware/articles/6406271.html](https://www.cnblogs.com/baihuitestsoftware/articles/6406271.html)
>
> ~~~bash
> [root@CentOS share]# jhat -J-Xmx1024M java_dump.hprof
> Reading from java_dump.hprof...
> Dump file created Sun Dec 13 20:23:26 CST 2020
> Snapshot read, resolving...
> Resolving 2850637 objects...
> ......
> Snapshot resolved.
> Started HTTP server on port 7000
> Server is ready.
> ~~~
>
> * 考虑到dump堆内存会影响在线服务，在线排查使用`arthas`会更加合适

### 3.4 [`Arthas`](https://arthas.aliyun.com/doc/)在线排查工具

> 排查生产环境问题时、遇到一些不容易排查的问题，用`threaddump`或`heapdump`会收到限制或查不出原因、增加日志又过于繁琐时，使用在线排查工具会比较方便

#### (1) 资料

> * [https://arthas.aliyun.com/doc/](https://arthas.aliyun.com/doc/)
> * [https://github.com/alibaba/arthas](https://github.com/alibaba/arthas)

#### (2) 快速开始

推荐方法：使用`arthas-boot`

> ~~~bash
> [root@CentOS share]# curl -O https://alibaba.github.io/arthas/arthas-boot.jar
>   % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
>                                  Dload  Upload   Total   Spent    Left  Speed
> 100  135k  100  135k    0     0   124k      0  0:00:01  0:00:01 --:--:--  124k
> [root@CentOS share]# ls arthas-boot.jar
> arthas-boot.jar
> [root@CentOS share]# java -jar arthas-boot.jar -h
> [INFO] arthas-boot version: 3.4.5
> Usage: arthas-boot [-h] [--target-ip <value>] [--telnet-port <value>]
>        [--http-port <value>] [--session-timeout <value>] [--arthas-home <value>]
>        [--use-version <value>] [--repo-mirror <value>] [--versions] [--use-http]
>        [--attach-only] [-c <value>] [-f <value>] [--height <value>] [--width
>        <value>] [-v] [--tunnel-server <value>] [--agent-id <value>] [--app-name
>        <value>] [--stat-url <value>] [--select <value>] [pid]
> Bootstrap Arthas
> EXAMPLES:
>   java -jar arthas-boot.jar <pid>
>   java -jar arthas-boot.jar --target-ip 0.0.0.0
>   java -jar arthas-boot.jar --telnet-port 9999 --http-port -1
>   java -jar arthas-boot.jar --tunnel-server 'ws://192.168.10.11:7777/ws'
> --app-name demoapp
> ……
> ~~~
>
>  如果速度慢，也可以使用阿里云的镜像
>
> ~~~bash
> 
> java -jar arthas-boot.jar --repo-mirror aliyun --use-http
> ~~~

其他方法：使用`as.sh`安装

> ~~~bash
> curl -L https://alibaba.github.io/arthas/install.sh | sh
> ~~~

### (3) 将`Arthas` attach到运行中的java程序上

在一个终端窗口启动`FullGCIssueDemo`，让它运行一段时间后开始频繁Full GC

> ~~~bash
> [root@CentOS share]# java -Xms100M -Xmx100M -XX:+PrintGC FullGCIssueDemo
> [GC (Allocation Failure)  27328K->1144K(99008K), 0.0026640 secs]
> [GC (Allocation Failure)  28472K->12127K(99008K), 0.0348546 secs]
> ~~~

在另一个窗口，启动`Arthas`，`Arthas`会找到当前正在运行的java进程并提示要debug哪一个，之后进入`Arthas`交互式命令行

> ~~~bash
> [root@CentOS share]# ls arthas-boot.jar
> arthas-boot.jar
> [root@CentOS share]# java -jar arthas-boot.jar
> [INFO] arthas-boot version: 3.4.5
> [INFO] Found existing java process, please choose one and input the serial number of the process, eg : 1. Then hit ENTER.
> * [1]: 1769 FullGCIssueDemo
> 1
> [INFO] Start download arthas from remote server: https://arthas.aliyun.com/download/3.4.5?mirror=aliyun
> [INFO] Download arthas success.
> [INFO] arthas home: /root/.arthas/lib/3.4.5/arthas
> [INFO] Try to attach process 3078
> [INFO] Found java home from System Env JAVA_HOME: /usr/java/latest
> [INFO] Attach process 3078 success.
> [INFO] arthas-client connect 127.0.0.1 3658
>   ,---.  ,------. ,--------.,--.  ,--.  ,---.   ,---.
>  /  O  \ |  .--. ''--.  .--'|  '--'  | /  O  \ '   .-'
> |  .-.  ||  '--'.'   |  |   |  .--.  ||  .-.  |`.  `-.
> |  | |  ||  |\  \    |  |   |  |  |  ||  | |  |.-'    |
> `--' `--'`--' '--'   `--'   `--'  `--'`--' `--'`-----'
> 
> 
> wiki      https://arthas.aliyun.com/doc
> tutorials https://arthas.aliyun.com/doc/arthas-tutorials.html
> version   3.4.5
> pid       1769
> time      2020-12-14 11:42:59
> 
> [arthas@1769]$
> 
> ~~~

在交互式命令行输入`help`可以查看可用的命令

> ~~~bash
> [arthas@1769]$ help
>  NAME         DESCRIPTION
>  help         Display Arthas Help
>  keymap       Display all the available keymap for the specified connection.
>  sc           Search all the classes loaded by JVM
>  sm           Search the method of classes loaded by JVM
>  classloader  Show classloader info
>  jad          Decompile class
>  getstatic    Show the static field of a class
>  monitor      Monitor method execution statistics, e.g. total/success/failure count, average rt
>               , fail rate, etc.
>  stack        Display the stack trace for the specified class and method
>  thread       Display thread info, thread stack
>  trace        Trace the execution time of specified method invocation.
>  watch        Display the input/output parameter, return object, and thrown exception of specif
>               ied method invocation
>  tt           Time Tunnel
>  jvm          Display the target JVM information
>  perfcounter  Display the perf counter information.
>  ognl         Execute ognl expression.
>  mc           Memory compiler, compiles java files into bytecode and class files in memory.
>  redefine     Redefine classes. @see Instrumentation#redefineClasses(ClassDefinition...)
>  dashboard    Overview of target jvm's thread, memory, gc, vm, tomcat info.
>  dump         Dump class byte array from JVM
>  heapdump     Heap dump
>  options      View and change various Arthas options
>  cls          Clear the screen
>  reset        Reset all the enhanced classes
>  version      Display Arthas version
>  session      Display current session information
>  sysprop      Display, and change the system properties.
>  sysenv       Display the system env.
>  vmoption     Display, and update the vm diagnostic options.
>  logger       Print logger info, and update the logger level
>  history      Display command history
>  cat          Concatenate and print files
>  echo         write arguments to the standard output
>  pwd          Return working directory name
>  mbean        Display the mbean information
>  grep         grep command for pipes.
>  tee          tee command for pipes.
>  profiler     Async Profiler. https://github.com/jvm-profiling-tools/async-profiler
>  stop         Stop/Shutdown Arthas server and exit the console.
> ~~~

#### (4) `Arthas`常用命令

##### `jvm`：相当于之前的`jinfo`命令、查看jvm的详细配置情况

> ~~~bash
> [arthas@1769]$ jvm
>  RUNTIME
> ---------------------------------------------------------------------------------
>  MACHINE-NAME               1769@CentOS
>  JVM-START-TIME             2020-12-14 13:05:19
>  MANAGEMENT-SPEC-VERSION    1.2
>  SPEC-NAME                  Java Virtual Machine Specification
>  SPEC-VENDOR                Oracle Corporation
>  SPEC-VERSION               1.8
>  VM-NAME                    Java HotSpot(TM) 64-Bit Server VM
>  VM-VENDOR                  Oracle Corporation
>  VM-VERSION                 25.191-b12
>  INPUT-ARGUMENTS            -Xms100M
>                             -Xmx100M
>                             -XX:+PrintGC
> 
>  CLASS-PATH                 .
>  BOOT-CLASS-PATH            /usr/java/jdk1.8.0_191-amd64/jre/lib/resources.jar:/usr/java/jdk1.8
>                             .0_191-amd64/jre/lib/rt.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/su
>                             nrsasign.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/jsse.jar:/usr/jav
>                             a/jdk1.8.0_191-amd64/jre/lib/jce.jar:/usr/java/jdk1.8.0_191-amd64/j
>                             re/lib/charsets.jar:/usr/java/jdk1.8.0_191-amd64/jre/lib/jfr.jar:/u
>                             sr/java/jdk1.8.0_191-amd64/jre/classes
>  LIBRARY-PATH               /usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
> 
> ---------------------------------------------------------------------------------
>  CLASS-LOADING
> ---------------------------------------------------------------------------------
>  LOADED-CLASS-COUNT         3442
>  TOTAL-LOADED-CLASS-COUNT   3442
>  UNLOADED-CLASS-COUNT       0
>  IS-VERBOSE                 false
> 
> ---------------------------------------------------------------------------------
>  COMPILATION
> ---------------------------------------------------------------------------------
>  NAME                       HotSpot 64-Bit Tiered Compilers
>  TOTAL-COMPILE-TIME         2078
>  [time (ms)]
> 
> ---------------------------------------------------------------------------------
>  GARBAGE-COLLECTORS
> ---------------------------------------------------------------------------------
>  Copy                       name : Copy
>  [count/time (ms)]          collectionCount : 5
>                             collectionTime : 93
> 
>  MarkSweepCompact           name : MarkSweepCompact
>  [count/time (ms)]          collectionCount : 0
>                             collectionTime : 0
> 
> 
> ---------------------------------------------------------------------------------
>  MEMORY-MANAGERS
> ---------------------------------------------------------------------------------
>  CodeCacheManager           Code Cache
> 
>  Metaspace Manager          Metaspace
>                             Compressed Class Space
> 
>  Copy                       Eden Space
>                             Survivor Space
> 
>  MarkSweepCompact           Eden Space
>                             Survivor Space
>                             Tenured Gen
> 
> 
> ---------------------------------------------------------------------------------
>  MEMORY
> ---------------------------------------------------------------------------------
>  HEAP-MEMORY-USAGE          init : 104857600(100.0 MiB)
>  [memory in bytes]          used : 35131368(33.5 MiB)
>                             committed : 101384192(96.7 MiB)
>                             max : 101384192(96.7 MiB)
> 
>  NO-HEAP-MEMORY-USAGE       init : 2555904(2.4 MiB)
>  [memory in bytes]          used : 26585768(25.4 MiB)
>                             committed : 29032448(27.7 MiB)
>                             max : -1(-1 B)
> 
>  PENDING-FINALIZE-COUNT     0
> 
> ---------------------------------------------------------------------------------
>  OPERATING-SYSTEM
> ---------------------------------------------------------------------------------
>  OS                         Linux
>  ARCH                       amd64
>  PROCESSORS-COUNT           2
>  LOAD-AVERAGE               0.28
>  VERSION                    3.10.0-1127.19.1.el7.x86_64
> 
> ---------------------------------------------------------------------------------
>  THREAD
> ---------------------------------------------------------------------------------
>  COUNT                      65
>  DAEMON-COUNT               14
>  PEAK-COUNT                 65
>  STARTED-COUNT              67
>  DEADLOCK-COUNT             0
> 
> ---------------------------------------------------------------------------------
>  FILE-DESCRIPTOR
> ---------------------------------------------------------------------------------
>  MAX-FILE-DESCRIPTOR-COUNT  4096
>  OPEN-FILE-DESCRIPTOR-COUN  67
>  T
> ~~~
>
> * GARBAGE-COLLECTORS 
>   * Copy：伊甸区会用Copy垃圾回收算法
>   * MarkSweepCompact：老年代会用扫描标记压缩回收算法
> * MEMORY-MANAGERS：可以看到上面列出的垃圾回收算法都用在了哪些内存分区上
> * FILE-DESCRIPTOR：文件句柄使用情况

##### `thread`：各个线程的阻塞情况和CPU使用情况

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_arthas_thread.jpg)

##### `thread ${ARTHAS_THREAD_ID}`：查看线程调用栈

> ~~~bash
> [arthas@1769]$ thread 30
> "pool-1-thread-23" Id=30 WAITING on java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject@3c6b06a5
>     at sun.misc.Unsafe.park(Native Method)
>     -  waiting on java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject@3c6b06a5
>     at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
>     at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
>     at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:1088)
>     at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:809)
>     at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
>     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
>     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
>     at java.lang.Thread.run(Thread.java:748)
> ~~~

##### `dashboard`：查看整体线程情况（类似之前的`top -Hp ${pid}`但功能会更丰富)，有哪些线程，内存情况怎么，哪些线程在消耗CPU

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_arthas_dashboard.jpg)

##### `heapdump`：导出堆文件，相当于`jmap -dump:format=b,file=${file_path} ${pid}`

> 该命令对程序性能也有影响，尽量不要dump，而是使用在线定位
>
> ~~~bash
> [arthas@1769]$ heapdump
> Dumping heap to /tmp/heapdump2020-12-14-13-366916075808810091709.hprof ...
> Heap dump file created
> [arthas@1769]$ heapdump /root/share/heap_dump_FullGCIssueDemo_pid_1769.hprof
> Dumping heap to /root/share/heap_dump_FullGCIssueDemo_pid_1769.hprof ...
> Heap dump file created
> ~~~
>
> 可以使用`jhat`载入dump文件并启动一个http server供交互式分析
>
> ~~~bash
> [root@CentOS share]# ls *hprof
> heap_dump_FullGCIssueDemo_pid_1769.hprof
> [root@CentOS share]# jhat -J-mx512M heap_dump_FullGCIssueDemo_pid_1769.hprof
> Reading from heap_dump_FullGCIssueDemo_pid_1769.hprof...
> Dump file created Mon Dec 14 13:38:10 CST 2020
> Snapshot read, resolving...
> Resolving 2502183 objects...
> ......
> Snapshot resolved.
> Started HTTP server on port 7000
> Server is ready.
> ~~~
>
> 用浏览器访问dump结果
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_arthas_jhat.jpg)
>
> 使用`Show heap histogram`或者`Show instances counts for all classes`查询可以找到哪个类分配了大量对象占用了内存
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_arthas_jhat_histo.jpg)
>
> 使用`Execute Object Query Language (OQL) query`可以查看某个类new了多少个对象、占用多少字节，每个对象有多少个引用等。在某些特别复杂的case中可以用来看是怎样的对象出了问题
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_athas_jhat_oql.jpg)
>
>  出了`jhat`，使用`MAT`,`jvisualvm`也都可以分析heap dump文件（需要jvm版本一致） 

#### (5) Arthas特有的功能

##### `jad`：反编译

>  用于辅助分析动态代理问题、第三方类问题、以及确认程序版本是不是自己最新提交的版本
>
> ~~~bash
> [arthas@1769]$ jad --help
>  USAGE:
>    jad [--classLoaderClass <value>] [-c <value>] [-h] [--hideUnicode] [-E] [--source-only] class-pattern [method-name]
> 
>  SUMMARY:
>    Decompile class
> 
>  EXAMPLES:
>    jad java.lang.String
>    jad java.lang.String toString
>    jad --source-only java.lang.String
>    jad -c 39eb305e org/apache/log4j/Logger
>    jad -c 39eb305e -E org\\.apache\\.*\\.StringUtils
> 
>  WIKI:
>    https://arthas.aliyun.com/doc/jad
> 
>  OPTIONS:
>      --classLoaderClass <value>          The class name of the special class's classLoader.
>  -c, --code <value>                      The hash code of the special class's classLoader
>  -h, --help                              this help
>      --hideUnicode                       hide unicode, default value false
>  -E, --regex                             Enable regular expression to match (wildcard matching by default)
>      --source-only                       Output source code only
>  <class-pattern>                         Class name pattern, use either '.' or '/' as separator
>  <method-name>                           method name pattern, decompile a specific method instead of the whole class
> ~~~

##### `redefine`：热替换

> 目前有限制，只能修改方法实现（方法已经运行完成）、不能改方法名，不能改属性
>
> ~~~bash
> [arthas@2429]$ redefine --help
>  USAGE:
>    redefine [-c <value>] [--classLoaderClass <value>] [-h] classfilePaths...
> 
>  SUMMARY:
>    Redefine classes. @see Instrumentation#redefineClasses(ClassDefinition...)
> 
>  EXAMPLES:
>    redefine /tmp/Test.class
>    redefine -c 327a647b /tmp/Test.class /tmp/Test\$Inner.class
> 
>  WIKI:
>    https://arthas.aliyun.com/doc/redefine
> 
>  OPTIONS:
>  -c, --classloader <value>               classLoader hashcode
>      --classLoaderClass <value>          The class name of the special class's classLoader.
>  -h, --help                              this help
>  <classfilePaths>                        .class file paths
> ~~~

##### `sc`：search class

> 找到某个Class并打印详细信息
>
> ~~~bash
> [arthas@1906]$ sc -d *CardInfo
>  class-info        FullGCIssueDemo$CardInfo
>  code-source       /root/share/
>  name              FullGCIssueDemo$CardInfo
>  isInterface       false
>  isAnnotation      false
>  isEnum            false
>  isAnonymousClass  false
>  isArray           false
>  isLocalClass      false
>  isMemberClass     true
>  isPrimitive       false
>  isSynthetic       false
>  simple-name       CardInfo
>  modifier          private,static
>  annotation
>  interfaces
>  super-class       +-java.lang.Object
>  class-loader      +-sun.misc.Launcher$AppClassLoader@73d16e93
>                      +-sun.misc.Launcher$ExtClassLoader@4be5871e
>  classLoaderHash   73d16e93
> 
> Affect(row-cnt:1) cost in 12 ms.
> ~~~

##### `watch`：观察方法执行

> 方法调用时的参数、返回值、抛出的异常
>
> ~~~bash
> [arthas@1906]$ watch *FullGCIssueDemo getAllCardInfo
> Press Q or Ctrl+C to abort.
> Affect(class count: 1 , method count: 1) cost in 143 ms, listenerId: 1
> ts=2020-12-14 16:10:36; [cost=1.920062ms] result=@ArrayList[
>     @Object[][isEmpty=true;size=0],
>     null,
>     @ArrayList[isEmpty=false;size=100],
> ]
> ts=2020-12-14 16:10:36; [cost=0.044709ms] result=@ArrayList[
>     @Object[][isEmpty=true;size=0],
>     null,
>     @ArrayList[isEmpty=false;size=100],
> ]
> ......
> ~~~

#### (6) `Arthas`没有包含的功能

> `jmap -histo ${pid}`的功能

## 4 其他场景

### 4.1 宕机程序

> * 如果程序启动时设置了`-XX:+HeapDumpOnOutOfMemoryError`会自动dump heap以供分析
> * 如果程序没有设置上述选项，宕机时不要立刻重启，此时用`jmap`仍然可以dump heap到文件

### 4.2 系统内存飙高调优 

> 内存飙高，一定是堆内存飙高
>
> 1. 导出堆内存（`jmap`)
> 2. 分析（`jhat`, `jvisual vm`,`mat`, `jprofiler`……等工具）

### 4.3 `Lambda`表达式导致方法区溢出

> 例如[../demos/src/com/javaprojref/jvm/grp05_gc/LambdaGC.java](demos/src/com/javaprojref/jvm/grp05_gc/LambdaGC.java)
>
> ~~~java
> public class LambdaGC {
>     public static void main(String[] args) {
>         for(;;) {
>             I i = C::n; //每一次调用都会产生一个新的Class对象
>         }
>     }
>     public static interface I {
>         void m();
>     }
>     public static class C {
>         static void n() {
>             System.out.println("hello");
>         }
>     }
> }
> ~~~
>
> 每一个`Lambda`表达式都会产生一个新的Class，分配在方法区，最终将方法区占满，发生例如`java.lang.OutOfMemoryError: Compressed class space`这样的错误（有的垃圾回收器不清理方法区、有的垃圾回收器有条件回收并且条件比较严格）

### 4.4 栈溢出

> 如果没有bug（如递归相关），可以增加`-Xss`设定

### 4.5 监控JVM

> `jstat`， `jvisualvm`， `jprofiler`，`arthas`，`top` ……

## 其他

### (1) 代码优化

代码1：

~~~java
for (int i = 0; i < 100; ++i) {
Object o = new Object();
// ...
}
~~~

代码2：

~~~java
Object o = null;
for (int i = 0; i < 100; ++i) {
o = new Object();
// ...
}
~~~

相比代码1，代码2给JVM带来的负担更小，更加优化

### (2) 安装`htop`

> Mac上的`top`命令的功能限制比较多、可是安装`htop`来实现查看线程等功能，但是Mac上的java程序运行与Linux差别还是很大，因此还是在Linux上进行JVM分析，最接近生产环境

方法一：使用`homebrew`安装htop

1. 解决`terminal`翻墙问题

> * 方法1：用`ctrl + c`让`homebrew`跳过update阶段，
>   * 参考链接 [https://learnku.com/articles/18908](https://learnku.com/articles/18908) 
>   * 仅限链接中的方法一，方法二应该已经不能使用了，它导致我的brew无法使用最终重装了brew)
>
> * 方法2：用能够为terminal提供`http`, `https`, `sock5`代理的翻墙软件
>
>   ~~~
>   export https_proxy=http://127.0.0.1:7890 http_proxy=http://127.0.0.1:7890 all_proxy=socks5://127.0.0.1:7890
>   ~~~

2. 执行`brew install htop-osx`安装`htop`

方法二：使用`mac ports`安装`htop`

1. 解决mac ports安装时卡顿的问题：[https://blog.csdn.net/z93701081/article/details/101622064](https://blog.csdn.net/z93701081/article/details/101622064)
2. Mac ports配置国内源：[https://www.jianshu.com/p/f10c745602d9](https://www.jianshu.com/p/f10c745602d9)
3. 执行`sudo port install htop`安装：[https://qastack.cn/unix/98253/how-do-i-install-htop-inside-mac-os-x](

