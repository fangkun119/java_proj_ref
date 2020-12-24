# Spring Batch

> [O‘Reilly Learning Spring Batch](http://shop.oreilly.com/product/0636920044673.do) 的学习笔记，这个视频是`spring.io`官方推荐的学习视频。
>
> * 该视频录制于2016年（`Spring Batch 3.0.6`），我用目前最新的`Spring Boot 2.4.1`（`Spring Batch 4.2.5`）实现了一遍视频中提到的所有功能，同时也试验不同的代码组合来进行补充。
> * 笔记对知识点进行精简，主要记录原始代码和视频中没有补充内容，或者一些增强理解的补充说明；同时为了便于阅读、也摘录了一些原视频中的理论内容
>
> * 关于视频及原始Demo代码，可点击链接到O`Reilly网站上观看和下载（新用户可以免费申请10天的试用）

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
>

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
>     * 一个`Job`可能配置了多个`Chunk Step`：每一个`Chunk Step`都会处理所有的数据，才认为该Step运行完毕
>     * 在单个`Chunk Step`中：计算会分多轮，每一轮计算处理一个chuck，具体如下：
>       * `ItemReader`：负责拿到**一个**完整的chuck
>       * `ItemProcessor`：依次拿到`ItemReader`处理当前chuck的每一个record，进行转换，检查，附加逻辑等计算
>       * `ItemWriter`拿到该chuck完整的处理结果，这样它有机会执行类似bulk insert之类的写优化操作
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

#### Memory based repository

> 使用`memory based repository`，一旦程序关闭，所有运行状态都会丢失，重启同一个Job Instance会重新执行所有步骤

只需在`@SpringBootApplication`注解中加上`exclude = {DataSourceAutoConfiguration.class })`，就可以忽略`application.properties`中的配置、开启`memory based repository`

> ```java
> @EnableBatchProcessing
> @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
> public class SpringBatchApplication {
>     public static void main(String[] args) {
>         SpringApplication.run(TransitionDemoApplication.class, args);
>     }
> }
> ```

### 2.4 Job运行的状态数据

> 数据层级
>
> * `Job`：一个Batch Job的定义，可以理解为程序部署的抽象
>   * `Job Instance`：Job的一次运行（例如某个daily job在2020-12-01的运行）
>     * `Job Execution`：Job Instance的执行
>       * 正常情况下一个Job Instance只有一个Job Execution
>       * 发生失败重试时，就会产生第2、第3个Job Execution
>
> 数据库ER图如下，debug时会用到
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/004_spring_batch/spring_batch_job_state_db.jpg)
>
> 其中
>
> *  `BATCH_JOB_INSTANCE.JOB_KEY`使用JOB_NAME和JOB参数的hash码生成，可以唯一定位一个Job Instance
> * `BATCH_JOB_EXECUTION.STATUS`是Spring Batch内部的状态，定义在`BatchStatus.java`中
> * `BATCH_JOB_EXECUTION.EXIT_CODE`和`BATCH_JOB_EXECUTION.EXIT_MESSAGE`是框架使用者在代码中设置的Job运行结果，如果未设置Spring Batch会根据`STATU`用默认值进行填充

## 3  Job Flow

### 3.1 Transactions：step跳转

> 配置Step之间依赖关系

原始Demo：`Learning Spring Batch - Working Files/Chapter 3/transitions`

简单串行step配置

> ~~~java
> @Bean
> public Job transitionJobSimpleNext() {
> 		//next(): transition to next step on successful completion of the current step. All other outcomes are treated as failures
>     return jobBuilderFactory.get("transitionJobNext")
>             .start(step1()) 
>             .next(step2()) 
>             .next(step3()) 
>             .build();			
>   	// 等价于
> 		// return jobBuilderFactory.get("transitionJobNext")
>     //      .start(step1()).on("COMPLETED").to(step2())
>   	//			// 理解为有向图中的一条边，表示从<step2,COMPLETED>到<step3>的依赖关系
>     //      .from(step2()).on("COMPLETED").to(step3()) 
>     //      .from(step3()).on("COMPLETED").end()
>     //      .build();
> }
> ~~~

基于任务依赖有向图的配置

> ```java
> @Bean
> public Job transitionJobSimpleNext() {
>     // from(),on(),to()：增加一条任务依赖，相当于向任务依赖有向图（DAG）中添加一条有向边
>   	//		from()	：用来指定基于哪个step的执行结果
>   	//		on()		：用来指定基于这个step的哪种执行结果
>   	//		to()		：用来指定满足from(),on()条件时，接下来执行哪个step
>   	// 整条有向边的起点是`(from(),on())`二元组所表示的状态，终点是`to()`
>     return jobBuilderFactory.get("transitionJobNext")
>       			// 从step1开始，step1成功后执行step2
>             .start(step1()).on("COMPLETED").to(step2())
>       			// 在step2、3之间加入instance stop
>       			// 程序会在step2成功后退出，下次执行从step3开始继续（需要jdbc job repository）
>       			// 使用场景例如需要在两个step之间执行人工操作等场景
>             .from(step2()).on("COMPLETED").stopAndRestart(step3()) 
>       			.from(step3()).end()
>       			.build();
> }
> ```
>
> 上面代码中的`.stopAndRestart(step3())`还可以换成`.stop()`，`.end()`，`.fail()`，让程序在step2成功之后出发不同的job返回状态：
>
> * `stop()`：整个job以`STOPPED`状态结束； `stopAndRestart()`会更进一步、制定了重启`job instance`时从哪一步继续
> * `end()`：整个job以`COMPLETED`状态结束
> * `fail()`：整个job以`FAILED`状态结束
>
> 有时候job有多个终止节点时，可以配多个`end()`/`fail()`/`stop()`

备注：使用`Spring Boot 2.4.1`（`Spring Batch 4.2.5`）参考2.4小节

### 3.2 Flows：将一组step组合在一起

> 一组`Step`之间的步骤依赖形成的有向图可以抽象为一个`Flow`，有了这层依赖，就可以
>
> * 对`Flow`进行组合、产生更加复杂的`Flow`并配置在`Job`中
> * 对`Flow`进行复用、降低代码复杂度

原始Demo：`Learning Spring Batch - Working Files/Chapter 3/flow`

Flow的配置形式如下：

> ```java
> @Bean
> public Flow flowA() {
>     FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowA");
>     flowBuilder.start(innerStep1())
>             .next(innerStep2())
>             .end();
>     return flowBuilder.build();
> }
> ```

外层代码可以把Flow和外层的Step组合配置在Job中，例如

> ```java
> @Bean
> public Job flowBLastJob(@Qualifier("flowB") Flow flowB) {
>     return jobBuilderFactory.get("flowBLastJob")
>             .start(outStep1())
>             .next(outStep2())
>             .on("COMPLETED").to(flowB) //对于flow没有next()这样的快捷方式
>             .end()
>             .build();
> }
> ```

也可以将个Flow组合成一个更复杂的Flow

> ```java
> @Bean
> public Job parallelFlowsJob(@Qualifier("flowA") Flow flowA, @Qualifier("flowB") Flow flowB) {
>     FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("parallelFlow");
>     Flow parallelFlow = flowBuilder
>       			.split(new SimpleAsyncTaskExecutor()).add(flowA, flowB)
>             .end();
>     return jobBuilderFactory.get("splitJob")
>             .start(outStep1())
>             .next(outStep2())
>             .on("COMPLETED").to(parallelFlow)
>             .end()
>             .build();
> }
> ```

### 3.3 Splits：flow并发执行

内容

> 在上一个demo中出现了`split`，用来让几个flow在多线程中并发执行，接下来的Demo更多地介绍`split`
>
> 原Demo代码：`Learning Spring Batch - Working Files/Chapter 2/decisions`

下面是对Demo的一些修改，主要是增加了`tasklet`的`bean`作用域，以及尝试在`next()`和`to()`之后使用`flow()`

(1) 使用`split`让几个flow并发执行

> 方法1：定义一个并行Flow，传给方法`start()`或者`to()`，如上一个demo
>
> ```java
> Flow parallelFlow = flowBuilder
> 				.split(new SimpleAsyncTaskExecutor()).add(flowA, flowB)
>     		.end();
> return jobBuilderFactory.get("splitJob")
>         .start(outStep1())
>         .next(outStep2())
>         .on("COMPLETED").to(parallelFlow)
>         .end()
>         .build();
> ```
> 方法2：`start()`后面使用`split`，创建一个并行执行的flow
>
> ```java
> @Bean
> public Job job() {
>     return jobBuilderFactory.get("job")
>       			// 如果在to()或者next()之后创建并发流，应当使用方法1
>       			// 方法2无法让flow2执行，即无法创建并发的flow
>             .start(flow1()).split(new SimpleAsyncTaskExecutor()).add(flow2())
>             .end()
>             .build();
> }
> ```

(2) 在`flow`中复用`tasklet`的代码

> 定义tasklet
>
> ```java
> public static class DemoTasklet implements Tasklet {
>     @Override
>     public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
>         System.out.println(String.format("%s has been executed on thread %s", chunkContext.getStepContext().getStepName(), Thread.currentThread().getName()));
>       	// 用来查看执行step的DemoTasklet对象是不是同一个对象
>         System.out.println("Tasklet hashcode: " + this.hashCode());
>         return RepeatStatus.FINISHED;
>     }
> }
> @Bean
> @Scope("prototype")  // 如果不设置"prototype"，那么这个bean是单例的，意味着CountingTasklet中任何成员变量都会被多线程同时访问
> public Tasklet tasklet() {
>     return new DemoTasklet();
> }
> ```
>
> 使用tasklet
>
> ```java
> @Bean
> public Flow flow1() {
>     return new FlowBuilder<Flow>("flow1")
>             .start(stepBuilderFactory.get("step1").tasklet(tasklet()).build())
>             .build();
> }
> @Bean
> public Flow flow2() {
>     return new FlowBuilder<Flow>("flow2")
>             .start(stepBuilderFactory.get("step2").tasklet(tasklet()).build())
>             .next( stepBuilderFactory.get("step3").tasklet(tasklet()).build())
>             .build();
> }
> ```

### 3.4 Decisions：在任务依赖中加入判断节点

介绍

> 相当于在任务依赖的有向图中，加入一个判断节点；可以让某个step结束后执行该判断逻辑；也可以在判断逻辑中根据判断结果执行不同的step

原Demo代码：`Learning Spring Batch - Working Files/Chapter 3/decision/`

定义判断节点

> ```java
> // 定义decider，它会放在一个Singleton Bean中
> public static class OddDecider implements JobExecutionDecider {
>     private int count = 0;
>     @Override
>     public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
>         // 如果需要基于前一个Step的执行状态来做判断，可以使用stepExecution，例如stepExecution.getExitStatus()
>         count++;
>         if(count % 2 == 0) {
>             return new FlowExecutionStatus("EVEN");
>         } else {
>             return new FlowExecutionStatus("ODD");
>         }
>     }
> }
> 
> @Bean
> public JobExecutionDecider decider() {
>     return new OddDecider();
> }
> ```

使用`decider()`，其实就是把`decider()`当做一个节点，作为任务依赖有向图中一条边的起点或终点

> ```java
> @Bean
> public Job job() {
> 		return jobBuilderFactory.get("job")
> 				.start(startStep())
> 				.next(decider())														// `起始节点`执行成功之后进入`判断节点`、执行一次`decide()`方法
> 				.from(decider()).on("ODD").to(oddStep())		// `判断节点`返回"ODD"时执行`oddStep`
> 				.from(decider()).on("EVEN").to(evenStep())	// `判断节点`返回"EVEN"时执行`evenStep`
> 				.from(oddStep()).on("*").to(decider())			// `oddStep`执行完毕不论结果如何，都跳转到判断节点，再执行一次`decide()`方法
> 				.end()
> 				.build();
> }
> ```

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



