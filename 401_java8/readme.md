<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Java 8 笔记](#java-8-%E7%AC%94%E8%AE%B0)
  - [1. Lambda表达式的语法](#1-lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%9A%84%E8%AF%AD%E6%B3%95)
  - [2. Stream](#2-stream)
  - [3. Lambda表达式的应用](#3-lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%9A%84%E5%BA%94%E7%94%A8)
  - [4. Java FX（跳过）](#4-java-fx%E8%B7%B3%E8%BF%87)
  - [5. 新的日期和时间库](#5-%E6%96%B0%E7%9A%84%E6%97%A5%E6%9C%9F%E5%92%8C%E6%97%B6%E9%97%B4%E5%BA%93)
  - [6. 并发库改进](#6-%E5%B9%B6%E5%8F%91%E5%BA%93%E6%94%B9%E8%BF%9B)
  - [7. JS Engine Noshorn（跳过）](#7-js-engine-noshorn%E8%B7%B3%E8%BF%87)
  - [8. 其他改进](#8-%E5%85%B6%E4%BB%96%E6%94%B9%E8%BF%9B)
- [Java 7 笔记](#java-7-%E7%AC%94%E8%AE%B0)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Java 8 笔记

摘要版本（用于速查）：[summary.md](summary.md)

## 1. Lambda表达式的语法

完整笔记：[https://github.com/fangkun119/java8note/blob/master/note/ch01_lambda.md](https://github.com/fangkun119/java8note/blob/master/note/ch01_lambda.md)

内容

> (1) Lambda表达式的编写，应用例子，以及变量访问权限
>
> (2) 函数式接口
>
> (3) 方法引用、构造器引用、数组构造器引用 
>
> (4) 接口默认方法、接口静态方法 
>
> (5) `java.util.Comparator`接口提供的静态方法

## 2. Stream

完整笔记：[https://github.com/fangkun119/java8note/blob/master/note/ch02_stream.md](https://github.com/fangkun119/java8note/blob/master/note/ch02_stream.md)

内容

> (1) Stream与Collection的区别，Sream Pipeline的结构
>
> (2) 创建Stream：`Colletion.stream`/`Stream.of`/`Arrays.Stream`/`Stream.empty`/`Stream.generate`/` Stream.iterate`
>
> (3) 中间操作：`filter`/`map`/`flatMap`/`limit`/`skip`/`concat`/`peak`/`distinct`/`sorted`
>
> (4) 终止操作
>
> * 简单聚合：`count`/`max`/`anyMatch`/`findAny`/`findFirst`/……
> * 使用`reduce`聚合
> * 收集结果：`iterator`/`toArray`/`forEach`
> * 收集结果：`collect（supplier, accumulator, comibner)`、用自定义的方式收集到集合中
> * `collect(Collector)`、使用类库或第三方提供的收集器
> * 分组分片收集：`groupingBy`、`partitioningBy`
>
> (5) 并行流：`parallelStream()`/`parallel()`
>
> (6) 原始类型流`IntStream`/`LongStream`/`DoubleStream`
>
> (7) `Optional<T>`的使用，Stream API用到的函数式接口
>
> (8) Comparator提供的静态方法、以及Stream API用到的函数式接口

## 3. Lambda表达式的应用

完整笔记：[https://github.com/fangkun119/java8note/blob/master/note/ch03_use_lambda.md](https://github.com/fangkun119/java8note/blob/master/note/ch03_use_lambda.md)

内容

> (1) 利用Lambda表达式延迟执行的特性减少代码的性能浪费
>
> (2) Lambda表达式作为方法参数时的传参原理
>
> (3) 函数式接口：(a) 编写；(b) 组合使用；(c) Java 8提供的函数式接口
>
> (4) 把函数（lambda表达式）用作方法的返回值
>
> (5) 函数组合：编写把两个函数（lambda表达式）组合在一起的方法
>
> (6) 按需组合一组`函数`，延迟到一个特定时间点一起执行
>
> (7) 让传入的函数并行执行，例如下面的代码
>
> (8) Lambda表达式抛出异常的处理方法
>
> (9) lambda表达式与泛型：(a) 使用`T[]:new`解决泛型擦除问题；(b) 泛型通配符的使用

## 4. Java FX（跳过）

## 5. 新的日期和时间库

完整笔记：[https://github.com/fangkun119/java8note/blob/master/note/ch05_date_time.md](https://github.com/fangkun119/java8note/blob/master/note/ch05_date_time.md)

内容

> (1) `Instant`/`Duration`，`LocalDate`/`Period`/`TemporalAdjusters`
>
> (2) `ZonedDateTime`
>
> (3) `DateTimeFormatter`
>
> (4) 新老类型相互转换

## 6. 并发库改进

完整笔记：[https://github.com/fangkun119/java8note/blob/master/note/ch06_concurrent.md](https://github.com/fangkun119/java8note/blob/master/note/ch06_concurrent.md)

内容

>  (1) 原子变量和乐观锁：
>
>  * `compareAndSet`，`updateAndGet`/`accumulateAndGet`
>  * 乐观锁分组封装以减少线程互斥：`LongAdder`/`DoubleAdder`/`LongAccumulator`/`DoubleAccumulator`
>  * 底层乐观锁API：`StampedLock`
>
>  (2) ConcurrentHashMap改进
>
>  * 设计改进：分组降低互斥；mappingCount()解决int32溢出；拉链扫描优化
>  * 原子性保证：`replace`，`LongAdder`作为value类型，`compute`/`merge`方法
>  * Set视图：新建或映射Hash Key为一个`Set<Key>`
>
>  (3) 并行数组操作（Arrays类）：并行排序，使用lambda表达式并行地设置数组元素
>
>  (4) 支持回调函数的`CompletableFuture<T>`以及CompletableFuture流水线创建

## 7. JS Engine Noshorn（跳过）

> 在Java 11中已经被废弃，Java 11使用GraalVM的JavaScript引擎替代Nashorn

## 8. 其他改进

完整笔记：[https://github.com/fangkun119/java8note/blob/master/note/ch08_other_imporvements.md](https://github.com/fangkun119/java8note/blob/master/note/ch08_other_imporvements.md)

内容

> (1) 类似Python的字符串拼接：`String.join(String, String[])`
>
> (2) 包装类（Integer/Long/Double/Float/Short/Byte/Character/Boolean）
>
> * 字节数：`BYTES`字段
> * stream聚合(reduce)支持：`sum`/`max`/`min`/`logicalAnd`/`logicalOr`/`logicalXor`
> * 无符号运算支持: `xxxUnsigned`/`toUnsignedXXX`/`isFinite`/`xxxValueExact`
>
> (3) 新的数学函数
>
> * 带溢出检查的数学运算：`Math.xxxExact`
> * 带溢出检查的数值转换：`Math.toXXXExact`
> * 能够避免结果为负的求模函数：`Math.floorMod`
> * 比指定值小但是最接近的浮点数：`Math.nextDown`
>
> (4) 集合类改进
>
> * Java 8新增的方法
> * Comparator：字段比较器，多级比较器，定制字段提取比较规则，省去装箱拆箱的比较器，兼容null的比较器，倒序比较器
>
> (5) 文件
>
> * 按行读取文件：`Stream<String> lines = Files.lines(Path)`
> * 按行读取其他数据：`Stream<String> lines = bufferedReader.lines()`
> * 自动异常类型转换（转为UncheckedIOException）
> * 遍历目录项的stream：`Files.list(Path)`,`Files.walk(Path)`,`Files.find(...)`
> * 在Stream中自动Base64自动编码解码
>
> (6) 注解
>
> * 支持编写能够重复使用的注解
> * 容许在`类型`上也使用注解
> * 方法参数反射
>
> (7) 其他
>
> * stream中null值检查：`Object.isNull`, `Object.nonNull`
> * 延迟操作
> * 正则表达式改进：(a) 使用命名捕获组来提取字符串 (b) 用正则表达式生成`Stream<String>`
> * 语言环境支持： `Locale`
> * JDBC支持：增加`LocalDate`/`LocalTime`/`LocalDateTime`，`executeLargeUpdate`，`getObject`/`setObject`

# Java 7 笔记

完整笔记：[https://github.com/fangkun119/java8note/blob/master/note/ch09_java7_features.md](https://github.com/fangkun119/java8note/blob/master/note/ch09_java7_features.md)

内容

> (1) 使用try-with-resource自动释放资源
>
> (2) 捕获多个异常
>
> (3) 反射方法异常捕捉改进
>
> (4) 用`Paths`接口替代`File`类
>
> (5) 读取小文件的API：按Byte或按行组织、一次读入整个文件
>
> (6) `目录/文件`的创建、检查、删除；对临时文件/目录的支持
>
> (7) 文件复制、移动、删除
>
> (8) 简化`equals`、`hashCode`、`CompareTo`方法的编写
>
> (9) 其他：(a) 全局Logger静态代码段死锁问题修正；(b) `Object.requireNonNull`；(c) `ProcessBuilder`； (d) `URLClassLoader`实现AutoClosable接口，可用在try-with-resource语句中；(e) BitSet





