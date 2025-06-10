# Spring Bean生命周期及代码编写

## 概述

| 步骤                         | 用途及方法                                                   |
| ---------------------------- | ------------------------------------------------------------ |
| 1.实例化                     | 用途：创建Bean对象                                           |
| 2.属性赋值                   | 用途：依赖注入                                               |
| 3.设置Bean Aware接口         | 用途：给Bean提供额外支持<br />例如：1. 注入Bean Factory；2. 获得Bean Name；3. 获得容器信息等 |
| 4.Bean PostProcessor前置处理 | 用途：所有Bean在初始化之前执行统一操作<br />方法：装配一个实现了BeanPostProcessor接口的Bean |
| 5.Bean 初始化                | 用途：自定义某个Bean初始化逻辑的途径<br />方法：1. 让Bean实现InitializingBean接口；2. 为Bean设置init-method属性 |
| 6.Bean PostProcessor后置处理 | 用途：所有Bean执行初始化之后执行统一操作<br />方法：1. 装配一个实现了BeanPostProcessor接口的Bean |
| 7.使用                       |                                                              |
| 8.Bean销毁                   | 用途：自定义Bean的销毁逻辑<br />方法：1. 让Bean实现DisposableBean接口；2. 为Bean设置destroy-method属性 |

## 步骤1：实例化

使用构造函数创建一个Bean实例

### 1：装配Bean的三种写法

下面的例子，装配一个名称为beanB的Bean，并将它注入到beanA中

#### (1) 使用注解

例如@Component, @Repository, @Service, @Controller等

~~~java
@Component
public class BeanA {
    @Autowired
    private BeanB beanB;
}
~~~

#### (2) 使用@Configuration类

~~~java
@Configuration
public class AppConfig {
    @Bean
    public BeanA beanA(@Autowired BeanB beanB) {
        return new BeanA(beanB);
    }
}
~~~

#### (3) 使用XML配置

~~~xml
<bean id="beanA" class="com.example.BeanA">
    <property name="beanB" ref="beanB"/>
</bean>
<bean id="beanB" class="com.example.BeanB"/>
~~~

### 2：创建Bean的五种方法

创建Bean有以下几种方法：1. 使用无参构造方法；2. 使用推断构造方法；3. 使用静态工厂方法；4. 使用实例工厂方法；5. BeanDefinitionRegistryPostProcessor动态注册Bean

#### (1) 使用无参构造方法

~~~java
public class BeanA {
    public BeanA() {
        // 无参构造方法
    }
}
~~~

#### (2) 使用推断构造方法

~~~java
public class BeanA {
    private final BeanB beanB;

    public BeanA(BeanB beanB) {
        this.beanB = beanB;
    }
}
~~~

#### (3) 使用静态工厂方法

Spring也可以使用静态工厂方法来实例化Bean。这需要在配置类中使用`@Bean`注解定义工厂方法，并使用`factory-method`属性指定静态工厂方法名。

~~~java
public class BeanA {
    private final BeanB beanB;

    public BeanA(BeanB beanB) {
        this.beanB = beanB;
    }

    public static BeanA create(BeanB beanB) {
        return new BeanA(beanB);
    }
}

@Configuration
public class AppConfig {
    @Bean
    public BeanA beanA(BeanB beanB) {
        return BeanA.create(beanB);
    }
}
~~~

#### (4) 使用实例工厂方法

Spring还可以使用实例工厂方法来实例化Bean。这需要在配置类中使用`@Bean`注解定义工厂方法，并使用`factory-bean`属性指定工厂Bean，以及`factory-method`属性指定实例工厂方法名。例如：

~~~java
public class BeanA {
    private final BeanB beanB;

    public BeanA(BeanB beanB) {
        this.beanB = beanB;
    }

    public BeanA create(BeanB beanB) {
        return new BeanA(beanB);
    }
}

@Configuration
public class AppConfig {
    @Bean
    public BeanA beanA(BeanB beanB) {
        return new BeanA(beanB);
    }

    @Bean
    public BeanB beanB() {
        return new BeanB();
    }
}
~~~

