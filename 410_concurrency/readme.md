<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Java Multi-Thread & Concurrency](#java-multi-thread--concurrency)
  - [Java 6](#java-6)
    - [1. 线程封装、创建、Join、异常捕获](#1-%E7%BA%BF%E7%A8%8B%E5%B0%81%E8%A3%85%E5%88%9B%E5%BB%BAjoin%E5%BC%82%E5%B8%B8%E6%8D%95%E8%8E%B7)
    - [2. 互斥机制、线程本地存储、线程优雅终止、线程阻塞及中断](#2-%E4%BA%92%E6%96%A5%E6%9C%BA%E5%88%B6%E7%BA%BF%E7%A8%8B%E6%9C%AC%E5%9C%B0%E5%AD%98%E5%82%A8%E7%BA%BF%E7%A8%8B%E4%BC%98%E9%9B%85%E7%BB%88%E6%AD%A2%E7%BA%BF%E7%A8%8B%E9%98%BB%E5%A1%9E%E5%8F%8A%E4%B8%AD%E6%96%AD)
    - [3. 线程协作、生产者消费者、BlockingQueue、线程管道、死锁](#3-%E7%BA%BF%E7%A8%8B%E5%8D%8F%E4%BD%9C%E7%94%9F%E4%BA%A7%E8%80%85%E6%B6%88%E8%B4%B9%E8%80%85blockingqueue%E7%BA%BF%E7%A8%8B%E7%AE%A1%E9%81%93%E6%AD%BB%E9%94%81)
    - [4. java.util.concurrent包的并发组件](#4-javautilconcurrent%E5%8C%85%E7%9A%84%E5%B9%B6%E5%8F%91%E7%BB%84%E4%BB%B6)
    - [5. Synchronized修饰、Copy On Write、原子类与乐观锁、Activity Object](#5-synchronized%E4%BF%AE%E9%A5%B0copy-on-write%E5%8E%9F%E5%AD%90%E7%B1%BB%E4%B8%8E%E4%B9%90%E8%A7%82%E9%94%81activity-object)
  - [Java 8](#java-8)
    - [1. Java 8对并发的改进](#1-java-8%E5%AF%B9%E5%B9%B6%E5%8F%91%E7%9A%84%E6%94%B9%E8%BF%9B)
    - [2. 其他资料](#2-%E5%85%B6%E4%BB%96%E8%B5%84%E6%96%99)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Java Multi-Thread & Concurrency

## Java 6

> 资料来源：Java编程思想第4版

### 1. 线程封装、创建、Join、异常捕获

> 笔记[https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_1.pdf](https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_2.pdf)

主要内容

> 1. 线程创建：Runnable,  Thread, ThreadFactory， ExecutorService， Runnable， 
>
>     Callable
>
> 2. 常用线程池：CachedThreadPool，SingleThreadPool，FixedThreadPool
>
> 3. 线程异常：异常无法跨线程捕捉；线程异常不捕捉的结果；设置UncaughtExceptionHandler
>
> 4. 后台线程
>
> 5. 线程优先级，获得线程名称，中断一个线程，Join一个线程

### 2. 互斥机制、线程本地存储、线程优雅终止、线程阻塞及中断

> 笔记[https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_2.pdf](https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_2.pdf)

主要内容

> 1. synchronized, static synchronized, 临界区的实现原理、用途、可重入性
> 2. 显示锁ReentrantLock的使用方法及优缺点
> 3. 变量读写操作不一定具有原子性，volatile变量的作用和可用场景
> 4. 原子类
> 5. 线程本地存储
> 6. 线程优雅终止
> 7. 线程状态、阻塞及中断

### 3. 线程协作、生产者消费者、BlockingQueue、线程管道、死锁

> 笔记
>
> [https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_3.pdf](https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_3.pdf)

主要内容

> 1. wait、notify、notifyAll实现原理、使用时的注意事项、代码模板
> 2. 单生产者单消费者队列实现
> 3. 显式的Lock、Condition的使用
> 4. 阻塞队列：LinkedBlockingQueue、ArrayBlockingQueue、SynchronousQueue
> 5. 死锁避免
> 6. PipedWrite和PipedReader

### 4. java.util.concurrent包的并发组件

> 笔记
>
> [https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_4.pdf](https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_4.pdf)

主要内容

> 1. `CountDownLatch`：并发执行子任务 → 等待全部任务执行完毕 → 执行后续操作
> 2. `CyclicBarrier`：并发执行子任务 → 汇总任务 → 并发执行子任务 → 汇总任务 → ……
> 3. `DelayQueue`：入队是存入`Delayed`对象，出队时如果有对象超时则可以取出、否则阻塞或只能取出null
> 4. `PriorityBlockingQueue`：队空时take()方法会阻塞，有元素时会让优先级最高的元素返回并出队
> 5. `ScheduledThreadPoolExecutor`：将Runnable对象设置为在某个时间点运行，或按照固定的时间间隔运行
> 6. `Sempaphore`：信号量封装、以同步方式记录可用的资源数量，例如用于实现资源池等
> 7. `Exchanger`：两个线程交换对象使用，线程A调用exchanger.exchange()时阻塞，直到线程B也调用exchanger.exchange()并交换双方持有的对象

### 5. Synchronized修饰、Copy On Write、原子类与乐观锁、Activity Object

> 笔记
>
> [https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_5.pdf](https://github.com/kenfang119/pics/blob/main/multithread/think_in_java_4/java_multithread_5.pdf)

主要内容

> 1. `Collections.synchronizedXXX`方法：将非同步容器修饰为同步容器、支持Collection、List、Set、SortedSet、Map
> 2. CopyOnWrite类库：CopyOnWriteArrayList、CopyOnWriteArraySet、ConcurrentHashMap、ConcurrentLinkedQueue
> 3. 原子类与乐观锁
> 4. 读写锁
> 5. ActiveObject多线程设计模式

## Java 8

### 1. Java 8对并发的改进

> 笔记
>
> [https://github.com/fangkun119/java8note/blob/master/note/ch06_concurrent.md](https://github.com/fangkun119/java8note/blob/master/note/ch06_concurrent.md)

主要内容

> 1. 原子变量和乐观锁
>
>     `compareAndSet`，`updateAndGet`/`accumulateAndGet`
>
>     乐观锁分组封装以减少线程互斥：`LongAdder`/`DoubleAdder`/`LongAccumulator`/`DoubleAccumulator`
>
>     底层乐观锁API：`StampedLock`
>
> 2. ConcurrentHashMap改进
>
>     设计改进：分组降低互斥；mappingCount()解决int32溢出；拉链扫描优化
>
>     原子性保证：`replace`，`LongAdder`作为value类型，使用lambda表达式的`compute`/`merge`方法
>
>     Set视图：新建或映射Hash Key为一个`Set<Key>`
>
> 3. 并行数组操作（Arrays类）：并行排序，使用lambda表达式并行地设置数组元素
>
> 4. 支持回调函数的`CompletableFuture<T>`以及CompletableFuture流水线创建

### 2. 其他资料

> `On Java 8`（Java编程思想第5版）第24章：Concurrency
>
> 中文版
>
> [https://github.com/LingCoder/OnJava8/blob/1ef7ec48e492862300e667e24c245e9b3a5ccd98/docs/book/24-Concurrent-Programming.md](https://github.com/LingCoder/OnJava8/blob/1ef7ec48e492862300e667e24c245e9b3a5ccd98/docs/book/24-Concurrent-Programming.md)
>
> 代码
>
> [https://github.com/sjsdfg/OnJava8-Examples-Maven/tree/master/concurrent](https://github.com/sjsdfg/OnJava8-Examples-Maven/tree/master/concurrent)
>
> 笔记（todo）

