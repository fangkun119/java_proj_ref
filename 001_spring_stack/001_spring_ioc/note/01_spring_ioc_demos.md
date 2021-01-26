# Spring IOC简单示例

[TOC]

##  1 概念

[控制反转(Inversion of Control，缩写为IoC)](https://zh.wikipedia.org/wiki/%E6%8E%A7%E5%88%B6%E5%8F%8D%E8%BD%AC)，对象在创建的时候，由外部容器，将其所依赖的对象的引用传递给它

> * 是面向对象编程中的一种设计模式、用来降低系统耦合
> * 常见的实现方法是 `依赖注入（Dependency Injection，简称DI）`、还有一种方式叫`依赖查找`
> * “哪些方面的控制被反转了？”：依赖对象的获得被反转了

未使用IOC的时：

> 需要通过硬编码的方式来设置对象之间的依赖关系
>
>  ~~~java
> Person person = (Person)ctx.getBean("person");
> Food food = ctx.getBean("food",Food.class); 
> 
> // 需要通过硬编码的方式来设置依赖
> food.setName("apple"); 
> person.setName("zhangsan");
> person.setAge(18); 
> person.setFood(food);
>  ~~~

使用IOC之后

> 程序代码不需要关注对象之间的依赖
>
> ~~~java
> ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
> Person person = (Person)ctx.getBean("person");
> ~~~
>
> 依赖交给容器托管
>
> ~~~xml
> <bean id="person" class="com.javaref.spring.Person">
> 	<!-- 使用了构造器注入，会调用Person的构造函数 Person(String name, int age, Food food) -->
> 	<constructor-arg name="name" value="maxiaosan"></constructor-arg>
> 	<constructor-arg name="age" value="18"></constructor-arg>
> 	<constructor-arg name="food" ref="food"></constructor-arg>
> </bean>
> ~~~

##  2 XML依赖注入配置语法

### 2.1 使用多个xml配置文件

####  方法1：初始化容器时，指定多个xml配置文件

> ~~~java
> ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationCon>text(
>    "applicationContext.xml","application-service.xml"); 
> ~~~

#### 方法2：初始化容器时，仅指定一个root  xml 配置文件 ，由这个root  xml来import多个其他的xml

> ~~~java
> ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
> ~~~
>
> ~~~xml
> <?xml version="1.0" encoding="UTF-8"?>
> <beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
> 		<!-- 使用多个DI配置文件，可以使用通配符 -->
> 		<import resource="applicationContext-*.xml"/>
> 	<import resource="another-sprint-context.xml"/>
> 		...
> </beans>
> ~~~
>
> Demo：
>
> [../spring_demos/01_rawproj_and_construct_inject/src/applicationContext.xml](../spring_demos/01_rawproj_and_construct_inject/src/applicationContext.xml)

### 2.2 XML配置、构造函数注入

#### (1) 常用配置方式

> ~~~xml
> <bean id="person" class="com.javaref.spring.Person">
> 	<!-- 使用了构造器注入，会调用Person的构造函数 Person(String name, int age, Food food) -->
> 	<constructor-arg name="name" value="maxiaosan"></constructor-arg>
> 	<constructor-arg name="age" value="18"></constructor-arg>
> 	<constructor-arg name="food" ref="food"></constructor-arg>
> </bean>
> ~~~
>
> Demo： 
> 
> * [../spring_demos/01_rawproj_and_construct_inject/src/applicationContext.xml](../spring_demos/01_rawproj_and_construct_inject/src/applicationContext.xml)
> * [../spring_demos/01_rawproj_and_construct_inject/src/com/javaref/spring/TestGetBean.java](../spring_demos/01_rawproj_and_construct_inject/src/com/javaref/spring/TestGetBean.java)

### 2.3 XML配置、属性注入

####  (1) 常用的配置方式

>  ~~~xml
> <!-- 属性对象注入，Spring会调用对象的set方法来完成DI -->
> <bean id="person1" name="human,star" class="com.javaref.spring.Person">
> 	<!-- 可以给bean提供多个name，除了id，这些name也可以用于被java代码获取bean的对象 -->
> 	<!-- 三种指定属性值的方式：(1)value标签 (2)value属性 (3)引用另一个bean -->
> 		<property name="name"><value>Jerry</value></property>
> 		<property name="age" value="18" />
> 		<property name="food" ref="food" />
> </bean>
> <bean id="food" class="com.javaref.spring.Food"></bean>
>  ~~~
>
> ~~~java
> Person person1 = ctx.getBean("person1", Person.class);
> System.out.println(ToStringBuilder.reflectionToString(person1));
> //输出：
> //com.javaref.spring.Person@6321e813[name=Jerry,age=18,food=com.javaref.spring.Food@3224f60b]
> ~~~

####  (2) `p-namespace`简写方式

> ~~~xml
> <!-- 属性对象注入的p-namespace简写方式（不常使用）-->
> <bean id="person2" class="com.javaref.spring.Person" p:age="21" p:name = "Tom" p:food-ref="food2"/>
> <bean id="food2" class="com.javaref.spring.Food"></bean>
> ~~~

####  (3) 空值注入及null值注入

> ~~~xml
> <!-- 空值注入-->
> <bean id="person_with_empty_name" class="com.javaref.spring.Person">
> 	<property name="name"><value></value></property>
> </bean>
> 
> <!-- null值注入-->
> <bean id="person_with_null_name" class="com.javaref.spring.Person">
> 	<property name="name"><null></null></property>
> </bean>
> ~~~
>
> Demo：
>
> * [../spring_demos/02_mvn_and_setter_inject/src/main/resources/applicationContext.xml](../spring_demos/02_mvn_and_setter_inject/src/main/resources/applicationContext.xml)
> * [../spring_demos/02_mvn_and_setter_inject/src/main/java/com/javaref/spring/TestGetBean.java](../spring_demos/02_mvn_and_setter_inject/src/main/java/com/javaref/spring/TestGetBean.java)

#### (4) 注入List、Set、Map类型、以及注入到java.util.Properties类型的属性上

> ~~~xml
> <!-- 属性对象注入(<property ..>)，Spring会调起对象的set方法来完成DI -->
> <bean id="person" name="human,star" class="com.javaref.spring.Person">
> 		<!-- 三种定义属性值的方式：(1)<value>标签; (2)value属性；(3)引用其他bean -->
>  <property name="name"><value>Jerry</value></property>
>  <property name="age"  value="18"/>
>  <property name="food" ref="food"/>
> 
>  <!-- 注入一个List -->
>  <property name="list">
>      <array>
>          <value>a</value>
>          <value>b</value>
>          <value>c</value>
>      </array>
>  </property>
> 
>  <!-- 注入一个Set, 两个c会被去重 -->
>  <property name="set">
>      <array>
>          <value>a</value>
>          <value>b</value>
>          <value>c</value>
>          <value>c</value>
>      </array>
>  </property>
> 
>  <!-- 注入一个Map，两个b会被去重 -->
>  <property name="map">
>      <props>
>          <prop key="a">1</prop>
>          <prop key="b">2</prop>
>      </props>
>      <!-- <map>
>          <entry key="a" value="1"></entry>
>          <entry key="b" value="2"></entry>
>          <entry key="b" value="2"></entry>
>      </map> -->
>  </property>
> 
>  <!-- 如果属性的类型是java.util.Properties，可以采用下面的方法注入 -->
>  <!-- 对于注入来自文件的数据，Properties类会比较方便 -->
>  <property name="gift">
>      <!-- java代码中可以使用gift.getProperty("douban")来获得douban的值 -->
>      <value>
>              douban=http://douban.com/movie/xx.html
>              dushu=200
>      </value>
>  </property>
> </bean>
> ~~~
>
> ~~~java
> public class Person {
> 	private String name;
> 	private int age;
> 	private Food food;
> 	private Properties gift;
> 	private List<String> list;
> 	private Set<String> set;
> 	private Map<String, String> map;
> 	private String[] array;
>   
> 	public void setArray(String[] array) {
> 		this.array = array;
> 	}
> 	public void setList(List<String> list) {
> 		this.list = list;
> 	}
> 	……
> }
> ~~~
>
> Demo：
>
> [../spring_demos/03_inject_collection_and_life_cycle/src/main/resources/applicationContext.xml](../spring_demos/03_inject_collection_and_life_cycle/src/main/resources/applicationContext.xml)
>
> [../spring_demos/03_inject_collection_and_life_cycle/src/main/java/com/javaref/spring/Person.java](../spring_demos/03_inject_collection_and_life_cycle/src/main/java/com/javaref/spring/Person.java)
>
> [../spring_demos/03_inject_collection_and_life_cycle/src/main/java/com/javaref/spring/TestGetBean.java](../spring_demos/03_inject_collection_and_life_cycle/src/main/java/com/javaref/spring/TestGetBean.java)]

