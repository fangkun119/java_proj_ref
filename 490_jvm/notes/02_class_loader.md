# Class加载和初始化

## 1. 步骤 

> 1. Loading ：将`class文件`加载到内存，使用`双亲委派模型`和`Lazy Loading`
> 2. Linking
> 
>	* Verfication：检查`class文件`是否符合标准 
> 	* Perparation：为`class`的静态变量赋<b>默认值</b>. 
> 	* Resolution：把`class`常量池里面的`符号引用`转换成可以直接取到值的内容
> 3. Initializing：把静态变量赋为初始值（这时才调用静态代码块）

## 2. 加载

### 2.1 理解类加载器（`ClassLoader`）

1. 如何查某个类是被哪个ClassLoader加载到内存中的

> 每一个类被加载之后，在内存中存在两部分内容：(1) 这个类的数据（在`metaspace`中）；(2) 一个`Class类对象`来代表这个类的类数据 
> 
> 使用`.class`可以获得该`Class类对象`，进而拿到初始化这个类的`ClassLoader`。以String为例：
> 
> ~~~java
> String.class.getClassLoader();
> ~~~

2. 类加载器层次

类加载器层次如下，上面的加载器是下面的`父加载器`（`父子加载器`关系，不是`父子类`关系，在`ClassLoader`模板类实现中，父加载器通过子加载器的成员变量来访问）

> * `Bootstrap层`：负责加载`JDK`中最核心的`JAR`，如`lib/rt.jar`、`charset.jar`等，`C++`实现
> * `Extension层`：负责加载扩展`JAR`包，加载`jre/lib/ext*.jar`或者`-Djava.ext.dirs指定的目录`
> * `App层`：加载`classpath`指定的目录的jar包
> * `CustomClassLoader`：自定义`ClassLoader`
> 
> ~~~java
> System.out.println(String.class.getClassLoader());	
> //  输出 null，被bootstrap层的加载器加载（C++实现、所以输出是null）
> System.out.println(sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader());
> //  输出 sun.misc.Launcher$ExtClassLoader@6f94fa3e，被Extension层的ClassLoader加载
> System.out.println(Grp02Demo01ClassLoaderLevel.class.getClassLoader());
> //  输出 sun.misc.Launcher$AppClassLoader@18b4aac2，被App层的ClassLoader加载 
> ~~~

双亲委派模型：

> *  `检查阶段`：查看一个类是否已经被加载，是自底向上检查的过程，按`CustomClassLoader` -> `App层` -> `Extension层`  -> `Bootstrap层`的顺序检查，某一层确认已经加载时返回
> *  `加载阶段`：加载一个还未被加载的类，是自顶向下尝试的过程：按`Bootstrap层` -> `Extension层` -> `App层`. -> `CustomClassLoader`的顺序匹配，如果某一层确认可以加载，就由这一层进行加载
> 
> 以上就是类加载的“双亲委派模型”，是出于安全考虑，防止发生诸如`Custom ClassLoader`加载`java.util.String`类的情况，始终让上层（对String来说是`BootStrap`层）有更高的优先级来加载：
> 
> * `检查阶段`：检查代码被写死了、不容许串改 （final native）
> * `加载阶段`：加载代码也被写死了、必须父加载器先尝试加载

类加载器的加载范围：

> ~~~java
> // Bootstrap ClassLoader加载路径：sun.boot.class.path
> String pathBoot = System.getProperty("sun.boot.class.path");
> // ExtensionClassLoader加载路径：java.ext.dirs
> String pathExt = System.getProperty("java.ext.dirs");
> // AppClassLoader加载路径：java.class.path
> String pathApp = System.getProperty("java.class.path");
> ~~~

Launch：

