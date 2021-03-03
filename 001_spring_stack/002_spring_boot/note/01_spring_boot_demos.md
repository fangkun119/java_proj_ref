#  Spring Boot示例

[TOC]

## 01 Spring Boot

Spring Boot项目创建

> 使用http://start.spring.io/，或使用与之类似的IDE向导

热部署

> (1) 如果使用Spring官网提供STS作为IDE，引入`spring-boot-devtools`依赖后，可以在代码修改后立刻重启
>
> ~~~xml
> <dependency>
>    <groupId>org.springframework.boot</groupId>
>    <artifactId>spring-boot-devtools</artifactId>
> </dependency>
> ~~~
>
> (2) 使用`JRabel`，可以做到真正的热部署

Spring项目结构

> 项目位置：[../demos/03_spring_boot_proj_structure/](../demos/03_spring_boot_proj_structure/)

有多个main方法时，指定使用哪一个

> ```xml
> <build>
>    <plugins>
>       <plugin>
>          <groupId>org.springframework.boot</groupId>
>          <artifactId>spring-boot-maven-plugin</artifactId>
>          <configuration>
>             <mainClass>com.javaref.springboot02.FirstSpringBoot02Application</mainClass>
>          </configuration>
>       </plugin>
>    </plugins>
> </build>
> ```

## 02 Spring MVC

### 2.1 Demo

> 代码位置：[../demos/demos/04_spring_boot_mvc_thymeleaf/](../demos/demos/04_spring_boot_mvc_thymeleaf/)
>
> 项目结构：
>
> * Controller，Service，DAO，Thymeleaf模板
>
> * 传递数据给Thymeleaf

### 2.2 常用表单数据接收方式

#### Path Variable

> ~~~java
> @GetMapping(value = "/hello/{id}")
> public String hello(@PathVariable("id") Integer id){
> 	return "ID:" + id;
> }
> ~~~

#### url参数名

> ~~~java
> @PostMapping(value = "/post")
> public String post(@RequestParam(name = "name") String name,
>                    @RequestParam(name = "age") Integer age) {
>     String content = String.format("name = %s,age = %d", name, age);
>     return content;
> }
> ~~~

#### 普通实体对象

> ~~~java
> @PostMapping(value = "/user")
> public User saveUser2(User user) {
>     return user;
> }
> ~~~

#### Json数据

> ~~~java
> @PostMapping(value = "/user")
> public User saveUser2(@RequestBody User user) {
>     return user;
> }
> ~~~

### 2.3 后端参数校验

#### 例子

> ~~~java
> @Getter
> @Setter
> @ToString
> public class Person {
>     @NotNull
>     private String name;
>     
>     @NotNull
>     @Positive
>     private Integer age;
> 
>     @Valid // 让InnerChild的属性也参与校验
>     @NotNull
>     private InnerChild child;
> 
>     @Getter
>     @Setter
>     @ToString
>     public static class InnerChild {
>         @NotNull
>         private String name;
>         @NotNull
>         @Positive
>         private Integer age;
>     }
> }
> 
> @RestController
> @RequestMapping
> public class HelloController {
>     @PostMapping("/hello")
>     public Object helloPost(@Valid @RequestBody Person person, BindingResult result) {
>         System.out.println(result.getErrorCount());
>         System.out.println(result.getAllErrors());
>         return person;
>     }
> }
> ~~~

#### 常用注解

> `@NotBlank`判断字符创是否是null或者是空串(去掉首尾空格)。
>
> `@NotEmpty`判断字符串是否为null会者是空串。
>
> `@Length`判断字符的长度（最大或最小）
>
> `@Min`判断数值最小值
>
> `@Max`判断数值最大值
>
> `@Email`判断邮箱是否合法
>
> `@Null`限制只能为null
>
> `@NotNull`限制必须不为null
>
> `@AssertFalse`限制必须为false
>
> `@AssertTrue`限制必须为true
>
> `@DecimalMax(value)`限制必须为一个不大于指定值的数字
>
> `@DecimalMin(value)`限制必须为一个不小于指定值的数字
>
> `@Digits(integer,fraction)`限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fractio
>
> `@Max(value)`限制必须为一个不大于指定值的数字
>
> `@Min(value)`限制必须为一个不小于指定值的数字
>
> `@Future`限制必须是一个将来的日期
>
> `@Past`限制必须是一个过去的日期
>
> `@Pattern(value)`限制必须符合指定的正则表达式
>
> `@Size(max,min)`限制字符长度必须在min到max之间
>
> `@NotEmpty`验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0）
>
> `@NotBlank`验证注解的元素值不为空（不为null、去除首尾空格后长度不为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格
>
> `@Email`验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式