#### (5) 通过工厂来注入bean

##### 动态工厂 

> 注入时通过调用`工厂bean`的`non-static`方法来创建对象
>
> ~~~xml
> <!-- 先注入工厂Bean，再调用其成员函数（non-static）来注入Car bean -->
> <bean id="dynamicCarFactory" class="com.javaref.spring.CarFactory">
> 	<!-- 如果CarFactory有setAttrName方法，可以用property注入来调用该方法，如下 -->
> 	<!-- <property name="attrName" value="attrVal"></property>      -->
> </bean>
> 
> <!-- 再通过工厂bean来创建bean -->
> <bean id="car1" factory-bean="dynamicCarFactory" factory-method="getCar">
> 	<!-- 传给getCar方法参数name的值时"audi"，用于构造Car对象 -->
>   	<constructor-arg name="name" value="audi"/>
> </bean>
> ~~~
>
> 工厂的代码形如：
>
> ~~~java
> public class CarFactory {
>     public CarFactory() {
>         super();
>         System.out.println("new CarFactory()");
>     }
> 
>     public Car getCar(String name) throws Exception {
>       	// 这里可以封装更丰富的业务逻辑
>         return new Car();
>     }
>   	
>   	// 可以添加额外的属性和set方法
> }
> ~~~

##### 静态工厂 

