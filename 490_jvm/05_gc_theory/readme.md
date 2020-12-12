# JVM调优理论知识：GC Collector三色标记

## 1 JVM如何找到垃圾对象

<b>引用计数</b>为0的是垃圾对象；但引用计数不能解决循环引用问题，还需要<b>`Root  Searching`</b>，把那些不能从`root instances`抵达的对象也标记为垃圾对象

> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_root_searching.jpg)

## 2 GC算法

### (1) `Mark-Sweep` （标记清除）：标记垃圾对象、然后清除

> * 缺点是：(1) 两边扫描（第一遍标记、第二遍清除）效率偏低；(2) 容易产生碎片
> * 适用于：存活对象多的场景

### (2) `Copying` （拷贝）：拷贝非垃圾对象、然后清理整块内存

> * 缺点是：(1) 存在对象移动复制、需要调整空间；(2) 空间浪费
> * 适用于：存活对象少的场景

### (3) `Mark-Compact`（标记压缩）：清理垃圾对象、并将非垃圾对象移动到一起

> * 缺点是：(1) 扫描两次；(2) 需要移动对象、效率偏低
> * 优点是：(1) 不会产生内存碎片（2）不会产生内存减半

## 3 JVM内存分代模型

### 3.1 使用分代模型的JVM

部分（不是所有）垃圾回收器会使用分代模型，目前：

> * 除`Epsilon`（一个debug用的垃圾回收器）、`ZGC`、`Shenandoah`之外的GC都是逻辑分代模型
> * `G1`是逻辑分代物理不分代，除此之外不仅逻辑分代而且物理分代
> * `逻辑分代`：给内存块在概念上进行区分；`物理分代`：真的会把内存划分成区域

### 3.2 分代模型，及对象从创建到消亡过程

> 下图是内存分代模型（未包含`方法区`）部分，其中的数字是内存大小的默认比例（Java 1.8，不适用于`G1`/`Shenandoah`/...等新的JVM，可以通过JVM参数修改）
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_mem_generation.jpg)
>
> * 新生代（`new`、也叫做`young`）：存活对象少、适合`Mark-Sweep`算法
> 	* 刚创建的对象，如果不能在栈上分配，就分配在`eden`
> 	* 回收后仍存活放入`Survivor 1`，再次回收仍存活放入`Survivor 2`，……，如此反复直到达到threshold（`-XX:Max TenuringThreshold`）
> * 老年代：存活对象多、适合`Copying`或者`Mark-Compact`算法
>   * 垃圾回收很多次都没有被回收的对象
> * 方法区（`Method Area`）
>   * 1.8**之前**使用`永久代`（`Perm Generation`）实现，大小必须预先指定，动态生成的Class也会放在永久代、更容易发生溢出
>   * 1.8**开始**使用`元数据区`（`Metaspace`）实现，大小不做限制，如果不设置大小上限为物理内存
>   * 存储以下两类数据
>     * 元数据 - Class，Method Area
>     * 字符串常量（仅限Java 1.7及之前、1.8开始存入堆中）

### 3.3 GC触发

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_gc_triggering.jpg)
> 
> * `MinorGC`（也叫`YGC`、`YongGC`）：新生代空间耗尽时触发
> * `MajorGC/FullGC`：老年代无法继续分配空间时触发、新生代老年代同时进行回收
> * `-Xmn`：新生代空间大小；`-Xms`：初始内存大小；`-Xmx`：内存上限（可以使用`java -X`命令查看）

### 3.4 栈上分配以及`TLAB`

栈上分配：

> * 线程私有小对象
> * 无逃逸，只在某一段代码中被使用
> * 支持标量替换，例如class T { int m; int n }可以用两个int来替代

TLAB：`Thread Local Allocation Buffer`

> * 占用eden、默认1%
> * 多线程的时候不用竞争eden就可以申请空间、以提高效率
> * 小对象

例子：[jvm/grp05_gc/TestTLAB.java](../demos/src/com/javaprojref/jvm/grp05_gc/TestTLAB.java) 