## 03 用JPA实现DAO

### 3.1 Demo

代码位置：[../demos/05_spring_boot_mvc_thymeleaf_jpa/](../demos/05_spring_boot_mvc_thymeleaf_jpa/)

代码概要：

> 依赖：[pom.xml](../demos/05_spring_boot_mvc_thymeleaf_jpa/pom.xml)
>
> ```xml
> <!-- 用JPA连接MySQL来实现DAO-->
> <dependency>
> 	<groupId>org.springframework.boot</groupId>
> 	<artifactId>spring-boot-starter-data-jpa</artifactId>
> </dependency>
> <dependency>
> 	<groupId>mysql</groupId>
> 	<artifactId>mysql-connector-java</artifactId>
> 	<scope>runtime</scope>
> </dependency>
> ```
>
> Entity：[/src/main/java/.../entity/City.java](../demos/05_spring_boot_mvc_thymeleaf_jpa/src/main/java/com/javaref/springbootmvc01/entity/City.java)
>
> ```java
> // 想让JPA能够识别并处理City类，需要增加相关的javax.persistence.*注解
> @Entity
> @Table(name = "city")
> public class City {
> 	@Id
> 	@GeneratedValue(strategy = GenerationType.IDENTITY)
> 	private Integer id;
> 	private String name;
> 	...    
> }
> ```
>
> DAO：[/src/main/java/.../dao/CityRepository.java](../demos/05_spring_boot_mvc_thymeleaf_jpa/src/main/java/com/javaref/springbootmvc01/dao/CityRepository.java)
>
> ~~~java
> // 继承JpaRepository之后，会得到很多内置的DB查询方法，供service层的代码使用
> // 不需要加类似@Repository的注解，因为JpaRepository有@NoRepositoryBean注解，该注解一层层点上去，最终可以找到@Repository注解
> public interface CityRepository extends JpaRepository<City, Integer> {
> }
> ~~~
>
> Service：[src/main/java/.../service/CityService.java](../demos/05_spring_boot_mvc_thymeleaf_jpa/src/main/java/com/javaref/springbootmvc01/service/CityService.java)
>
> 使用JPA后、DAO会继承大量的方法，可以在Service层中使用
>
> ~~~java
> @Service
> public class CityService {
>     // 使用JPA，这里要指向一个CityRepository接口，而不是类
>     @Autowired
>     CityRepository cityRepo;
> 
>     public List<City> findAll() {
>         // CityRepository extends JpaRepository<City, Integer>之后，具有findAll方法
>         // JpaRepository<City, Integer>是个接口
>         // 该接口的方法由JPA来实现
>         return cityRepo.findAll();
>     }
> 
>     public City findOne(Integer id) {
>         return cityRepo.getOne(id);
>     }
> }
> ~~~
>
> Controller：[/src/main/java/.../controller/MainController.java](../demos/05_spring_boot_mvc_thymeleaf_jpa/src/main/java/com/javaref/springbootmvc01/controller/MainController.java)
>
> DataSource：[/src/main/resources/application.properties](../demos/05_spring_boot_mvc_thymeleaf_jpa/src/main/resources/application.properties)
>
> ```properties
> spring.datasource.url=jdbc:mysql://localhost:3306/dbname1?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
> spring.datasource.username=root
> spring.datasource.password=12345678
> ```

### 3.2 JAP常用API