> 注入时调用工厂类的静态方法创建对象
>
> ~~~xml
> <!-- 静态工厂 -->
> <!-- 注入bean car，该bean直接调用CarFactory.getCarStatic静态函数创建 -->
> <bean id="car2" class="com.javaref.spring.CarFactory" factory-method="getCarStatic" >
> 	<!-- 传给getCarStatic方法参数name的值时"audi"，用于构造Car对象 -->
> 	<constructor-arg value="audi"/>
> </bean>
> ~~~
>
> 工厂的代码静茹：
>
> ~~~java
> public class CarFactory {    
> 	public static Car getCarStatic(String name) throws Exception {
>       	// 这里可以封装更丰富的业务逻辑
>         return new Car();
>     }
> }
> ~~~
>
> Demo：
>
> * [../spring_demos/04_inject_with_factory/src/main/resources/applicationContext.xml](../spring_demos/04_inject_with_factory/src/main/resources/applicationContext.xml)
> * [../spring_demos/04_inject_with_factory/src/main/java/com/javaref/spring/TestGetBean.java](../spring_demos/04_inject_with_factory/src/main/java/com/javaref/spring/TestGetBean.java)
> * [../spring_demos/04_inject_with_factory/src/main/java/com/javaref/spring/CarFactory.java](../spring_demos/04_inject_with_factory/src/main/java/com/javaref/spring/CarFactory.java)

###  2.4 其他 

#### (1) `alias`: 为bean指定别名

> ~~~xml
> <bean id="person1" name="human,star" class="com.javaref.spring.Person">
> 	<!-- 可以给bean提供多个name，除了id，这些name也可以用于被java代码获取bean的对象 -->
> 	<!-- 三种指定属性值的方式：(1)value标签 (2)value属性 (3)引用另一个bean -->
> 	<property name="name"><value>Jerry</value></property>
> 	<property name="age" value="18" />
> 	<property name="food" ref="food" />
> </bean>
> <bean id="food" class="com.javaref.spring.Food"></bean>
> 
> <!-- 有时上面的bean是其他team维护的，遇到命名冲突时不方便修改，可以使用alias -->
> <alias name="person1" alias="person1_alias"/>
> ~~~
>
> ~~~java
> Person person1 = ctx.getBean("person1_alias", Person.class);
> System.out.println(ToStringBuilder.reflectionToString(person1));
> //输出：
> //com.javaref.spring.Person@6321e813[name=Jerry,age=18,food=com.javaref.spring.Food@3224f60b]
> ~~~
> 

####  (2) `lazy-init`和`depends-on`

