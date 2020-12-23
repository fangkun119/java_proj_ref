# Spring Batch

> [Learning Spring Batch](http://shop.oreilly.com/product/0636920044673.do) 的学习笔记

## 1 Spring Batch

Batch Job

> (1) 有限计算量（`Finite`） ：计算有终止点
>
> (2) 没有交互（`INTERACTION`）和中断（`INTERRUPTION`）

Spring Batch

> [Spring Batch](https://docs.spring.io/spring-batch/docs/4.2.x/reference/html/domain.html#domainLanguageOfBatch)基于[JSR-352](https://jcp.org/en/jsr/detail?id=352)，其中的概念也来自于JSR-352，在文档中可以看到两者一一对应之处
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/004_spring_batch/springbatch_key_concept.jpg)

Spring Batch的特性（3.0.6）

> (1) `JOB FLOW`状态机：一组Step组成在一个有向图中，通过XML或者Java Based DSL配置
>
> (2) 事务处理：例如将文件内容分成很多小的chunk，当某个chunk处理失败时，可以从失败的check开始重启
>
> (3) `DECLARATIVE I/O`：提供各类`Reader`、`Writer`、`Processor`，从而简化数据读写，专注在业务逻辑上
>
> (4) 错误处理：提供Error Handling机制来减少程序错误时的人工干预
>
> (5) 可扩展（`scalability`）：可以从single JVM扩展到多个JVM上
>
> 在实际应用中久经考验、并且是Spring生态圈的一部分

## 2 Spring Batch Job

> 通过[start.spring.io](http://start.spring.io)或者IDEA的`Spring Intitalizer`创建Spring Batch项目（在`Dependencies`中加入`Spring Batch`），并了解项目结构

### 2.1 Demo

#### 代码

> * [https://resources.oreilly.com/examples/0636920044673/tree/master/Learning%20Spring%20Batch%20-%20Working%20Files/Chapter%202/helloWorld](https://resources.oreilly.com/examples/0636920044673/tree/master/Learning%20Spring%20Batch%20-%20Working%20Files/Chapter%202/helloWorld)
>
> * 当使用`Spring Boot 2.4.1`（`Spring Batch 4.2.5`）时，必须配置`Job Repository`所使用的`jdbc data source`，否则会报错。具体的配置方法见 `2.4小结`。
>
>   ~~~bash
>   Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
>   
>   Reason: Failed to determine a suitable driver class
>   
>   Action:
>   
>   Consider the following:
>   	If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
>   	If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active)
>   ~~~
>
> * 以下所有Demo都一样，需要配置Job Repository

#### Job配置

> [`io.spring.batch.configuration`](https://resources.oreilly.com/examples/0636920044673/blob/master/Learning%20Spring%20Batch%20-%20Working%20Files/Chapter%202/helloWorld/src/main/java/io/spring/batch/configuration/JobConfiguration.java) 是该Batch Application的Job配置，它配置了`Job`，`Step` 并为Step设设置执行的任务内容（`tasklet`）
>
> ```java
> @Configuration
> @EnableBatchProcessing
> public class JobConfiguration {
>     @Autowired
>     private JobBuilderFactory jobBuilderFactory;
> 
>     @Autowired
>     private StepBuilderFactory stepBuilderFactory;
> 
>     @Bean
>     public Step step1() {
>         return stepBuilderFactory.get("step1")
>                 .tasklet(new Tasklet() {
>                     @Override
>                     public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
>                         System.out.println("Hello World");
>                         return RepeatStatus.FINISHED;
>                     }
>                 }).build();
>     }
> 
>     @Bean
>     public Job helloWorldJob() {
>         return jobBuilderFactory.get("helloWorldJob")
>                 .start(step1())
>                 .build();
>     }
> }
> ```

### 2.3 Spring Batch Job的组成

>  `Job`：定义`Job Flow状态机`中的一组状态，以及如何在状态之间跳转
>
>  `Step`：每个Step都是状态机中的一个状态，有两种step
>
>  * `Tasklet` ：在single method中运行的step，执行单一任务，例如发送邮件、保存pdf等、执行一个脚本等
>
>  * `Chunk` ：是item based step，用来处理items，由三个组件组成：
>
>     * `ItemReader`：处理输入chuck中的record
>     * `ItemProcessor`（可选）：执行转换，检查，附加逻辑等
>     * `ItemWriter`：处理输出
>
>      备注：
>
>     * 当配置多个Chunk Step时：
>       * 每一个Chunk Step都会处理所有的输入并输出，然后才开始下一个Chunk Step
>       * 当然也有办法对这些Chunk Step进行并行配置，让他们同时工作
>     * 在每个Chunk Step中：输入的records会被分成几组chunk（基于chuck size limit配置），每一轮计算处理一个chuck，具体如下：
>       * `ItemReader`会拿到**一个**完整的chuck
>       * `ItemProcessor`会依次拿到`ItemReader`处理当前chuck时输出的每一个record
>       * `ItemWriter`会拿到该chuck完整的处理结果，从而有机会执行类似bulk insert之类的写优化
>
>  `JobLaunch`：用来运行Job，根据Job定义以及`JobRepository`中的数据进行状态跳转

### 2.4 Job Repository及配置

`JobRepository`：用来存储Job运行时状态机的状态，包括step、chunk、item相关的数据，以便在故障时可以重启。它有两种实现方式

* `jdbc based repository`：使用数据库存储
* `memory based repository`：仅用于测试，使用HashMap之类的内存数据结构存储

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
>   <artifactId>mysql-connector-java</artifactId>
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

#### memory based repository

> 使用`memory based repository`，一旦程序关闭，所有运行状态都会丢失，重启同一个Job Instance会重新执行所有步骤

只需在`@SpringBootApplication`注解中加上`exclude = {DataSourceAutoConfiguration.class })`，就可以忽略`application.properties`中的配置、开启`memory based repository`

> ```java
> @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
> public class SpringBatchApplication {
>     public static void main(String[] args) {
>         SpringApplication.run(TransitionDemoApplication.class, args);
>     }
> }
> ```

### 2.5 Job运行的状态数据

> 数据层级
>
> * `Job`：一个Batch Job的定义，可以理解为程序部署的抽象
>   * `Job Instance`：Job的一次运行（例如某个daily job在2020-12-01的运行）
>     * `Job Execution`：Job Instance的执行
>       * 正常情况下一个Job Instance只有一个Job Execution
>       * 发生失败重试时，就会产生第2、第3个Job Execution
>
> 数据库ER图
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/004_spring_batch/spring_batch_job_state_db.jpg)
>
> 其中
>
> *  `BATCH_JOB_INSTANCE.JOB_KEY`使用JOB_NAME和JOB参数的hash码生成，可以唯一定位一个Job Instance
> * `BATCH_JOB_EXECUTION.STATUS`是Spring Batch内部的状态，定义在`BatchStatus.java`中
> * `BATCH_JOB_EXECUTION.EXIT_CODE`和`BATCH_JOB_EXECUTION.EXIT_MESSAGE`是框架使用者在代码中设置的Job运行结果，如果未设置Spring Batch会根据`STATU`用默认值进行填充

## 3  Job Flow

### 3.1 Transactions：状态（step）跳转

> 如何配置Spring来实现各个Step之间的跳转，以及如何跳转到Terminate Step并返回Task Execution的运行结果

#### 3.1.1 Demo

> https://resources.oreilly.com/examples/0636920044673/tree/master/Learning%20Spring%20Batch%20-%20Working%20Files/Chapter%203/transitions

#### 3.1.2 主要代码

> ```java
> @Bean
> public Job transitionJobSimpleNext() {
>   	// start()：	表示起始step
>     // from() -> on() -> to()：相当于有向图中的一个箭头
>   	//		from()：	用来指定on()的基于哪个step的执行结果来做判断
>   	//		on()：		用来指定基于那种执行结果
>   	//		to()：		用来指定当前状态下下一步的操作
>   	// end(), stop(), fail()：停止状态
>   	//		end()：	表示job instance执行成功、该instance将不能再次执行
>   	//		fail()：	表示job instance执行失败并停止
>   	//		stop()：	表示触发了预设的程序停止的条件，同时如果使用stepAndRestart()还可以指定之后重启这个job instance时、从哪一步开始运行
>     return jobBuilderFactory.get("transitionJobNext")
>             .start(step1()).on("COMPLETED").to(step2())	// 从step1开始，step1成功
>             .from(step2()).on("COMPLETED").stopAndRestart(step3()) // 强制在step2和step3之间加入一个job instaance stop，例如可以在两个step之间执行人工操作
>             .from(step3()).end() //step3执行成功则认为job instance执行成功
>             .build();
> }
> ```

#### 3.1.3 其他配置示例

> ~~~java
> @Bean
> public Job transitionJobSimpleNext() {
> 		//next(): Transition to the next step on successful completion of the current step. All other outcomes are treated as failures
>     return jobBuilderFactory.get("transitionJobNext")
>             .start(step1()) 
>             .next(step2()) 
>             .next(step3()) 
>             .build();			
>   	// 等价于
> 		// return jobBuilderFactory.get("transitionJobNext")
>     //      .start(step1()).on("COMPLETED").to(step2())	
>     //      .from(step2()).on("COMPLETED").to(step3())
>     //      .from(step3()).end()
>     //      .build();
> }
> ~~~
>
> ~~~bash
> 2020-12-23 17:55:28.850  INFO 43589 --- [  restartedMain] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
> >> This is step 1
> 2020-12-23 17:55:28.863  INFO 43589 --- [  restartedMain] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 13ms
> 2020-12-23 17:55:28.868  INFO 43589 --- [  restartedMain] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step2]
> >> This is step 2
> 2020-12-23 17:55:28.872  INFO 43589 --- [  restartedMain] o.s.batch.core.step.AbstractStep         : Step: [step2] executed in 3ms
> 2020-12-23 17:55:28.874  INFO 43589 --- [  restartedMain] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step3]
> >> This is step 3
> ~~~

备注：使用`Spring Boot 2.4.1`（`Spring Batch 4.2.5`）参考2.4小节

### 3.2 Flows



### 3.3 Splits



### 3.4 Decisions



### 3.5 Nested Jobs



### 3.6 Listeners



### 3.8 Job Parameters



## 4 Input



## 5 Output



## 6 Processing



## 7 Error Handling



## 8 Scaling Jobs



## 9 Job Orchestration



## 10 Spring Batch with Spring Integration