在这个例子中，`BeanA`类和`BeanB`类都有各自的构造方法。`AppConfig`配置类中，使用`@Bean`注解定义了两个Bean：`beanA`和`beanB`。`beanA`Bean的实例化过程会触发`beanB`的实例化，因为它依赖于`beanB`。

#### (5) 使用BeanDefinitationRegistryPostProcessor动态注册Bean

BeanDefinitionRegistryPostProcessor是Spring提供的一种扩展点，用于在Bean定义注册到容器之前，对Bean定义进行修改。我们可以实现这个接口，并在postProcessBeanDefinitionRegistry方法中动态注册Bean定义。

在下面的例子中，我们定义了一个`MyBeanDefinitionRegistryPostProcessor`类，它动态注册了一个名为`beanA`的Bean。这个Bean的类型是`BeanA`，它将在Spring容器启动时被实例化。

~~~java
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        RootBeanDefinition beanADefinition = new RootBeanDefinition(BeanA.class);
        registry.registerBeanDefinition("beanA", beanADefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 不需要修改
    }
}
~~~

`BeanDefinitionRegistryPostProcessor`通常在以下场合中使用：

| 用途                     | 说明                                                         |
| ------------------------ | ------------------------------------------------------------ |
| 动态注册Bean             | 根据环境或配置在运行时动态注册Bean，例如根据数据库配置动态创建数据库连接池Bean |
| 修改Bean定义             | 在Spring容器启动前修改Bean的定义，例如在启动前修改Bean的依赖关系或作用域 |
| 替换Bean                 | 在Spring容器中替换某个Bean的实现，将某个Bean的实现类替换为另一个类 |
| 动态添加或修改Bean的属性 | 在Spring容器启动前动态添加或修改Bean的属性，<br />例如在启动前为Bean添加一个属性值，或者修改一个属性的值 |
| 注册Spring组件           | 动态注册Spring组件（如`@Component`、`@Service`、`@Repository`、`@Controller`等），可以实现BeanDefinitionRegistryPostProcessor来扫描类路径中的组件并动态注册它们 |

总之，`BeanDefinitionRegistryPostProcessor`提供了在Spring容器启动前修改Bean定义的能力，可以用于实现各种动态配置和扩展功能。

## 步骤2：属性赋值

根据BeanDefinition中的信息，将Bean的属性值注入到Bean实例中

### 1：指定要注入的Bean（使用@Autowired注解时）



### 2：指定要注入的Bean（使用@Configuration类时）



### 3：避免循环依赖



## 步骤3：设置Aware接口

如果Bean实现了`org.springframework.beans.factory.Aware`接口，Spring会调用相应的setter方法，将对应的信息注入到Bean中。在Spring中，有许多Aware接口，它们提供了一种方式来让Bean获取容器中的特定信息。

| 接口                           | 注入的信息                                                   |
| ------------------------------ | ------------------------------------------------------------ |
| BeanFactoryAware               | 让Bean获取所属的BeanFactory，从而访问容器中的其他Bean        |
| ApplicationContextAware        | 让Bean获取所属的ApplicationContext，从而访问容器中的其他Bean、配置文件等信息 |
| ResourceLoaderAware            | 让Bean获取ResourceLoader，用于访问外部资源文件。             |
| ServletContextAware            | 让Bean获取ServletContext，用于访问Servlet上下文中的信息。    |
| ServletConfigAware             | 让Bean获取ServletConfig，用于访问Servlet配置信息。           |
| PortletContextAware            | 让Bean获取PortletContex，用于访问Portlet上下文中的信息。     |
| PortletConfigAware             | 让Bean获取PortletConfig，用于访问Portlet配置信息             |
| MessageSourceAware             | 让Bean获取MessageSource，用于获取国际化信息。                |
| ApplicationEventPublisherAware | 让Bean获取ApplicationEventPublisher，用于发布应用事件。      |
| BeanNameAware                  | 让Bean获取其在容器中的名称。                                 |
| BeanClassLoaderAware           | 让Bean获取其类的类加载器。                                   |

需要注意的是，实现Aware接口会让Bean与Spring容器耦合，从而影响代码的可重用性和可测试性。因此，在实现Aware接口时，要考虑是否真的有必要