> ~~~txt
> And     	--- 等价于 SQL 中的 and 关键字，比如 findByUsernameAndPassword(String user, Striang pwd)；
> Or      	--- 等价于 SQL 中的 or 关键字，比如 findByUsernameOrAddress(String user, String addr)；
> Between  	--- 等价于 SQL 中的 between 关键字，比如 findBySalaryBetween(int max, int min)；
> LessThan 	--- 等价于 SQL 中的 "<"，比如 findBySalaryLessThan(int max)；
> GreaterThan --- 等价于 SQL 中的">"，比如 findBySalaryGreaterThan(int min)；
> IsNull   	--- 等价于 SQL 中的 "is null"，比如 findByUsernameIsNull()；
> IsNotNull 	--- 等价于 SQL 中的 "is not null"，比如 findByUsernameIsNotNull()；
> NotNull 	--- 与 IsNotNull 等价；
> Like    	--- 等价于 SQL 中的 "like"，比如 findByUsernameLike(String user)；
> NotLike 	--- 等价于 SQL 中的 "not like"，比如 findByUsernameNotLike(String user)；
> OrderBy 	--- 等价于 SQL 中的 "order by"，比如 findByUsernameOrderBySalaryAsc(String user)；
> Not     	--- 等价于 SQL 中的 "！ ="，比如 findByUsernameNot(String user)；
> In      	--- 等价于 SQL 中的 "in"，比如 findByUsernameIn(Collection<String> userList) ，方法的参数可以是 Collection 类型，也可以是数组或者不定长参数；
> NotIn   	--- 等价于 SQL 中的 "not in"，比如 findByUsernameNotIn(Collection<String> userList) ，方法的参数可以是 Collection 类型，也可以是数组或者不定长参数；
> ~~~
>
> 还可以使用`@Qurey`注解为`添加定制查询`，参考小节`5.2 JPA定制查询`

## 04 JSP 

使用JSP来生成前端页面，而非Thymeleaf

代码位置：[../demos/06_spring_boot_mvc_jsp_jpa/](../demos/06_spring_boot_mvc_jsp_jpa/)

> 依赖：[/pom.xml](../demos/06_spring_boot_mvc_jsp_jpa/pom.xml)
>
> ```xml
> <!-- 如果引入thymeleaf，Controller返回的string会被当做thyemleaf模板来处理，而不会当做JSP -->
> <!--
> <dependency>
>    	<groupId>org.springframework.boot</groupId>
>    	<artifactId>spring-boot-starter-thymeleaf</artifactId>
> </dependency>
> -->
> 
> <!-- 添加JSP要用到的依赖, Spring Boot没有针对JSP的Starter，需要手动添加 -->
> <dependency>
>    	<groupId>javax.servlet</groupId>
>    	<artifactId>jstl</artifactId>
> </dependency>
> <dependency>
>    	<groupId>org.apache.tomcat.embed</groupId>
>    	<artifactId>tomcat-embed-jasper</artifactId>
>    	<scope>provided</scope>
> </dependency>
> ```
>
> 配置：[/src/main/resources/application.properties](../demos/06_spring_boot_mvc_jsp_jpa/src/main/resources/application.properties)
>
> ```properties
> # 静态文件目录约定：Spring Boot默认
> #   先在src/main/webapp/下查找静态文件，例如"src/main/webapp/accessable.html"可以通过http://localhost:8080/accessable.html被访问到
> #   再在src/main/resources/static/下查找静态文件
> 
> # JSP不是模板、无法放在src/main/resources/templates中让框架自动加载，需要配置JSP文件的路径
> # src/main/webapp/WEB-INF/目录下的文件被tomcat保护，不能像访问静态文件那样直接访问
> # 可以在这个目录下存放JSP文件
> spring.mvc.view.prefix=/WEB-INF/jsp/
> spring.mvc.view.suffix=.jsp
> ```
>
> JSP模板：[/src/main/webapp/WEB-INF/jsp/one_jsp.jsp](../demos/06_spring_boot_mvc_jsp_jpa/src/main/webapp/WEB-INF/jsp/one_jsp.jsp)
>
> Controller
>
> ```java
> @Controller
> @RequestMapping("/city")
> public class MainController {
>    	@Autowired
>    	CityService citySrv;
>    	// 测试url：localhost:8080/city/10
>    	@RequestMapping("{id}")
>    	public String getOne(@PathVariable("id") Integer id, Model map) {
>    		City city = citySrv.findOne(id);
>    		map.addAttribute("city", city);
> 
>    		// 如果使用thymeleaf（在pom.xml中引入spring-boot-starter-thymeleaf）
>    		// 返回"one"，Spring Boot会用src/main/resourcestemplates/one.html作为模板来生成返回页面
>    		// return "one";
> 
>    		// 如果使用JSP
>    		// 1. 在pom.xml中不引入spring-boot-starter-thymeleaf，它会去templates下查找thymeleaf模板
>    		// 2. 在pom.xml中引入jstl, tomcat-embed-jasper，开启对JSP的支持
>    		// 3. 在application.properties中配置
>    		//      spring.mvc.view.prefix=/WEB-INF/jsp/
>    		//      spring.mvc.view.suffix=.jsp
>    		return "one_jsp"; //Spring Boot会用src/main/web-app/WEB-INF/jsp/one_jsp.jsp来生成返回页面
>    	}
>    	...
> }
> ```