> * `sun.misc.Launcher$ExtClassLoader`表示`sun.misc.Launcher`类下面的一个内部类`ExtClassLoader`
> * `Launcher`是`class loader的启动类`，如下图所示，从`Launcher`的代码中可以看出它内置的3个ClassLoader，以及这三个ClassLoader所负责的加载范围
> 
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/490_jvm/jvm_classloader_launcher.jpg)

通过ClassLoader来加载一个类：

> 例如Spring的动态代理、JRebal热部署，都需要通过ClassLoader来加载类
> 
> ~~~java
> Class clazz = Demo04InvokeClassLoaderManually.class.getClassLoader().loadClass(
>                 "com.javaprojref.jvm.grp02_classloader.Demo04InvokeClassLoaderManually");
> ~~~
> 
> 可以看出`loadClass(...)`方法是`ClassLoader`加载类的入口函数，以下是findClass的代码片段及注释翻译

Demo代码：

> * 查看一个类的加载器，加载器的加载器，加载器的父加载器：[grp02_classloader/Demo01ClassLoaderLevel.java](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo01ClassLoaderLevel.java)
> * 加载器的加载器、加载器的父加载器比较：[grp02_classloader/Demo02ParentClassLoader.java](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo02ParentClassLoader.java)
> * 各个类加载器的加载范围：[grp02_classloader/Demo03ClassLoaderScope.java](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo03ClassLoaderScope.java)
> * 

### 2.2. 双亲委派加载过程

#### (1) 如何通过`ClassLoader`加载一个类

诸如`Spring的动态代理`、`JRebal热部署`等各类场景、都需要通过ClassLoader来加载类，例子如下

> ~~~java
> Class clazz = Demo04InvokeClassLoaderManually.class.getClassLoader().loadClass(
>                 "com.javaprojref.jvm.grp02_classloader.Demo04InvokeClassLoaderManually");
> ~~~
> 
> 可以看出`loadClass(...)`方法是`ClassLoader`加载类的入口函数

完整代码

> [`../demos/src/com/javaprojref/jvm/grp02_classloader/Demo04InvokeClassLoaderManually.java`](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo04InvokeClassLoaderManually.java)

#### (2) 类加载（`loadClass方法`）执行过程

> 在`Idea`中通过`ClassLoader.class`可以查看到`ClassLoader`的代码，从入口函数`loadClass()`开始可以看出加载过程
>
> ~~~java
> package java.lang;
> 
> public Class ClassLoader {
>     ...
>     private final ClassLoader parent;  
>     ...
>     public Class<?> loadClass(String name) throws ClassNotFoundException {
>         return loadClass(name, false);
>     }
> 
>     /**
>     * 按照如下顺序：
>     *   1. 调用findLoadedClass(String)去检查类是否已经被加载，如果有则返回
>     *   2. 调用父加载器的loadClass(...)方法（如果父加载器为null，调用jvm内置的bootstrap加载器)，如果加载成功则返回
>     *   3. 调用findClass(String)方法去查找要加载的类
>     * 如果在上面步骤3找到了要加载的类，并且`resolve`参数为`true`，会调用resolveClass(Class)方法返回一个Class对象 
>     * 备注1：ClassLoader子类可以override findClass方法
>     * 备注2：除非被override，可以保证findClass是synchronized
>     */
>     protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException  {
>         synchronized (getClassLoadingLock(name)) {
>             // 检查是否已经被加载，它会调用JVM的方法（private native final Class<?> findLoadedClass0(String name)）
>             Class<?> c = findLoadedClass(name);
>             // 如果没有被加载，会调用父加载器parent.loadClass(...)，而这个函数仍然会先调用它自己的findLoadedClass(name)，
>             // 因此是先进行自底向上的查找，然后自顶向下加载 
>             if (c == null) {
>                 long t0 = System.nanoTime();
>                 try {
>                     if (parent != null) {
>                         // 父加载器的loadClass(...)，其中parent是final的不可修改
>                         c = parent.loadClass(name, false);     
>                     } else {
>                         // 最顶层的bootstrap加载器是用C++写的，因此没有Java对象
>                         c = findBootstrapClassOrNull(name); 
>                     }
>                 } catch (ClassNotFoundException e) {
>                     // 不处理父加载器抛出的ClassNotFoundException，保持c==null即可，会由下面的findClass方法来再次查找
>                 }
> 
>                 // 如果父加载器都不能加载这个class，由当前加载自自己来加载
>                 if (c == null) {
>                     ...
>                     c = findClass(name);  // 留给子类来实现
>                     ...
>                 }
>             }
>             if (resolve) {
>                 resolveClass(c);
>             }
>             return c;
>         }
> 
>     // findClass是protected、留给自定义加载器自己来实现
>     protected Class<?> findClass(String name) throws ClassNotFoundException {
>         throw new ClassNotFoundException(name);
>         }
>     }
> 	...
> }
> ~~~
> 
> 从上面可以看出，双亲委派使用了模板方法设计模式，如果要自定义`ClassLoader`，关键也是实现findClass方法

