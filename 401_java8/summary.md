<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Java 8 Note](#java-8-note)
  - [1. Lambda表达式的语法](#1-lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%9A%84%E8%AF%AD%E6%B3%95)
    - [完整笔记](#%E5%AE%8C%E6%95%B4%E7%AC%94%E8%AE%B0)
    - [摘要](#%E6%91%98%E8%A6%81)
      - [(1) Lambda表达式的编写，应用例子，以及变量访问权限](#1-lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%9A%84%E7%BC%96%E5%86%99%E5%BA%94%E7%94%A8%E4%BE%8B%E5%AD%90%E4%BB%A5%E5%8F%8A%E5%8F%98%E9%87%8F%E8%AE%BF%E9%97%AE%E6%9D%83%E9%99%90)
      - [(2) 函数式接口](#2-%E5%87%BD%E6%95%B0%E5%BC%8F%E6%8E%A5%E5%8F%A3)
      - [(3) 方法引用、构造器引用、数组构造器引用](#3-%E6%96%B9%E6%B3%95%E5%BC%95%E7%94%A8%E6%9E%84%E9%80%A0%E5%99%A8%E5%BC%95%E7%94%A8%E6%95%B0%E7%BB%84%E6%9E%84%E9%80%A0%E5%99%A8%E5%BC%95%E7%94%A8)
      - [(4) 接口默认方法、接口静态方法](#4-%E6%8E%A5%E5%8F%A3%E9%BB%98%E8%AE%A4%E6%96%B9%E6%B3%95%E6%8E%A5%E5%8F%A3%E9%9D%99%E6%80%81%E6%96%B9%E6%B3%95)
      - [(5) `java.util.Comparator`接口提供的静态方法](#5-javautilcomparator%E6%8E%A5%E5%8F%A3%E6%8F%90%E4%BE%9B%E7%9A%84%E9%9D%99%E6%80%81%E6%96%B9%E6%B3%95)
  - [2. Stream](#2-stream)
    - [完整笔记](#%E5%AE%8C%E6%95%B4%E7%AC%94%E8%AE%B0-1)
    - [摘要](#%E6%91%98%E8%A6%81-1)
      - [(1) Stream与Collection的区别，Sream Pipeline的结构](#1-stream%E4%B8%8Ecollection%E7%9A%84%E5%8C%BA%E5%88%ABsream-pipeline%E7%9A%84%E7%BB%93%E6%9E%84)
      - [(2) 创建Stream的方法](#2-%E5%88%9B%E5%BB%BAstream%E7%9A%84%E6%96%B9%E6%B3%95)
        - [`Collection.stream`或使用各种类库提供的方法](#collectionstream%E6%88%96%E4%BD%BF%E7%94%A8%E5%90%84%E7%A7%8D%E7%B1%BB%E5%BA%93%E6%8F%90%E4%BE%9B%E7%9A%84%E6%96%B9%E6%B3%95)
        - [静态方法 `Sream.of` 或 `Arrays.stream`](#%E9%9D%99%E6%80%81%E6%96%B9%E6%B3%95-sreamof-%E6%88%96-arraysstream)
        - [`Stream.empty`，`Stream.generate`，`Stream.Iterator`](#streamemptystreamgeneratestreamiterator)
      - [(3) 中间操作：`filter`、`map`、`flatMap`、`limit`、`skip`、`concat`、`peak`、`distinct`、`sorted`](#3-%E4%B8%AD%E9%97%B4%E6%93%8D%E4%BD%9Cfiltermapflatmaplimitskipconcatpeakdistinctsorted)
      - [(4) 终止操作](#4-%E7%BB%88%E6%AD%A2%E6%93%8D%E4%BD%9C)
        - [简单聚合：`count`、`max`、`anyMatch`、`findAny`、`findFirst`、……](#%E7%AE%80%E5%8D%95%E8%81%9A%E5%90%88countmaxanymatchfindanyfindfirst)
        - [使用`reduce`聚合](#%E4%BD%BF%E7%94%A8reduce%E8%81%9A%E5%90%88)
        - [收集结果：`iterator`、`toArray`、`forEach`](#%E6%94%B6%E9%9B%86%E7%BB%93%E6%9E%9Citeratortoarrayforeach)
        - [收集结果：`collect（supplier, accumulator, comibner)`、用自定义的方式收集到集合中](#%E6%94%B6%E9%9B%86%E7%BB%93%E6%9E%9Ccollectsupplier-accumulator-comibner%E7%94%A8%E8%87%AA%E5%AE%9A%E4%B9%89%E7%9A%84%E6%96%B9%E5%BC%8F%E6%94%B6%E9%9B%86%E5%88%B0%E9%9B%86%E5%90%88%E4%B8%AD)
        - [`collect(Collector)`、使用类库或第三方提供的收集器](#collectcollector%E4%BD%BF%E7%94%A8%E7%B1%BB%E5%BA%93%E6%88%96%E7%AC%AC%E4%B8%89%E6%96%B9%E6%8F%90%E4%BE%9B%E7%9A%84%E6%94%B6%E9%9B%86%E5%99%A8)
      - [(5) 并行流：`parallelStream()`/`parallel()`](#5-%E5%B9%B6%E8%A1%8C%E6%B5%81parallelstreamparallel)
      - [(6) 原始类型流`IntStream`/`LongStream`/`DoubleStream`](#6-%E5%8E%9F%E5%A7%8B%E7%B1%BB%E5%9E%8B%E6%B5%81intstreamlongstreamdoublestream)
        - [创建](#%E5%88%9B%E5%BB%BA)
        - [装箱，和解除装箱](#%E8%A3%85%E7%AE%B1%E5%92%8C%E8%A7%A3%E9%99%A4%E8%A3%85%E7%AE%B1)
        - [使用以及与普通Stream的区别](#%E4%BD%BF%E7%94%A8%E4%BB%A5%E5%8F%8A%E4%B8%8E%E6%99%AE%E9%80%9Astream%E7%9A%84%E5%8C%BA%E5%88%AB)
      - [(7) `Optional<T>`的使用，Stream API用到的函数式接口](#7-optionalt%E7%9A%84%E4%BD%BF%E7%94%A8stream-api%E7%94%A8%E5%88%B0%E7%9A%84%E5%87%BD%E6%95%B0%E5%BC%8F%E6%8E%A5%E5%8F%A3)
      - [(8) Comparator提供的静态方法、以及Stream API用到的函数式接口](#8-comparator%E6%8F%90%E4%BE%9B%E7%9A%84%E9%9D%99%E6%80%81%E6%96%B9%E6%B3%95%E4%BB%A5%E5%8F%8Astream-api%E7%94%A8%E5%88%B0%E7%9A%84%E5%87%BD%E6%95%B0%E5%BC%8F%E6%8E%A5%E5%8F%A3)
  - [3. Lambda表达式的应用](#3-lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%9A%84%E5%BA%94%E7%94%A8)
    - [完整笔记](#%E5%AE%8C%E6%95%B4%E7%AC%94%E8%AE%B0-2)
    - [摘要](#%E6%91%98%E8%A6%81-2)
      - [(1) 利用Lambda表达式延迟执行的特性减少代码的性能浪费](#1-%E5%88%A9%E7%94%A8lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E5%BB%B6%E8%BF%9F%E6%89%A7%E8%A1%8C%E7%9A%84%E7%89%B9%E6%80%A7%E5%87%8F%E5%B0%91%E4%BB%A3%E7%A0%81%E7%9A%84%E6%80%A7%E8%83%BD%E6%B5%AA%E8%B4%B9)
      - [(2) Lambda表达式作为方法参数时的传参原理](#2-lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E4%BD%9C%E4%B8%BA%E6%96%B9%E6%B3%95%E5%8F%82%E6%95%B0%E6%97%B6%E7%9A%84%E4%BC%A0%E5%8F%82%E5%8E%9F%E7%90%86)
      - [(3) 函数式接口](#3-%E5%87%BD%E6%95%B0%E5%BC%8F%E6%8E%A5%E5%8F%A3)
      - [(4) 把函数（lambda表达式）用作方法的返回值](#4-%E6%8A%8A%E5%87%BD%E6%95%B0lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%94%A8%E4%BD%9C%E6%96%B9%E6%B3%95%E7%9A%84%E8%BF%94%E5%9B%9E%E5%80%BC)
      - [(5) 函数组合：编写把两个函数（lambda表达式）组合在一起的方法，例如下面的`compose`方法](#5-%E5%87%BD%E6%95%B0%E7%BB%84%E5%90%88%E7%BC%96%E5%86%99%E6%8A%8A%E4%B8%A4%E4%B8%AA%E5%87%BD%E6%95%B0lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%BB%84%E5%90%88%E5%9C%A8%E4%B8%80%E8%B5%B7%E7%9A%84%E6%96%B9%E6%B3%95%E4%BE%8B%E5%A6%82%E4%B8%8B%E9%9D%A2%E7%9A%84compose%E6%96%B9%E6%B3%95)
      - [(6) 按需组合一组`函数`，延迟到一个特定时间点一起执行，例如下面的`transform`和`toImage`](#6-%E6%8C%89%E9%9C%80%E7%BB%84%E5%90%88%E4%B8%80%E7%BB%84%E5%87%BD%E6%95%B0%E5%BB%B6%E8%BF%9F%E5%88%B0%E4%B8%80%E4%B8%AA%E7%89%B9%E5%AE%9A%E6%97%B6%E9%97%B4%E7%82%B9%E4%B8%80%E8%B5%B7%E6%89%A7%E8%A1%8C%E4%BE%8B%E5%A6%82%E4%B8%8B%E9%9D%A2%E7%9A%84transform%E5%92%8Ctoimage)
      - [(7) 让传入的函数并行执行，例如下面的代码](#7-%E8%AE%A9%E4%BC%A0%E5%85%A5%E7%9A%84%E5%87%BD%E6%95%B0%E5%B9%B6%E8%A1%8C%E6%89%A7%E8%A1%8C%E4%BE%8B%E5%A6%82%E4%B8%8B%E9%9D%A2%E7%9A%84%E4%BB%A3%E7%A0%81)
      - [(8) Lambda表达式执行时抛出异常的场景](#8-lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%89%A7%E8%A1%8C%E6%97%B6%E6%8A%9B%E5%87%BA%E5%BC%82%E5%B8%B8%E7%9A%84%E5%9C%BA%E6%99%AF)
      - [(9) lambda表达式与泛型](#9-lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E4%B8%8E%E6%B3%9B%E5%9E%8B)
        - [使用`T[]:new`可以解决泛型擦除问题，例如](#%E4%BD%BF%E7%94%A8tnew%E5%8F%AF%E4%BB%A5%E8%A7%A3%E5%86%B3%E6%B3%9B%E5%9E%8B%E6%93%A6%E9%99%A4%E9%97%AE%E9%A2%98%E4%BE%8B%E5%A6%82)
        - [使用泛型通配符时，输出类型的函数式接口使用协变，输入类型的使用逆变；例如](#%E4%BD%BF%E7%94%A8%E6%B3%9B%E5%9E%8B%E9%80%9A%E9%85%8D%E7%AC%A6%E6%97%B6%E8%BE%93%E5%87%BA%E7%B1%BB%E5%9E%8B%E7%9A%84%E5%87%BD%E6%95%B0%E5%BC%8F%E6%8E%A5%E5%8F%A3%E4%BD%BF%E7%94%A8%E5%8D%8F%E5%8F%98%E8%BE%93%E5%85%A5%E7%B1%BB%E5%9E%8B%E7%9A%84%E4%BD%BF%E7%94%A8%E9%80%86%E5%8F%98%E4%BE%8B%E5%A6%82)
        - [不能使用泛型通配符的情况：输入输出类型依赖、逆变协变相互抵消](#%E4%B8%8D%E8%83%BD%E4%BD%BF%E7%94%A8%E6%B3%9B%E5%9E%8B%E9%80%9A%E9%85%8D%E7%AC%A6%E7%9A%84%E6%83%85%E5%86%B5%E8%BE%93%E5%85%A5%E8%BE%93%E5%87%BA%E7%B1%BB%E5%9E%8B%E4%BE%9D%E8%B5%96%E9%80%86%E5%8F%98%E5%8D%8F%E5%8F%98%E7%9B%B8%E4%BA%92%E6%8A%B5%E6%B6%88)
  - [4. Java FX（跳过）](#4-java-fx%E8%B7%B3%E8%BF%87)
  - [5. 新的日期和时间库](#5-%E6%96%B0%E7%9A%84%E6%97%A5%E6%9C%9F%E5%92%8C%E6%97%B6%E9%97%B4%E5%BA%93)
    - [完整笔记](#%E5%AE%8C%E6%95%B4%E7%AC%94%E8%AE%B0-3)
    - [摘要](#%E6%91%98%E8%A6%81-3)
      - [(1) `Instant`/`Duration`，`LocalDate`/`Period`/`TemporalAdjusters`](#1-instantdurationlocaldateperiodtemporaladjusters)
      - [(2) `ZonedDateTime`](#2-zoneddatetime)
      - [(3) `DateTimeFormatter`](#3-datetimeformatter)
      - [(4) 新老类型转换](#4-%E6%96%B0%E8%80%81%E7%B1%BB%E5%9E%8B%E8%BD%AC%E6%8D%A2)
  - [6. 并发库改进](#6-%E5%B9%B6%E5%8F%91%E5%BA%93%E6%94%B9%E8%BF%9B)
    - [完整笔记](#%E5%AE%8C%E6%95%B4%E7%AC%94%E8%AE%B0-4)
    - [摘要](#%E6%91%98%E8%A6%81-4)
      - [(1) 原子变量和乐观锁](#1-%E5%8E%9F%E5%AD%90%E5%8F%98%E9%87%8F%E5%92%8C%E4%B9%90%E8%A7%82%E9%94%81)
        - [Java 5乐观锁的使用：`compareAndSet`](#java-5%E4%B9%90%E8%A7%82%E9%94%81%E7%9A%84%E4%BD%BF%E7%94%A8compareandset)
        - [Java 8对乐观锁的封装：`updateAndGet`/`accumulateAndGet`](#java-8%E5%AF%B9%E4%B9%90%E8%A7%82%E9%94%81%E7%9A%84%E5%B0%81%E8%A3%85updateandgetaccumulateandget)
        - [乐观锁分组封装以减少线程互斥：`LongAdder`/`DoubleAdder`/`LongAccumulator`/`DoubleAccumulator`](#%E4%B9%90%E8%A7%82%E9%94%81%E5%88%86%E7%BB%84%E5%B0%81%E8%A3%85%E4%BB%A5%E5%87%8F%E5%B0%91%E7%BA%BF%E7%A8%8B%E4%BA%92%E6%96%A5longadderdoubleadderlongaccumulatordoubleaccumulator)
        - [底层乐观锁API：`StampedLock`](#%E5%BA%95%E5%B1%82%E4%B9%90%E8%A7%82%E9%94%81apistampedlock)
      - [(2) ConcurrentHashMap改进](#2-concurrenthashmap%E6%94%B9%E8%BF%9B)
        - [设计改进：分组降低互斥；mappingCount()；拉链扫描优化](#%E8%AE%BE%E8%AE%A1%E6%94%B9%E8%BF%9B%E5%88%86%E7%BB%84%E9%99%8D%E4%BD%8E%E4%BA%92%E6%96%A5mappingcount%E6%8B%89%E9%93%BE%E6%89%AB%E6%8F%8F%E4%BC%98%E5%8C%96)
        - [原子性保证：`replace`，`LongAdder`作为value类型，`compute`/`merge`方法](#%E5%8E%9F%E5%AD%90%E6%80%A7%E4%BF%9D%E8%AF%81replacelongadder%E4%BD%9C%E4%B8%BAvalue%E7%B1%BB%E5%9E%8Bcomputemerge%E6%96%B9%E6%B3%95)
  - [7. JS Engine Noshorn（跳过）](#7-js-engine-noshorn%E8%B7%B3%E8%BF%87)
  - [8. 其他改进](#8-%E5%85%B6%E4%BB%96%E6%94%B9%E8%BF%9B)
    - [完整笔记](#%E5%AE%8C%E6%95%B4%E7%AC%94%E8%AE%B0-5)
    - [摘要](#%E6%91%98%E8%A6%81-5)
      - [(1) 类似Python的字符串拼接](#1-%E7%B1%BB%E4%BC%BCpython%E7%9A%84%E5%AD%97%E7%AC%A6%E4%B8%B2%E6%8B%BC%E6%8E%A5)
      - [(2) 包装类（Integer/Long/Double/Float/Short/Byte/Character/Boolean）](#2-%E5%8C%85%E8%A3%85%E7%B1%BBintegerlongdoublefloatshortbytecharacterboolean)
        - [字节数：`BYTES`字段](#%E5%AD%97%E8%8A%82%E6%95%B0bytes%E5%AD%97%E6%AE%B5)
        - [stream聚合(reduce)支持：`sum`/`max`/`min`/`logicalAnd`/`logicalOr`/`logicalXor`](#stream%E8%81%9A%E5%90%88reduce%E6%94%AF%E6%8C%81summaxminlogicalandlogicalorlogicalxor)
        - [无符号运算支持: `xxxUnsigned`/`toUnsignedXXX`/`isFinite`/`xxxValueExact`](#%E6%97%A0%E7%AC%A6%E5%8F%B7%E8%BF%90%E7%AE%97%E6%94%AF%E6%8C%81-xxxunsignedtounsignedxxxisfinitexxxvalueexact)
      - [(3) 新的数学函数](#3-%E6%96%B0%E7%9A%84%E6%95%B0%E5%AD%A6%E5%87%BD%E6%95%B0)
        - [带溢出检查的数学运算：`Math.xxxExact`](#%E5%B8%A6%E6%BA%A2%E5%87%BA%E6%A3%80%E6%9F%A5%E7%9A%84%E6%95%B0%E5%AD%A6%E8%BF%90%E7%AE%97mathxxxexact)
        - [带溢出检查的数值转换：`Math.toXXXExact`](#%E5%B8%A6%E6%BA%A2%E5%87%BA%E6%A3%80%E6%9F%A5%E7%9A%84%E6%95%B0%E5%80%BC%E8%BD%AC%E6%8D%A2mathtoxxxexact)
        - [能够避免结果为负的求模函数：`Math.floorMod`](#%E8%83%BD%E5%A4%9F%E9%81%BF%E5%85%8D%E7%BB%93%E6%9E%9C%E4%B8%BA%E8%B4%9F%E7%9A%84%E6%B1%82%E6%A8%A1%E5%87%BD%E6%95%B0mathfloormod)
        - [比指定值小但是最接近的浮点数：`Math.nextDown`](#%E6%AF%94%E6%8C%87%E5%AE%9A%E5%80%BC%E5%B0%8F%E4%BD%86%E6%98%AF%E6%9C%80%E6%8E%A5%E8%BF%91%E7%9A%84%E6%B5%AE%E7%82%B9%E6%95%B0mathnextdown)
      - [(4) 集合类改进](#4-%E9%9B%86%E5%90%88%E7%B1%BB%E6%94%B9%E8%BF%9B)
        - [新方法](#%E6%96%B0%E6%96%B9%E6%B3%95)
        - [Comparator：字段比较器，多级比较器，定制字段提取比较规则，省去装箱拆箱的比较器，兼容null的比较器，倒序比较器](#comparator%E5%AD%97%E6%AE%B5%E6%AF%94%E8%BE%83%E5%99%A8%E5%A4%9A%E7%BA%A7%E6%AF%94%E8%BE%83%E5%99%A8%E5%AE%9A%E5%88%B6%E5%AD%97%E6%AE%B5%E6%8F%90%E5%8F%96%E6%AF%94%E8%BE%83%E8%A7%84%E5%88%99%E7%9C%81%E5%8E%BB%E8%A3%85%E7%AE%B1%E6%8B%86%E7%AE%B1%E7%9A%84%E6%AF%94%E8%BE%83%E5%99%A8%E5%85%BC%E5%AE%B9null%E7%9A%84%E6%AF%94%E8%BE%83%E5%99%A8%E5%80%92%E5%BA%8F%E6%AF%94%E8%BE%83%E5%99%A8)
      - [(5) 文件](#5-%E6%96%87%E4%BB%B6)
        - [按行读取文件：`Stream<String> lines = Files.lines(Path)`](#%E6%8C%89%E8%A1%8C%E8%AF%BB%E5%8F%96%E6%96%87%E4%BB%B6streamstring-lines--fileslinespath)
        - [按行读取其他数据：`Stream<String> lines = bufferedReader.lines()`](#%E6%8C%89%E8%A1%8C%E8%AF%BB%E5%8F%96%E5%85%B6%E4%BB%96%E6%95%B0%E6%8D%AEstreamstring-lines--bufferedreaderlines)
        - [自动异常类型转换（转为UncheckedIOException）](#%E8%87%AA%E5%8A%A8%E5%BC%82%E5%B8%B8%E7%B1%BB%E5%9E%8B%E8%BD%AC%E6%8D%A2%E8%BD%AC%E4%B8%BAuncheckedioexception)
        - [遍历目录项的stream：`Files.list(Path)`,`Files.walk(Path)`,`Files.find(...)`](#%E9%81%8D%E5%8E%86%E7%9B%AE%E5%BD%95%E9%A1%B9%E7%9A%84streamfileslistpathfileswalkpathfilesfind)
        - [在Stream中自动Base64自动编码解码](#%E5%9C%A8stream%E4%B8%AD%E8%87%AA%E5%8A%A8base64%E8%87%AA%E5%8A%A8%E7%BC%96%E7%A0%81%E8%A7%A3%E7%A0%81)
      - [(6) 注解](#6-%E6%B3%A8%E8%A7%A3)
        - [支持编写能够重复使用的注解](#%E6%94%AF%E6%8C%81%E7%BC%96%E5%86%99%E8%83%BD%E5%A4%9F%E9%87%8D%E5%A4%8D%E4%BD%BF%E7%94%A8%E7%9A%84%E6%B3%A8%E8%A7%A3)
        - [容许在`类型`上也使用注解](#%E5%AE%B9%E8%AE%B8%E5%9C%A8%E7%B1%BB%E5%9E%8B%E4%B8%8A%E4%B9%9F%E4%BD%BF%E7%94%A8%E6%B3%A8%E8%A7%A3)
        - [方法参数反射](#%E6%96%B9%E6%B3%95%E5%8F%82%E6%95%B0%E5%8F%8D%E5%B0%84)
      - [(7) 其他](#7-%E5%85%B6%E4%BB%96)
        - [stream中null值检查：`Object.isNull`, `Object.nonNull`](#stream%E4%B8%ADnull%E5%80%BC%E6%A3%80%E6%9F%A5objectisnull-objectnonnull)
        - [延迟操作](#%E5%BB%B6%E8%BF%9F%E6%93%8D%E4%BD%9C)
        - [正则表达式：(a) 使用命名捕获组来提取字符串 (b) 用正则表达式生成`Stream<String>`](#%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8Fa-%E4%BD%BF%E7%94%A8%E5%91%BD%E5%90%8D%E6%8D%95%E8%8E%B7%E7%BB%84%E6%9D%A5%E6%8F%90%E5%8F%96%E5%AD%97%E7%AC%A6%E4%B8%B2-b-%E7%94%A8%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%94%9F%E6%88%90streamstring)
        - [语言环境支持： `Locale`](#%E8%AF%AD%E8%A8%80%E7%8E%AF%E5%A2%83%E6%94%AF%E6%8C%81-locale)
        - [JDBC支持：增加`LocalDate`/`LocalTime`/`LocalDateTime`，`executeLargeUpdate`，`getObject`/`setObject`](#jdbc%E6%94%AF%E6%8C%81%E5%A2%9E%E5%8A%A0localdatelocaltimelocaldatetimeexecutelargeupdategetobjectsetobject)
- [Java 7 Note](#java-7-note)
    - [完整笔记](#%E5%AE%8C%E6%95%B4%E7%AC%94%E8%AE%B0-6)
    - [摘要](#%E6%91%98%E8%A6%81-6)
      - [(1) 使用try-with-resource自动释放资源](#1-%E4%BD%BF%E7%94%A8try-with-resource%E8%87%AA%E5%8A%A8%E9%87%8A%E6%94%BE%E8%B5%84%E6%BA%90)
      - [(2) 捕获多个异常](#2-%E6%8D%95%E8%8E%B7%E5%A4%9A%E4%B8%AA%E5%BC%82%E5%B8%B8)
      - [(3) 反射方法异常捕捉](#3-%E5%8F%8D%E5%B0%84%E6%96%B9%E6%B3%95%E5%BC%82%E5%B8%B8%E6%8D%95%E6%8D%89)
      - [(4) 用`Paths`接口替代`File`类](#4-%E7%94%A8paths%E6%8E%A5%E5%8F%A3%E6%9B%BF%E4%BB%A3file%E7%B1%BB)
      - [(5) 读取小文件的API：按Byte或按行组织、一次读入整个文件](#5-%E8%AF%BB%E5%8F%96%E5%B0%8F%E6%96%87%E4%BB%B6%E7%9A%84api%E6%8C%89byte%E6%88%96%E6%8C%89%E8%A1%8C%E7%BB%84%E7%BB%87%E4%B8%80%E6%AC%A1%E8%AF%BB%E5%85%A5%E6%95%B4%E4%B8%AA%E6%96%87%E4%BB%B6)
      - [(6) `目录/文件`的创建、检查、删除；对临时文件/目录的支持](#6-%E7%9B%AE%E5%BD%95%E6%96%87%E4%BB%B6%E7%9A%84%E5%88%9B%E5%BB%BA%E6%A3%80%E6%9F%A5%E5%88%A0%E9%99%A4%E5%AF%B9%E4%B8%B4%E6%97%B6%E6%96%87%E4%BB%B6%E7%9B%AE%E5%BD%95%E7%9A%84%E6%94%AF%E6%8C%81)
      - [(7) 文件复制、移动、删除](#7-%E6%96%87%E4%BB%B6%E5%A4%8D%E5%88%B6%E7%A7%BB%E5%8A%A8%E5%88%A0%E9%99%A4)
      - [(8) 简化`equals`、`hashCode`、`CompareTo`方法的编写](#8-%E7%AE%80%E5%8C%96equalshashcodecompareto%E6%96%B9%E6%B3%95%E7%9A%84%E7%BC%96%E5%86%99)
      - [(9) 其他：](#9-%E5%85%B6%E4%BB%96)
        - [修复"+123"转换为数字时的bug](#%E4%BF%AE%E5%A4%8D123%E8%BD%AC%E6%8D%A2%E4%B8%BA%E6%95%B0%E5%AD%97%E6%97%B6%E7%9A%84bug)
        - [全局Logger静态代码段死锁问题修正](#%E5%85%A8%E5%B1%80logger%E9%9D%99%E6%80%81%E4%BB%A3%E7%A0%81%E6%AE%B5%E6%AD%BB%E9%94%81%E9%97%AE%E9%A2%98%E4%BF%AE%E6%AD%A3)
        - [null检查](#null%E6%A3%80%E6%9F%A5)
        - [替代`Runtime.exec`的`ProcessBuilder`](#%E6%9B%BF%E4%BB%A3runtimeexec%E7%9A%84processbuilder)
        - [`URLClassLoader`实现AutoClosable接口](#urlclassloader%E5%AE%9E%E7%8E%B0autoclosable%E6%8E%A5%E5%8F%A3)
        - [BitSet](#bitset)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Java 8 Note

### 1. Lambda表达式的语法

#### 完整笔记

>  [https://github.com/fangkun119/java8note/blob/master/note/ch01_lambda.md](https://github.com/fangkun119/java8note/blob/master/note/ch01_lambda.md)

#### 摘要

##### (1) Lambda表达式的编写，应用例子，以及变量访问权限

> ~~~java
> Comparator<String> cmp = (first, second) -> Integer.compare(first.length(), second.length());
> ~~~

##### (2) 函数式接口

> ~~~java
> @FuntionalInterface
> public interface Comparator<T> {
>     int compare(T o1, T o2);
> }
> ~~~
>
> ~~~java
> Arrays.sort(stringArr, (first, second) -> Integer.compare(first.length(), second.length()));
> ~~~

##### (3) 方法引用、构造器引用、数组构造器引用 

> ~~~java
> // 类名::静态方法
> Arrays.sort(stringArr, SomeClass::someStaticMethod); 
> // 对象::静态方法
> button.setOnAction(System.out::println); 
> // 类名::对象实例方法
> Arrays.sort(strings, String::compareToIgnoreCase); // 相当于(x,y) -> x.compareToIgnoreCase(y)
> // this::对象实例方法
> list.forEach(this::process);  // 等价于 x -> this.process(x)
> // super::对象实例方法
> list.forEach(super::process); // 等价于 x -> super.process(x)
> // Class::new
> Stream<Button> stream = strArr.stream().map(Button::new); // 等价于x -> new Button(x)
> // Class[]::new
> Button[] buttons = stream.toArray(Button[]::new); // 等价于n -> new Button[n], 同时还能解决泛型擦除带来的类型丢失问题
> ~~~

##### (4) 接口默认方法、接口静态方法 

> ~~~java
> // 接口默认方法例子：Iterable.forEach
> // * 为了让容器类等支持函数式表达式而设计
> // * default方法命名冲突处理：(1) 父类-接口冲突时以父类为准（2）接口冲突时必须手动指定
> list.forEach(System.out::println);
> // 接口静态方法例子：Comparator.comparing
> // * 为了用户在编写代码时，减少类似Collections，Arrays这样的工具工具类而设计
> strArray.sort(Comparator.comparing(String::length));
> ~~~

##### (5) `java.util.Comparator`接口提供的静态方法

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/java8fastlearn/java8_interface_static_method.jpg" width="700" /></div>

### 2. Stream

#### 完整笔记

> [https://github.com/fangkun119/java8note/blob/master/note/ch02_stream.md](https://github.com/fangkun119/java8note/blob/master/note/ch02_stream.md)

#### 摘要

##### (1) Stream与Collection的区别，Sream Pipeline的结构

##### (2) 创建Stream的方法

######  `Collection.stream`或使用各种类库提供的方法

> ~~~java
> // 成员方法：Collection.stream()
> Stream<String> fromCollection = Arrays.asList("s1","s2","s3").stream();
> // 成员方法：Patterm.splitAsStream(String contents)
> Stream<String> fromPattern = Pattern.compile("[\\P{L}]+").splitAsStream(contentsString);
> // 静态方法：Files.lines
> Stream<String> fromFiles = Files.lines(filePath, StandardCharsets.UTF_8);
> ~~~

###### 静态方法 `Sream.of` 或 `Arrays.stream`

> ~~~java
> // 静态方法：Stream.of(T[])
> Stream<String> ofStrArr = Stream.of(strArray);
> // 静态方法：Stream.of(T, ...)
> Stream<String> ofStrPar = Stream.of("s1", "s2", "s3");
> // 静态方法：Arrays.stream(T[], int beginIdx, int endIdx) 
> Stream<String> ofArrStm = Arrays.stream(
> 	new String[]{"idx0", "idx1", "idx2", "idx3"}, // 下标从0开始
> 	1, 3 // [beginIdx, endIdx)：前闭后开区间
> );
> ~~~

###### `Stream.empty`，`Stream.generate`，`Stream.Iterator`

##### (3) 中间操作：`filter`、`map`、`flatMap`、`limit`、`skip`、`concat`、`peak`、`distinct`、`sorted`

> ~~~java
> // filter	: 元素挑选
> Stream<String> longWords = strList1.stream().filter(w -> w.length() > 12);
> // map		: 元素转换
> Stream<String> lowercaseWords = strList1.stream().map(String::toLowerCase);
> // flatMap	: 对嵌套stream中内层stream的元素进行展开
> Stream<Character> letters = strList3.stream().flatMap(str -> toCharacterStream(str));
> // limit	: 前k个元素
> Stream<Integer> firstFive = Stream.iterate(0, n -> n + 1).limit(5);
> // skip		: 跳过前k个元素
> Stream<Integer> notTheFirst = Stream.iterate(0, n -> n + 1).skip(1);
> // concat	: 两个stream合并成一个（第一个stream的元素数量必须有限）
> Stream<Character> combined = Stream.concat(stream1, stream2);
> // peak		: 在stream pipline中插入一个peek element的函数便于调试
> Object[] powers = Stream.iterate(1.0, p -> p * 2).peek(e -> System.out.println("featch " + e)).limit(5).toArray();
> // distinct	: 去重
> Stream<String> distinct = strList.stream().distinct();
> // sorted	: 排序、可指定Comparator
> Stream<String> sorted = strList.stream().sorted();
> Stream<String> sorted = strList.stream().sorted(Comparator.comparing(String::length).reversed());
> ~~~

##### (4) 终止操作

###### 简单聚合：`count`、`max`、`anyMatch`、`findAny`、`findFirst`、……

> ~~~java
> // max			: 取最大值
> Optional<String> largest = strList.stream().max(String::compareToIgnoreCase);
> // anyMatch		: 是否有元素满足条件
> boolean hasQStarted = strList.stream().anyMatch(s -> s.startsWith("Q"));
> // findFirst	: 第一个满足条件的元素
> Optional<String> startsWithQ = strList.stream().filter(s -> s.startsWith("Q")).findFirst();
> // findAny		: 某个满足条件的元素
> Optional<String> startsWithQ2 = strList.stream().parallel().filter(s -> s.startsWith("Q")).findAny();
> ~~~

###### 使用`reduce`聚合

> ~~~java
> // 不提供初始值，有可能返回Optional.empty
> Optional<Integer> sum1 = Stream.of(intArray).reduce((x, y) -> x + y);
> // 提供初始值
> Integer sum3 = Stream.of(intArray).reduce(0, (x, y) -> x + y);
> // 对元素的某个属性进行reduce
> int result = strList.stream().reduce(0, (s, w) -> s + w.length(), (s1, s2) -> s1 + s2);
> ~~~

###### 收集结果：`iterator`、`toArray`、`forEach`

> ~~~java
> // iterator				: 收集到Iterator中
> Iterator<Integer> iter = Stream.iterate(0, n -> n + 1).limit(5).iterator();
> // toArray				: 收集到数组中（传入Integer[]::new）是为了解决泛型擦除对数组造成的影响
> Integer[] numbers3 = Stream.iterate(0, n -> n + 1).limit(5).toArray(Integer[]::new);
> // forEach				: 嵌入对元素执行操作的函数
> Stream.iterate(0, n -> n + 1).limit(5).forEach(System.out::println);
> ~~~

###### 收集结果：`collect（supplier, accumulator, comibner)`、用自定义的方式收集到集合中

> ~~~java
> // collect(supplier, accumulator, combiner) : 收集到集合中，自定义收集方式
> // * supplier			: 创建容器的函数
> // * accumulater		: 元素加入容器的函数
> // * combiner			: 两个容器合并的函数
> Set<String> elemSet = strStream.collect(HashSet::new, HashSet::add, HashSet::addAll);
> ~~~

###### `collect(Collector)`、使用类库或第三方提供的收集器

> ~~~java
> // Collectors.toSet					: 收集到Set中
> Set<String> elemSet = strStream.collect(Collectors.toSet());
> // Collectors.toCollection(XXX:new)	: 收集到指定的集合中
> TreeSet<String> elemTreeSet = strStream.collect(Collectors.toCollection(TreeSet::new));
> // Collectors.joining				: 字符串拼接，可指定分隔符
> String result1 = strStream.limit(5).collect(Collectors.joining());
> String result2 = strStream.limit(5).collect(Collectors.joining(", "));
> // Collectors.summarizingInt		: 计算统计数据
> IntSummaryStatistics summary = strStream.collect(Collectors.summarizingInt(String::length));
> // Collectors.toMap					: 收集到Map中
> // * 使用Function.identity()可以指定元素本身
> // * 没有设置相同key处理函数时会在遇到相同key时抛出IllegalStateException
> Map<Integer, String> idToName   = personStream().collect(Collectors.toMap(Person::getId, Person::getName));
> Map<Integer, Person> idToPerson = personStream().collect(Collectors.toMap(Person::getId, Function.identity()));
> // * 自定义收集方式：略
> ~~~

分组分片收集：`groupingBy`、`partitioningBy`

##### (5) 并行流：`parallelStream()`/`parallel()`

> 见完整笔记 

##### (6) 原始类型流`IntStream`/`LongStream`/`DoubleStream`

###### 创建

> ~~~java
> IntStream is1 = IntStream.of(1,2,3,4,5);
> IntStream is2 = Arrays.stream(new int[] {1,2,3,4,5}, 1, 3);
> IntStream is3 = IntStream.generate(() -> (int)(Math.random() * 100));
> IntStream is4 = IntStream.range(5, 9);
> IntStream is5 = IntStream.rangeClosed(5, 9);
> IntStream is6 = new Random().ints();
> IntStream is7 = strList.mapToInt(String::length);
> IntStream is8 = integerStream.mapToInt(Integer::intValue); //从Stream<Integer>创建
> ...
> ~~~

###### 装箱，和解除装箱

> ~~~java
> Stream<Integer> integerStream = IntStream.range(0, 100).boxed();
> IntStream is8 = integerStream.mapToInt(Integer::intValue);
> ~~~

###### 使用以及与普通Stream的区别

> 见完整笔记

##### (7) `Optional<T>`的使用，Stream API用到的函数式接口

> ~~~java
> // ofNullable	: 为null时返回Optional.empty()，不为null时返回Optional.of(objT)
> Optional<T> optionalT = Optional.ofNullable(objT);
> // ifPresent	: 值存在时调用指定的函数
> optionalValue.ifPresent(myHashSet::add); 
> // map			: 在返回的Optionsl<ReturnType>中存储函数的返回值
> Optional<Boolean> added = optionalValue.map(myHashSet::add);
> // orElse		: stream为空时的用默认值替代
> String result1 = Optional.<String>empty().orElse("N/A");
> String result2 = Optional.<String>empty().orElseGet(() -> System.getProperty("user.dir"));
> // orElseThrow	: stream为空时抛出异常
> String result  = Optional.<String>empty().orElseThrow(NoSuchElementException::new);
> // flatMap		: 把返回stream的方法展开、其实可以串行调用（略）
> ~~~

##### (8) Comparator提供的静态方法、以及Stream API用到的函数式接口

> 见完整笔记 

### 3. Lambda表达式的应用

#### 完整笔记

>[https://github.com/fangkun119/java8note/blob/master/note/ch05_date_time.md](https://github.com/fangkun119/java8note/blob/master/note/ch05_date_time.md)

#### 摘要

##### (1) 利用Lambda表达式延迟执行的特性减少代码的性能浪费

> 见完整笔记 

##### (2) Lambda表达式作为方法参数时的传参原理

> 见完整笔记 

##### (3) 函数式接口
> 1. 编写自定义函数式接口并使用
>
> 2. 使用自定义函数式接口、结合接口的default方法来编写组合方法，提供类似如下的调用方式
>
>     ~~~java
>     (Predicate.isEqual(a)).or(Predicate.isEqual(b))
>     ~~~
>
> 3. Java 8为普通类型（对象）、原始类型（int、long、double等）提供的函数式接口

##### (4) 把函数（lambda表达式）用作方法的返回值

> 见完整笔记 

##### (5) 函数组合：编写把两个函数（lambda表达式）组合在一起的方法，例如下面的`compose`方法

> ~~~java
> Image image3 = transform(image, compose(Color::brighter, Color::grayscale));
> ~~~

##### (6) 按需组合一组`函数`，延迟到一个特定时间点一起执行，例如下面的`transform`和`toImage`

> ~~~java
> Image finalImage = LatentImage.from(image)
> 	.transform(Color::brighter)  // 加入函数
> 	.transform(Color::grayscale) // 加入函数
> 	.toImage();                  // 一起执行这些函数
> ~~~

##### (7) 让传入的函数并行执行，例如下面的代码

> ~~~java
> // 并行执行多个Collor::grayscaleb方法处理像素矩阵
> pixels = parallelTransform(pixels, Color::grayscale);
> ~~~

##### (8) Lambda表达式执行时抛出异常的场景

> 1. 同步执行场景（`func(lambda1, lambda2)`）：前面的lambda表达式抛异常则后面的不会执行
>
> 2. 主线程在try块中启动一个工作线程执行lambda表达式，则主线程的catch块无法捕捉工作线程抛出的异常。解决办法是：异常捕捉也放在工作线程中，主线程用lambda表达式定义所有的操作（包括异常捕捉操作），一起传递给工作线程
>
> 3. 很多函数式接口（例如JDK的`Supplier<T>`）没有异常抛出声明，而用于实现这些接口的lambda表达式会抛出异常，导致无法通过编译。解决办法：(A) 修改API、换一个函数式接口；(B) 用un-checked exception包裹checked-exception再抛出，un-checked exception对方法有异常抛出声明

##### (9) lambda表达式与泛型

###### 使用`T[]:new`可以解决泛型擦除问题，例如

> ~~~java
> String[] strArray = strList.toArray(new String[0]); // 老式代码
> String[] strArray = strList.toArray(String[]::new); // 新式代码
> ~~~

###### 使用泛型通配符时，输出类型的函数式接口使用协变，输入类型的使用逆变；例如

> ~~~java
> public static void makeJuice(
> 	// Supplier输出的形参是Fruit，但实际对象可能是Apple只是被泛型擦除为Fruit
> 	Supplier<? extends Fruit> provider,
> 	// makeJuice会给juicering传递类型为Fruit的参数，因此juicering的受理范围必须大于等于Fruit，即Fruit或它的基类
> 	Consumer<? super Fruit> juicering, 
> 	// makeJuice会给handler传递类型为Throwable的参数，因此handler的受理范围必须大于等于Throwble，即Throwble或它的基类
> 	Consumer<? super Throwable> handler 
> ) {
>     ...
> }
> ~~~

###### 不能使用泛型通配符的情况：输入输出类型依赖、逆变协变相互抵消

> ~~~java
> T reduce(T identity, BinaryOperator<T> accumulator)
> ~~~

### 4. Java FX（跳过）

### 5. 新的日期和时间库

#### 完整笔记

> [https://github.com/fangkun119/java8note/blob/master/note/ch05_date_time.md](https://github.com/fangkun119/java8note/blob/master/note/ch05_date_time.md)

#### 摘要

##### (1) `Instant`/`Duration`，`LocalDate`/`Period`/`TemporalAdjusters`

> 1. `Instant`表示时间线上的某一点，`Duration`表示两个Instance之间的距离，都是immutable对象
>
> 2. `LocalDate`表示本地日期，`Period`表示两个本地日期之间的间隔，它们可以自动考虑夏令时冬令时的切换
>
> 3. `LocalDate`叠加时间矫正函数`TemporalAdjusters`可以以简明的方式表示一段计算逻辑
>
>      ~~~java
>     // 基准时间：6月1日
>     // 矫正规则：某一天为起点，第1个星期2
>     // 计算结果：6月第1个星期2，另外with方法会返回新的LocalDate对象，不会改变已有的
>     LocalDate firstTuesday = LocalDate
>     						.of(2014, 6, 1)
>     						.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
>     ~~~
>
>     `TemporalAdjusters`提供了很多接口静态方法，来封装各种计算逻辑；也可以自己实现这个函数式接口实现自定义的计算逻辑

##### (2) `ZonedDateTime`

> `LocalTime`表示一天中的某个时间，`LocalDateTime`表示某个确定时区的某个时间点，`ZonedDateTime`则能够处理跨夏令时冬令时的计算
>
> ~~~java
> // 夏令时开始时会跳过一小时
> // 注意 2013-03-31 02:00 Europe/Berlin 进入夏令时
> // 因此 2013-03-31 02:30 Europe/Berlin 是一个不存在的时间点，用它来创建ZoneDateTime，系统会自动纠正
> // 输出 2013-03-31 03:30 Europe/Berlin
> ZonedDateTime skipped = ZonedDateTime.of(
>      LocalDate.of(2013, 3, 31),
>      LocalTime.of(2, 30),
>      ZoneId.of("Europe/Berlin"));
> System.out.println("skipped: " + skipped);
> // ZonedDateTime.of(LocalDate, LocalTime, ZoneId.of(...))
> // skipped: 2013-03-31T03:30+02:00[Europe/Berlin]
> ~~~

##### (3) `DateTimeFormatter`

> `DateTimeFormatter`提供机器可读的标准格式、人类阅读友好的语言环境模式、预定义模式、自定义模式；可以用于日期输出；也可以用于日期字符串解析

##### (4) 新老类型转换

> 1. `Instant`提供了与老式`java.util.Date`相互转换的APi
> 2. `ZonedDateTime`提供了与老式`GregorianCalendar`想换转换的API
> 3. `Instant`/`LocalDateTime`可以与`java.sql.TimeStamp`相互转换
> 4. `LocalDate`、`LocalTime`可以与`java.sql.Date`、`java.sql.Time`相互转换
> 5. 可以用`DateTimeFormatter`生成老式的`java.text.Format`以兼容历史遗留API

### 6. 并发库改进

#### 完整笔记

> [https://github.com/fangkun119/java8note/blob/master/note/ch06_concurrent.md](https://github.com/fangkun119/java8note/blob/master/note/ch06_concurrent.md)

#### 摘要

##### (1) 原子变量和乐观锁

###### Java 5乐观锁的使用：`compareAndSet`

> ~~~java
> public static AtomicLong largest = new AtomicLong();
> long oldValue, newValue;
> do {
> 	oldValue = largest.get();
> 	newValue = Math.max(oldValue, observed);
> 	// 如果执行CAS时largest的值被另一个线程改变导致结果不准确，CAS会返回false并在下一轮循环中重试
> } while (!largest.compareAndSet(oldValue, newValue));
> ~~~

######  Java 8对乐观锁的封装：`updateAndGet`/`accumulateAndGet`

> 封装了上面的while循环，只需提供用于更新原子变量的lambda表达式
>
> ~~~java
> // 原子类包括：
> // AtomicInteger、AtomicIntegerArray、AtomicIntegerFieldUpdater
> // AtomicLong、AtomicLongArray、AtomicLongFieldUpdater
> // AtomicReference、AtomicReferenceArray、AtomicReferenceFieldUpdater
> public static AtomicLong largest = new AtomicLong();
> largest.updateAndGet(x -> Math.max(x, observed)); 	// 方法将原子变量更新为当前最大值，并返回前一个值
> largest.accumulateAndGet(observed, Math::max);		// 方法将原子变量更新为当前最大值，并返回前一个值
> ~~~

###### 乐观锁分组封装以减少线程互斥：`LongAdder`/`DoubleAdder`/`LongAccumulator`/`DoubleAccumulator`

> * 累加器`LongAdder` / `DoubleAdder`在内部封装了多个原子变量，多个线程调用`increement`累加时会作用在不同的原子变量上，调用`sum`获取最终结果时再把这些原子变量值加总
> * 更新器`LongAccumulator`/`DoubleAccumulator`原理类似但更加灵活，它不是固定地执行累加操作，而是可以在构造时传入lambda表达式以指定要执行的操作

###### 底层乐观锁API：`StampedLock`

> * 乐观读：`long stamp = lock.tryOptimisticRead()`/`boolean valid = validate(stamp)`
> * 读悲观锁：`long stamp = lock.readLock()`/`lock unlockRead(stamp)`
> * 写悲观锁：`long stamp = lock.writeLock()`/`lock unlockWrite(stamp)`

##### (2) ConcurrentHashMap改进

###### 设计改进：分组降低互斥；mappingCount()；拉链扫描优化

> 1. 容许多个线程并发更新hash表不同部分且不会相互阻塞
> 2. 提供`mappingCount()`方法解决`size()`方法整形数溢出的问题
> 3. 块状拉链设计、使得hash冲突时扫描拉链表的时间复杂度降低为`log(n)`

###### 原子性保证：`replace`，`LongAdder`作为value类型，`compute`/`merge`方法

> `get(key)`，`put(key, val)`调用序列不保证原子性，保证原子性有如下方法
>
> 1. `replace`方法基于乐观锁（用法类似java 5原子变量的compareAndSet），可以保证原子性
>
> 2. 如果将Value为`LongAdder`，可以利用LongAdder的API来实现原子性
>
>     ~~~java
>     ConcurrentHashMap<String, LongAdder> map2 = new ConcurrentHashMap<>();
>     map2.putIfAbsent(word, new LongAdder()).increment();
>     ~~~
>
> 3. 传入lambda表达式给`compute`或`merge`方法，这些方法可以保证lambda表达式被原子执行
>
>     ~~~java
>     // 使用compute、computeIfAbsent, computeIfPresent方法保证lambda表达式被原子执行
>     concurrentHashMap.compute(word, (k, v) -> v == null ? 1 : v + 1);
>     map2.computeIfAbsent(word, k -> new LongAdder()).increment();
>     map2.computeIfPresent(word, (k,v) -> v).increment();
>     // 使用merge，与compute类似，但是lambda表达式只需要提供更新操作，而value初始值可以通过另一个参数传入
>     concurrentHashMap.merge(
>     	word,  // key
>     	1L,    // value缺失时的hash初始值
>     	(existingValue, newValue2) -> existingValue + newValue2 // value更新操作
>     );
>     concurrentHashMap.merge(word, 1L, Long::sum);
>     ~~~

### 7. JS Engine Noshorn（跳过）

> 在Java 11中已经被废弃，Java 11使用GraalVM的JavaScript引擎替代Nashorn

### 8. 其他改进

#### 完整笔记

> [https://github.com/fangkun119/java8note/blob/master/note/ch08_other_imporvements.md](https://github.com/fangkun119/java8note/blob/master/note/ch08_other_imporvements.md)

#### 摘要

##### (1) 类似Python的字符串拼接

> ~~~java
> String path = String.join("/", "usr", "local", "bin"); // "usr/local/bin"
> String ids  = String.join(", ", ZoneId.getAvailableZoneIds());
> ~~~

##### (2) 包装类（Integer/Long/Double/Float/Short/Byte/Character/Boolean）

###### 字节数：`BYTES`字段

>  用来表示该类型占用的字节数（不包括Boolean）

###### stream聚合(reduce)支持：`sum`/`max`/`min`/`logicalAnd`/`logicalOr`/`logicalXor`

> ~~~java
> OptionalInt sum = (new Random()).ints().limit(100).reduce(Integer::sum);
> OptionalInt sum = (new Random()).ints().limit(100).reduce(Integer::min);
> Optional<Boolean> xor = (new Random()).ints().limit(100).mapToObj(i -> i % 2 == 0).reduce(Boolean::logicalXor);
> ~~~

###### 无符号运算支持: `xxxUnsigned`/`toUnsignedXXX`/`isFinite`/`xxxValueExact`

> ~~~java
> // `Integer.MAX_VALUE + 1`是负数，但下面的方法可以把他们当成无符号数来计算
> int maxCmpNext          = Integer.compareUnsigned(Integer.MAX_VALUE, Integer.MAX_VALUE + 1); // -1
> int nextCmpMax          = Integer.compareUnsigned(Integer.MAX_VALUE + 1, Integer.MAX_VALUE); // 1
> int nextDivide65536     = Integer.divideUnsigned(Integer.MAX_VALUE + 1, 65536);     // 32768
> int nextRemainder65536  = Integer.remainderUnsigned(Integer.MAX_VALUE + 1,  65536); // 0
> 
> // toUnsignedInt, toUnsignedLong
> byte b = -127; //byte是8位
> int bi = Byte.toUnsignedInt(b); //  129
> long l = Integer.toUnsignedLong(Integer.MAX_VALUE + 1);  //2147483648
> 
> // Float, Double新增了静态方法isFinite：表示不是无穷大、无穷小、也不是Nan
> System.out.println(Double.isFinite(1.0 / 0.0));    		// false
> System.out.println(Double.isFinite(Math.sqrt(-1.0))); 	// false
> 
> // 从BigInteger中提取原始类型，超出范围时抛ArithmeticException异常
> int intExact = new BigInteger("129").intValueExact();       // 129
> long longExact = new BigInteger("129").longValueExact();    // 129
> short shortExact = new BigInteger("129").shortValueExact(); // 129
> byte byteExact = new BigInteger("129").byteValueExact();
> // Exception in thread "main" java.lang.ArithmeticException: BigInteger out of byte range
> // at java.base/java.math.BigInteger.byteValueExact(BigInteger.java:4846)
> // at Numbers.main(Numbers.java:70)
> ~~~

##### (3) 新的数学函数

###### 带溢出检查的数学运算：`Math.xxxExact`

> * 发生溢出时会抛出ArithmeticException，而不是返回一个错误的结果
>
> * 方法包括：`Math.addExact`, `Math.substractExact`, `Math.multiplyExact`, `Math.incrementExact`, `Math.decrementExact`, `Math.negateExact`

###### 带溢出检查的数值转换：`Math.toXXXExact`

> 发生溢出时（例如`Math.toIntExact(largerThanIntMax)`）会抛出ArithmeticException

###### 能够避免结果为负的求模函数：`Math.floorMod`

> 模（余数）都应当永远大于0。历史原因，有些机器上求模会返回一个负数，使用这个方法不需要手动修正

###### 比指定值小但是最接近的浮点数：`Math.nextDown`

##### (4) 集合类改进

###### 新方法

> | 集合类     | 新增方法                                                     |
> | ---------- | ------------------------------------------------------------ |
> | Iterable   | forEach                                                      |
> | Collection | removeIf                                                     |
> | List       | replaceAll, sort                                             |
> | Map        | forEach,  replace,  replaceAll,  remove(key,value) (仅在<key,value>存在时才删除),  putIfAbsent,  compute,  computeIf |
> | Iterator   | forEachRemaining (将迭代器剩余元素都传递给一个函数)          |
> | BitSet     | stream (生成集合中的所有元素，返回一个由int组成的stream）    |

###### Comparator：字段比较器，多级比较器，定制字段提取比较规则，省去装箱拆箱的比较器，兼容null的比较器，倒序比较器

> ~~~java
> // 字段比较器
> Arrays.sort(personArray, Comparator.comparing(Person::getName));
> // 多级比较器
> Arrays.sort(personArray, Comparator.comparing(Person::getLastName).thenComparing(Person::getFirstName));
> // 定制用于比较字段的`提取规则`和`比较规则`
> Arrays.sort(personArray, Comparator.comparing(Person::getName, (s, t) -> Integer.compare(s.length(), t.length())));
> // 省去装箱拆箱的比较器
> Arrays.sort(personArray, Comparator.comparingInt(p -> p.getName().length()));
> // 倒序比较器
> // Comparator<T>.natrualOrder().reversed()等价于Comparator<T>.reverseOrder()
> Arrays.sort(personArray, Comparator.comparing(
> 							Person::getMiddleName,
> 							nullsFirst(Comparator.<String>naturalOrder().reversed())));
> ~~~

##### (5) 文件

###### 按行读取文件：`Stream<String> lines = Files.lines(Path)`

###### 按行读取其他数据：`Stream<String> lines = bufferedReader.lines()`

###### 自动异常类型转换（转为UncheckedIOException）

> 上述操作会将IOException转为UncheckedIOException，以解决用于传入lambda表达式的函数式接口没有异常抛出声明的问题

###### 遍历目录项的stream：`Files.list(Path)`,`Files.walk(Path)`,`Files.find(...)`

###### 在Stream中自动Base64自动编码解码

##### (6) 注解

###### 支持编写能够重复使用的注解

> ~~~java
> @Entity
> @PrimaryKeyJoinColumn(name = "ID"),
> @PrimaryKeyJoinColumn(name = "REGION")
> public class Item { ... }
> ~~~

###### 容许在`类型`上也使用注解

> ~~~java
> List<@NotNull String>
> Comparator.<@NotNullString> reverseOrder()
> String[] @NotNull [] words
> ...
> ~~~

###### 方法参数反射

> ~~~java
> //java 8之前必须手动指定名称
> Person getEmployee(@PathParam("dept") Long dept, @QueryParam("id") Long id);
> //java 8开始可以借助参数反射得到参数的名称，可以编写注解进行如下简化
> Person getEmployee(@PathParam Long dept, @QueryParam Long id)
> ~~~

##### (7) 其他

###### stream中null值检查：`Object.isNull`, `Object.nonNull`

> ~~~java
> stream.anyMatch(Object::isNull);	// 检查是否有null值
> stream.filter(Object::nonNull);		// 获取所有非null对象
> ~~~

###### 延迟操作

> ~~~java
> // 日志等级容许输出时，才会执行lambda表达式
> logger.finest(() -> "x: " + x + ", y:" + y); 
> // inputParameter为null需要报错时，才执行lambda表达式
> this.directions = Objects.requireNotNull(
>     inputParameter,
> 	() -> "inputParameter for " + this.goal + "must not be null"
> )
> ~~~

###### 正则表达式：(a) 使用命名捕获组来提取字符串 (b) 用正则表达式生成`Stream<String>`

> 见完整笔记 

###### 语言环境支持： `Locale`

> 见完整笔记 

###### JDBC支持：增加`LocalDate`/`LocalTime`/`LocalDateTime`，`executeLargeUpdate`，`getObject`/`setObject`

> 见完整笔记 

## Java 7 Note

#### 完整笔记

> [https://github.com/fangkun119/java8note/blob/master/note/ch09_java7_features.md](https://github.com/fangkun119/java8note/blob/master/note/ch09_java7_features.md)

#### 摘要

##### (1) 使用try-with-resource自动释放资源

> ```java
> // Scanner类实现了AutoClosable接口
> // 当抛出异常，或者try块内的代码运行完毕后，都会调用scanner.close()方法来释放资源
> try (Scanner scanner = new Scanner(Paths.get("/usr/share/dict/words"))) {
> 	int count = 0;
> 	while (scanner.hasNext() && ++count < 4) {
> 		System.out.println(LOG_PREFIX + scanner.next().toLowerCase());
> 	}
> } ...
> ```
> 当业务逻辑异常A导致资源被自动释放，但释放过程中又抛出异常B时，`java 7`会抛出异常A（同时把B包裹在A的`getSupressed()`中）

##### (2) 捕获多个异常

> ~~~java
> try {
> 	...
> } catch (FileNotFoundException | UnknownHostException ex) {
> 	...
> }
> ~~~

##### (3) 反射方法异常捕捉

> 通过捕捉父异常`catch (ReflectiveOperationException e)`来捕捉多种的反射异常

##### (4) 用`Paths`接口替代`File`类

> `Path.get(String)`
>
> `Path.resolve(Path)`，`Path.resolveSibline(Path)`，`Path.relative(Path)`，`Path.normalize()`
>
> `Path.getParent()`，`Path.getFileName()`，`Path.getRoot()`

##### (5) 读取小文件的API：按Byte或按行组织、一次读入整个文件

> `Files.readAllBytes(Path)`，`Files.readAllLines(Path)`
>
> 对于大文件，仍然使用InputStream/OutputStream/Reader/Writer

##### (6) `目录/文件`的创建、检查、删除；对临时文件/目录的支持

> `Files.createDirectory(Path)`，`Files.createFile(Path)`，`Files.exists(Path)`，`Files.deleteIfExists(Path)`，`Files.createTempFile(...)`，`Files.createTempDirectory(...)`

##### (7) 文件复制、移动、删除

> `Files.copy(...)`，`Files.move(...)`，`Files.delete(...)`，`Files.deleteIfExists(...)`

##### (8) 简化`equals`、`hashCode`、`CompareTo`方法的编写

> 见完整笔记

##### (9) 其他：

###### 修复"+123"转换为数字时的bug

> ~~~java
> double   x1 = Double.parseDouble("+1.0");
> ~~~

###### 全局Logger静态代码段死锁问题修正

> ~~~java
> Logger.getGlobal().info("x1=" + 1.0);
> ~~~

###### null检查

> ~~~java
> this.directions = Objects.requireNonNull(directions, "directions must not be null");
> ~~~

###### 替代`Runtime.exec`的`ProcessBuilder`

> 见完整笔记

###### `URLClassLoader`实现AutoClosable接口

> 可用在try-with-resource语句中

###### BitSet

> 见完整笔记





