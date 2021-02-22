<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [JVM日志格式和常用参数](#jvm%E6%97%A5%E5%BF%97%E6%A0%BC%E5%BC%8F%E5%92%8C%E5%B8%B8%E7%94%A8%E5%8F%82%E6%95%B0)
  - [1. CMS日志格式](#1-cms%E6%97%A5%E5%BF%97%E6%A0%BC%E5%BC%8F)
    - [(1) 实验环境](#1-%E5%AE%9E%E9%AA%8C%E7%8E%AF%E5%A2%83)
    - [(2) CMS日志格式](#2-cms%E6%97%A5%E5%BF%97%E6%A0%BC%E5%BC%8F)
      - [(a) 年轻代回收日志（ParNew算法）](#a-%E5%B9%B4%E8%BD%BB%E4%BB%A3%E5%9B%9E%E6%94%B6%E6%97%A5%E5%BF%97parnew%E7%AE%97%E6%B3%95)
      - [(b) 老年代回收日志（CMS回收算法）](#b-%E8%80%81%E5%B9%B4%E4%BB%A3%E5%9B%9E%E6%94%B6%E6%97%A5%E5%BF%97cms%E5%9B%9E%E6%94%B6%E7%AE%97%E6%B3%95)
  - [2. G1日志格式](#2-g1%E6%97%A5%E5%BF%97%E6%A0%BC%E5%BC%8F)
    - [(1) G1回收过程](#1-g1%E5%9B%9E%E6%94%B6%E8%BF%87%E7%A8%8B)
    - [(2) 实验环境](#2-%E5%AE%9E%E9%AA%8C%E7%8E%AF%E5%A2%83)
    - [(3) G1 GC 日志格式](#3-g1-gc-%E6%97%A5%E5%BF%97%E6%A0%BC%E5%BC%8F)
  - [3 常用JVM参数](#3-%E5%B8%B8%E7%94%A8jvm%E5%8F%82%E6%95%B0)
    - [(1) 常用的通用参数](#1-%E5%B8%B8%E7%94%A8%E7%9A%84%E9%80%9A%E7%94%A8%E5%8F%82%E6%95%B0)
    - [(2) Parallel GC常用参数](#2-parallel-gc%E5%B8%B8%E7%94%A8%E5%8F%82%E6%95%B0)
    - [(3) CMS GC常用参数](#3-cms-gc%E5%B8%B8%E7%94%A8%E5%8F%82%E6%95%B0)
    - [(4) G1 GC常用参数](#4-g1-gc%E5%B8%B8%E7%94%A8%E5%8F%82%E6%95%B0)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# JVM日志格式和常用参数

## 1. CMS日志格式

### (1) 实验环境

Java版本

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ cat ~/.bash_profile | grep java.*8
> alias java8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/java
> alias javac8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/javac
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ java8 -version
> java version "1.8.0_121"
> Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
> Java HotSpot(TM) 64-Bit Server VM (build 25.121-b13, mixed mode)
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ javac8 -version
> javac 1.8.0_121
> ~~~

代码编译

> [./FullGCIssueDemo.java](./FullGCIssueDemo.java)
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ javac8 FullGCIssueDemo.java
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ ls *.class
> FullGCIssueDemo$1.class        FullGCIssueDemo$CardInfo.class FullGCIssueDemo.class
> ~~~

运行Demo

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ java8 -Xms20M -Xmx20M -XX:+PrintGCDetails -XX:+UseConcMarkSweepGC FullGCIssueDemo
> ~~~

其中

> `-Xms20M -Xmx20M`：减小JVM内存，让GC尽快出现
>
> `-XX:+PrintGCDetails`：打印GC日志
>
> `-XX:+UseConcMarkSweepGC`：使用CMS + ParNew垃圾回收器

日志样本

> [./cms_gc_log.txt](./cms_gc_log.txt)

### (2) CMS日志格式

#### (a) 年轻代回收日志（ParNew算法）

`[GC (Allocation Failure) [ParNew: 6144K->640K(6144K), 0.0265885 secs] 6585K->2770K(19840K), 0.0268035 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]`

> * `ParNew`：年轻代收集器
>
> * `6144->640`：年轻收集前后的对比
>
> * `(6144)`：年轻代容量
>
> * `6585 -> 2770`：整个堆收集前后的情况
>
> * `(19840)`：整个堆大小

#### (b) 老年代回收日志（CMS回收算法）

> ~~~bash
> # 阶段1：CMS Initial Mark阶段，是一个会STW（stop the world）的阶段 
> [GC (CMS Initial Mark) [1 CMS-initial-mark: 8511K(13696K)] 9866K(19840K), 0.0040321 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
> # 	8511 (13696) : 老年代使用内存（老年代最大内存）
> # 	9866 (19840) : 整个堆使用内存（最大堆内存）
> #	老年代占用比例到达阈值时启动老年代回收（由-XX:CMSInitaitingOccupancyFraction指定，默认68%，早期JVM默认值是92%）
> #	* 阈值太高：引发Promotion Failure导致FGC（STW后单线程回收），系统频繁卡顿
> #	* 阈值太低：CMS回收频繁
> 
> # 阶段2：Concurrent Mark阶段
> [CMS-concurrent-mark-start]
> [CMS-concurrent-mark: 0.018/0.018 secs] [Times: user=0.01 sys=0.00, real=0.02 secs] 
> # 	这里的时间意义不大，因为是并发执行
> 
> # 阶段3.1：清理之前准备阶段
> [CMS-concurrent-preclean-start]
> [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
> # 	pre-clean：标记Card为Dirty，也称为Card Marking
> #	Card：表示代际引用、用于提高GC标记效率的数据结构
> 
> # 阶段3.2：重新标记、也叫最终标记，是一个会STW（stop the world）的阶段
> [GC (CMS Final Remark) 
> 	[YG occupancy: 1597 K (6144 K)]			# 1597 K (6144 K): 年轻代内存占用量及年代总容量）
> 	[Rescan (parallel) , 0.0008396 secs]	# 子阶段(1)：STW状态下的存活对象标记，多线程并发进行
> 	[weak refs processing, 0.0000138 secs]	# 子阶段(2)：弱引用处理
> 	[class unloading, 0.0005404 secs]		# 子阶段(3)：卸载用不到的class，MetaSpace或Permanet Generation中的数据
> 	[scrub symbol table, 0.0006169 secs]	# 子阶段(4)：清理Symbol Table（保存class-level metadata的表）
> 	[scrub string table, 0.0004903 secs]	# 子阶段(5)：清理String Table（保存以初始化String的表）
> 	[1 CMS-remark: 8511K(13696K)] 			# 8511K(13696K): 阶段过后的老年代占用及老年代容量
> 10108K(19840K), 0.0039567 secs] 			# 10108K(19840K): 阶段过后的堆占用及容量
> [Times: user=0.00 sys=0.00, real=0.00 secs] 
> 
> # 阶段4.1：并发清理
> [CMS-concurrent-sweep-start]
> [CMS-concurrent-sweep: 0.005/0.005 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
> 
> # 阶段4.2：重置内部结构，为下次GC做准备
> [CMS-concurrent-reset-start]
> [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
> ~~~

CMS GC调优主要看：(1) CMS GC发生的是不是过于频繁（2）系统卡顿是不是过于频繁

## 2. G1日志格式

### (1) G1回收过程

> * 有很多Region构成，每个Region可能会属于Young、也可能属于Survivor、也可能数据Old或巨型区
>
> * 对于年轻代的YGC：会STW，不推荐指定Young区的大小，G1会根据STW卡顿时间来调整Young区大小
>
> 参考：[https://www.oracle.com/technical-resources/articles/java/g1gc.html](https://www.oracle.com/technical-resources/articles/java/g1gc.html)

### (2) 实验环境

Java版本

> ~~~java
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ cat ~/.bash_profile | grep java.*8
> alias java8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/java
> alias javac8=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/javac
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ java8 -version
> java version "1.8.0_121"
> Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
> Java HotSpot(TM) 64-Bit Server VM (build 25.121-b13, mixed mode)
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ javac8 -version
> javac 1.8.0_121
> ~~~

代码编译

> [./FullGCIssueDemo.java](./FullGCIssueDemo.java)
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ rm *.class
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ javac8 FullGCIssueDemo.java
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ ls *.class
> FullGCIssueDemo$1.class        FullGCIssueDemo$CardInfo.class FullGCIssueDemo.class
> ~~~

程序启动

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/notes/
> $ java8 -Xms20M -Xmx20M -XX:+PrintGCDetails -XX:+UseG1GC FullGCIssueDemo
> [0.041s][info   ][gc,init] Pre-touch: Disabled
> [0.041s][info   ][gc,init] Parallel Workers: 4
> [0.041s][info   ][gc,init] Concurrent Workers: 1
> ~~~

日志样本

> [./g1gc_log.txt](./g1gc_log.txt)

### (3) G1 GC 日志格式

G1 的3种GC：`YGC`（回收年轻代）/`MixedGC`（既回收年轻代又回收老年代）/`FGC`（STW单线程完整回收）

> 不会顺序执行，在不同的条件下触发不同的GC
>
> 有时会有FGC、要调优尽量避免FGC

日志例子

> ~~~bash
> [GC pause (G1 Evacuation Pause) (young) (initial-mark), 0.0015790 secs]
> # young			：年轻代 
> # pause			：表示STW（stop the world）
> # evacuation	：复制存活对象，将一个Region中的存活对象复制到另一个Region中
> # initial-mark	：表示是一个混合回收的阶段，这里个日志片段是YGC混合老年代回收（mixed gc），与Mixed GC中的initail-mark阶段同时进行
> # 以下是Evacuation阶段（复制年轻代存活对象到另一个Region并进行压缩）时的执行过程
>    [Parallel Time: 1.5 ms, GC Workers: 1]  	# 并行任务花费的STW时间（阻塞时间），启动了多少个GC线程
>       [GC Worker Start (ms):  92635.7]		# 	每个线程的开始时间
>       [Ext Root Scanning (ms):  1.1]		# 	EXT根对象（JNI、全局变量、线程栈等）扫描
>       [Update RS (ms):  0.0]				# 	RSet（上一章介绍）更新
>          [Processed Buffers:  1]
>       [Scan RS (ms):  0.0]					# 	RSet（上一章介绍）扫描
>       [Code Root Scanning (ms):  0.0]		# 	Cood根对象（JIT编译后的代码中引用heap的对象）扫描
>       [Object Copy (ms):  0.1]				# 	对象拷贝
>       [Termination (ms):  0.0]				# 	尝试结束到真正结束（等待其他线程）之间的耗时
>          [Termination Attempts:  1]
>       [GC Worker Other (ms):  0.0]			# 	其他工作耗时
>       [GC Worker Total (ms):  1.2]			# 	每个线程花费时间的总和
>       [GC Worker End (ms):  92636.9]		# 	每个线程的结束时间
>    [Code Root Fixup: 0.0 ms]			# 修复GC期间code root指针改变的耗时
>    [Code Root Purge: 0.0 ms]			# 清除code root的耗时
>    [Clear CT: 0.0 ms]					# 清除Card Table和Dirty Card的耗时
>    [Other: 0.1 ms]					# 其他耗时
>       [Choose CSet: 0.0 ms]			
>       [Ref Proc: 0.0 ms]
>       [Ref Enq: 0.0 ms]
>       [Redirty Cards: 0.0 ms]
>       [Humongous Register: 0.0 ms]
>       [Humongous Reclaim: 0.0 ms]
>       [Free CSet: 0.0 ms]			
>    [Eden: 0.0B(1024.0K)->0.0B(1024.0K) Survivors: 0.0B->0.0B Heap: 18.8M(20.0M)->18.8M(20.0M)]
>    # 各代内存变化：格式为”GC前占用内存(最大内存)->GC后占用内存(最大内存)“
>    # 上面的例子中：可以看到内存已经无法回收了，该现象对应了代码中的内存泄漏问题
>    [Times: user=0.00 sys=0.00, real=0.00 secs] 
>    # 总时间消耗
> # 以下是混合的其他回收阶段（例如Mixed GC initial-mark阶段）
> [GC concurrent-root-region-scan-start]
> [GC concurrent-root-region-scan-end, 0.0000078 secs]
> [GC concurrent-mark-start]
> ...
> 
> # 当无法evacuation（拷贝，会进行FGC，发现该问题要查找原因）
> [Full GC (Allocation Failure)  18M->18M(20M), 0.0719656 secs]
>    [Eden: 0.0B(1024.0K)->0.0B(1024.0K) Survivors: 0.0B->0.0B Heap: 18.8M(20.0M)->18.8M(20.0M)], 
>    [Metaspace: 3876K->3876K(1056768K)] 
>    [Times: user=0.07 sys=0.00, real=0.07 secs]
> ~~~

参考：[https://www.jianshu.com/p/ac1ba3479c08](https://www.jianshu.com/p/ac1ba3479c08)

## 3 常用JVM参数

### (1) 常用的通用参数

| 参数                                                   | 用途                                                         |            |
| ------------------------------------------------------ | ------------------------------------------------------------ | ---------- |
| `-Xmn -Xms -Xmx -Xss`                                  | 年轻代 最小堆 最大堆 栈空间                                  |            |
| `-XX:+UseTLAB`                                         | 使用TLAB，默认打开                                           | 不常用     |
| `-XX:+PrintTLAB`                                       | 打印TLAB的使用情况                                           | 不常用     |
| `-XX:TLABSize`                                         | 设置TLAB大小                                                 | 不常用     |
| `-XX:+DisableExplictGC`                                | 让`System.gc()`不起作用、默认disable，防止代码误写引发频繁FGC | 不常用     |
| `-XX:+PrintGC`                                         | 打印GC信息                                                   |            |
| `-XX:+PrintGCDetails`                                  | 打印GC详细信息                                               |            |
| `-XX:+PrintHeapAtGC`                                   | 打印GC信息时输出堆的详细情况                                 |            |
| `-XX:+PrintGCTimeStamps`                               | 打印发生GC时的系统时间                                       |            |
| `-XX:+PrintGCApplicationConcurrentTime (低)`           | 打印应用程序时间                                             | 不常用     |
| `-XX:+PrintGCApplicationStoppedTime （低）`            | 打印应用程序暂停时长（即STW时间）                            |            |
| `-XX:+PrintReferenceGC （重要性低）`                   | 记录回收了多少种不同引用类型的引用                           | 不常用     |
| `-verbose:class`                                       | 类加载（Class Loader）详细过程                               |            |
| `-XX:+PrintVMOptions`                                  | 打印JVM参数                                                  |            |
| `-XX:+PrintFlagsFinal  `<br />`-XX:+PrintFlagsInitial` | 最终参数、初始默认参数。查找参数名时常用，<br />例如`java -XX:+PrintFlagsFinal -version | grep G1` | 常用       |
| `-Xloggc:opt/log/gc.log`                               | GC日志输出到文件                                             | 常用       |
| `-XX:MaxTenuringThreshold`                             | GC升代年龄，最大值为15（CMS默认为6，其他GC默认为15）         |            |
| `-XX:PreBlockSpin`                                     | 自旋锁升级成重量级锁的自选次数                               | 不建议修改 |
| `-XX:CompileThreshold`                                 | 代码执行多少次后会变成热点代码进行本地化编译                 | 不建议修改 |

### (2) Parallel GC常用参数

| 参数                         | 用途                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| `-XX:SurvivorRatio`          | Suvivor区所占的比例，`Eden:S1:S2`默认`8:1:1`，不建议修改     |
| `-XX:PreTenureSizeThreshold` | 大对象到底多大，大对象会被直接分配在老年代                   |
| `-XX:+ParallelGCThreads`     | 并行收集器的线程数，同样适用于CMS，一般设为和CPU核数相同。可根据调试前后的对比来确定（参考前面章节介绍的图形化工具） |
| `-XX:+UseAdaptiveSizePolicy` | 自动选择各区大小比例                                         |

### (3) CMS GC常用参数

| 参数                                     | 用途                                                         |
| ---------------------------------------- | ------------------------------------------------------------ |
| `-XX:+UseConcMarkSweepGC`                | 使用CMS作为垃圾回收器                                        |
| `-XX:ParallelCMSThreads`                 | CMS线程数量，一般是核的一半（它只用于OLD区、不能把核都占了） |
| `-XX:CMSInitiatingOccupancyFraction`     | 使用多少比例的老年代后开始CMS收集，默认是68%(近似值)，如果频繁发生SerialOld卡顿，应该调小（频繁CMS回收） |
| `-XX:+UseCMSCompactAtFullCollection`     | 在FGC时进行压缩，开启后增加卡顿、减轻CMS碎片化的问题         |
| `-XX:CMSFullGCsBeforeCompaction`         | 多少次FGC之后进行压缩，触发压缩时增加卡顿时间，减轻CMS碎片化的问题，是上一个参数的折中 |
| `-XX:+CMSClassUnloadingEnabled`          | 回收Perm（MetaSpace，1.8之前叫永久代）                       |
| `-XX:CMSInitiatingPermOccupancyFraction` | 内存占用达到什么比例时进行Perm回收                           |
| `-XX:GCTimeRatio`                        | 设置GC时间占用程序运行时间的百分比，给GC的建议、GC会尽量达到这个目标但不保证 |
| `-XX:MaxGCPauseMillis`                   | 停顿时间，是一个建议时间，GC会尝试用各种手段达到这个时间，比如减小年轻代 |

### (4) G1 GC常用参数

| 参数                                 | 用途                                                         |
| ------------------------------------ | ------------------------------------------------------------ |
| `-XX:+UseG1GC`                       | 使用G1GC作为垃圾回收器                                       |
| `-XX:MaxGCPauseMillis`               | 建议值，G1会尝试调整Young区的块数来达到这个值（增加YGC频率） |
| `-XX:GCPauseIntervalMillis`          | 最少多长可以进行一次GC                                       |
| `-XX:+G1HeapRegionSize`              | Regin大小。建议逐渐增大该值，1 2 4 8 16 32（随着size增加，垃圾的存活时间更长，GC间隔更长，但每次GC的时间也会更长；ZGC做了改进采用了动态区块大小） |
| `-XX:+G1NewSizePercent`              | 新生代最小比例，默认为5%                                     |
| `-XX:G1MaxNewSizePercent`            | 新生代最大比例，默认为60%                                    |
| `-XX:GCTimeRatio`                    | GC时间建议比例，G1会根据这个值调整堆空间。一般设置`MaxGCPauseMillis`即可，不用设置`GCTimeRatio` |
| `-XX:ConcGCThreads`                  | GC线程数量                                                   |
| `-XX:InitiatingHeapOccupancyPercent` | 触发并发标记阶段的 Java 堆占用率阈值                         |



