# `Parallel Scavenge`及`Parallel Old`GC日志及调优

## 1 PS(`Parallel Scavenge`) PO（`Parallel Old`) GC日志

代码：[./GCCmdDemo.java](./GCCmdDemo.java)

> ~~~java
> import java.util.List;
> import java.util.LinkedList;
> 
> public class GCCmdDemo {
>  public static void main(String[] args) {
>      System.out.println("HelloGC");
>      List list = new LinkedList();
>      for (;;) {
>          byte[] b = new byte[1024 * 1024];
>        	@unchecked
>          list.add(b);
>      }
>  }
> }
> ~~~

编译

> ~~~bash
> _____________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> /Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/javac -Xlint:unchecked GCCmdDemo.java
> _____________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ ls
> GCCmdDemo.class GCCmdDemo.java  readme.md
> ~~~

临时设置别名`java8`来执行下列命令 ，以便在不修改Java Home的情况下方便地使用`java 8`测试

> ~~~bash
> _____________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ alias java8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/java
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