## 步骤4：BeanPostProcessor前置处理（BeanPostProcessor Before Initialization）

通过实现org.springframework.beans.factory.BeanPostProcessor接口，可以装配一个Bean，封装对容器中其他Bean的统一处理方法

~~~java
// 定义一个Post Processor
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(
        Object bean, String beanName) throws BeansException {
        // 为所有类型为MyBean的Bean，在它们初始化之前，设置DefaultValue
        if (bean instanceof MyBean) {
            ((MyBean) bean).setDefaultValue("Default Value");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(
        Object bean, String beanName) throws BeansException {
        return bean;
    }
}
~~~

~~~java
// 用@Configuration来装配这个Bean，而不是使用@Component注解
// 这样同事在读代码时可以找到这个Post Processor
@Configuration
public class AppConfig {
    @Bean
    public MyBeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
~~~

## 步骤5：初始化（Initialization）

在Initialization步骤中，可以自定义某个Bean的初始化逻辑，Spring Framework提供了两方法

### 1：方法1 - 定义initMethod属性

~~~java
@Getter
@Setter
public class MyBean {
    private String name;

    // 自定义初始化方法
    public void myInit() {
        System.out.println("Custom initialization for Bean: " + this.getName());
    }
}
~~~

装配Bean的时候，指定myInit为它的initMethod，Spring在Bean Initialization阶段调用该方法，程序会输出“Custom initialization for Bean: myBean”

~~~java
@Configuration
public class AppConfig {
    @Bean(initMethod = "myInit")
    public MyBean myBean() {
        return new MyBean();
    }
}
~~~

### 2：方法2 - 实现InitializingBean接口

如果Bean实现了org.springframework.beans.factory.InitializingBean接口，Spring会在Bean Initialization阶段调用它的afterPropertiesSet()方法

~~~java
@Setter
@Getter
@Component
public class MyBean implements InitializingBean {
    private String name;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 程序会输出“Custom initialization for Bean: myBean”
        System.out.println("Custom initialization for Bean: " + this.getName());
    }
}
~~~

## 步骤6：BeanPostProcessor后置处理（BeanPostProcessor After Initialization）

如果Bean实现了org.springframework.beans.factory.BeanPostProcessor接口，Spring会调用postProcessAfterInitialization()方法，对Bean进行一些后置处理。

~~~java
// 定义一个post processor
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(
        Object bean, String beanName) throws BeansException {
        // 为所有类型为MyBean的Bean，在它们初始化之前，设置DefaultValue
        if (bean instanceof MyBean) {
            ((MyBean) bean).setDefaultValue("Default Value");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 为所有类型为MyBean的Bean，在它们初始化之后，执行
        if (bean instanceof MyBean) {
			System.out.println(String.fromat(
                "BeanPostProcessor: post-initialization proc with default value: %s",
                (MyBean) bean.getDefaultValue();
            );
        }
        return bean;
    }
}
    
// 用@Configuration来装配这个Bean，而不是使用@Component注解
// 这样同事在读代码时可以找到这个Post Processor
@Configuration
public class AppConfig {
    @Bean
    public MyBeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
~~~

## 步骤7：使用（Usage）

此时，Bean已经准备就绪，可以被应用程序使用。

## 步骤8：Bean销毁（Destruction）

当Bean不再需要时，Spring会负责销毁它。Spring Framework提供了两种方法：

### 1：设置destroyMethod属性

Spring会检测Bean是否定义了destroyMethod属性，如果定义了，则调用指定的销毁方法

~~~java
@Getter
@Setter
public class MyBean {
    private String name;

    public void destroy() {
        System.out.println("Destroying Bean: " + this.getName());
    }
}

@Configuration
public class AppConfig {
    @Bean(destroyMethod = "destroy")
    public MyBean myBean() {
        return new MyBean();
    }
}
~~~

### 2：实现DisposableBean接口 

如果Bean实现了org.springframework.beans.factory.DisposableBean接口，Spring会调用destroy()方

~~~java
@Getter
@Setter
@Component
public class MyBean implements DisposableBean {
    private String name;

    @Override
    public void destroy() throws Exception {
        System.out.println("Custom cleanup for Bean: " + this.getName());
    }
}
~~~



