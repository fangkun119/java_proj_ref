package com.javaref.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		Person person1 = ctx.getBean("person",Person.class);
		System.out.println(ToStringBuilder.reflectionToString(person1));

		Food food2 = ctx.getBean("food2", Food.class);
		System.out.println(ToStringBuilder.reflectionToString(food2));

		Person person2 = ctx.getBean("person", Person.class);
		Person person3 = ctx.getBean("person", Person.class);
		System.out.println(person2);
		System.out.println(person3);
		//com.javaref.spring.Person@63e2203c //内存地址生成的object hash code
		//com.javaref.spring.Person@63e2203c
		//默认情况下bean都是单例的，所以person2，person3的内存地址也相同，即作用域是"singleton"

		Person protoPerson1 = ctx.getBean("person_proto", Person.class);
		Person protoPerson2 = ctx.getBean("person_proto", Person.class);
		System.out.println(protoPerson1);
		System.out.println(protoPerson2);
		//com.javaref.spring.Person@7494e528
		//com.javaref.spring.Person@4bbfb90a
		//使用scope="prototype'属性把bean person_proto的声明周期设置成prototype，所以两个对象的内存地址不同

		// Spring的6种作用域，scope属性可以设置为下面6种值
		// singleton 	：单例
		// prototype 	：每次获取bean都new一个新对象

		// 下面的四种声明周期也是单例的，但是单例存续的声明周期不同
		// websocket 	：网页长链接（浏览器到服务器）内的单例
		// request		：请求处理周期内单例，从Controller接到请求到返回View给用户
		// session		：用户登录后产生的session内的单例
		// application	：spring的隔离application环境下（多个ApplicationContext)时，application内的单例

		// MVC
		// * Controller（Servlet: 根据url路径决定使用哪个Servlet类）
		// * Module层（Service，业务逻辑，数据库访问），
		// * View（jsp等，返回结果给用户）

		// Spring中Controller是单例的, Model中的Service, DAO，JDBC Connection……也是单例的（只有pojo，entity bean是多例的），提升性能
		// 容器根据用户请求，请求可能是被不同的线程处理的
		// 		线程会使用ThreadLocal来存储框架中为这些类编写的属性，例如与用户相关信息等
		// 		但是框架以外、Controller、Service、DAO、...的成员变量是共享的，因此这些类里面不能写成员变量！
		// 这些单例类（Controller、Service、DAO……）的方法可以不用担心多线程调用、放心使用（框架用ThreadLocal对其访问的数据做了线程隔离）
		// 但是不能在这些类中写成员变量、状态数据（如果写了要非常小心地使用）

		// SSM架构：Spring、Spring MVC、MyBatis
		// Spring在SSM架构中，主要用于创建对象
	}
}
