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

## 8. 其他

> d

### 补充内容1： 多参数列表（柯里化）

> 文档：[https://docs.scala-lang.org/zh-cn/tour/multiple-parameter-lists.html](https://docs.scala-lang.org/zh-cn/tour/multiple-parameter-lists.html)
>
> 方法可以定义多个参数列表
>
> * 当使用较少的参数列表调用多参数列表的方法时，会产生一个新的函数
> * 这个新的函数接收剩余的参数列表作为其参数
>
> 例如：
>
> 在Scala collection traits `TraversableOnce` 中定义的 `foldLeft`，方法签名为
>
> ~~~scala
> def foldLeft[B](z: B)(op: (B, A) => B): B
> ~~~
>
> 下面是使用该方法的例子
>
> ~~~scala
> val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
> 
> // 多参数列表方法的调用，代码更加清晰
> val res = numbers.foldLeft(0)((m, n) => m + n)
> print(res) // 55
> 
> // 可以使用scala简化代码
> numbers.foldLeft(0)(_ + _)
> 
> // 可以固定一部分参数，使其变成一个新的函数
> val numberFunc = numbers.foldLeft(List[Int]())_
> val squares = numberFunc((xs, x) => xs:+ x*x) // List(1, 4, 9, 16, 25, 36, 49, 64, 81, 100)
> val cubes = numberFunc((xs, x) => xs:+ x*x*x) // List(1, 8, 27, 64, 125, 216, 343, 512, 729, 1000)
> ~~~
>
> 如果要指定参数列表中的某些参数为隐式（implicit），应该使用多参数列表。例如
>
> ~~~scala
> def execute(arg: Int)(implicit ec: ExecutionContext) = ...
> ~~~

### 补充内容2：`implicit`

#### (1) implicit parameters：容许查找隐式值来转换参数

> 文档：[https://docs.scala-lang.org/zh-cn/tour/implicit-parameters.html](https://docs.scala-lang.org/zh-cn/tour/implicit-parameters.html)
>
> 带有implicit关键字的参数，被称为`隐式参数`，当没有方法要求传参时，会尝试查找可以获得正确类型的隐式值来自动传入这些参数。查询位置包括：
>
> 1. 查找可以直接访问的隐式定义和隐式参数
> 2. 在伴生对象中查找与隐式候选类型相关的有implicit标记的成员
>
> 详细参考：[https://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html](https://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html)
>
> 例如
>
> ~~~scala
> abstract class Monoid[A] {
>   def add(x: A, y: A): A
>   def unit: A
> }
> 
> object implicitTest {
> 	// 定义两个可以隐式使用的对象，类型分别是Monoid[String]和Monoid[Int]
> 	implicit val stringMonoid: Monoid[String] 
>     	= new Monoid[String] {
> 			def add(x: String, y: String): String = x concat y
> 			def unit: String = ""
> 		}
> 	implicit val intMonoid: Monoid[Int]
>     	= new Monoid[Int] {
> 			def add(x: Int, y: Int): Int = x + y
> 			def unit: Int = 0
> 		}
> 	// 隐式参数：implicit m:Monoid[A]
> 	// implicit表示，如果能够找到隐式的Monoid[A]应用在隐式参数m上
> 	// 那么在调用sum方法时，只需要为xs:List[A]传参
> 	def sum[A] (xs: List[A]) (implicit m: Monoid[A]): A =
> 		if (xs.isEmpty) m.unit
> 		else m.add(xs.head, sum(xs.tail))
> 	// 测试
> 	def main(args: Array[String]): Unit = {
> 		// 使用了intMoid和stringMonoid对象当做隐式参数
> 		println(sum(List(1, 2, 3)))       // uses IntMonoid implicitly
> 		println(sum(List("a", "b", "c"))) // uses StringMonoid implicitly
> 	}
> }
> ~~~

#### (2) implicit classes：主构造函数可用于隐式转换

> 参考：[https://docs.scala-lang.org/zh-cn/overviews/core/implicit-classes.html](https://docs.scala-lang.org/zh-cn/overviews/core/implicit-classes.html)
>
> 在对应的作用域内，带有implicit关键字的主构造函数可以用于隐式转换。定义implicit class有如下限制条件：
>
> 1. 只能在别的trait/class/object内部定义
> 2. 构造函数只能携带一个非隐式参数
> 3. 在同一个作用域内、隐式类命名必须唯一（不能与任何类、方法、成员、对象、隐式类重名），这也意味这它不可以是case class
>
> 例子
>
> ~~~scala
> object Helpers {
> 	// 可以将Int隐式转换成IntWithTimes对象，该对象有一个times操作符可以调用
> 	implicit class IntWithTimes(x: Int) {
> 		def times[A](f: => A): Unit = {
> 			def loop(current: Int): Unit = {
>         		if(current > 0) {
> 					f
> 					loop(current - 1)
> 				}
>  	  			loop(x)
> 			}
> 		}
>   	}
> }
> ~~~
>
> ~~~bash
> scala> import Helpers._
> import Helpers._
> 
> scala> 5 times println("HI") # 5被隐式转换成了IntWithTimes对象
> HI
> HI
> HI
> HI
> HI
> ~~~

#### (3) implicit functions：引入到上下文中可以让指定类型进行隐式转换的函数

> 参考：[https://docs.scala-lang.org/zh-cn/tour/implicit-conversions.html](https://docs.scala-lang.org/zh-cn/tour/implicit-conversions.html)
>
> 定义一个用于将类型`S`（表示Start）到类型`T`（表示Target）的类型装换函数，它在两种情况下会被用到

情况1：表达式`e`需要类型`T`、而传给它的是类型`S`

> scala会查找符合`S => T`的`implicit function`

情况2：访问类型为`S`的对象`e`的成员`e.m`，但是被访问的`m`并没有在类型`S`中声明

> scala会查找符合`S => T`（`T`包含成员`m`）的`implicit function`

例子如下

> ~~~scala
> import scala.language.implicitConversions
> 
> // 隐式方法list2ordered：它将 List[A] 转换为 Ordered[List[A]]
> implicit def list2ordered[A]
> 	(x: List[A]) 
> 	// 它有一个隐式参数：代表着还会寻找另外一个隐式方法来讲A转换为Ordered[A]
> 	// 在scala.Predef.intWrapper中有符合”Int => Ordered[Int]“的方法提供
> 	(implicit elem2ordered: A => Ordered[A])
> : Ordered[List[A]] = {
> 	new Ordered[List[A]] { 
> 		//replace with a more useful implementation
> 		def compare(that: List[A]): Int = 1
>   	}
> }
> ~~~
>
> 将`List[A] => Ordered[List[A]]`和`Int => Ordered[Int]`两个隐式转换方法import到上下文中，就可以对`List[Int]`进行比较，例如
>
> ~~~scala
> List(1,2,3) <= List(4,5)
> ~~~

