[TOC]

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

## 2 CMS垃圾回收算法

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

## 3 G1垃圾回收算法

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
> `H`代表`Humongous`（`大对象区`），用来存放特别大、有可能跨2个甚至更多Region的对象。超过单个region的50%的对象、即为humongous object

### 3.2 G1的特点

> * 并发收集
>   * G1使用与`CMS`相同的三色标记（）算法、而之后的`ZGC`和`Shenandoah`则使用颜色指针（colored pointer）算法
>   * 三色标记：把对象分成三种不同颜色、分别表示未标记、标记中、已标记
>   * 颜色指针（colored pointer）：64位指针中留出3位用来标记指针所指向地址的变化状态，减少GC过程中的扫描量，只扫描地址变化过的指针
> * 压缩空闲空间不会延长GC的暂停时长，更容易预测的GC暂停时间，适合不需要实现很高吞吐量的场景
> * 新老年代比例：5% -  60%，G1根据STW时间来动态调整，一般不用手工指定

### 3.2 `Card`、`Card Table`：读GC文档时会遇到的概念

`Card`

> * 将内存分成一个一个的Card
>
> * 如果老年代的Card内有活着对象引用了年轻代的对象，就将这个Card标记为“Dirty”
> * 这样在年轻代YGC需要标记活着的对象时，对于老年代那部分，只需要扫描哪些dirty的Card就可以、而不需要扫描整个老年代

`Card Table`

> 用来记录Card的信息，例如哪些Card是dirty的，用bitmap实现

### 3.3 `RSet`、`CSet`：G1特有的概念

`CSet`（Collection  Set）：需要被回收的Card，记录在一张表格中，叫做`Collection Set`

> 在CSet中存活的数据会在GC过程中被移动到另一个可用分区。CSet中的Card可以来自Eden空间、Survivor空间、或者老年代。
>
> 占用空间大约不到堆内存整体空间的1%

`RSet`（Remembered Set）：每个`Regin`中都有一块表格（hash map）记录其他Region到本Region的引用

> 它使得垃圾收集器不需要扫描整个堆栈，就可以找到谁引用了当前分区中的对象
>
> 占用空间比较大，约10%，因此ZGC不再使用RSet（ZGC通过颜色指针将此信息记录在引用本身当中），只有G1使用

### 3.4 G1的GC

YGC：Eden空间不足时被触发

> 多线程并行执行、回收Eden区的Region
>
> 根据响应时间动态调整Eden区的Region数

MixedGC：作用相当于CMS

> `XX:InitiatingHeapOccupacyPercent`：默认值45%、（当YGC来不及清理）堆内存占用（Heap Occupacy Percent）超过这个值时，启动MixedGC
>
> 过程与CMS类似：
>
> (1) 初始标记（STW）：标记GC Root
>
> (2) 并发标记：使用三色标记 （下一小节介绍）
>
> (3) 最终标记（STW）（重新标记 ）：并发标记阶段会发生变化，因此在STW（stop the world）的状态下再更新一次。与CMS使用`Incremental Update`算法不同，G1使用`SATB（Snapshot At The Beginning）算法`（下一小节介绍），是为了避免重新扫描，提高速度
>
> (4) 筛选回收（STW）（并行）：筛选出垃圾占用最多的Region，复制存活对象之后，释放Region

FGC：Old空间不足、或者调用`System.gc()`时

> 分配过于频繁、Region回收跟不上、导致对象无法分配时，仍然会发生FGC
>
> 此时的解决方法由
>
> 1. 增加内存
> 2. 提高CPU性能：业务逻辑速度固定，提高CPU性能有助于加快垃圾回收
> 3. 降低MixedGC触发的阈值，让`MixedGC`提早发生（默认值是45%）
>
> G1的调优目标是FGC尽量没有

### 3.5 并发标记算法 (CMS，G1中使用)

并发标记

> 在初始标记阶段、标记了GC Root之后进入并发标记阶段，程序一边运行GC一边标记被Root Object引用的对象（没有STW），采用三色标记算法

三色标记算法：对象在逻辑上被标记为三种颜色

> * 白：对象没有标记
> * 灰：对象标记了，但是它的fields还没有标记完成
> * 黑：对象标记了，且它的fields也标记完成

在下面的条件下、三色标记会发生对象漏标

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_3color_miss_mark.jpg)
>
> 原本B指向D（灰色指向白色），但是在GC线程执行并发标记过程中，程序线程将对象引用变为A指向D（黑色指向白色），使得D在并发标记阶段没有机会被标记到、产生漏标

解决对象漏标的两种方法

> (1) Incremental Update（增量更新）：关注新增的引用（找到上图中的A→D）、方法是把黑色的A重新标记为灰色，重新触发对A的fields扫描
>
> (2) SATB - Snapshot  At The Beginning：关注引用的删除，当上图B→D消失时，把这个消失的引用关系推到GC的堆栈，保证D还能被GC扫描到
>
> CMS使用` Incremental Update`， 而G1使用`SATB - Snapshot  At The Beginning`是为了避免重新扫描整个堆、并且配合RSet来提高速度

## 4. 参考资料

> https://blogs.oracle.com/jonthecollector/our-collectors