### 3.5 对象何时进入老年代

(1) MinorGC次数：经历`MinorGC`次数超过 `XX:MaxTenuringThreshold` 的对象会放入老年代，该参数默认值

> * Parallel Scavenge：`15` （对象头中的GC位是`4 bit`，最大值是`15`）
> * CMS：`6`
> * G1：`15`

(2) 动态对象年龄判定：

> 当`MinorGC`拷贝对象时（从`Eden`、`Survival ${i}` 拷贝存活对象到 `Survival ${1 + (i + 1) % 2}`时，对进行年龄判断，判断依据如下：
> 
> * 年龄从小到大进行累加
> * 当加入某个年龄段后，累加和超过survivor区域50%（由`TargetSurvivorRatio`参数控制）位置会有一个对象
> * 这个对象年龄往上的，移入到老年代

(3) 空间担保：

> `YGC`期间survivor区空间不够用时、对象通过空间担保直接进入老年代

## 4 常见垃圾回收器

### 4.1 概要

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_garbage_collectors.jpg)
> 
> 备注：`Serial`指单线程，`Parallel`指多线程，带`Old`后缀的会使用适合老年代的算法

查看当前使用的垃圾回收器：`java  -XX:+PrintCommandLineFlags -version`

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ /Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/bin/java  -XX:+PrintCommandLineFlags -version
> -XX:InitialHeapSize=134217728 -XX:MaxHeapSize=2147483648 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
> ~~~

### 4.2 物理分代模型中常用的垃圾回收器

历史

> * 早期的垃圾回收器是`Serial` + `Serial Old`，为了提高效率诞生了`Parallel Scavenge` + `Parallel Old`，1.4版本后期引入`CMS`，为了配合`CMS`又引入了`ParNew`
> * CMS开启了并发回收、但问题较多、因此没有JDK把它用作默认垃圾回收器（除非手动指定）

常用垃圾回收器组合

> * `Serial` + `Serial Old`
> * `Parallel Scavenge` + `Parallel Old`：目前主要在用
> * `ParNew` + `CMS`
> * 另外还有一种组合：`CMS` + `Serial Old`

垃圾回收器

> * `Serial`：会`stop-the-world`、使用`copying`回收算法的单线程垃圾回收器（另外单CPU效率高、虚拟机是Client模式时的默认垃圾回收器）
> * `Serial Old`：会`stop-the-world`、使用`mark-sweep`算法或`sweep-compact`算法来回收垃圾，单线程，已经很少使用了
> * `Parrallel Scavenge`：会`stop-the-world`使用`copying`回收算法的`多线程`垃圾回收器
> * `Parrallel Old`：使用`compacting`回收算法的`多线程`垃圾回收器
> * `ParNew`：在`Parrallel Scavenge`基础上修改（例如回收器间的同步机制、由吞吐量优先改为响应时间优先）使其兼容`CMS`垃圾回收器
> * `CMS`（ConcurrentMarkSweep）：
> 	* 老年代并发回收，垃圾回收和应用程序同时运行，降低STW的时间（200ms)
> 	* 但是CMS问题多、以至于没有JDK版本把CMS设为默认垃圾回收器

垃圾收集器跟内存大小的关系
> * `Serial`：几十兆
> * `PS`：上百兆 - 几个G
> * `CMS`：20G
> * `G1`：上百G （1.8开始比较完善，1.9的默认垃圾回收器）
> * `ZGC`：4T ~ 16T（16T需要JDK13）

### 4.3 CMS（`ConcurrentMarkSweep`)垃圾回收器

列出`CMS`的四阶段回收过程、同时也有助于理解并发回收器

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_gc_cms.jpg)
> 
> * 初始标记：标记根对象`root object`
> * 并发标记：标记要回收的对象，该阶段耗时最高，因此与应用程序同时运行
> * 重新标记：是一个`stop-the-world`，标记阶段2过程中重新产生的垃圾（因为垃圾数量少、STW的时间短）
> * 并发清理：回收对象，与应用程序同时运行

CMS产生于小内存的时代，现在的主机内存量大，使用CMS有如下问题