## 05 Bootstrap, Thymeleaf, JPA定制查询

> (1) 使用Thymeleaf + Boostrap生成Web页面
>
> (2) 为使用JPA生成的DAO添加定制查询

代码位置：

> [../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/)

配置：

> [/src/main/resources/application.properties](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/resources/application.properties)
>
> ```properties
> ...
> 
> # 所有的url，都在前面加上"/account"
> server.servlet.context-path=/account
> 
> ...
> ```

### 5.1 使用Thymeleaf + Bootstrap生成Web页面

#### (1) Demo

> Bootstrap的css和js（下载存放到下面位置，或者在thymeleaf模板中直接使用cdn地址）
>
> ~~~xml
> <!--
> 	从 https://v3.bootcss.com/getting-started/#download 下载 "用于生产环境的 bootstrap"
> 	解压后，
> 	拷贝 bootstrap.min.css 到 src/main/resources/static/css/
> 	拷贝 bootstrap.min.js  到 src/main/resources/static/js/
> -->
> ~~~
>
> Thymeleaf模板：[/src/main/resources/templates/register.html](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/resources/templates/register.html)
>
> ~~~html
> <!DOCTYPE html>
> <html lang="en" xmlns:th="http://www.thymeleaf.org">
> <head>
> 	...
> 	<!-- thymeleaf会自动给这些url加上Servlet Context前缀，在application.properties中的配置项为server.servlet.context-path -->
> 	<link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
>     <script th:src="@{/js/bootstrap.min.js}"></script>
> 	...
> </head>
> <body>
> 	<!-- 用thymeleaf条件表达式，来让下面的标签仅在Controller设置的"stat"属性不为null时显示 -->
> 	<!-- class="bg-danger" 来自 https://v3.bootcss.com/css/#helper-classes -->
> 	<!-- <p class="bg-danger">...</p> -->
> 	<p th:text="${stat == null ? '' : stat.message}" class="bg-danger">...</p>
> 	<a th:href="@{/list}">用户列表</a>
> 	<a th:href="@{/register}">用户注册</a>
> 
> 	<!-- 添加bootstrap组件 -->
> 	<!-- 从 https://v3.bootcss.com/css/#forms-example 拷贝的form标签并粘贴到下方，根据需要进行修改 -->
> 	<!-- 增加表单提交时，对应的路径和方法 -->
> 	<form action="register" method="post">
> 		<div class="form-group">
> 			<label for="loginName">用户名</label>
> 			<input type="text" name="loginName" class="form-control" id="loginName" placeholder="请输入用户名">
> 		</div>
> 		...
> 		<button type="submit" class="btn btn-default">提交</button>
> 	</form>
> </body>
> ~~~
>
> Controller：[/src/main/java/.../controller/MainController.java](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/java/com/javaref/springbootmvc/springbootmvc03/controller/MainController.java)
>
> ```java
> @Controller
> public class MainController {
> 	@Autowired
> 	AccountService accSrv;
> 
> 	...
> 
> 	// 测试url: http://localhost:8080/account/register
> 	@RequestMapping("register")
> 	public String registerPost(HttpServletRequest request, Account account) {
> 		// 获取表单数据，可以通过HttpServletRequest，也可以通过注入@Entity对象account注入来获取
> 		// 处于演示需要，这里两种方式一起使用
> 		String loginName = (String)request.getAttribute("loginName");
> 
> 		// 注册用户，结果存入context
> 		RespStat stat = accSrv.save(account);
> 		request.setAttribute("stat", stat);
> 
> 		// 返回结果用"register.html"模板来渲染
> 		return "register";
>  	}
> 	
>     ...
> }
> ```
>
> Response封装 
>
> * 在Controller中可以看到，`request.setAttribute("stat", stat);`，给前端的response封装在一个RespStat对象中
>
> * 在Thymeleaf模板中可以看到，`<p th:text="${stat == null ? '' : stat.message}" class="bg-danger">...</p>`，渲染html页面是使用了`RespStat stat`对象的数据
> * `RespStat`是一个POJO，代码为[`/src/main/java/.../service/RespStat.java`](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/java/com/javaref/springbootmvc/springbootmvc03/service/RespStat.java)

