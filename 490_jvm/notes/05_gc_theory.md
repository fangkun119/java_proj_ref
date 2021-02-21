<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents** generated with [DocToc](https://github.com/thlorenz/doctoc)-->

- [JVM调优理论知识和GC算法概要](#jvm%E8%B0%83%E4%BC%98%E7%90%86%E8%AE%BA%E7%9F%A5%E8%AF%86%E5%92%8Cgc%E7%AE%97%E6%B3%95%E6%A6%82%E8%A6%81)
  - [1 JVM如何找到垃圾对象](#1-jvm%E5%A6%82%E4%BD%95%E6%89%BE%E5%88%B0%E5%9E%83%E5%9C%BE%E5%AF%B9%E8%B1%A1)
  - [2 GC算法](#2-gc%E7%AE%97%E6%B3%95)
    - [(1) `Mark-Sweep` （标记清除）：标记垃圾对象、然后清除](#1-mark-sweep-%E6%A0%87%E8%AE%B0%E6%B8%85%E9%99%A4%E6%A0%87%E8%AE%B0%E5%9E%83%E5%9C%BE%E5%AF%B9%E8%B1%A1%E7%84%B6%E5%90%8E%E6%B8%85%E9%99%A4)
    - [(2) `Copying` （拷贝）：拷贝非垃圾对象、然后清理整块内存](#2-copying-%E6%8B%B7%E8%B4%9D%E6%8B%B7%E8%B4%9D%E9%9D%9E%E5%9E%83%E5%9C%BE%E5%AF%B9%E8%B1%A1%E7%84%B6%E5%90%8E%E6%B8%85%E7%90%86%E6%95%B4%E5%9D%97%E5%86%85%E5%AD%98)
    - [(3) `Mark-Compact`（标记压缩）：清理垃圾对象、并将非垃圾对象移动到一起](#3-mark-compact%E6%A0%87%E8%AE%B0%E5%8E%8B%E7%BC%A9%E6%B8%85%E7%90%86%E5%9E%83%E5%9C%BE%E5%AF%B9%E8%B1%A1%E5%B9%B6%E5%B0%86%E9%9D%9E%E5%9E%83%E5%9C%BE%E5%AF%B9%E8%B1%A1%E7%A7%BB%E5%8A%A8%E5%88%B0%E4%B8%80%E8%B5%B7)
  - [3 JVM内存分代模型](#3-jvm%E5%86%85%E5%AD%98%E5%88%86%E4%BB%A3%E6%A8%A1%E5%9E%8B)
    - [3.1 使用分代模型的JVM](#31-%E4%BD%BF%E7%94%A8%E5%88%86%E4%BB%A3%E6%A8%A1%E5%9E%8B%E7%9A%84jvm)
    - [3.2 分代模型，及对象从创建到消亡过程](#32-%E5%88%86%E4%BB%A3%E6%A8%A1%E5%9E%8B%E5%8F%8A%E5%AF%B9%E8%B1%A1%E4%BB%8E%E5%88%9B%E5%BB%BA%E5%88%B0%E6%B6%88%E4%BA%A1%E8%BF%87%E7%A8%8B)
    - [3.3 GC触发](#33-gc%E8%A7%A6%E5%8F%91)
    - [3.4 栈上分配以及`TLAB`](#34-%E6%A0%88%E4%B8%8A%E5%88%86%E9%85%8D%E4%BB%A5%E5%8F%8Atlab)
    - [3.5 对象何时进入老年代](#35-%E5%AF%B9%E8%B1%A1%E4%BD%95%E6%97%B6%E8%BF%9B%E5%85%A5%E8%80%81%E5%B9%B4%E4%BB%A3)
  - [4 常见垃圾回收器](#4-%E5%B8%B8%E8%A7%81%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8)
    - [4.1 概要](#41-%E6%A6%82%E8%A6%81)
    - [4.2 物理分代模型中常用的垃圾回收器](#42-%E7%89%A9%E7%90%86%E5%88%86%E4%BB%A3%E6%A8%A1%E5%9E%8B%E4%B8%AD%E5%B8%B8%E7%94%A8%E7%9A%84%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8)
    - [4.3 CMS（`ConcurrentMarkSweep`)垃圾回收器](#43-cmsconcurrentmarksweep%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8)
    - [4.4 `G1`垃圾回收器（逻辑上的分代回收器）](#44-g1%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8%E9%80%BB%E8%BE%91%E4%B8%8A%E7%9A%84%E5%88%86%E4%BB%A3%E5%9B%9E%E6%94%B6%E5%99%A8)
  - [5  参考](#5--%E5%8F%82%E8%80%83)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# JVM调优理论知识和GC算法概要

> 概括GC算法相关的内容，为JVM调优准备知识，更详细的GC算法介绍参考[../08_gc_algorithm/readme.md](../08_gc_algorithm/readme.md)

## 1 JVM如何找到垃圾对象

<b>引用计数</b>为0的是垃圾对象；但引用计数不能解决循环引用问题，还需要<b>`Root  Searching`</b>，把那些不能从`root instances`抵达的对象也标记为垃圾对象

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_root_searching.jpg" width="800" /></div>

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
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_mem_generation.jpg" width="800" /></div>
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

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_gc_triggering.jpg" width="800" /></div>
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

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_garbage_collectors.jpg" width="800" /></div>
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

列出四阶段的[CMS垃圾回收过程](https://www.cnblogs.com/zhangxiaoguang/p/5792468.html)、同时也有助于理解并发回收器

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_gc_cms.jpg" width="800" /></div>
> 
> * 初始标记：标记根对象`root object`（会**STW**，但时间非常短，因为根对象数量少）
> * 并发标记：遍历整个老年代并且标记所有存活的对象，该阶段耗时最高，因此与应用程序同时运行
> * 重新标记：标记老年代全部的存活对象，包括那些在并发标记阶段更改的或者新创建的引用对象（会**STW**，但是时间非常短，因为只是执行阶段2时新产生的垃圾，数量少）
> * 并发清理：回收对象，与应用程序同时运行（其实在并发清理之前，还有两个准备阶段：Concurrent Preclean和Concurrent Abortable Preclean，因此有时也用六阶段来描述）

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

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_g1_3color_mark.jpg" width="600" /></div>
>
> * `CMS` ：使用`三色标记` + `Incremental Update`算法
> * `G1`(10ms) ：使用`三色标记` + `SATB`（Snapshop at the beginning）算法，配合`RSet`进行

## 5  参考

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


