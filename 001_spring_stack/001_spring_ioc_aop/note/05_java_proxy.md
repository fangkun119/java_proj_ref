[TOC]

# Java 代理

> 在使用[Spring AOP](04_spring_aop.md)以及[Session Bean注入到Singleton Bean](02_spring_ioc_advanced.md)时，其背后都是基于Java代理来实现，这篇笔记对Java代理进行详细说明

## 1 Proxy设计模式

用途：通过占位符（surrogate or placeholder）来访问某个具体的对象，并对访问过程进行管理，例如：

> (1) 通过代理进行惰性初始化
>
> (2) 通过代理提供计算结果缓存
>
> (3) 通过代理来封装远程调用
>
> (4) 通过代理进行访问权限管理
>
> (5) 通过代理提供固定的附加操作，例如耗时计算、访问次数累加等
>
> (6) ……

## 2 静态代理

### (1) 类图

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/001_spring_aop_proxy_pattern.jpg" width="500" /></div>

### (2) 例子

> 按照上面的类图，以编写代码的方式来实现代理，即为静态代理。以给某个类（`HelloSpeaker`）添加日志打印功能为例，静态代理实现如下：
>
> Subject：IHello接口
>
> ~~~java
> public interface IHello {
> 	public void hello(String name);
> }
> ~~~
>
> RealObject：HelloSpeaker类
>
> ~~~java
> public class HelloSpeaker implements IHello {
> 	@Override
> 	public void hello(String name) {
> 		System.out.println("Hello, " + name);
>     }
> }
> ~~~
>
> Proxy：HelloProxy类
>
> ~~~java
> public class HelloProxy implements IHello { 
> 	private Logger logger = Logger.getLogger(this.getClass().getName());
> 	private IHello helloObject; 
> 	public HelloProxy(IHello helloObject) { 
> 		this.helloObject = helloObject; 
> 	} 
>     public void hello(String name) { 
> 		logger.log(Level.INFO, "hello method starts...");      
> 		helloObject.hello(name);
> 		logger.log(Level.INFO, "hello method ends  ..."); 
> 	} 
> }
> ~~~
>
> Client：使用HelloProxy
>
> ~~~java
> public class ProxyDemo {
> 	public static void main(String[] args) {
> 		IHello proxy = new HelloProxy(new HelloSpeaker());
> 		proxy.hello("Justin");
> 	}
> }
> ~~~

### (3) 优点缺点

> * 优点点时：可以在不修改RealObject的前提下，为RealObject添加了新的功能。
> * 缺点是：对于不同类型的RealObject、编写不同的代理类。假如有N种代理功能、M种RealObject，那么需要N*M种代理类。

## 3 动态代理

### (1) 解决的问题

> 静态代理的问题是，假如有N种代理功能、M种RealObject，需要N*M种代理类。
>
> * 希望有一个公用类，能够自动为不同的Object生成不同的代理，将代理类的数量从N*M种降低到N种
> * 通过Java的动态代理来实现

### (2) 类图

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/001_spring_aop_dyn_proxy.jpg" width="700" /></div>
>
> 为每一种功能类型的代理，编写一个InvocationHandler：共N种
>
> 但InvocationHandler可以根据传入的RealObject生成不同的Proxy类，从而将要编写的类的数量从M*N降低到N种

### (2) 例子

> InvocationHandler类
>
> ~~~java
> import java.util.logging.*; 
> import java.lang.reflect.*; 
> 
> public class LogHandler implements InvocationHandler { 
> 	private Logger logger = Logger.getLogger(this.getClass().getName()); 
> 	private Object delegate; // 被代理的RealObject
> 
> 	public Object bind(Object delegate) { 
> 		// 被代理的RealObject
> 		this.delegate = delegate; 
> 		// 生成代理对象、它实现的接口与RealObject实现的接口相同
> 		return Proxy.newProxyInstance( 
> 			delegate.getClass().getClassLoader(), 
> 			delegate.getClass().getInterfaces(), 
> 			this); 
> 	}
> 
> 	// 对RealObject的所有方法进行代理（本例是添加日志操作）
> 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { 
> 		Object result = null; 
> 		try { 
> 			logger.log("method starts..." + method); 			// 添加日志操作
> 			result = method.invoke(delegate, args);				// 执行被代理的方法
> 			logger.log(Level.INFO, "method ends..." + method); 	// 添加日志操作
> 		} catch (Exception e){ 
> 			logger.log(e.toString());  // 添加日志操作
> 		}
> 		return result; 
> 	} 
> }
> ~~~
>
> Client代码
>
> ~~~java
> import java.lang.reflect.Proxy;
> public class ProxyDemo {
> 	public static void main(String[] args) {
>         // 使用上面的LogHandler生成Proxy类类为RealObject进行代理
> 		// 这个Proxy类实现了所有RealObject所实现的接口
> 		LogHandler logHandler  = new LogHandler(); 
> 		IHello helloProxy = (IHello) logHandler.bind(new HelloSpeaker()); 
>         // 通过Proxy来执行RealObject的方法
> 		helloProxy.hello("baba");            
> 	}
> }
> ~~~



## 4. CGLib

> TODO: 
>
> [https://github.com/cglib/cglib](https://github.com/cglib/cglib)
>
> [https://www.baeldung.com/cglib](https://www.baeldung.com/cglib)
>
> [https://objectcomputing.com/resources/publications/sett/november-2005-create-proxies-dynamically-using-cglib-library](https://objectcomputing.com/resources/publications/sett/november-2005-create-proxies-dynamically-using-cglib-library)

## 5. 相关应用

### (1) 开启Spring AOP自动代理

> 使用@Configuration类进行配置：增加`@EnableAspectJAutoProxy`注解
>
> 使用XML进行配置：使用`<aop:aspectj-autoproxy/>`
>
> 详细参考：[04_spring_aop.md](04_spring_aop.md)

### (2) 将Session Bean注入到Singleton Bean

> Session Bean注入到Singleton Bean时，使用代理，可以使其仍然保持Session属性。实现原理是，将Proxy注入到Singleton Bean中，当调用Proxy的方法时，该调用被指向当前Session的Bean中。
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_inject_session_bean_into_singleton.jpg" width="1024" /></div>