#### (2) Thymeleaf

> 参考 [./02_thymeleaf.md](./02_thymeleaf.md)

### 5.2 JPA定制查询

#### (1) Demo

> DAO：[/src/main/java/.../repository/AccountRepository.java](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/java/com/javaref/springbootmvc/springbootmvc03/repository/AccountRepository.java)
>
> ```java
> public interface AccountRepository extends JpaRepository<Account, Integer> {
> 
>     // 自定义方法: JPA根据方法声明约定，自动生成Hibernate QL以及对应的方法实现
>     List<Account> findByIdBetween(int min, int max);
>     Optional<Account> findByLoginNameAndPassword(String loginName, String password);
> 
>     // 自定义 Hibernate QL
>     // 面向对象的select，因此不是select * 而是select一个Account，放在变量acc中，存入返回列表
>     @Query("select acc from Account acc where acc.id=1 ")
>     List<Account> queryWithHQL1();
> 
>     @Query("select acc from Account acc where acc.id=?1 ")
>     List<Account> queryWithHQL2(int id);
> 
>     // 除了HQL，JPA也支持动态拼接SQL，SQL；
>     // 太复杂的查询也放在这一层，但是会使用ES，Solr等
>     // 更多JPA相关内容，见附加的JPA文档
> 
>     // Spring JPA在跨表查询时，问题会非常多，并且需要深入学习JPA及Hibernate底层的知识
>     // 1. 如果使用Spring JPA，尽量在设计上避免在DAO层跨表查询
>     // 2. 这也是Spring JPA在企业中使用较少的原因，并且很多公司禁止使用JPA跨表查询
>     // 3. N+1问题：
>     //  (1) ORM: 一个实体类 <-> 一张表；一个实体类对象 <-> 一张表中的一行数据
>     //  (2) 表关联时（例如1:N映射）
>     //      如果代码写不好、会解析成N条SQL查询，改起来比较复杂，需要覆盖一些JPA/Hibernate的方法
>     //      在team人员经验不齐时，难以保证质量
> 
>     // 对比JPA、使用MyBatis会更加灵活和容易掌握；对比JDBC、使用MyBatis更加简洁高效
> }
> ```
>
> Service：[/src/main/java/.../service/AccountService.java](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/java/com/javaref/springbootmvc/springbootmvc03/service/AccountService.java)
>
> ```java
> @Service
> public class AccountService {
> 	@Autowired
> 	AccountRepository accRep;
> 
> 	// 使用上面编写的自定义方法
> 	public List<Account> queryWithHQL1() {
> 		return accRep.queryWithHQL1();
> 	}
> 
> 	public List<Account> queryWithHQL2(int id) {
> 		return accRep.queryWithHQL2(id);
> 	}
> 
> 	// 使用JpaRepository<Account, Integer> 接口自带方法
> 	public Optional<Account> findById(int id)  {
> 		return accRep.findById(id);
> 	}
> 
> 	public List<Account> findByIdBetween(int max, int min) {
> 		return accRep.findByIdBetween(max, min);
> 	}
>     
> 	...
> }
> ```

#### (2)  JPA自定义SQL注解`@Query`

