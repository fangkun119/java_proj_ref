[TOC]

# Java 8 Note

## 1. Lambda表达式的语法

完整笔记链接：[https://github.com/fangkun119/java8note/blob/master/note/ch01_lambda.md](https://github.com/fangkun119/java8note/blob/master/note/ch01_lambda.md)

内容摘要：

> 1. Lambda表达式的编写，应用例子，以及变量访问权限
>
>     ~~~java
>     Comparator<String> cmp = (first, second) -> Integer.compare(first.length(), second.length());
>     ~~~
>
> 2. 函数式接口
>
>     ~~~java
>     @FuntionalInterface
>     public interface Comparator<T> {
>         int compare(T o1, T o2);
>     }
>     ~~~
>
>     ~~~java
>     Arrays.sort(stringArr, (first, second) -> Integer.compare(first.length(), second.length()));
>     ~~~
>
> 3. 方法引用、构造器引用、数组构造器引用 
>
>     ~~~java
>     // 类名::静态方法
>     Arrays.sort(stringArr, SomeClass::someStaticMethod); 
>     // 对象::静态方法
>     button.setOnAction(System.out::println); 
>     // 类名::对象实例方法
>     Arrays.sort(strings, String::compareToIgnoreCase); // 相当于(x,y) -> x.compareToIgnoreCase(y)
>     // this::对象实例方法
>     list.forEach(this::process);  // 等价于 x -> this.process(x)
>     // super::对象实例方法
>     list.forEach(super::process); // 等价于 x -> super.process(x)
>     // Class::new
>     Stream<Button> stream = strArr.stream().map(Button::new); // 等价于x -> new Button(x)
>     // Class[]::new
>     Button[] buttons = stream.toArray(Button[]::new); // 等价于n -> new Button[n], 同时还能解决泛型擦除带来的类型丢失问题
>     ~~~
>
> 4. 接口默认方法、接口静态方法 
>
>     ~~~java
>     // 接口默认方法：使用了Iterable.forEach方法
>     // * 为了让容器类等支持函数式表达式而设计
>     // * default方法命名冲突处理：(1) 父类-接口冲突时以父类为准（2）接口冲突时必须手动指定
>     list.forEach(System.out::println);
>     // 接口静态方法：使用了Comparator.comparing方法
>     // * 为了用户在编写代码时，减少类似Collections，Arrays这样的工具工具类而设计
>     strArray.sort(Comparator.comparing(String::length));
>     ~~~
>
>     `java.util.Comparator`接口提供的静态方法
>
>     <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/java8fastlearn/java8_interface_static_method.jpg" width="700" /></div>

## 2. Stream

完整笔记链接：[https://github.com/fangkun119/java8note/blob/master/note/ch02_stream.md](https://github.com/fangkun119/java8note/blob/master/note/ch02_stream.md)

内容摘要：

> 1. Stream与Collection的区别；Sream Pipeline的结构，`创建`、`中间操作管道`、`终止操作`
>
> 2. 创建Stream的方法
>
>     (1) `Collection.stream`或使用各种类自带的方法
>
>     ~~~java
>     // 成员方法：Collection.stream()
>     Stream<String> fromCollection = Arrays.asList("s1","s2","s3").stream();
>     // 成员方法：Patterm.splitAsStream(String contents)
>     Stream<String> fromPattern = Pattern.compile("[\\P{L}]+").splitAsStream(contentsString);
>     // 静态方法：Files.lines
>     Stream<String> fromFiles = Files.lines(filePath, StandardCharsets.UTF_8);
>     ~~~
>
>     (2) 静态方法 `Sream.of` 或 `Arrays.stream`
>
>     ~~~java
>     // 静态方法：Stream.of(T[])
>     Stream<String> ofStrArr = Stream.of(strArray);
>     // 静态方法：Stream.of(T, ...)
>     Stream<String> ofStrPar = Stream.of("s1", "s2", "s3");
>     // 静态方法：Arrays.stream(T[], int beginIdx, int endIdx) 
>     Stream<String> ofArrStm = Arrays.stream(
>     	new String[]{"idx0", "idx1", "idx2", "idx3"}, // 下标从0开始
>     	1, 3 // [beginIdx, endIdx)：前闭后开区间
>     );
>     ~~~
>
>     (3) `Stream.empty`，`Stream.generate`，`Stream.Iterator`
>
> 3. 中间操作：`filter`、`map`、`flatMap`、`limit`、`skip`、`concat`、`peak`、`distinct`、`sorted`
>
>     ~~~java
>     // filter	: 元素挑选
>     Stream<String> longWords = strList1.stream().filter(w -> w.length() > 12);
>     // map		: 元素转换
>     Stream<String> lowercaseWords = strList1.stream().map(String::toLowerCase);
>     // flatMap	: 对嵌套stream中内层stream的元素进行展开
>     Stream<Character> letters = strList3.stream().flatMap(str -> toCharacterStream(str));
>     // limit	: 前k个元素
>     Stream<Integer> firstFive = Stream.iterate(0, n -> n + 1).limit(5);
>     // skip		: 跳过前k个元素
>     Stream<Integer> notTheFirst = Stream.iterate(0, n -> n + 1).skip(1);
>     // concat	: 两个stream合并成一个（第一个stream的元素数量必须有限）
>     Stream<Character> combined = Stream.concat(stream1, stream2);
>     // peak		: 在stream pipline中插入一个peek element的函数便于调试
>     Object[] powers = Stream.iterate(1.0, p -> p * 2)
>     	.peek(e -> System.out.println("\t// Fetching " + e))
>     	.limit(5).toArray();
>     // distinct	: 去重
>     Stream<String> distinct = strList.stream().distinct();
>     // sorted	: 排序、可指定Comparator
>     Stream<String> sorted = strList.stream().sorted();
>     Stream<String> sorted = strList.stream().sorted(Comparator.comparing(String::length).reversed());
>     ~~~
>
> 4. 终止操作
>
>     (1) 简单聚合：`count`、`max`、`anyMatch`、`findAny`、`findFirst`、……
>
>     ~~~java
>     
>     ~~~
>
>     
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

## 3. Lambda表达式的应用

笔记链接：

内容：

> 1. Lambda表达式延迟执行的特性，以及使用该特性减少代码的性能浪费
>
> 2. Lambda表达式作为方法参数时的传参原理
>
> 3. 函数式接口
>     (1) 编写自定义函数式接口并使用
>     (2) 使用自定义函数式接口、结合接口的default方法来编写组合方法，提供类似如下的调用方式
>
>     ~~~java
>     Predicate.isEqual(a).or(Predicate.isEqual(b))
>     ~~~
>
>     (3) Java 8为普通类型（对象）、原始类型（int、long、double等）提供的函数式接口
>
> 4. 把函数（lambda表达式）用作方法的返回值
>
> 5. 函数组合：编写把两个函数（lambda表达式）组合在一起的方法，例如下面的`compose`方法
>
>     ~~~java
>     Image image3 = transform(image, compose(Color::brighter, Color::grayscale));
>     ~~~
>
> 6. 按需组合一组`函数`，延迟到一个特定时间点一起执行，例如下面的`transform`和`toImage`
>
>     ~~~java
>     Image finalImage = LatentImage.from(image)
>     	.transform(Color::brighter)  // 加入函数
>     	.transform(Color::grayscale) // 加入函数
>     	.toImage();                  // 一起执行这些函数
>     ~~~
>
> 7. 让传入的函数并行执行，例如下面的代码
>
>     ~~~java
>     // 并行执行多个Collor::grayscaleb方法处理像素矩阵
>     pixels = parallelTransform(pixels, Color::grayscale);
>     ~~~
>
> 8. lambda表达式执行时抛出异常的场景
>
>     
>
> 9. 的

## 4. Java FX

> 跳过

## 5. 日期和时间

> 

## 6. 并发库

## 7. JS Engine Noshorn

## 8. 其他改进



# Java 7 Note