#### (3) 双亲委派机制有可能被打破吗？

要通过重写`loadClass()`方法，有以下三种情况，详细内容在《[深入理解Java虚拟机：JVM高级特性与最佳实践](https://www.amazon.cn/dp/B082PTTSNB/ref=sr_1_1?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99&dchild=1&keywords=%E6%B7%B1%E5%85%A5%E7%90%86%E8%A7%A3java%E8%99%9A%E6%8B%9F%E6%9C%BA&qid=1606801962&sr=8-1)》（周志明著）中有提到：

> 1. `JDK1.2`之前，自定义ClassLoader必须重写loadClass()
> 2. `ThreadContextClassLoader`可以通过基础类调用实现类代码、通过`thread.setContextClassLoader`指定
> 3. `osgi`, `tomcat`都有自己的模块指定classloader（可以加载同一类库的不同版本）

下面是两个演示：

> 1. 	[`Demo09ClassReloading1.java`](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo09ClassReloading1.java)：热加载被双亲委派机制阻止，无法加载新的类
> 2.	[`Demo09ClassReloading2.java`](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo10ClassReloading2.java)：通过重写`loadClass`方法来打破双亲委派机制

###  2.3. 自定义类加载器

#### (1) 继承`ClassLoader`实现自定义加载器

方法之一是`继承ClassLoader`类，例子如下

> [../demos/src/com/javaprojref/jvm/grp02_classloader/Demo05SelfDefinedClassLoader.java](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo05SelfDefinedClassLoader.java)

#### (2) 为自定义加载器指定非默认的父加载器

默认情况下，自定义ClassLoader的父加载器是`ClassLoader.getSystemClassLoader()`即`AppClassLoader`
> 
> 如[Demo01ClassLoaderLevel](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo01ClassLoaderLevel.java)中的演示
> 
> ~~~java
> System.out.println(new Demo05SelfDefinedClassLoader().getParent());
> //  输出 sun.misc.Launcher$AppClassLoader@18b4aac2
> System.out.println(ClassLoader.getSystemClassLoader());
> //  输出 sun.misc.Launcher$AppClassLoader@18b4aac2
> ~~~
> 
> 在模板类`ClassLoader.java`的代码中也可以看出，默认构造函数是如何为一个自定义ClassLoader指派父加载器
> 
> ~~~java
> protected ClassLoader() {
>         this(checkCreateClassLoader(), getSystemClassLoader());
>  }
> ~~~

如何为自定义ClassLoader指派父加载器，代码参考

> [../demos/src/com/javaprojref/jvm/grp02_classloader/Demo08AssignParentClassLoader.java](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo08AssignParentClassLoader.java)

### 2.4. JVM的Lazy Loading

> 通常说的“懒加载”、严格讲应该叫“懒初始化”（Lazy Initializing），因为JVM规范并没有规定一个类何时加载、而是规定了何时初始化

在以下场合，类将会被初始化

> * `new`以及static相关指令（`getstatic`、`putstatic`、`invokestatic`）被执行时，访问final变量除外
> * `java.lang.reflect`对类进行反射调用时
> * 子类初始化时、父类必须首先初始化
> * 虚拟机启动时、被执行的主类必须初始化
> * 动态语言支持`java.lang.invoke.MethodHandle`解析的结果为`REF_getstatic`/`REF_putstatic`/`REF_invokestatic`的方法句柄时，该类必须初始化

代码：

> [../demos/src/com/javaprojref/jvm/grp02_classloader/Demo07LazyLoading.java](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo07LazyLoading.java)

### 2.5. 解释器和即时编译器（`JIT）

(1) 解释器和编译器

> * 解释器：一边解释字节码一边执行
> * JIT即时编译器：将代码编译成可以执行的指令、之后可以直接执行

(2) 混合模式（默认）

> 混合使用解释器，结合热点代码编译，过程如下
> 
> * 起始阶段采用解释执行
> * 热点代码检测
> 	* 多次被调用的方法（方法计数器：监听方法执行频率）
> 	* 多次被调用的循环（循环计数器：检测循环执行频率）
> * 对热点代码进行编译

> 为何要使用解释器
> 
> * 解释器在很多情况下效率也不低
> * 程序启动快（不用全编译一遍再启动）

(3) 在JVM参数（VM Options）中指定程序执行模式

> * `-Xmixed`：混合模式
> * `-Xint`：纯解释模式、启动快，执行稍慢
> * `-Xcomp`：纯编译模式、执行快、启动很慢（当jar包和类很多时）

## 3. Linking以及Initializtion

### 3.1 Linking的步骤

> 1. Verfication：检查`class文件`是否符合标准 
> 2. Perparation：为`class`的静态变量赋<b>默认值</b>（如int类型的赋默认值0）
> 3. Resolution：把`class`常量池里面的`符号引用`转换成具体的内存地址、偏移量等直接引用
> 
> 	这一步其实就是`ClassLoader`类`loadClass(2)`方法中的第二个参数`resolve`所激活的操作
> 
> 	~~~java
> 	protected Class<?> loadClass(String name, boolean resolve)
> 	~~~

### 3.2 Initialization的步骤

> Initializing：把静态变量赋为初始值（而不再是默认值），这时会调用静态代码块

### 3.3 代码演示

> [../demos/src/com/javaprojref/jvm/grp02_classloader/Demo11ClassLoadLinkInitSteps.java](../demos/src/com/javaprojref/jvm/grp02_classloader/Demo11ClassLoadLinkInitSteps.java)

### 3.4 对于非静态成员变量

以上面演示中的代码为例：

> ~~~java
> class T1 {
>     public static int count = 2;
>     public static T1 t = new T1();
>     private int m = 8; // 非静态成员变量，new对象的时候才创建，过程是：(1)申请内存（2）赋默认值0，（3）执行构造函数设初始值为8
>     private T1() {
>         count ++; //这样的代码不规范，对静态变量的初始化操作，应当放在静态代码块中
>     }
> }
> ~~~

### 3.5 半初始化问题

> ~~~java
> public class Mgr {
> 	private static volatile Mgr INSTANCE;  // 需要加volatile
> 	...
> 	public static Mgr getInstance() {
> 		if (null == INSTANCE) {
> 			// 双重检查
> 			synchroized (Mgr.class) {
> 				if (null == INSTANCE) {
> 					// 如果INSTANCE变量不使用volatile
> 					// 初始化到一半时但还没完成时，会因指令重排发生INSTANCE不为null的情况
> 					// 这时另一个线程getInstance()会拿到一个正在初始化过程中的单例对象来使用
> 					INSTANCE = new Mgr(); 
> 				}
> 			}
> 		}
> 	}
> }
> ~~~