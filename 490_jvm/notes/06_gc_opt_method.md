<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [GC调优方法概述](#gc%E8%B0%83%E4%BC%98%E6%96%B9%E6%B3%95%E6%A6%82%E8%BF%B0)
  - [1 GC调优相关的概念](#1-gc%E8%B0%83%E4%BC%98%E7%9B%B8%E5%85%B3%E7%9A%84%E6%A6%82%E5%BF%B5)
    - [1.1 调优目标](#11-%E8%B0%83%E4%BC%98%E7%9B%AE%E6%A0%87)
    - [1.2 什么是调优](#12-%E4%BB%80%E4%B9%88%E6%98%AF%E8%B0%83%E4%BC%98)
    - [1.3 调优规划](#13-%E8%B0%83%E4%BC%98%E8%A7%84%E5%88%92)
  - [2 调优规划和预调优](#2-%E8%B0%83%E4%BC%98%E8%A7%84%E5%88%92%E5%92%8C%E9%A2%84%E8%B0%83%E4%BC%98)
    - [例子1：订单系统用什么样配置的机器做压测](#%E4%BE%8B%E5%AD%901%E8%AE%A2%E5%8D%95%E7%B3%BB%E7%BB%9F%E7%94%A8%E4%BB%80%E4%B9%88%E6%A0%B7%E9%85%8D%E7%BD%AE%E7%9A%84%E6%9C%BA%E5%99%A8%E5%81%9A%E5%8E%8B%E6%B5%8B)
    - [例子2：如何支撑大并发流量系统（100万并发）的抢票功能](#%E4%BE%8B%E5%AD%902%E5%A6%82%E4%BD%95%E6%94%AF%E6%92%91%E5%A4%A7%E5%B9%B6%E5%8F%91%E6%B5%81%E9%87%8F%E7%B3%BB%E7%BB%9F100%E4%B8%87%E5%B9%B6%E5%8F%91%E7%9A%84%E6%8A%A2%E7%A5%A8%E5%8A%9F%E8%83%BD)
  - [3 优化JVM运行环境](#3-%E4%BC%98%E5%8C%96jvm%E8%BF%90%E8%A1%8C%E7%8E%AF%E5%A2%83)
    - [例子1：50万PV资料类网站，服务器从32位1.5G堆升级到64位16G堆时发生严重卡顿](#%E4%BE%8B%E5%AD%90150%E4%B8%87pv%E8%B5%84%E6%96%99%E7%B1%BB%E7%BD%91%E7%AB%99%E6%9C%8D%E5%8A%A1%E5%99%A8%E4%BB%8E32%E4%BD%8D15g%E5%A0%86%E5%8D%87%E7%BA%A7%E5%88%B064%E4%BD%8D16g%E5%A0%86%E6%97%B6%E5%8F%91%E7%94%9F%E4%B8%A5%E9%87%8D%E5%8D%A1%E9%A1%BF)
    - [例子2：系统CPU经常100%、如何调优](#%E4%BE%8B%E5%AD%902%E7%B3%BB%E7%BB%9Fcpu%E7%BB%8F%E5%B8%B8100%25%E5%A6%82%E4%BD%95%E8%B0%83%E4%BC%98)
    - [例子3：系统内存飙高、如何调优](#%E4%BE%8B%E5%AD%903%E7%B3%BB%E7%BB%9F%E5%86%85%E5%AD%98%E9%A3%99%E9%AB%98%E5%A6%82%E4%BD%95%E8%B0%83%E4%BC%98)
    - [例子4：如何监控JVM](#%E4%BE%8B%E5%AD%904%E5%A6%82%E4%BD%95%E7%9B%91%E6%8E%A7jvm)
  - [4 JVM命令行参数](#4-jvm%E5%91%BD%E4%BB%A4%E8%A1%8C%E5%8F%82%E6%95%B0)
    - [4.1 JVM命令行参数参考](#41-jvm%E5%91%BD%E4%BB%A4%E8%A1%8C%E5%8F%82%E6%95%B0%E5%8F%82%E8%80%83)
    - [4.2 HotSpot参数分类](#42-hotspot%E5%8F%82%E6%95%B0%E5%88%86%E7%B1%BB)
  - [5 常见垃圾回收器组合参数设定（1.8）](#5-%E5%B8%B8%E8%A7%81%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8%E7%BB%84%E5%90%88%E5%8F%82%E6%95%B0%E8%AE%BE%E5%AE%9A18)
  - [6 查看GC类型](#6-%E6%9F%A5%E7%9C%8Bgc%E7%B1%BB%E5%9E%8B)
  - [7 常用JVM参数](#7-%E5%B8%B8%E7%94%A8jvm%E5%8F%82%E6%95%B0)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# GC调优方法概述



## 1 GC调优相关的概念

### 1.1 调优目标 

(1) `吞吐量`：用户代码执行时间 / (用户代码执行时间 + 垃圾回收时间)

(2) `响应时间`：`STW`(Stop The World)时间越短，响应时间越好

首先要确定调优的目标：吞吐量优先？还是响应时间优先？还是在满足一定响应时间的情况下要达到多少的吞吐量，例如：

> 科学计算，数据挖掘：吞吐量优先、一般使用垃圾回收器`PS + PO`即可
>
> 网站、GUI、API：响应时间优先，一般会用1.8 G1或其他优化响应时间的垃圾回收器
>
> 但`CMS + ParNew`也是有用的，虽然`G1`的响应时间比`CMS + ParNew`低、也没有上一节提到的`CMS + ParNew`的问题（问题轻很多），但是`CMS + ParNew`的吞吐量比`G1`好（`G1`的吞吐量少15%）

### 1.2 什么是调优

> 1.根据需求进行JVM规划和预调优
>
> 2.优化运行JVM运行环境
>
> 3.解决JVM运行过程中出现的各种问题（例如OOM等）

### 1.3 调优规划

从业务场景出发、没有业务场景的调优都是耍流氓；无监控（压力测试、能看到结果）不调优

步骤：

1. 熟悉业务场景、选定合适的垃圾回收器（响应时间、吞吐量）组合

2. 计算内存需求（内存小回收频繁但回收快，内存大回收少但有可能卡顿会变高）

3. 选定CPU（越高越好，核的数量影响到回收的并发能力，计算速度影响到回收速度）

4. 设计年代大小、升级年龄

5. 设定日志参数（例子如下，5个文件循环使用，用满时最早的1个文件被替换，文件太大查问题困难）或者每天产生一个日志文件

   > ~~~bash
   > -Xloggc:/opt/xxx/logs/xxx-xxx-gc-%t.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=20M -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCCause
   > ~~~

6. 观察日志情况

## 2 调优规划和预调优

### 例子1：订单系统用什么样配置的机器做压测

> 峰值每日订单量上限 → 推算日内高峰期每秒订单量 → 结合 (1) 单笔订单内存 (2) 响应时间要求及响应时间上限 → 推算出这些订单占用的内存 （还要结合其他内存的占用）→ 推算出总内存占用量

### 例子2：如何支撑大并发流量系统（100万并发）的抢票功能

> CDN -> LVS -> NGINX -> 业务系统 -> 每台机器1万（单机10K问题）并发 * 100台机器
>
> 一笔交易：【下单 -> 订单系统（IO）减库存 -> 等待用户付款】
>
> * 普通系统：当做一个事务
> * 高并发系统的一种可能的模型：异步
>   * 线程（1）负责减库存
>   * 线程（2）将下单信息放入队列或Redis中，等待付款
>   * 线程（3）订单处理线程取付款数据、持久化订单数据到HBase或MySQL
> * 库存服务器的负载均衡、数据倾斜问题解决

具体问题具体分析，要深入业务逻辑

## 3 优化JVM运行环境 

> 例如解决系统卡顿、CPU100%、内存飙升等

### 例子1：50万PV资料类网站，服务器从32位1.5G堆升级到64位16G堆时发生严重卡顿

> 原网站慢的原因：大量数据load到内存、内存不足频繁GC、STW编程，响应时间变慢
>
> 升级后卡顿的原因：内存越大Full GC时间越长 
>
> 解决办法：回收器换成`Parallel New` + `CMS` 或者 `G1`

### 例子2：系统CPU经常100%、如何调优

> CPU 100%：说明一定有线程在占用系统资源
>
> 1. 找出哪个进程CPU高：`top`命令
> 2. 找出该进程中哪个线程CPU高：`top -Hp`命令
> 3. （如果是java程序）导出该线程的堆栈、看看它都在调用什么：`jstack`
> 4. 查找哪个方法（栈帧）消耗时间：`jstack`
>
> 具体的使用方法在demo []() 中列出

### 例子3：系统内存飙高、如何调优

> 内存飙高，一定是堆内存飙高
>
> 1. 导出堆内存（`jmap`)
> 2. 分析（`jhat`, `jvisual vm`,`mat`, `jprofiler`……等工具）

### 例子4：如何监控JVM

> `jstat`, `jvisualvm`, `jprofiler`,`arthas`,`top` ……

## 4 JVM命令行参数

### 4.1 JVM命令行参数参考

> [https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html)

### 4.2 HotSpot参数分类

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

查看不稳定参数的最终使用参数值：`java -XX:+PrintFlagsFinal -version`，可以结合`grep`来使用

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

查看不稳定参数的默认参数值：`java -XX:+PrintFlagsInitial`

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
>   * 例如：`java -XX:+UseConcMarkSweepGC -XX:+PrintCommandLineFlags GCCmdDemo`
> * **-XX:+UseParallelGC**  （1.8默认）
>   * **使用`Parallel Scavenge` +` Parallel Old`** 
> * -XX:+UseParallelOldGC 
>   * 使用`Parallel Scavenge` + `Parallel Old`组合
> * **-XX:+UseG1GC = G1**
>   * **使用G1 GC**
>

## 6 查看GC类型

> * 使用`java +XX:+PrintCommandLineFlags -version`查看不稳定参数，然后在参数列表中查找（部分JVM可以找到），例如`java +XX:+PrintCommandLineFlags -version | grep GC`
> * 通过GC的日志来分辨
> * Linux下1.8版本默认的垃圾回收器
>   * 1.8.0_181 默认（看不出来）Copy MarkCompact
>   * 1.8.0_222 默认 PS + PO

## 7 常用JVM参数

> * `-XX:+DisableExplictGC`：禁用显式GC（例如：`System.gc()`)