> 通过参数位置（占位符）来传递参数
>
> ~~~java
> public interface UserDao extends Repository<AccountInfo, Long> { 
> 	@Query("select a from AccountInfo a where a.accountId = ?1") 
> 	public AccountInfo findByAccountId(Long accountId); 
>  
> 	@Query("select a from AccountInfo a where a.balance > ?1") 
> 	public Page<AccountInfo> findByBalanceGreaterThan( 
> 	Integer balance,Pageable pageable); 
> }
> ~~~
>
> 使用参数名来传递参数
>
> ~~~java
> public interface UserDao extends Repository<AccountInfo, Long> { 
> 	public AccountInfo save(AccountInfo accountInfo); 
> 
> 	@Query("from AccountInfo a where a.accountId = :id") 
> 	public AccountInfo findByAccountId(@Param("id")Long accountId); 
> 
> 	@Query("from AccountInfo a where a.balance > :balance") 
> 	public Page<AccountInfo> findByBalanceGreaterThan( 
> 		@Param("balance")Integer balance,Pageable pageable); 
> }
> ~~~
>
> 更新
>
> ~~~java
> @Modifying 
> @Query("update AccountInfo a set a.salary = ?1 where a.salary < ?2") 
> public int increaseSalary(int after, int before);
> ~~~
>
> 直接使用Native SQL
>
> ~~~java
> public interface UserRepository extends JpaRepository<User, Long> {
> 	@Query(value = "SELECT * FROM USERS WHERE EMAIL_ADDRESS = ?1", nativeQuery = true)
> 	User findByEmailAddress(String emailAddress);
> }
> ~~~

## 06  MyBatis

> 使用MyBatis生成DAO
>
> 分页插件PageHelper的使用 
>
> MyBatis Generator GUI（https://github.com/zouzg/mybatis-generator-gui）的使用 

代码位置：[../demos/08_spring_boot_mvc_mybatis/](../demos/08_spring_boot_mvc_mybatis/)

### 6.1 项目搭建 

> 依赖：[/pom.xml](../demos/08_spring_boot_mvc_mybatis/pom.xml)
>
> ```xml
> <!-- MyBatis及DB Connector -->
> <dependency>
> <groupId>org.mybatis.spring.boot</groupId>
> <artifactId>mybatis-spring-boot-starter</artifactId>
> <version>2.0.1</version>
> </dependency>
> <dependency>
> <groupId>mysql</groupId>
> <artifactId>mysql-connector-java</artifactId>
> <scope>runtime</scope>
> </dependency>
> 
> <!-- 分页插件 -->
> <!-- https://mvnrepository.com/artifact/com.github.pagehelper/pagehelper-spring-boot-starter -->
> <dependency>
> <groupId>com.github.pagehelper</groupId>
> <artifactId>pagehelper-spring-boot-starter</artifactId>
> <version>1.2.12</version>
> </dependency>
> ```
>
> 配置：[/src/main/resources/application.properties](../demos/08_spring_boot_mvc_mybatis/src/main/resources/application.properties)
>
> ```properties
> ## 数据库连接信息
> spring.datasource.url=jdbc:mysql://localhost:3306/ssm?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
> spring.datasource.username=root
> spring.datasource.password=12345678
> 
> ## MyBatis配置
> # Mapper接口所在的包
> mybatis.type-aliases-package=com.javaref.springboot.mapper
> # <Mapper方法,SQL>映射配置文件的位置，其中classpath在代码中对应于src/main/resources
> mybatis.mapper-locations=classpath:mybatis/mapper/*.xml
> 
> # 在日志中打印SQL
> logging.level.com.mashibing.springboot.mapper=debug
> ```
>
> 设置DAO（MyBatis Mapper）扫描的package：[/src/main/java/.../SpringBootMvc04Application.java](../demos/08_spring_boot_mvc_mybatis/src/main/java/com/javaref/springboot/SpringBootMvc04Application.java)
>
> ```java
> @SpringBootApplication
> @MapperScan(value = "com.javaref.springboot.mapper")
> public class SpringBootMvc04Application {
>     public static void main(String[] args) {
>        SpringApplication.run(SpringBootMvc04Application.class, args);
>     }
> }
> ```

### 6.2 手写MyBatis Mapper

