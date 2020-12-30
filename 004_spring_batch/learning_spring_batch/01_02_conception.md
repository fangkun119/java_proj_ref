## 1 Spring Batch

> [Spring Batch](https://docs.spring.io/spring-batch/docs/4.2.x/reference/html/domain.html#domainLanguageOfBatch)基于[JSR-352](https://jcp.org/en/jsr/detail?id=352)，其中的概念也来自于JSR-352，在文档中可以看到两者一一对应之处
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/004_spring_batch/springbatch_key_concept.jpg)

Spring Batch的设计目标

> (1) `JOB FLOW`状态机：`Step`之间的依赖，通过XML或者Java Based DSL配置
>
> (2) 事务处理：例如将文件内容分成很多小的chunk，当某个chunk处理失败时，可以从失败的check开始重启
>
> (3) `DECLARATIVE I/O`：提供各类`Reader`、`Writer`、`Processor`，从而简化数据读写，专注在业务逻辑上
>
> (4) 错误处理：提供Error Handling机制来减少程序错误时的人工干预
>
> (5) 可扩展（`scalability`）：可以从Single JVM扩展到多个JVM上

## 2 `Spring Batch`项目创建

> 使用`Spring Boot 2.4.1`（`Spring Batch 4.2.5`）创建Spring Batch项目，基本上与视频中基于`Spring Batch 3.0.6`的过程相同，差异点记录如下

(1) 项目创建

> 通过[start.spring.io](http://start.spring.io)或者IDEA的`Spring Intitalizer`创建、在`Starter依赖`中加入`Spring Batch`、`MySQL Driver` (如果使用MySQL存储batch job运行状态)

(2) 当使用`Spring Boot 2.4.1`（`Spring Batch 4.2.5`）时，必须配置`Job Repository`所使用的`jdbc data source`，否则会报错

> ~~~bash
> Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
> Reason: Failed to determine a suitable driver class
> Action:
> Consider the following:
> 	If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
> 	If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active)
> ~~~

配置`Job Repository`

> `Job Repository`用来存储batch job instance的运行状态、例如执行到了哪一步，处理了多少数据等
>
> * 如果使用数据库存储：需要确保`pom.xml`中已经添加JDBC依赖，并配置`application.properties`
> * 如果使用内存存储（job运行完毕后数据会丢失，但很方便用于调试和测试）：需要给main函数所在的类加注解
>
> 具体方法参考2.3小节

(3) `@EnableBatchProcessing`

> 除了IDEA自动生成的`@SpringBootApplication`注解以外，还要给main函数所在的类加上`EnableBatchProcessing`注解，以便让框架能够创建Spring Batch专用的bean

## 2 `Job`与`Step`

原始Demo：`Learning Spring Batch - Working Files/Chapter 2/helloWorld`

> 配置了一个简单的`Job`，该`Job`只有一个类型为`Tasklet`的`Step`

### 2.2 Job组成

>  `Job`：定义了一组`Step`之间的依赖关系：
>
>  * 任务依赖关系可以用有向图（DAG）描述
>  * `Job`的运行（叫做`Job Instance`）就是一个状态机，每个状态对应一个`Step`，`Job Instance`的状态在`Step`之间切换
>
>  `Step`：执行一个具体操作，有两种step：
>
>  * `Tasklet` ：执行发送邮件、保存pdf、执行一个脚本等简单任务
>  * `Chunk Step` ：用于数据批处理，输入的Item数量可能很多，逐条处理效率慢、一次全部处理可能受限于容量限制，因此把他们划分在多个`Chunk`中，逐批处理。这类`Step`由`ItemReader`、`ItemProcessor`（可选）、`ItemWriter`三部分组成
>      * 一个`Job`可能配置了多个`Chunk Step`：每一个`Chunk Step`都会处理所有的数据，才认为该Step运行完毕
>      * 在单个`Chunk Step`中：计算会分多轮，每一轮计算处理一个chuck，具体如下：
>          * `ItemReader`：负责拿到**一个**完整的chuck
>          * `ItemProcessor`：依次拿到`ItemReader`处理当前chuck的每一个record，进行转换，检查，附加逻辑等计算
>          * `ItemWriter`拿到该chuck完整的处理结果，这样它有机会执行类似bulk insert之类的写优化操作
>
>  `JobLaunch`：
>
>  * 用来运行Job，根据Job定义以及`JobRepository`中的数据进行状态跳转

### 2.3 Job Repository及配置

#### `JobRepository`：

> 用来存储Job运行时状态机的状态，包括step、chunk、item相关的数据，以便在故障时可以重启。它有两种实现方式
>
> * `jdbc based repository`：使用数据库存储
>
> * `memory based repository`：仅用于测试，使用HashMap之类的内存数据结构存储

#### `JDBC based repository`

> 使用`JDBC based repository`，Job Execution及Step的执行状态会记录到数据库中，任何一个Job Instance的执行状态都会被持久化到数据库中。也就是说，如果重启同一个Job Instance，它会跳过已经执行完毕的部分、从未执行部分开始继续执行。

以MySQL为例、配置方法如下

> (1) 在MySQL中创建一个schema名为spring_batch_demo
>
> (2) 在`pom.xml`中增加MySQL的依赖
>
> ~~~xml
> <dependency>
> 	<groupId>mysql</groupId>
> <artifactId>mysql-connector-java</artifactId>
> </dependency>
> ~~~
>
> (3) 在`application.properties`中增加`datasource`相关的配置，例如
>
> ~~~properties
> spring.batch.initialize-schema=always
> spring.datasource.url=jdbc:mysql://localhost:3306/spring_batch_demo?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
> spring.datasource.username=root
> spring.datasource.password=12345678
> ~~~
>
> 其中`spring.batch.initialize-schema=always`让Spring在启动时自动建表
>
> **以下所有Demo均相同，需要加入MySQL依赖，并配置datasource**

#### Memory based repository

> 使用`memory based repository`，一旦程序关闭，所有运行状态都会丢失，重启同一个Job Instance会重新执行所有步骤

只需在`@SpringBootApplication`注解中加上`exclude = {DataSourceAutoConfiguration.class })`，就可以忽略`application.properties`中的配置、开启`memory based repository`

> ```java
> @EnableBatchProcessing
> @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
> public class SpringBatchApplication {
>  public static void main(String[] args) {
>      SpringApplication.run(TransitionDemoApplication.class, args);
>  }
> }
> ```

### 2.4 Job运行的状态数据

> 数据层级
>
> * `Job`：一个Batch Job的定义，可以理解为程序部署的抽象
>     * `Job Instance`：Job的一次运行（例如某个daily job在2020-12-01的运行）
>         * `Job Execution`：Job Instance的执行
>             * 正常情况下一个Job Instance只有一个Job Execution
>             * 发生失败重试时，就会产生第2、第3个Job Execution
>
> 数据库ER图如下，debug时会用到
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/004_spring_batch/spring_batch_job_state_db.jpg)
>
> 其中
>
> *  `BATCH_JOB_INSTANCE.JOB_KEY`使用JOB_NAME和JOB参数的hash码生成，可以唯一定位一个Job Instance
> *  `BATCH_JOB_EXECUTION.STATUS`是Spring Batch内部的状态，定义在`BatchStatus.java`中
> *  `BATCH_JOB_EXECUTION.EXIT_CODE`和`BATCH_JOB_EXECUTION.EXIT_MESSAGE`是框架使用者在代码中设置的Job运行结果，如果未设置Spring Batch会根据`STATU`用默认值进行填充