> *  `lazy-init="true"`：bean延迟到使用时才创建
> *  `depends-on`：让一个bean在另一个bean之后创建
>
> ```xml
> <bean id="A1" class="com.javaref.spring.A"></bean>
> 
> <!-- 因为没有依赖注入（强引用），因此保证B在A之前初始化要通过depends-on来保证 -->
> <bean id="A2" class="com.javaref.spring.A" depends-on="B1"></bean>
> <bean id="B1" class="com.javaref.spring.B"></bean>
> 
> <!-- C1不会在Spring加载applicationContext.xml时初始化，而是延迟到getBean时再初始化 -->
> <bean id="C1" class="com.javaref.spring.C" lazy-init="true"></bean>
> ```
>
> ```java
> ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
> 
> A a1 = ctx.getBean("A1",A.class);
> System.out.println(a1);
> 
> A a2 = ctx.getBean("A2",A.class);
> System.out.println(a2);
> 
> C c1 = ctx.getBean("C1", C.class);
> System.out.println(c1);
> 
> // 输出: Spring加载applicationContext.xml后初始化A1（不依赖B1）
> // A depends on:  B.hasInitializedObject = false //是否被初始化B1取决于Spring的初始化顺序
> // A init.
> // 输出：Spring加载applicationContext.xml后初始化A2（depends-on="B1")
> // B init.
> // A depends on:  B.hasInitializedObject = true  //强制B1在A1之前初始化
> // A init.
> // 输出：前两个System.out.println
> // com.javaref.spring.A@27c20538
> // com.javaref.spring.A@72d818d1
> // 输出: 因为C1设置了lazy-init="true"，因此它延迟到getBean时才初始化
> // C init.
> // com.javaref.spring.C@59494225
> ```
>
> Demo：
>
> * [../spring_demos/06_bean_dependson_and_lazyinit/src/main/resources/applicationContext.xml](../spring_demos/06_bean_dependson_and_lazyinit/src/main/resources/applicationContext.xml)
> * [../spring_demos/06_bean_dependson_and_lazyinit/src/main/java/com/javaref/spring/TestGetBean.java](../spring_demos/06_bean_dependson_and_lazyinit/src/main/java/com/javaref/spring/TestGetBean.java)

## 3 自动装配

### 3.1 基于XML的自动装配

#### (1) 解决的问题

> 解决项目变大以后配置内容庞大的问题
>
> 使用自动装配，只需要指定为哪些bean来自动装配他们的依赖项，而不需要配置具体的依赖关系，避免配置文件变得庞大

#### (2) 哪些Bean的依赖可被自动装配

#####  所有bean

> `<beans>`标签的`default-autowire`属性设为`byType`或`byName`
>
> ```xml
> <?xml version="1.0" encoding="UTF-8"?>
> <beans xmlns="http://www.springframework.org/schema/beans"
>        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd"
>        default-autowire="byType"
> >
>     ...
> </beans>
> ```

##### 特定的bean

> <bean>标签的`autowire`属性设为`byType`或`byName`，例如
>
> ```xml
> <bean id="beanD" class="com.javaref.spring.D" autowire="byType"></bean>
> ```

#### (3) 如何找到依赖项

##### `byType`

要求：每种被依赖的class只能生成一个bean

> ```xml
> <!-- autowire="byName"要求dependent bean的id (singleTonB/C)与成员变量（以及get,set方法）命名相一致 -->
> <bean id="beanD" class="com.javaref.spring.D" autowire="byType"></bean>
> <bean id="beanE" class="com.javaref.spring.E" autowire="byType"></bean>
> <bean id="beamF" class="com.javaref.spring.F" autowire="byType"></bean>
> ```

##### `byName`

要求：因为是通过属性注入的，因此要求`dependent bean的id`与`成员变量（以及get,set方法）`命名相一致。这样框架才能够进行名称匹配

> ```xml
> <!-- autowire="byName"要求dependent bean的id (singleTonB/C)与成员变量（以及get,set方法）命名相一致 -->
> <bean id="singletonA" class="com.javaref.spring.A" autowire="byName"></bean>
> <bean id="singletonB" class="com.javaref.spring.B" autowire="byName"></bean>
> <bean id="singletonC" class="com.javaref.spring.C" autowire="byName"></bean>
> ```

#### (4) 代码

> * [../spring_demos/07_bean_autowire_01_by_xml/src/main/resources/applicationContext.xml](../spring_demos/07_bean_autowire_01_by_xml/src/main/resources/applicationContext.xml)
>
> * [../spring_demos/07_bean_autowire_02_by_annotation/src/main/java/com/javaref/spring/TestGetBean.java](../spring_demos/07_bean_autowire_02_by_annotation/src/main/java/com/javaref/spring/TestGetBean.java)

### 3.2 基于注解的自动装配

#### (1) 简单例子

开启自动注解，并指定bean扫描范围