> DAO：[/src/main/java/.../mapper/AccountMapper.java](../demos/08_spring_boot_mvc_mybatis/src/main/java/com/javaref/springboot/mapper/AccountMapper.java)
>
> ```java
> // 因为项目的主类被@MapperScan(value = "com.javaref.springboot.mapper")注解
> // Mapper扫描可以扫到这里，因此AccountMapper也可以不使用@Mapper注解
> // @Mapper
> public interface AccountMapper {
>        // SQL配置在AccountMapper.xml中
>        List<Account> findAllByXMLBinding();
>        void add(Account account);
> 
>        // 不需要向AccountMapper.xml中添加配置
>        @Select("select * from account")
>        List<Account> findAllByAnnotation();
> }
> ```
>
> Mapper配置：[/src/main/resources/mybatis/mapper/AccountMapper.xml](../demos/08_spring_boot_mvc_mybatis/src/main/resources/mybatis/mapper/AccountMapper.xml)
>
> ```xml
> <?xml version="1.0" encoding="UTF-8"?>
> <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
> 
> <!-- 将namespace命名为Mapper接口，就不需要实例化这个接口了 -->
> <mapper namespace="com.javaref.springboot.mapper.AccountMapper">
>        <!-- 映射关系：POJO对象属性 - 表字段，如果不配置、将按照MyBatis默认的名称规则来映射 -->
>        <resultMap type="com.javaref.springboot.mapper.Account" id="AccountResultMap">
>            <!-- column   : 表字段        -->
>            <!-- property : 实体对象的属性 -->
>            <result column="login_name" property="loginName"/>
>            <result column="password" property="password"/>
>        </resultMap>
> 
>        <!-- SQL与方法绑定：通过id="findAllByXMLBinding"、绑定到AccountMapper.findAllByXMLBinding()                -->
>        <!-- 返回的ResultSet与POJO绑定：通过resultMap="AccountResultMap"找到上面的id="AccountResultMap"的<resultMap>标签 -->
>        <select id="findAllByXMLBinding" resultMap="AccountResultMap">
>            select *
>            from account
>        </select>
> 
>        <!-- 向SQL中传参数 -->
>        <!-- #{}表达式中的内容是Account类的属性，框架通过动态代理+反射，找到Account的loginName、password属性的get方法拿到对应的值 -->
>        <insert id="add" parameterType="Account">
>            insert into account(login_name, password)
>            values (#{loginName}, #{password})
>        </insert>
> 
>        <!-- 另一种做法时，用@Select("select * from account")注解AccountMapper接口中的方法，而不用写xml配置 -->
>        <!-- 例如AccountMapper.findAllByAnnotation()方法 -->
> </mapper>
> 
> <!-- 想要自动生成上面的这些配置、以及Mapper的代码  -->
> <!--
>    	方法1：MyBatis Generator，基于XML配置，使用不方便
>    	方法2：https://github.com/zouzg/mybatis-generator-gui, 使用GUI生成配置
> -->
> ```

### 6.3 使用 MyBatis Generator  GUI编写MyBatis Mapper

> 使用MyBatis  Generator GUI生成DAO及Mapper配置类，配置好之后 ，它会连接数据库根据数据表的Schema生成代码
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/002_spring_boot/mybatis_generator_gui_menu_mapper.jpg)
>
> DAO：[/src/main/java/.../mapper/MenuMapper.java](../demos/08_spring_boot_mvc_mybatis/src/main/java/com/javaref/springboot/mapper/MenuMapper.java)
>
> ```java
> /**
>  * 用 https://github.com/zouzg/mybatis-generator-gui 自动生成（勾选Query by Example）
>  * 自动生成的代码包括：
>  * 	 类：Menu，MenuExample, MenuMapper, MyBatisBaseDap
>  * 	 配置：AccountMapper.xml
>  **/
> @Repository
> public interface MenuMapper extends MyBatisBaseDao<Menu, Integer, MenuExample> {
> }
> ```
>
> Service（[/src/main/java/.../service/MenuService.java](../demos/08_spring_boot_mvc_mybatis/src/main/java/com/javaref/springboot/service/MenuService.java)）：使用自动生成的DAO
>
> ```java
> @Service
> public class MenuService {
> 	@Autowired
> 	MenuMapper menuMapper;
>     
> 	public List<Menu> findAll() {
> 		// MenuExample是MyBatisGenerator(GUI)生成的类、用来封装复杂查询的查询条件
> 		// 不添加任何条件，直接扫全表
> 		MenuExample example = new MenuExample();
> 		return menuMapper.selectByExample(example);
> 	}
> 
> 	public Menu findById(Integer id) {
> 		// MenuExample是MyBatisGenerator(GUI)生成的类、用来封装复杂查询的查询条件
> 		// 添加查询条件，查询特定数据
> 		MenuExample example = new MenuExample();
> 		example.createCriteria().andIdEqualTo(id);
> 
> 		List<Menu> list = menuMapper.selectByExample(example);
> 		return list.size() == 1?list.get(0) : null;
> 		// 由于主键查询是常用的查询，MyBatisGenerator(GUI)生成的MenuMapper类中提供selectByPrimaryKey方法，可以直接使用
> 		// Menu menu = menuMapper.selectByPrimaryKey(id);
> 		// return menu;
> 	}
> 
> 	public void add() {
> 		// insert
> 		Menu menu = new Menu();
> 		menu.setIndex("0");
> 		menu.setName("首页");
> 		menu.setRoles("all");
> 		// MyBatisGenerator(GUI)生成的MenuMapper类中提供insert方法，可以直接使用
> 		menuMapper.insert(menu);
> 	}
>    
> 	...
> }
> ```

