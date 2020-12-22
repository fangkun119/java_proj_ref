# GC算法

## 1 垃圾回收算法回顾

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_garbage_collectors.jpg)
>
> * 左边的6个垃圾回收算法为分代垃圾回收器，G1为逻辑上的分代垃圾回收器，其他的不分代
> * `<Serial，SerialOld>（几十兆内存）`→`<Parallel Scavenge, Parallel Old>（百兆至几个G的内存）`→`<ParNew, CMS>（20G内存）`→`G1（上百G内存）`的发展历程，是逐渐适应越来越大的内存的过程
>   * `<Serial，SerialOld>`组合：单线程垃圾回收器
>   * `<Parallel Scavenge, Parallel Old>`组合：多线程垃圾回收器
>   * `<ParNew, CMS>`组合，多线程垃圾回收器，老年代回收能够与程序运行同时进行，减少STW（Stop The World）的时间
>   * `G1`：不再进行物理分代，仅仅是逻辑分代，将内存分为很多内存块来分而治之，从根本上避免老年代扫描的问题
>   * `ZGC`：不分代，内存块大小变得更加灵活

## 2 CMS垃圾回收算法概要

> CMS（Concurrent Mark Sweep）是一个承前启后的算法，诞生过程非常波折（10年），它只是一个阶段性垃圾回收器。
>
> CMS有`碎片化`和`浮动垃圾`这两个导致回退到`单线程Full GC`的问题， 调优目标也只能是尽量让它避免`Full GC`，因此任何一个JVM都没有把`CMS`用作默认垃圾回收器（1.8是`Parallel`，1.9是`G1`）。
>
> 学习CMS的垃圾回收算法，更多是为了理解后续新的垃圾回收器。

`CMS`的四（六）阶段回收过程：[https://www.cnblogs.com/zhangxiaoguang/p/5792468.html](https://www.cnblogs.com/zhangxiaoguang/p/5792468.html)

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_gc_cms.jpg)
>
> * 初始标记：标记根对象`root objects`（会**STW**，但时间非常短，因为根对象数量少）
> * 并发标记：遍历整个老年代并且标记所有存活的对象，该阶段耗时最高，因此与应用程序同时运行
> * 重新标记：标记老年代全部的存活对象，包括那些在并发标记阶段更改的或者新创建的引用对象（会**STW**，但是时间非常短，因为只是执行阶段2时新产生的垃圾，数量少）
> * 并发清理：回收对象，与应用程序同时运行（其实在并发清理之前，还有两个准备阶段：Concurrent Preclean和Concurrent Abortable Preclean，因此有时也用**六阶段**来描述）。
>
> 在`并发清理`阶段新产生的垃圾叫做`浮动垃圾`、将在下一次垃圾回收中进行清 理
>
> 图片中的竖线用来切分阶段，蓝色箭头表示应用程序的线程，黄色箭头表示垃圾回收器的线程

## 3 G1垃圾回收算法概要

> 传统垃圾回收器（Serial，Parallel，CMS）的缺点是大块分代内存布局，导致内存空间太大，很难再提升效率减少阻塞（STW）。G1采用分治的思想、改为基于Region的小块内存布局。它的设计目标如下：
>
> * 服务端垃圾回收器，用在多核、大内存机器上
> * 目标是暂停时间短（200毫秒），同时保持较高的吞吐量（吞吐量比Parallel低10%-15%）
>
> 而G1名称，则来自`Garbage First Garbage Collector (G1 GC)`，它表示垃圾回收开始时优先回收Live Data最少（即垃圾最多）的`regions`
>
> 参考：
>
> [https://www.oracle.com/technical-resources/articles/java/g1gc.html](https://www.oracle.com/technical-resources/articles/java/g1gc.html)
>
> [https://tech.meituan.com/2016/09/23/g1.htm](https://tech.meituan.com/2016/09/23/g1.htm)

### 3.1 G1的内存布局

> 传统GC的内存布局
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_mem_layout_traditional.jpg)
>
> G1的内存布局，物理上是分成小块（`region`），逻辑上每个小块依然属于某一代（但属于哪一代并不固定）
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_mem_layout_g1.jpg)
>
> `Eden`（新生代）、`Survivor`（幸存区）、 `Old`（老年代）与传统GC的含义相同
>
> `H`代表`Humongous`（`大对象区`），用来存放特别大、有可能跨2个甚至更多Region的对象

### 3.2 G1的特点

> * 并发收集
>   * G1使用与`CMS`相同的三色标记（）算法、而之后的`ZGC`和`Shenandoah`则使用颜色指针（colored pointer）算法
>   * 三色标记：把对象分成三种不同颜色、分别表示未标记、标记中、已标记
>   * 颜色指针（colored pointer）：64位指针中留出3位用来标记指针所指向地址的变化状态，减少GC过程中的扫描量，只扫描地址变化过的指针
> * 压缩空闲空间不会延长GC的暂停时长
> * 更容易预测的GC暂停时间
> * 适用于不需要实现很高吞吐量的场景

### 3.N 一些概念

> 读相关文档时会遇到的一些概念，实际当中用不到，列出来仅为了便于参考
>
> * `Card`：将内存分成一个一个的Card，如果老年代的Card内有活着对象引用了年轻代的对象，就将这个Card标记为“Dirty”。这样在年轻代YGC需要标记活着的对象时，对于老年代那部分，只需要扫描哪些dirty的Card就可以、而不需要扫描整个老年代
> * `Card Table`：用来记录Card的信息，例如哪些Card是dirty的，用bitmap实现
> * 