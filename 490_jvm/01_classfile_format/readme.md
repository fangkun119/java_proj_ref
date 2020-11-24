# JVM：介绍及`class file`格式

## 1：JVM介绍

### (1) 代码编译和执行过程

stage 1: javac

> `*.java` -> `*.class`

stage 2: java 

>（1）`*.class` + `java类库` --> `ClassLoader`
>（2）`ClassLoader`  --> `字节码解释器`，`JIT即时编译器` --> `执行引擎` （与OS、硬件交互）
> 
>  对Java来说，解释和执行是混合的：特别常用的一部分代码，会被JIT即时编译，转换成可以直接发给处理器执行的指令（而不像普通 代码 ，仅仅被编译到字节码的阶段，需要经过字节码解释器解释才能够执行）

### (2) 跨语言跨平台

> * 可以理解为虚构的一台计算机，其中的`字节码指令集`的作用对应于汇编语言；`JVM内存管理`对应于栈、堆、方法区等。
> * 只与`class`有关，支持100多种语言和各种硬件平台。

### (3) JVM规范 

java  virtual machine specifications

> * [https://docs.oracle.com/en/java/javase/13/](https://docs.oracle.com/en/java/javase/13/)
> * [https://docs.oracle.com/javase/specs/index.html](https://docs.oracle.com/javase/specs/index.html)

> `Java 13`为例，下图所示的内容，可以看到`java 13`的语言特性以及对应JVM的`虚拟机`特性（比较晦涩庞大，不适合读，但是可以用来查问题；内容只涉及规范和命令等、不包含调优）
> 
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/javase_13.jpg)

### (4) 常见JVM实现

> `Hotspot`（Oracle官方版本、8之后升级要收费）、OpenJDK（`Hotspot`的开源版本）、`Jrockit`（合并到hotspot中了）、`J9`（IBM）、`Microsoft VM`、`Taobao VM`（hostspot深度定制版）、`LiquidVM`（直接针对硬件）、`azul. zing`（最新垃圾回收的业界标杆，土豪版本，www.azul.com）...

> ~~~bash
> $ java -version
> openjdk version "15.0.1" 2020-10-20
> OpenJDK Runtime Environment (build 15.0.1+9-18)
> OpenJDK 64-Bit Server VM (build 15.0.1+9-18, mixed mode, sharing)
> ~~~

## 2：Class文件结构

### (1) Demo代码：`Grp01Demo01ByteCode.java`

写一个空的类`Grp01Demo01ByteCode.java`，并在IDEA中编译生成`Grp01Demo01ByteCode.class`

> [Grp01Demo01ByteCode.java.java](../demos/src/com/javaprojref/jvm/demogrp01/Grp01Demo01ByteCode.java)

### (2) 查看`.class`文件的方法

> * 查看`.class`文件反编译生成的代码：Idea中点击生成的`.class`文件
> * 查看`.class`文件的16进制源码：
> 	* Idea中可以安装`BinEd`插件来查看（“open as binary”）
> 	* 也可以用sublime或记事本打查看16进制源码
> * 查看`.class`文件所包含的信息：`javap -v`命令
> * 其他工具：
> 	* `JBE`：可以修改`.class`文件
> 	* `jClassLib`: IDEA插件，安装后在IDEA菜单栏点击`view`->`show ByteCode with jclassLib`，比使用`javap -v`或者用IDEA内置的`view`->`show ByteCode`更方便

### (3) `.class`文件结构

> 1. `Magic Number`，`Minor Version`，`Major Version`：Version对应不同的JDK
> 2. `constant_pool_count`，`constant_pool`：长度为`constant_pool_count - 1`的表
> 3. `access_flags`：整个class的修饰符
> 
>  	* `ACC_PUBLIC` `Ox0001`：是否为public
> 	* `ACC_FINAL` `Ox0010`：是否为final
> 	* `ACC_SUPER` `Ox0020`：该flag在JDK1.0.2之后要求必须设置、用来指明invokespectial指令使用新语义
> 	* `ACC_INTERFACE` `Ox0200`：是否为接口
> 	* `ACC_ABSTRACT` `Ox0400`：接口或者抽象类
> 	* `ACC_SYNTHETIC` `Ox1000`：编译期自动生成、并非用户代码产生
> 	* `ACC_ANNOTATION` `Ox2000`：是否是注解
> 	* `ACC_ENUM`：`Ox4000`：是否是enum 
> 
> 4. `this_class`，`super_class`，`interfaces_count`，`interfaces`：当前类名、父类名、实现的接口，会指向常量池中对应的内容
> 5. `fields_count`，`fields`，`methods_count`，`methods`：属性、方法
> 6.	`attributes_count`，`attributes`：附加属性

### (4) 用`javap -v`命令查看`Grp01Demo01ByteCode.class`

> 在IDEA的console执行javap命令，可以保证javap的版本与编译代码生成.class的JDK版本一致
> 
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/490_jvm/demos/out/production/demos/com/javaprojref/jvm/demogrp01/ 
> $ javap -v Grp01Demo01ByteCode.class 
> Classfile /Users/fangkun/Dev/git/java_proj_ref/490_jvm/demos/out/production/demos/com/javaprojref/jvm/demogrp01/Grp01Demo01ByteCode.class
>   Last modified 2020年11月24日; size 342 bytes
>   SHA-256 checksum a71628dbe4c983585a82a877b6ac89fd2c190c7368bd4be529970108a73104e3
>   Compiled from "Grp01Demo01ByteCode.java"
> public class com.javaprojref.jvm.demogrp01.Grp01Demo01ByteCode
>   minor version: 0
>   major version: 59
>   flags: (0x0021) ACC_PUBLIC, ACC_SUPER
>   this_class: #7                          // com/javaprojref/jvm/demogrp01/Grp01Demo01ByteCode
>   super_class: #2                         // java/lang/Object
>   interfaces: 0, fields: 0, methods: 1, attributes: 1
> Constant pool:
>    #1 = Methodref          #2.#3          // java/lang/Object."<init>":()V
>    #2 = Class              #4             // java/lang/Object
>    #3 = NameAndType        #5:#6          // "<init>":()V
>    #4 = Utf8               java/lang/Object
>    #5 = Utf8               <init>
>    #6 = Utf8               ()V
>    #7 = Class              #8             // com/javaprojref/jvm/demogrp01/Grp01Demo01ByteCode
>    #8 = Utf8               com/javaprojref/jvm/demogrp01/Grp01Demo01ByteCode
>    #9 = Utf8               Code
>   #10 = Utf8               LineNumberTable
>   #11 = Utf8               LocalVariableTable
>   #12 = Utf8               this
>   #13 = Utf8               Lcom/javaprojref/jvm/demogrp01/Grp01Demo01ByteCode;
>   #14 = Utf8               SourceFile
>   #15 = Utf8               Grp01Demo01ByteCode.java
> {
>   public com.javaprojref.jvm.demogrp01.Grp01Demo01ByteCode();
>     descriptor: ()V
>     flags: (0x0001) ACC_PUBLIC
>     Code:
>       stack=1, locals=1, args_size=1
>          0: aload_0
>          1: invokespecial #1                  // Method java/lang/Object."<init>":()V
>          4: return
>       LineNumberTable:
>         line 3: 0
>       LocalVariableTable:
>         Start  Length  Slot  Name   Signature
>             0       5     0  this   Lcom/javaprojref/jvm/demogrp01/Grp01Demo01ByteCode;
> }
> SourceFile: "Grp01Demo01ByteCode.java"
> ~~~

### (5) 用`jClassLib`插件查看.class文件

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jclasslib.jpg)
> 
> * `一般信息`：参考前面的内容
> * `常量池`：每一种常量类型（例如：CONSTANT_Methodref_Info）对应一种不同的二进制数据结构
> 	* `二进制结构`中各个字段的含义可以参考 [https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4](https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4) 
> 	* `jClassLib`根据`二进制结构`解析.class并显示解析后的内容
> 	* 数据结构中的项有可能指向常量池中的其他项目（例如`cp_info#1`表示常量池第1项) ，如下图所示
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/constant_pool_item.jpg)
> 	* 遇到`Fieldm描述符`和`方法描述符`，例如`V`（void），可参考下面链接：
> 		* [https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.2](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.2)
> 		* [https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.3](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.3)
> * `接口`，`Fields`：接口和成员变量
> * `Methods`：方法，点击其中的`Code`项可以看到JVM字节码指令（每个指令解析自.class中的一个指令码）
> 	* `jClassLIb`为这些指令增加了链接，可以跳转到Oracle的页面上查看指令的用途，例如下面的`aload`
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/class_file_method.jpg)
> 	* 也可以在 [https://docs.oracle.com/javase/specs/index.html](https://docs.oracle.com/javase/specs/index.html)中找到参考