###  6.4 分页器（后端部分）

>  Controller：[/src/main/java/.../controller/MainController.java](../demos/08_spring_boot_mvc_mybatis/src/main/java/com/javaref/springboot/controller/MainController.java)
>
>  ```java
>  // 测试url：http://localhost:8080/page?pageNum=2&pageSize=2
>  @RequestMapping("/page")
>  @ResponseBody
>  public Object page (
>        // @RequestParam(required = false)：url中没有该参数时，传入null
>        @RequestParam(required = false) Integer pageNum,
>        @RequestParam(required = false) Integer pageSize
>  ) {
>    	List<Menu> menus = menuSrv.findByPage(pageNum,pageSize);
>    	return menus;
>  }
>  ```
>
>  Service：[/src/main/java/.../service/MenuService.java](../demos/08_spring_boot_mvc_mybatis/src/main/java/com/javaref/springboot/service/MenuService.java)
>
>  ```java
>  public List<Menu> findByPage(Integer pageNum, Integer pageSize) {
>        // 引入
>        //     <groupId>com.github.pagehelper</groupId>
>        //     <artifactId>pagehelper-spring-boot-starter</artifactId>
>        // 之后
>        // 函数头部的 PageHelper.startPage(pageNum, pageSize) 代码
>        // 可以影响函数底部 menuMapper.selectByExample(example) 生成的SQL
>        // 这是pagehelper通过AOP来实现的
>  
>        PageHelper.startPage(pageNum, pageSize);
>        MenuExample example = new MenuExample();
>        // AOP
>        return menuMapper.selectByExample(example);
>  }
>  ```

## 99 附录

### 99.1 常用配置

> ~~~properties
> # JPA相关
> # validate      加载hibernate时，验证创建数据库表结构
> # create   	    每次加载hibernate，重新创建数据库表结构，这就是导致数据库表数据丢失的原因。
> # create-drop   加载hibernate时创建，退出是删除表结构
> # update        加载hibernate自动更新数据库结构
> # validate      启动时验证表的结构，不会创建表
> # none          启动时不做任何操作
> spring.jpa.hibernate.ddl-auto=validate 
> # 控制台打印sql
> spring.jpa.show-sql=true
> 
> # 需要Spring Dev Tool依赖
> spring.devtools.restart.enabled: true
> 
> # thymeleaf相关
> spring.thymeleaf.cache=false
> spring.thymeleaf.encoding=UTF-8
> 
> # encoding
> spring.http.encoding.force=true
> spring.http.encoding.charset=UTF-8
> spring.http.encoding.enabled=true
> server.tomcat.uri-encoding=UTF-8
> 
> spring.mvc.date-format=yyyy-MM-dd
> ~~~

### 99.2 文件上传

> 指定大小
>
> ~~~properties
> spring.http.multipart.maxFileSize=200MB
> spring.http.multipart.maxRequestSize=200MB
> 
> spring.servlet.multipart.max-request-size = 200MB
> spring.servlet.multipart.max-file-size = 200MB
> ~~~
>
> 表单
>
> ~~~xml
> <form action="fileUploadController" method="post" enctype="multipart/form-data">
> 		上传文件：<input type="file" name="filename"/><br/>
> 		<input type="submit"/>
> </form>
> ~~~
>
> Controller
>
> ~~~java
> @RequestMapping("/fileUploadController")
> public String fileUpload(MultipartFile filename) throws Exception{
>     // 通常应当保存到分布式文件系统上
> 	String orignalFileName = filename.getOriginalFilename());
> 	filename.transferTo(new File(...));
> 	return "ok";
> }
> ~~~