> * `Mark Sweep`带来老年代内存碎片，内存碎片多到影响新内存分配时，会回退到`Serial Old`来做Full GC（单线程对老年代做`Mark-Compress`，速度很慢）
> * 并发清理完成之前，新产生的垃圾（浮动垃圾）填满了存储空间，也会回退到`Serial Old`

缓解上述两个问题的参数如下

> * 减少内存碎片：增加压缩可以减少内存碎片、但也会增加Full-GC（不是老年代并发清理）带来的停顿
>   *  `-XX:+UseCMSCompactAtFullCollection`：在`Full GC`时开启压缩
>   * `-XX:CMSFullGCsBeforeCompaction`：经过多少次Full GC才压缩（默认为0）
> * 减少浮动垃圾：
>   * 降低触发CMS（老年代并发清理）的阈值：调低`-XX:CMSInitiatingOccupancyFraction`留出空间容纳浮动垃圾，让CMS保持老年代有足够的空间
>
> 参考： [JVM调优——CMS常见参数解析](https://www.cnblogs.com/onmyway20xx/p/6605324.html)
>
> 内存越大Full GC卡顿越长

### 4.4 `G1`垃圾回收器（逻辑上的分代回收器）

> `1.7`才开始出现`G1`但不够完善，`1.8`可以使用`G1`，`1.9`默认GC是`G1` 

理解“逻辑分代回收器”：

> G1也对内存分代、将一块块内存分别归入到新生代、老年代……，但者只是逻辑上的、意味着这些内存块可以进行角色转换，例如当一次垃圾回收之后，一个内存块由新生代变成老年代

G1同样使用四阶段回收过程

> * 初始标记：标记根对象
> * <b>并发标记</b>：标记要回收的对象，该阶段耗时最高，因此与应用程序同时运行
> * 重新标记：标记阶段2过程中重新产生的垃圾
> * 并发清理：回收对象，与应用程序同时运行

区别主要在于阶段2、即<b>并发标记</b>阶段

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_g1_3color_mark.jpg)
>
> * `CMS` ：使用`三色标记` + `Incremental Update`算法
> * `G1`(10ms) ：使用`三色标记` + `SATB`（Snapshop at the beginning）算法，配合`RSet`进行

## 5 常见垃圾回收器组合参数设定（1.8）

