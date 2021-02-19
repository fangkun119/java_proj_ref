# Java 8 Note

## 1. Lambda表达式

笔记链接：[https://github.com/fangkun119/java8note/blob/master/note/ch01_lambda.md](https://github.com/fangkun119/java8note/blob/master/note/ch01_lambda.md)

内容：

> 1. Lambda表达式的编写，以及变量访问权限
> 2. 函数式接口
> 3. 方法引用、构造器引用、数组构造器引用 
> 4. 接口默认方法、接口静态方法 

## 2. Stream

笔记链接：[https://github.com/fangkun119/java8note/blob/master/note/ch02_stream.md](https://github.com/fangkun119/java8note/blob/master/note/ch02_stream.md)

内容：

> 1. Stream与Collection的差别；Sream Pipeline的结构，`创建`、`中间操作管道`、`终止操作`
>
> 2. 创建Stream的方法
>
>     (1) 从`Collection`转化、或者各种类自带的方法生成Stream
>
>     (2) `Sream.of` 或 `Arrays.stream`
>
>     (3) `Stream.empty`，`Stream.generate`，`Stream.Iterator`
>
> 3. 中间操作：`filter`、`map`、`flatMap`、`limit`、`skip`、`concat`、`peak`、`distinct`、`sorted`
>
> 4. 终止操作
>
>     (1) 简单聚合：`count`、`max`、`anyMatch`、`findAny`、`findFirst`、……
>
>     (2) 使用`reduce`聚合
>
>     (3) 收集结果：`iterator`、`toArray`、`forEach`、`collect`、`Collectors.toMap`
>
>     (4) 分组分片收集 ：
>
>     `Collectors.groupingBy(...).toXXX()/counting()/...`
>
>     `Collectors.partioningByBy(...).toXXX()/counting()/...`
>
> 5. 并行流`parallelStream()`/`parallel()`，原始类型流`IntStream`/`LongStream`/`DoubleStream`
>
> 6. `Optional<T>`的使用，Stream API用到的函数式接口

## 3. Lambda使用

## 4. Java FX

## 5. 日期和时间

## 6. 并发库

## 7. JS Engine Noshorn

## 8. 其他改进



# Java 7 Note

