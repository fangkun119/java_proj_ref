# JVM运行时数据区及常用指令

> 之前的笔记是关于：(1) `class -> JVM`：class load、link、initialize；（2）JVM：run engine。这篇关于接下来的一部分，JVM运行时的`run-time data area`是什么样的 
> 
> 最官方的标准：《The Java Virtual Machine Specification》（`JVMS`）和《The Java Language Specification》（`JLS`）

## 1. Java运行时数据区

### 1.1 运行时数据区

 ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_runtime_data_areas.jpg)
 
> * `Program Counter`(简称`PC`)：存放下一条指令的位置，每个Java线程都有自己的`PC`
> * `JVM stacks`:  用来运行Java方法，每个线程有一个栈
> * `Native Method Stacks`：用来运行JVM本地方法（JVM内部用C/C++编写的方法 ）
> * `Direct Memory`：NIO用的、直接访问操作系统内核态IO缓冲区的内存，省去向JVM内存拷贝的过程
> * `Method Area`（`方法区`）：存储各类Class数据以及常量池数据（`Runtime Constant Pool`），所有线程共享。 方法区是一个逻辑上的概念；`Perm Space`/`Meta Space`是具体的实现
> 	* `1.8`之前：
> 		* 方法区实现在`Perm Space`中
> 		* 字符串类型的常量也位于`Perm Space`
> 		* `Perm Space`不会被`Full GC`清理、大小在启动时指定、不能变
> 	* `1.8`开始：
> 		* 方法区实现在`Meta Space`中，会被`Full GC`清理
> 		* `Meta Space`的大小如果不设定，则不会限制最大值
> 		* 字符串常量转移到了堆
> * `Heap`：存放分配的类对象、数组……，所有线程共享 
> 
> 每个线程都有自己的`PC`、`JVM Stacks`、`Native Method Stacks`；多个线程共享`Heap`、`Method Area`（`Perm`或`Meta Space`）

### 1.2 JVM Stack

`JVM Stack`：每个线程有一个`JVM Stack`，同时运行中的每个方法对应一个`Frame`（`栈帧`）

`Frame`(`栈帧`)用来：(1) 存储`data`和`partial resule`；(2) `dynamic linking`；(3) 存储`return values`和`dispatch exceptions`，最主要的是下面的部分：

> * 局部变量表（`Local Variable Table`）
> * 操作栈（`Operand Stack`）
> 	* 对于long的处理（store and load），多数虚拟机的实现都是原子的（jls 17.7），没必要加volatile
> * `Dynamic Linking`：解析常量池中的符号链接
>	* 参考1：[https://blog.csdn.net/qq_41813060/article/details/88379473](https://blog.csdn.net/qq_41813060/article/details/88379473 )
>	* 参考2：jvms 2.6.3
> * `return address`：方法`a`调用方法`b`、`b`返回值存放的地方

### 1.3 局部变量表

代码：[jvm/grp04_runtimearea/TestIPulsPlus.java](../demos/src/com/javaprojref/jvm/grp04_runtimearea/TestIPulsPlus.java) 

> ~~~java
> public class TestIPulsPlus {
>     public static void main(String[] args) {
>         int i = 8;
>         //i = i++;  // 输出：8
>         i = ++i;    // 输出：9
>         System.out.println(i);
>     }
> }
> ~~~

局部变量表：

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_local_variable_table.jpg)
> 
> * `Nr. 0`：对应方法参数`String[] args` （因为是`static`方法没有`this`，所以Nr.0是方法参数；否则Nr.0要用来表示`this`）
> * `Nr. 1`：对应局部变量`i`

代码块对应的字节码：

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_method_byte_code.jpg)
> 
> * `bipush 8`：把`8`对应的byte扩展成int value、随后入栈（因为byte可以容纳8、所以用了bipush；如果是129用short才能容纳、字节码会变成sipush 129）
> * `istore_1`：将`int`存储到局部变量表下标为`1`的表项中（对应变量`i`，值为8）
> * `iinc 1 by 1`：从局部变量表表项`1`加1（对应变量`i`，值为9）
> * `iload_1`：局部变量表表项`1`的值入栈（值为9）
> * `istore_1`：将`int`存储到局部变量表表项`1`（对应变量`i`，值为9）
> * `return`：方法返回
> 
> 如果把`i = ++i`改成`i = i++`，上面指令3和指令4会互换，从而导致i的值为8 （在jclasslib的界面，点击字节码指令，可以看到对应的含义，不需要自己查）

备注：

> * 上述“指令”都是JVM指令、不是CPU的寄存器指令
> * 不能保证原子性、即使是单条指令也有可能不具备原子性

## 2 常用指令

> * `<clint>`：class initialize，执行静态语句块
> * `<init>`：构造方法
> * `_store`：
> * `_load`： 
> * `invoke_xxx`：
>	* `InvokeStatic`：调用静态函数，例如[jvm/grp04_runtimearea/Demo01InovkeStatic.java](../demos/src/com/javaprojref/jvm/grp04_runtimearea/Demo01InovkeStatic.java)
>	* `InvokeVirtual`：调用普通的protected、public方法（自带多态），例如[jvm/grp04_runtimearea/Demo02InvokeVirtual.java](../demos/src/com/javaprojref/jvm/grp04_runtimearea/Demo02InvokeVirtual.java)
>	* `InvokeInterface`：通过接口调用的方法，例如[jvm/grp04_runtimearea/Demo03InvokeSpecial.java](../demos/src/com/javaprojref/jvm/grp04_runtimearea/Demo03InvokeSpecial.java)
>	* `InovkeSpecial`：调用可直接定位无需多态的方法（如private方法、构造方法），例如[jvm/grp04_runtimearea/Demo03InvokeSpecial.java](../demos/src/com/javaprojref/jvm/grp04_runtimearea/Demo03InvokeSpecial.java)
>	* `InvokeDynamic`：lambda表达式、反射、其他动态语言scala kotlin、CGLib ASM……，动态产生的class会用到的指令，例如[jvm/grp04_runtimearea/Demo05InvokeDynamic.java](../demos/src/com/javaprojref/jvm/grp04_runtimearea/Demo05InvokeDynamic.java)

> 使用`lambda表达式`时要小心下面的代码：
> 
> ~~~java
> for (...) {
> 	 I i = C::n;  // lambda表达式
> }
> ~~~
> 
> 这样的代码会在MethodArea产生大量的对象，造成性能浪费（指1.8及之后的版本、它们将方法区是现在Meta Space中；而1.8之前方法区是实现在Perm Space中并且Full GC时不会回收并直接导致OOM）
