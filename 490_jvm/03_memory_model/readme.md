# 内存屏障、Java内存布局

> * 高并发情况下Java内存怎么支持
> * 一个对象new出来之后，在Java内存中是怎么布局的

## 1. 硬件层的并发优化基础知识

1. 存储器层次结构（《深入理解计算机系统》，第三版，P421 ）

> `L0`：寄存器；`L1`/`L2`/`L3`：高速缓存；`L4`：主存（内存）；`L5`：硬盘；`L6`：远程文件存储
> 
> * 寄存器：1 cycle
> * L1：3-4 cycle、约1ns
> * L2：约10 cycles、3ns
> * L3：约40-45 cycles、15ns
> * QPI总线传输：约20ns
> * 主存：约60-80ns

2. 总线锁、缓存锁

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

3. 缓存行（`Cache Line`）

> 为了提高效率，读取缓存时以缓存行（`Cache Line`）为基本单位，目前主流的实现是`64 Bytes`

4. 伪共享

> * 位于同一缓存行的两个不同数据被两个不同CPU锁定，产生互相影响的共享问题 

## 2. CPU指令重排带来的执行顺序问题

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

(1) Java代码

> [jvm/grp03_memory_model/TestVolatile.java](../demos/src/com/javaprojref/jvm/grp03_memory_model/TestVolatile.java)
> 
> ~~~java
> public class TestVolatile {
> 	int i;
> 	volatile int j;
> }
> ~~~

(2) 字节码层面

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jclasslib_volatile.jpg)
> 
> 只是为变量`j`增加了`ACC_VOLATILE`标记

(3) JVM层面

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

(4) OS和硬件层面

> * [volatile与lock前缀指令](https://blog.csdn.net/qq_26222859/article/details/52235930)：使用`hsdis - HotSpot Dis Assembler`观察汇编码、`lock ${instruction_to_be_locked}`指令执行时对内存区域的加锁

#### `synchronized`实现细节

(1) java代码

> [`jvm/grp03_memory_model/TestVolatile.java`](../demos/src/com/javaprojref/jvm/grp03_memory_model/TestVolatile.java)

(2) 字节码层面

`synchronized void m() { }`

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_synchronized_method.jpg) 
> 
> 使用`ACC_SYNCHRONIZED`来标记函数

`void n() { synchronized(this) {} }`

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_synchronized_code_block.jpg)
> 
> 使用monitorenter来告知JVM需要加锁、使用monitorexit来解锁（共2个：1个用于正常退出；1个用于异常退出）

(3) JVM层面

> `C` `C++` 调用了操作系统提供的同步机制

(4) OS和硬件层面

> X86：`lock comxchg xxxx`指令 (locked compare and exchange)
> 
> 参考： [Java使用字节码和汇编语言同步分析volatile，synchronized的底层实现](https://blog.csdn.net/21aspnet/article/details/88571740)










