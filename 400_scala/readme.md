[TOC]

## Scala笔记

> 书籍：[Scala编程思想](https://item.jd.com/10028651578645.html)
>
> 代码：[https://github.com/fangkun119/atomic-scala-examples](https://github.com/fangkun119/atomic-scala-examples)

## 1. 安装和运行

### CH01-02【工具】：Scala编辑器；OS Shell

> Scala编辑器：P01
>
> OS Shell：P02

### CH03-04【安装】：安装Scala（Windows、Mac、Linux）

> Windows：P05
>
> Mac：P09
>
> Linux：P13

### CH05-07【运行】：运行Scala；注释；运行脚本

> 运行Scala：P19
>
> 注释：P20
>
> 运行脚本：P21

## 2. 基础语法

### CH09-CH16【基本单元】：值；类型；变量；表达式；条件表达式（1）；计算顺序；组合表达式；总结1

> 值（`val`）：P22
>
> 数据类型及类型推断：P24
>
> 变量（`var`）：P27
>
> 表达式及`Unit`：P29
>
> 条件表达式（基本表达式）：P31
>
> 运算符优先级：P34
>
> 组合表达式（嵌套表达式）：P37
>
> 总结1：P41

### CH17-CH24【类/对象/包】：方法；类和对象；ScalaDoc；类构造（1）；类中的方法；包；演示工具；字段

#### 方法：P45

> ~~~scala
> def methodName(arg1:Type1, arg2:Type2, ...):returnType = { 
> 	lines of codes
> 	results
> }
> ~~~

#### 类和对象：P18

> ~~~scala
> val r1 = Range(0, 10)
> ~~~

#### ScalaDoc参考手册：P19

> [http://www.scaal-lang.org/api/current/index.html](http://www.scaal-lang.org/api/current/index.html)
>
> [http://docs.oracle.com/javase/6/docs/apijava/lang/String.html](http://docs.oracle.com/javase/6/docs/apijava/lang/String.html)

#### 类构造（1）——类体中的代码：P20

> ~~~scala
> class Hyena {
>     println("this line will be executed when object is being constructed")
> }
> val hyena = new Hyena
> ~~~

#### 类中的方法及调用：P58

> ~~~scala
> class MyClass1 {
>     def func1():String = { "func1" }
>     def func2():String = {
>         func1() + " func2"
>     }
> }
> val obj1 = new MyClass1
> obj1.func2()
> ~~~

#### 包的定义和导入：P61

> ~~~scala
> import util.Random
> import util.Properties
> import util.Random, util.Properties
> import util.{Random, Properties}
> import util.{Random => r, Properties => p}
> import util._
> ~~~

#### 演示工具——作者编写的用于代码演示的测试框架：P65

#### 类的字段——域：P70

> ~~~scala
> class MyClass1 {
>     var fieldAsVariable = 0;
>     val fieldAsValue = 1;
>     ...
> }
> ~~~

### CH25-CH28【循环/向量/匹配】：循环；Vector；条件表达式（2）；总结2；模式匹配（1）

#### `for`循环：P72

> ~~~scala
> // 遍历0，1，2，……，9
> for (i <- 0 to 9) {...}
> for (i <- 0 until 10) {...}
> for (i <- Range(0, 10)) {...}
> for (i <- Range(0, 9).inclusive /*方法调用简写、省略了()*/) {...}
> // 遍历0，2，4，……，18
> for (i <- Range(0, 20, 2)) {...}
> ~~~

#### `Vector`：P75

> ~~~scala
> val myVec = Vector(1, 3, 5, 7, 9) // 不需要new
> myVec(1) // 查看元素
> 
> val result = ""
> for (i <- Vec) { 
>     result += i + " " // 自动类型推断
> }
> 
> myVec.sorted //方法调用简写、省略了()
> myVec.sort
> myVec.reversed
> myVec.reverse
> myVec.head
> myVec.tail
> ~~~

#### 条件表达式（2）：P79

> 在条件表达式中使用return来减少代码行数

#### 总结2：P82

#### 模式匹配1——`Match-Case`语法：P91

> ~~~scala
> def matchColor(color:String):String = {
> 	color match {
> 		case "red" => "RED"
> 		case "blue" => "BLUE"
> 		case "green" => "GREEN"
> 		case _ => "UNKNOW COLOR: " + color	
> 	}
> }
> ~~~

### CH30-CH36【类】：类参数定义；传参；重载；类成分初始化；辅助构造器；练习；case类

#### 类参数定义：P94

> ~~~scala
> class MyClass(a:Int/*类外不可见*/, val b:Int/*类外只读*/, var c:Int/*类外可读写*/, args:Int* /*可变参数列表*/) {
> 	// 类体中的代码会在对象创建时执行
> 	println(func /*间写模式，省略了func()中的()*/)
> 
> 	def f():Int = { a * 10 /*访问了类参数*/ }
> 
> 	def argsSum():Int = {
> 		var total = 0;
> 		for (n <- args) { /*访问可变参数列表*/
> 			total += n
> 		}
> 		total
> 	}
> }
> 
> val obj = new MyClass(1, 2, 3, 10,11,12,13,14,15)
> // println(obj.a) // 出错，参数a类外不可见
> println(obj.b)    // 参数b类外只读
> obj.c = 24        // 参数c类外可读可写
> ~~~

#### 传参：具名参数和缺省参数：P98

> ~~~scala
> class Color(red:Int = 100, blue:Int = 100, green:Int = 100)
> new Color(20)
> new Color(20, 17)
> new Color(blue = 20)
> new Color(red = 11, green = 42)
> // 说明：
> // 1. 具名参数不仅适用于类参数，也适用于方法参数
> // 2. 可变参数列表必须出现在最后
> // 3. 出现可变参数列表时，使用具名参数，必须严格遵循参数的定义顺序
> ~~~

#### 重载：P101

> ~~~scala
> class Overloading {
>     def f():Int = {88}
>     def f(n:Int):Int = {n + 2}
> }
> ~~~

#### 构造器1——类成分的初始化：P104

> (1) 用传参的方式初始化类参数
>
> (2) 用初始值初始化类体内的var和val
>
> (3) 用类体中的代码完成对象构造时的计算

#### 构造器2——辅助构造器`this`：P108

> ~~~scala
> class GardenGnome(val height:Double, val weight:Double, val happy:Boolean /*类参数*/) {
> 	// 类体内的var、val
> 	var painted = true
> 	// 类体内的代码
> 	println("Inside primary constructor")
> 	// 辅助构造器1：调用默认的构造器
> 	def this(height:Double) {
> 		this(height, 100.0, true)
> 		// 此时：painted的值为true
> 	}
> 	// 辅助构造器2：调用辅助构造器1
> 	def this(name:String) = {
> 		this(15.0)
> 		// 此时：painted的值为true
> 	}
> }
> ~~~

#### 类的练习：P110

#### `case Class`：P112

> ~~~scala
> // 对于封装属性字段的类，使用case Class可以简化代码
> case Class Cat (name:String /*默认是val*/, var age:Int /*可以显式标记为var*/)
> val myCat = Cat("Tom", 2) // 不需要new
> println(myCal.name) 
> myCat.age = 5 // val myCat只保证不能让它指向其他Cat，但可以修改对象的var
> 
> // 不需要new之后，使用会很方便
> val cats = Vector(Cat("Miffy", 3), Cat("Rags", 2))
> 
> // 自动覆盖了toString方法
> println(myCat)
> // 输出Cat("Tom", 5)
> ~~~

## 3. 代码编写进阶

### CH37-CH41【杂项】：字符串插值；类型参数化；方法对象；map和reduce；类型推导

> d

### CH42-CH43【模式匹配】：模式匹配（2：基于类型）；模式匹配（3：基于case类）

> d

### CH44-CH48【代码简化】：简洁性；风格拾遗；地道Scala；自定义操作符；toString

> d

## 4. 类/元组/Trait进阶

> d

## 5. 应用及设计

> d

## 6. 集合

> d

## 7. 异常处理

> d

### 8. 其他

> d