> 设定参数可以指定使用哪种垃圾回收器
>
> * -XX:+UseSerialGC 
>   * 使用`Serial New (DefNew)` + `Serial Old`组合
>   * 小适用于型程序
>   * 默认情况下不会开启该选项，HotSpot会根据计算及配置和JDK版本自动选择收集器
> * -XX:+UseParNewGC
>   * 使用``ParNew` +` SerialOld`组合，这个组合已经很少用（在某些版本中已经废弃）
>   * https://stackoverflow.com/questions/34962257/why-remove-support-for-parnewserialold-anddefnewcms-in-the-future
> * -XX:+UseConc<font color=red>(urrent)</font>MarkSweepGC 
>   * 使用`ParNew` +` CMS` +` Serial Old`（promotion failure时回退到Serial Old）组合
>   * 某些版本用`-XX:+UseConcMarkSweepGC`参数即可、某些需要加上<font color=red>(urrent)</font>，使用-XX:+UseConcurrentMarkSweepGC 
> * **-XX:+UseParallelGC**  （1.8默认）
>   * **使用`Parallel Scavenge` +` Parallel Old`** 
> * -XX:+UseParallelOldGC 
>   * 使用`Parallel Scavenge` + `Parallel Old`组合
> * **-XX:+UseG1GC = G1**
>   * **使用G1 GC**
>
> 查看默认GC：
>
> * java +XX:+PrintCommandLineFlags -version （Windows，Mac）
> * 通过GC的日志来分辨
> * Linux下1.8版本默认的垃圾回收器
>   * 1.8.0_181 默认（看不出来）Copy MarkCompact
>   * 1.8.0_222 默认 PS + PO

## 6 JVM命令行参数

### 6.1 JVM命令行参数参考

> [https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html)

### 6.2 HotSpot参数分类

> * `-`：标准参数、所有`HotSpot`都支持
> * `-X`：非标准参数、特定版本`HotSpot`支持特定命令
> * `-XX`：不稳定参数，下个版本有可能取消
> * `:`后面的`+`表示开启、`-`表示关闭

查看非标参数：`java -X`

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ java -X
> -Xint			仅解释模式执行
> -Xlog:<opts>	配置或启用采用 Java 虚拟机 (Java Virtual Machine, JVM) 统一记录框架进行事件记录。使用 -Xlog:help可了解详细信息。
> -Xmixed		混合模式执行（默认值）
> -Xmn<size> 	为年轻代（新生代）设置初始和最大堆大小（以字节为单位）
> -Xms<size> 	设置初始 Java 堆大小
> -Xmx<size> 	设置最大 Java 堆大小
> -Xnoclassgc	禁用类垃圾收集
> -Xss<size>  	设置 Java 线程堆栈大小
> ……
> ~~~

查看不稳定参数：`java -XX:+PrintFlagsFinal -version`，可以结合`grep`来使用

> ~~~bash
> ___________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/05_gc/
> $ java -XX:+PrintFlagsFinal -version | head
> [Global flags]
> 	int ActiveProcessorCount  = -1   {product} {default}
> 	uintx AdaptiveSizeDecrementScaleFactor = 4    {product} {default}
> 	uintx AdaptiveSizeMajorGCDecayTimeScale = 10    {product} {default}
> 	uintx AdaptiveSizePolicyCollectionCostMargin = 50    {product} {default}
> 	uintx AdaptiveSizePolicyInitializingSteps = 20   {product} {default}
> 	...
> 	uintx NewRatio = 2    {product} {default}
> 	bool PrintFlagsFinal  = true    {product} {command line}
> 	...
> openjdk version "15.0.1" 2020-10-20
> OpenJDK Runtime Environment (build 15.0.1+9-18)
> OpenJDK 64-Bit Server VM (build 15.0.1+9-18, mixed mode, sharing)
> ~~~
>
> 其中`NewRatio = 2`是新生代老年代内存比例；`PrintFlagsFinal = true`用来打印不稳定参数

## 7  参考

> * JVM垃圾回收的基础知识：[https://www.cnblogs.com/zhangyibing/p/13786446.html](https://www.cnblogs.com/zhangyibing/p/13786446.html)
> * [https://blogs.oracle.com/](https://blogs.oracle.com/jonthecollector/our-collectors)[jonthecollector](https://blogs.oracle.com/jonthecollector/our-collectors)[/our-collectors](https://blogs.oracle.com/jonthecollector/our-collectors)
> * [https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html)
> * [http://java.sun.com/javase/technologies/hotspot/vmoptions.jsp](http://java.sun.com/javase/technologies/hotspot/vmoptions.jsp)
> * JVM调优参考文档：[https://docs.oracle.com/en/java/javase/13/gctuning/introduction-garbage-collection-tuning.html#GUID-8A443184-7E07-4B71-9777-4F12947C8184](https://docs.oracle.com/en/java/javase/13/gctuning/introduction-garbage-collection-tuning.html#GUID-8A443184-7E07-4B71-9777-4F12947C8184) 
> * [https://www.cnblogs.com/nxlhero/p/11660854.html ](https://www.cnblogs.com/nxlhero/p/11660854.html)：在线排查工具
> * [https://www.jianshu.com/p/507f7e0cc3a3](https://www.jianshu.com/p/507f7e0cc3a3) arthas常用命令
> * Arthas手册：
> 	* 启动arthas java -jar arthas-boot.jar
> 	* 绑定java进程
> 	* dashboard命令观察系统整体情况
> 	* help 查看帮助
> 	* help xx 查看具体命令帮助
> * jmap命令参考： https://www.jianshu.com/p/507f7e0cc3a3 
> 	* jmap -heap pid
> 	* jmap -histo pid
> 	* jmap -clstats pid