> ```xml
> <?xml version="1.0" encoding="UTF-8"?>
> <!-- 需要引入命名空间xmlns:context，并且在schemaLocation中指定context相关的配置 -->
> <beans xmlns="http://www.springframework.org/schema/beans"
>    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>    xmlns:p="http://www.springframework.org/schema/p"
>    xmlns:context="http://www.springframework.org/schema/context"
>    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd"
>    >
>    <!-- 指定component-scan的package，该package下的带有Component标签(或其他实现了@Component的标签）的类，会纳入spring管理 -->
>    <!-- 也可以在base-package中添加多个包 -->
>    <context:component-scan base-package="com.javaref.spring"></context:component-scan>
> </beans>
> ```

在定义bean的位置，用@Component，或者`@Controller`、`@Repository`、`@Service`等具化的注解来标注，例如

> ```java
> @Repository("daoMysql")
> public class UserDaoMysqlImpl implements UserDao {
>    @Autowired()
>    User user;
>    
>    public User getUserByName(String name) {
>       ...
>    }
> }
> ```
>
> ```java
> @Repository("daoSS")
> public class UserDaoSqlServerImpl implements UserDao {
>    ...
> }
> ```

在需要注入的位置，使用`@Autowire`注解来自动装配

> ```java
> @Service
> public class MainService {
>    @Autowired
>    @Qualifier("daoMysql")
>    UserDao dao;
>    ...
> }
> ```

Demo:

> [../spring_demos/07_bean_autowire_02_by_annotation/](../spring_demos/07_bean_autowire_02_by_annotation/pom)

## 4 Bean的作用域

6种作用域 ：`singleton`，`prototype`，`websocket`，`request`，`session`，`application`

首先是`singleton`，`prototype`

> * `singleton`：单例
> * `prototyp`：每次从容器获取bean时都会new一个新的对象

接下来的4种其实也是单例的、只是单例存续的生命周期不同

> * `websocket`：网页长连接（浏览器到服务器）内单例
> * `request`：请求处理周期内单例，从`Controller`接到请求到返回`View`给用户
> * `session`：用户登录后产生的session内的单例
> * `application` ：Spring的隔离Application环境（多个ApplicationContext时）下，Application内的单例

MVC中`Controller`是单例的，`Model`中的`Service`、`DAO`等等也是单例，只有pojo，entity bean是多例的

设置bean的作用域：

> ~~~xml
> <!-- bean默认是Singleton，使用scope属性可以更改bean的作用域 -->
> <bean id="person_proto" scope="prototype" class="com.javaref.spring.Person">
> 	<property name="name"><value>Prototype</value></property>
> 	<property name="age"  value="18"/>
> 	<property name="food" ref="food"/>
> </bean>
> ~~~

Demo：

> * [../spring_demos/03_inject_collection_and_life_cycle/src/main/resources/applicationContext.xml](../spring_demos/03_inject_collection_and_life_cycle/src/main/resources/applicationContext.xml)
> * [../spring_demos/03_inject_collection_and_life_cycle/src/main/java/com/javaref/spring/TestGetBean.java](../spring_demos/03_inject_collection_and_life_cycle/src/main/java/com/javaref/spring/TestGetBean.java)

## 5 Bean循环依赖问题 

循环依赖：以对象为例、循环依赖演示如下，Bean循环依赖也类似

> ~~~java
> A a1 = new A();
> B b1 = new B();
> C c1 = new C();
> a1.setB(b1);
> b1.setC(c1);
> c1.setA(a1);
> ~~~
>
> 如果是在容器中，这样的循环依赖
>
> * 在容器初始化时（假如不是通过set方法、而是构造函数，则容器初始化时会发生“鸡生蛋、蛋生鸡”式的循环等待、谁都无法初始化）
> * 在bean销毁时，他们的引用计数始终大于1，也不便于bean的销毁
>
> 因此Spring在创建bean的时候，会对发生循环依赖的bean进行检查

循环依赖检查规则1：通过构造器注入的一组bean发生循环依赖：不论是singleton还是prototype都不容许

循环依赖检查规则2：相互依赖的bean都是`prototype`或者都是`singlton`的情况（依赖关系全部使用属性注入）

