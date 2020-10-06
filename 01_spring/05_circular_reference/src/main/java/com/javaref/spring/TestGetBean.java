package com.javaref.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	/**
	 * 创建了3个类，A依赖B，B依赖C，C依赖A，构成循环依赖
	 * > 都通过构造器注入时，无论这些bean是singleton还是prototype，在获取bean的时候都会失败。
	 * > 都通过属性注入时，都是singleton注入成功，都是prototype时注入失败
	 * > 同时有singleton和prototype时，先获取的那个bean是singleton时，就会成功，否则失败
	 *
	 * 原理：循环依赖导致bean在创建时发生循环等待
	 * > 当Spring容器在创建A时（发现A引用B），会先去创建B（发现B引用C），会先去创建C（发现C引用A），又要创建A……
	 * > A、B、C之间互相等待，谁都没法创建成功
	 *
	 * 老的JVM下，循环依赖导致Object的引用计数始终大于0，对象无法销毁（新的JVM下使用GC Root来销毁，仍然可以销毁对象）
	 * 对于循环依赖，Spring在启动阶段仍然会检测并解决bean的循环依赖，这是由Spring使用了三级缓存的bean管理机制导致
	 *
	 * Spring如何管理bean：
	 * > 1. 用[bean name/id -> bean object]来存储bean的注册信息
	 * > 2. 加载配置文件以后，初始化bean
	 * >	如果是单例：实例化这个bean并存入到注册表中
	 * >	如果是prototype：先初始化它引用的其他bean，此时就会出现初始化死循环
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// 如果不使用容器，自己new出一组循环引用的对象，如下面的代码，导致Object的引用计数始终大于0，对象无法销毁
		// 对于单例模式来说，因为内存有限，影响并不大；对于非单例模式来说，对象数量不限，会产生内存隐患
		System.out.print("---- Spring is not used ----");
		A a1 = new A();
		B b1 = new B();
		C c1 = new C();
		a1.setB(b1);
		b1.setC(c1);
		c1.setA(a1);

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		// 如果交给Spring框架来管理，Spring会发现这样的内存隐患，
		// 如果prototype bean之间发生循环引用，Spring会阻止这些bean的初始化
		// 如果是singleton bean并使用属性注入，Spring可以初始化这些bean
		System.out.println("----- 3 singleton beans in circular reference -----");
		A singletonA1_1 = ctx.getBean("singletonA1", A.class);
		System.out.println(ToStringBuilder.reflectionToString(singletonA1_1));
		System.out.println(ToStringBuilder.reflectionToString(singletonA1_1.getB()));
		System.out.println(ToStringBuilder.reflectionToString(singletonA1_1.getB().getC()));
		// 输出：
		// com.javaref.spring.A@544fe44c[b=com.javaref.spring.B@3327bd23]
		// com.javaref.spring.B@3327bd23[c=com.javaref.spring.C@4e1d422d]
		// com.javaref.spring.C@4e1d422d[a=com.javaref.spring.A@544fe44c]
		A singletonA1_2 = ctx.getBean("singletonA1",A.class);
		System.out.println(singletonA1_1 == singletonA1_2);
		System.out.println(singletonA1_1.getB() == singletonA1_2.getB());
		// 输出
		// true
		// true

		// 如果相互依赖的bean，有的是singleton bean，有的是prototype bean（依赖关系全部使用属性注入）
		// * 当被依赖的Bean是singleton bean时，这些单例bean能够直接实例化，而不用等待其他依赖
		// * 当被依赖的Bean是prototype bean时，这些bean要等待dependencies先实例化完毕（循环依赖时可能会导致bean无法实例化，也可能不会）
		System.out.println("----- 1 singleton bean and 2 prototype benas in circular reference -----");
		A singletonA2_1 = ctx.getBean("singletonA2", A.class);
		System.out.println(singletonA2_1);
		System.out.println(singletonA2_1.getB());
		System.out.println(singletonA2_1.getB().getC());
		A singletonA2_2 = ctx.getBean("singletonA2", A.class);
		System.out.println(singletonA2_2);
		System.out.println(singletonA2_2.getB());
		System.out.println(singletonA2_2.getB().getC());
		System.out.println(singletonA2_1 == singletonA2_2);
		System.out.println(singletonA2_1.getB() == singletonA2_2.getB());
		// 输出：A是单例，B、C是被A引用的prototype
		// com.javaref.spring.A@66480dd7  // A是单例，先被初始化
		// com.javaref.spring.B@3327bd23  // B是prototype，依赖prototype C，等C先初始化完毕之后，B再初始化
		// com.javaref.spring.C@4e1d422d  // C是prototype，依赖singleton A，A已经初始化完毕
		// com.javaref.spring.A@66480dd7  // 第二轮：A是单例，A已经初始化完毕，直接从注册表中取出，不再需要初始化
		// com.javaref.spring.B@3327bd23  // B是prototype，但是来自单例A，不需要再初始化
		// com.javaref.spring.C@4e1d422d  // C是prototype，但是来自单例A，不需要再初始化
		// true
		// true  // B虽然是prototype，但是都来自于单例A，因此对象地址的hashCode都相同，要小心多线程问题

		return;
	}
}
