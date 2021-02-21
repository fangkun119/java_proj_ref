<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [内存屏障、Java内存布局](#%E5%86%85%E5%AD%98%E5%B1%8F%E9%9A%9Cjava%E5%86%85%E5%AD%98%E5%B8%83%E5%B1%80)
  - [1 硬件层的并发优化基础知识](#1-%E7%A1%AC%E4%BB%B6%E5%B1%82%E7%9A%84%E5%B9%B6%E5%8F%91%E4%BC%98%E5%8C%96%E5%9F%BA%E7%A1%80%E7%9F%A5%E8%AF%86)
    - [1.1 存储器层次结构（《深入理解计算机系统》，第三版，P421 ）](#11-%E5%AD%98%E5%82%A8%E5%99%A8%E5%B1%82%E6%AC%A1%E7%BB%93%E6%9E%84%E6%B7%B1%E5%85%A5%E7%90%86%E8%A7%A3%E8%AE%A1%E7%AE%97%E6%9C%BA%E7%B3%BB%E7%BB%9F%E7%AC%AC%E4%B8%89%E7%89%88p421-)
    - [1.2 总线锁、缓存锁](#12-%E6%80%BB%E7%BA%BF%E9%94%81%E7%BC%93%E5%AD%98%E9%94%81)
    - [1.3 缓存行（`Cache Line`）](#13-%E7%BC%93%E5%AD%98%E8%A1%8Ccache-line)
    - [1.4 伪共享](#14-%E4%BC%AA%E5%85%B1%E4%BA%AB)
  - [2. CPU指令重排、内存屏障、`volatile`及`synchronized`](#2-cpu%E6%8C%87%E4%BB%A4%E9%87%8D%E6%8E%92%E5%86%85%E5%AD%98%E5%B1%8F%E9%9A%9Cvolatile%E5%8F%8Asynchronized)
    - [2.1 CPU指令重排](#21-cpu%E6%8C%87%E4%BB%A4%E9%87%8D%E6%8E%92)
    - [2.2 如何保证特定情况下不发生指令重排](#22-%E5%A6%82%E4%BD%95%E4%BF%9D%E8%AF%81%E7%89%B9%E5%AE%9A%E6%83%85%E5%86%B5%E4%B8%8B%E4%B8%8D%E5%8F%91%E7%94%9F%E6%8C%87%E4%BB%A4%E9%87%8D%E6%8E%92)
      - [(1) CPU内存屏障](#1-cpu%E5%86%85%E5%AD%98%E5%B1%8F%E9%9A%9C)
      - [(2) JVM级别的内存屏障（`JSR133`）](#2-jvm%E7%BA%A7%E5%88%AB%E7%9A%84%E5%86%85%E5%AD%98%E5%B1%8F%E9%9A%9Cjsr133)
      - [(3) `Volatile`的实现细节](#3-volatile%E7%9A%84%E5%AE%9E%E7%8E%B0%E7%BB%86%E8%8A%82)
      - [`synchronized`实现细节](#synchronized%E5%AE%9E%E7%8E%B0%E7%BB%86%E8%8A%82)
  - [3 Java对象内存布局](#3-java%E5%AF%B9%E8%B1%A1%E5%86%85%E5%AD%98%E5%B8%83%E5%B1%80)
    - [3.1 对象创建过程](#31-%E5%AF%B9%E8%B1%A1%E5%88%9B%E5%BB%BA%E8%BF%87%E7%A8%8B)
    - [3.2 对象在内存中的布局](#32-%E5%AF%B9%E8%B1%A1%E5%9C%A8%E5%86%85%E5%AD%98%E4%B8%AD%E7%9A%84%E5%B8%83%E5%B1%80)
      - [(1) 虚拟机设置](#1-%E8%99%9A%E6%8B%9F%E6%9C%BA%E8%AE%BE%E7%BD%AE)
      - [(2) 普通对象布局](#2-%E6%99%AE%E9%80%9A%E5%AF%B9%E8%B1%A1%E5%B8%83%E5%B1%80)
      - [(3) 数组对象布局](#3-%E6%95%B0%E7%BB%84%E5%AF%B9%E8%B1%A1%E5%B8%83%E5%B1%80)
      - [(4) 对象头包含的内容](#4-%E5%AF%B9%E8%B1%A1%E5%A4%B4%E5%8C%85%E5%90%AB%E7%9A%84%E5%86%85%E5%AE%B9)
      - [(5) 对象怎么定位](#5-%E5%AF%B9%E8%B1%A1%E6%80%8E%E4%B9%88%E5%AE%9A%E4%BD%8D)
  - [4 其他](#4-%E5%85%B6%E4%BB%96)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 内存屏障、Java内存布局

> * 高并发情况下Java内存怎么支持
> * 一个对象new出来之后，在Java内存中是怎么布局的

## 1 硬件层的并发优化基础知识

### 1.1 存储器层次结构（《深入理解计算机系统》，第三版，P421 ）

> `L0`：寄存器；`L1`/`L2`/`L3`：高速缓存；`L4`：主存（内存）；`L5`：硬盘；`L6`：远程文件存储
> 
> * 寄存器：1 cycle
> * L1：3-4 cycle、约1ns
> * L2：约10 cycles、3ns
> * L3：约40-45 cycles、15ns
> * QPI总线传输：约20ns
> * 主存：约60-80ns

### 1.2 总线锁、缓存锁

> 问题：每个核都有专用L1/L2缓存，多个核共享L3缓存时，如何保证同一个变量load到L1/L2里面的值，在不同的CPU内核之间保持一致？
> 
> 方法1：使用`Bus Lock`，问题是效率低
> 方法2：用`Cache一致性协议`实现缓存锁
> 
> * 例如[MESI Cache](https://www.cnblogs.com/z00377750/p/9180644.html) (用在Intel CPU上）
> 	* CPU的每个缓存航（`Cache Line`）提供两个bit用于4种标记：`Modified`（存储的数据被当前内核上修改）、`Shared`（其他内核也在读）、`Exclusive`（当前内核独占）、`Invalid`（存储的数据被其他内核上修改、需要从内存同步最新数据）
> * 其他一致性协议诸如：`MSI`、`MOSI`、`Synapse Firefly Dragon`，……
> * 对于无法被缓存的数据、或者跨多个`缓存行`的数据，依然使用总线锁
> * 现代CPU的数据一致性通过`缓存锁`、`总线锁`来实现

### 1.3 缓存行（`Cache Line`）

> 为了提高效率，读取缓存时以缓存行（`Cache Line`）为基本单位，目前主流的实现是`64 Bytes`

### 1.4 伪共享

> * 位于同一缓存行的两个不同数据被两个不同CPU锁定，产生互相影响的共享问题

## 2. CPU指令重排、内存屏障、`volatile`及`synchronized`

### 2.1 CPU指令重排

> * CPU为了提高执行效率，会在一条执行的执行过程中（例如慢速的读内存），同时会去执行另一条指令，前提是这两条指令之间没有关系 [[参考](https://www.cnblogs.com/liushaodong/p/4777308.html)] 
> 	* `读指令`的同时、可以执行不影响的其他指令
> 	* `写指令`可以使用`WCBuffer`进行`合并写`来解决`L2缓存`速度慢于`L1缓存`的问题
> 	* `WCBuffer`：Writing Combining Buffer、速度极高、容量很小通常只有4 bytes
> * Demo：演示会发生指令重排
> 	* [../demos/src/com/javaprojref/jvm/grp03_memorymgr/Demo01InstructionReOrder.java](../demos/src/com/javaprojref/jvm/grp03_memorymgr/Demo01InstructionReOrder.java) 

### 2.2 如何保证特定情况下不发生指令重排

#### (1) CPU内存屏障

`volatile` 的底层实现，就使用了`CPU内存屏障`（Windows是`Lock`指令）

> 以Intel X86为例，以下是相关的指令
> 
> * `sfence`：该指令之前的写（Save）操作、必须在该指令之后的写操作开始前完成
> * `ifence`：该指令之前的读（Load）操作、必须在该指令之后的读操作开始前完成
> * `mfence`：该指令之前的操作，不论读写，必须在该指令之后的操作开始前完成
> * `lock`：后接另一条指令、如`lock add ...`，表示后面那条指令完成之前，所操作的内存不容许改变

用`volatile`修饰的java变量时，该变量访问前后都会设置`CPU内存屏障`

#### (2) JVM级别的内存屏障（`JSR133`）

> * `LoadLoad Barrier`，对应类似`Load1; LoadLoad; Load2`的指令序列，表示Load2及后续读操作要在Load1完成之后才能开始
> * `StoreStore Barrier`，对应类似`Store1; StoreStore; Store2`的指令序列，表示Store2及后续写操作要在Store1完成之后才能开始
> * `LoadStore Barrier`，对应类似`Load1; LoadStore; Store2`的指令序列，表示Store2及后续写操作要在Load1完成之后才能开始
> * `StoreLoad Barrier`，对应类似`Store1; StoreLoad; Load2`的指令序列，表示Load2及后续读操作要在Store`完成之后才能开始

#### (3) `Volatile`的实现细节

Java代码

> [jvm/grp03_memory_model/TestVolatile.java](../demos/src/com/javaprojref/jvm/grp03_memory_model/TestVolatile.java)
> 
> ~~~java
> public class TestVolatile {
> 	int i;
> 	volatile int j;
> }
> ~~~

字节码层面

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jclasslib_volatile.jpg" width="800" /></div>
> 
> 只是为变量`j`增加了`ACC_VOLATILE`标记

JVM层面

> volatile内存区的读写都加屏障
> 
> 写操作
> 
> ~~~text
> StoreStoreBarrier
> volatile写操作
> StoreLoadBarrier
> ~~~
>
> 读操作
> 
> ~~~text
> LoadLoadBarrier
> volatile读操作
> LoadStoreBarrier
> ~~~

OS和硬件层面

> * [volatile与lock前缀指令](https://blog.csdn.net/qq_26222859/article/details/52235930)：使用`hsdis - HotSpot Dis Assembler`观察汇编码、`lock ${instruction_to_be_locked}`指令执行时对内存区域的加锁

#### `synchronized`实现细节

java代码

> [`jvm/grp03_memory_model/TestVolatile.java`](../demos/src/com/javaprojref/jvm/grp03_memory_model/TestVolatile.java)

字节码层面

`synchronized void m() { }`

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_synchronized_method.jpg" width="800" /></div> 
> 
> 使用`ACC_SYNCHRONIZED`来标记函数

`void n() { synchronized(this) {} }`

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_synchronized_code_block.jpg" width="800" /></div>
> 
> 使用monitorenter来告知JVM需要加锁、使用monitorexit来解锁（共2个：1个用于正常退出；1个用于异常退出）

JVM层面

> `C` `C++` 调用了操作系统提供的同步机制

OS和硬件层面

> X86：`lock comxchg xxxx`指令 (locked compare and exchange)
> 
> 参考： [Java使用字节码和汇编语言同步分析volatile，synchronized的底层实现](https://blog.csdn.net/21aspnet/article/details/88571740)

## 3 Java对象内存布局

### 3.1 对象创建过程

>1. 	`Class Loading`：把`.class`文件加载到 内存
>2. 	`Class Linking (Verification, Preparation, Resolution)`
>	* Verification：检查是否符合标准
>	* Preparation：静态变量设默认值
>	* Resolution：把常量池里面的符号引用转换成可以直接取到值的内容
>3. `Class Initialaization`：类静态变量设为初始值，执行静态代码块
>4.	申请对象内存
>5. 	成员变量赋默认值
>6. 	调用构造方法，在字节码中用`<init>`来表示
>	* 成员变量顺序赋初始值：例如代码中的`private int m = 8`
>	* 执行构造方法语句：先调用`super()`再调用构造方法

### 3.2 对象在内存中的布局

#### (1) 虚拟机设置

> 对象大小和布局与虚拟机实现和设置有关，要先看一下虚拟机的配置
> 
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/
> $ java -XX:+PrintCommandLineFlags -version
> -XX:ConcGCThreads=1 -XX:G1ConcRefinementThreads=4 -XX:GCDrainStackTargetSize=64 -XX:InitialHeapSize=134217728 -XX:MarkStackSize=4194304 -XX:MaxHeapSize=2147483648 -XX:MinHeapSize=6815736 -XX:+PrintCommandLineFlags -XX:ReservedCodeCacheSize=251658240 -XX:+SegmentedCodeCache -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC
> openjdk version "15.0.1" 2020-10-20
> OpenJDK Runtime Environment (build 15.0.1+9-18)
> OpenJDK 64-Bit Server VM (build 15.0.1+9-18, mixed mode, sharing)
> ~~~
>
> 其中的
> 
> * `-XX:+UseCompressedClassPointers`：与对象头有关
> * `-XX:+UseCompressedOops`：与对象布局有关

#### (2) 普通对象布局

1. 对象头（在HotSpot中称为markword）： 8字节
2. ClassPointer指针：指向所对应的Class类对象，`-XX:+UseCompressedClassPointers`开启时为4字节、关闭时为8字节
3. 实例数据：成员变量，其中的引用类型、当`-XX:+UseCompressedOops`开启时为4字节、关闭时为8字节
4. Padding对齐：为8的倍数，为了提高读取效率

例如：`Object o = new Object`在内存中占多少个字节：

> 总共16字节（没有成员变量）
> 
> * 对象头：8字节
> * ClassPointer：4字节（压缩时）或8字节（不压缩时）
> * Padding：4字节（压缩时）或0字节（不压缩时）

#### (3) 数组对象布局

1.对象头
2.ClassPointer指针
3.数组长度：4字节（比普通的对象多出来的一项）
4.数组数据
5.Padding对齐

#### (4) 对象头包含的内容

每个JVM版本实现都不一样，以下图某个`markword 32 bit`的JVM为例：

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_object_markword.jpg" width="800" /></div>

> *	`锁标志位`（2 bit）表示对象加锁状态
> 	* `无锁态`
> 	* `轻量级锁`：仅在JVM虚拟机层面的锁、不会调用内核
> 	* `重量级锁`：会调用到内核
> 	* `偏向锁`：偏向某个线程
> * `GC标记`；
> * `分代年龄`：4bit，因此GC分代年龄默认值是15
> * `hashCode`：当调用未重写过的`hashCode()`方法以及进一步`System.identityHashCode()`被调用时，JVM用`os::random::next_rand`为对象生成hashCode并存储在这里，因为对应的bit位已经被占用、该对象无法再进入`偏向锁`状态；对过重写过`hashCode()`方法、则不会使用这些bit位

#### (5) 对象怎么定位

参考：[https://blog.csdn.net/clover_lily/article/details/80095580](https://blog.csdn.net/clover_lily/article/details/80095580) 

有两种方法：(1) `句柄池`；(2) `直接指针`。不同的虚拟机采用不同的方法，`Hostspot`使用的是`直接指针·

1 句柄池

执行`T t = new T()`时，在句柄池中存入两个指针，一个指向生成的对象，一个指向对应的`Class`对象上；垃圾回收更快

2 直接指针：

执行`T t = new T()`时，生成对象，对象中有一个指针，指向对应的`Class`对象；找对象更快

## 4 其他

Java 8大原子操作（已经在JSR-133中被废弃、但JMM还没有发生变化，《深入理解Java虚拟机》P364）

> * `lock`：主内存操作、`标识`变量线程独占
> * `unlock`：主内存操作、`解除`变量线程独占
> * `read`：主内存操作、读取主内存读数据到工具内存
> * `load`：工作内存操作、把read到的值放入线程本地变量副本
> * `use`：工作内存操作、传值给执行引擎
> * `assign`：工作内存操作、执行引擎结果赋值给线程本地变量
> * `store`：工作内存、存值到主内存给write使用
> * `write`：主内存操作、写变量值

Java并发内存模型

> Java线程 <=> 工作内存 <=> Save和Load操作 <=> 主内存

`happens-before原则`：JVM规定重排序必须准守的规则（来自`Java Language Sepcification Java SE 12 Edition $17.4.5`）

> * `程序次序规则`：同一个线程内、按照代码出现的顺序，前面的代码先行于后面的代码（控制流顺序）、因为要考虑到分支和循环结构
> * `管程锁定规则`：一个unlock操作先行发生于后面（时间上）对同一个锁的lock操作
> * `volatile变量规则`：对一个volatile变量的写操作先行发生于后面（时间上）对这个变量的读操作
> * `线程启动规则` ：Thread的start()方法先行发生于这个线程的每一个操作
> * `线程终止规则`：线程的所有操作都先行于对此线程的终止检测。可以通过`Thread.join()`、`Thread.isAlive()`等检测线程的终止
> * `对象终结规则`：对象的初始化完成先于发生它的`finalize()`方法的开始
> * `传递性`：如果操作A先于B、B先于C，那么操作A先于C

`as if serial原则`：不管如何排序，单线程执行结果不会改变