> * 通过属性注入的一组bean：如果都是singleton时容许注入；如果都是prototype不容许
>
> ~~~xml
> <!-- 3个singleton bean循环依赖，通过属性注入：容许创建 -->
> <bean id="singletonA1" class="com.javaref.spring.A">
> 	<property name="b" ref="singletonB1"></property>
> </bean>
> <bean id="singletonB1" class="com.javaref.spring.B">
> 	<property name="c" ref="singletonC1"></property>
> </bean>
> <bean id="singletonC1" class="com.javaref.spring.C">
> 	<property name="a" ref="singletonA1"></property>
> </bean>
> ~~~
>
> ~~~java
> ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
> // 如果prototype bean之间发生循环引用，Spring会阻止这些bean的初始化
> // 如果是singleton bean并使用属性注入，Spring可以初始化这些bean
> A singletonA1_1 = ctx.getBean("singletonA1", A.class);
> System.out.println(ToStringBuilder.reflectionToString(singletonA1_1));
> System.out.println(ToStringBuilder.reflectionToString(singletonA1_1.getB()));
> System.out.println(ToStringBuilder.reflectionToString(singletonA1_1.getB().getC()));
> // 输出：
> // com.javaref.spring.A@544fe44c[b=com.javaref.spring.B@3327bd23]
> // com.javaref.spring.B@3327bd23[c=com.javaref.spring.C@4e1d422d]
> // com.javaref.spring.C@4e1d422d[a=com.javaref.spring.A@544fe44c]
> ~~~

循环依赖检查规则2：相互依赖的bean有的是`singleton`有的是`prototype`（依赖关系全部使用属性注入）

> * 当被依赖的Bean是singleton bean时，这些单例bean能够直接实例化，而不用等待其他依赖
> * 当被依赖的Bean是prototype bean时，这些bean要等待dependencies先实例化完毕（循环依赖时可能会导致bean无法实例化，也可能不会）
>
> ```xml
> <!-- 1个singleton bean依赖2个prototype bean，其中一个prototype bean又依赖这个singleton bean -->
> <bean id="singletonA2" class="com.javaref.spring.A">
>     <property name="b" ref="singletonB1"></property>
> </bean>
> <bean id="prototypeB1" class="com.javaref.spring.B" scope="prototype">
>     <property name="c" ref="singletonC1"></property>
> </bean>
> <bean id="prototypeC1" class="com.javaref.spring.C" scope="prototype">
>     <property name="a" ref="singletonA1"></property>
> </bean>
> ```
>
> ```java
> A singletonA2_1 = ctx.getBean("singletonA2", A.class);
> System.out.println(singletonA2_1);
> System.out.println(singletonA2_1.getB());
> System.out.println(singletonA2_1.getB().getC());
> A singletonA2_2 = ctx.getBean("singletonA2", A.class);
> System.out.println(singletonA2_2);
> System.out.println(singletonA2_2.getB());
> System.out.println(singletonA2_2.getB().getC());
> System.out.println(singletonA2_1 == singletonA2_2);
> System.out.println(singletonA2_1.getB() == singletonA2_2.getB());
> // 输出：A是单例，B、C是被A引用的prototype
> // com.javaref.spring.A@66480dd7  // A是单例，先被初始化
> // com.javaref.spring.B@3327bd23  // B是prototype，依赖prototype C，等C先初始化完毕之后，B再初始化
> // com.javaref.spring.C@4e1d422d  // C是prototype，依赖singleton A，A已经初始化完毕
> // com.javaref.spring.A@66480dd7  // 第二轮：A是单例，A已经初始化完毕，直接从注册表中取出，不再需要初始化
> // com.javaref.spring.B@3327bd23  // B是prototype，但是来自单例A，不需要再初始化
> // com.javaref.spring.C@4e1d422d  // C是prototype，但是来自单例A，不需要再初始化
> // true
> // true  // B虽然是prototype，但是都来自于单例A，因此对象地址的hashCode都相同，要小心多线程问题
> ```

Demo

> * [../spring_demos/05_circular_reference/src/main/resources/applicationContext.xml](../spring_demos/05_circular_reference/src/main/resources/applicationContext.xml)
> * [../spring_demos/05_circular_reference/src/main/java/com/javaref/spring/TestGetBean.java](../spring_demos/05_circular_reference/src/main/java/com/javaref/spring/TestGetBean.java)

##  6 编码小技巧 

###  (1) `ToStringBuilder`：为对象提供`toString`代码支持

maven依赖

> ~~~xml
> <!-- 来自https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
> <dependency>
> 	<groupId>org.apache.commons</groupId>
>   <artifactId>commons-lang3</artifactId>
>   <version>3.9</version>
> </dependency>
> ~~~

代码

> ~~~java
> Person person2 = ctx.getBean("person2", Person.class);
> System.out.println(ToStringBuilder.reflectionToString(person2));
> //输出：
> //com.javaref.spring.Person@63e2203c[name=Tom,age=21,food=com.javaref.spring.Food@1efed156]
> 
> ~~~



