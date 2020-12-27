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
> 
> // 如果不设置StepScope，这个bean会是单例的
> // 意味着全局共享同一个DemoTasklet对象，包括并发运行的两个Flow
> @Bean
> @StepScope 
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
>
> 输出
>
> ~~~bash
> INFO 63261 --- [  restartedMain] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=job]] launched with the following parameters: [{}]
> INFO 63261 --- [cTaskExecutor-1] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step2]
> INFO 63261 --- [cTaskExecutor-2] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
> step2 has been executed on thread SimpleAsyncTaskExecutor-1
> Tasklet hashcode: 181894191
> step1 has been executed on thread SimpleAsyncTaskExecutor-2
> Tasklet hashcode: 596123146
> INFO 63261 --- [cTaskExecutor-2] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 34ms
> INFO 63261 --- [cTaskExecutor-1] o.s.batch.core.step.AbstractStep         : Step: [step2] executed in 34ms
> INFO 63261 --- [cTaskExecutor-1] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step3]
> step3 has been executed on thread SimpleAsyncTaskExecutor-1
> Tasklet hashcode: 101957954
> INFO 63261 --- [cTaskExecutor-1] o.s.batch.core.step.AbstractStep         : Step: [step3] executed in 16ms
> INFO 63261 --- [  restartedMain] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=job]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 117ms
> 
> Process finished with exit code 0
> ~~~

### 3.4 Decisions：在任务依赖中加入判断节点

介绍

> 相当于在任务依赖的有向图中，加入一个判断节点；可以让某个step结束后执行该判断逻辑；也可以在判断逻辑中根据判断结果执行不同的step

原Demo代码：`Learning Spring Batch - Working Files/Chapter 3/decisions/`

定义判断节点

> ```java
> // 定义decider，它会放在一个Singleton Bean中
> public static class StepABDecider implements JobExecutionDecider {
>     @Override
>     public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
>         // 如果需要基于前一个Step的执行状态来做判断，可以使用stepExecution.getExitStatus()
> 				switch (getXYZStatus()) {
>         		case XYZStatus.X:
>           	case XYZStatus.Y:   
>             		return new FlowExecutionStatus("GO_STEP_A");
>           	case XYZStatus.Z:
>           	default:
>             		return new FlowExecutionStatus("GO_STEP_B");          
>         }
>     }
> }
> @Bean
> public JobExecutionDecider stepABDecider() {
>     return new StepABDecider();
> }
> ```

使用`decider()`，其实就是把`decider()`当做一个节点，作为任务依赖有向图中一条边的起点或终点

> ```java
> @Bean
> public Job job() {
> 		return jobBuilderFactory.get("job")
>       	// "起始节点"执行成功后进入"判断节点"，调用一次"decide()"
> 				.start(startStep()).next(stepABDecider()) 				
>       	// "判断节点"返回"GO_STEP_A"时执行"stepA()"
> 				.from(stepABDecider()).on("GO_STEP_A").to(stepA())		
>       	// "判断节点"返回"GO_STEP_B"时执行"stepB()"
> 				.from(stepABDecider()).on("GO_STEP_B").to(stepB())
>       	// 对于"stepA()"，不论执行结果如何，都跳转到"判断节点"进行判断
> 				.from(stepA()).on("*").to(stepABDecider())
> 				.end()
> 				.build();
> }
> ```

### 3.5 Nested Jobs

介绍：让一个Job（Parent Job）在执行的过程总调用另一个Job（Child Job）

原始Demo：`Learning Spring Batch - Working Files/Chapter 3/nestedJobs`

嵌套Job的定义方法如下：

> (1) 配置`Parent Job`
>
> ```java
> // 注入在其他使用`@Configuration`注解类中定义的`Child Job`
> @Autowired
> private Job childJob;
> 
> // 新注JobLauncher，用来运行这个`Child Job`
> @Autowired
> private JobLauncher jobLauncher;
> 
> // 定义Parent Job
> @Bean
> public Job parentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
>     // 把childJob包装在一个step中，这样它可以与普通的step和flow组合
>     Step childJobStep = new JobStepBuilder(new StepBuilder("childJobStep"))
> 						.job(childJob)
>       			// 在child job的层级、需要显示地指定、不能自动注入
> 						.launcher(jobLauncher).repository(jobRepository).transactionManager(transactionManager)
>             .build();
>     // 定义Parent Job，其中的step1只是parent job中的一个普通step
>     return jobBuilderFactory.get("parentJob")
>             .start(step1())
>             .next(childJobStep)
>             .build();
> }
> ```
>
> (2) `配置Child Job`：与之前Demo中的Job配置方法相同，其中`jobBuilderFactory.get("childJob")`将job name指定为`childJob`
>
> (3) 指定batch运行时启动哪个job：这时配置了两个、Job Bean，因此应当在`application.properties`中显示地指定使用哪个作为启动job
>
> ~~~properties
> spring.batch.initialize-schema=always
> spring.datasource.url=jdbc:mysql://localhost:3306/spring_batch_demo?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
> spring.datasource.username=root
> spring.datasource.password=12345678
> 
> spring.batch.job.names=parentJob  
> # spring.batch.job.names=childJob 
> ~~~
>
> (4) 使用jdbc job repository，运行job之后，在`BATCH_JOB_EXECUTION`表中可以看到两条job记录

### 3.6 Listeners

用途：Job Insance生命周期的各个时间节点执行预设的回调函数

方法1：实现接口

> Spring Batch提供了如下的Listener（接口定义在`org.springframework.batch.core`包中）
>
> * JobExecutionListener
> * StepExecutionListener
> * ChunkListener
> * ItemReadListener
> * ItemProcessListener
> * ItemWriteListener
>
> 查看接口中的函数名，可以看到每个函数对应于job instance生命周期的哪个时间节点

方法2：把注解加在POJO的方法上

> 这些注解定义在`org.springframework.batch.core.annotation`包中，包括
>
> `@AfterChunk`，`@AfterChunkError`，`@AfterJob`，`@AfterProcess`，`@AfterRead`，`@AfterStep`，`@AfterWrtie`，`@BeforeChunk`，`@BeforeJob`，`@BeforeProcess`，`@BeforeWrite`，`@OnProcessError`，`@OnReadError`，`@OnSkipInProcess`，`@OnSkipInRead`，`@OnSkipInWrite`，`@OnWriteError`

原始Demo：`Learning Spring Batch - Working Files/Chapter 3/listeners`（使用方法2）

实现例子（使用方法1）

(1) 定义和使用`ChunkListener`、`StepListener`

> 定义ChunkListener
>
> ```java
> public class MyChunkListener implements org.springframework.batch.core.ChunkListener {
>     @Override
>     public void beforeChunk(ChunkContext context) {
>         System.out.println("MyChunkListener.beforeChunk is running");
>     }
> 
>     @Override
>     public void afterChunk(ChunkContext context) {
>         System.out.println("MyChunkListener.afterChunk is running");
>     }
> 
>     @Override
>     public void afterChunkError(ChunkContext context) {
>         System.out.println("MyChunkListener.afterChunkError is running");
>     }
> }
> ```
>
> 定义StepListener
>
> ```java
> public class MyStepListener implements org.springframework.batch.core.StepExecutionListener {
>     @Override
>     public void beforeStep(StepExecution stepExecution) {
>         System.out.println("MyStepListener.beforeStep is running");
>     }
> 
>     @Override
>     public ExitStatus afterStep(StepExecution stepExecution) {
>         System.out.println("MyStepListener.afterStep is running");
>         return stepExecution.getExitStatus();
>     }
> }
> ```
>
> 使用ChunkListener和StepListener
>
> ```java
> @Bean
> public Step step1() {
>     return stepBuilderFactory.get("step1")
>             .<String, String>chunk(2) // 每2条记录作为一个chunk，输入输出类型都是String
>             .reader(reader()) 								// 设置reader
>             .writer(writer()) 								// 设置writer
>             .listener(new MyStepListener())  	// 设置step listener
>             .listener(new MyChunkListener()) 	// 设置chunk listener
>             .build();
> }
> ```

(2) 定义和使用Job Listener

> 定义Job Listener
>
> ```java
> public class MyJobListener implements JobExecutionListener {
>     @Override
>     public void beforeJob(JobExecution jobExecution) {
>         String jobName = jobExecution.getJobInstance().getJobName();
>         System.out.println("jobListener.beforeJob is running");
>     }
> 
>     @Override
>     public void afterJob(JobExecution jobExecution) {
>         System.out.println("JobListener.afterJob is running");
>     }
> }
> ```
>
> 使用Job Listener
>
> ```java
> @Bean
> public Job listenerJob() {
>     return jobBuilderFactory.get("listenerJob")
>             .start(step1())
>             .listener(new MyJobListener())
>             .build();
> }
> ```

(3) 程序日志

> ~~~bash
> INFO 63492 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=listenerJob]] launched with the following parameters: [{}]
> obListener.beforeJob is running
> INFO 63492 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
> MyStepListener.beforeStep is running
> MyChunkListener.beforeChunk is running
> Writing item one
> Writing item two
> MyChunkListener.afterChunk is running
> MyChunkListener.beforeChunk is running
> Writing item three
> MyChunkListener.afterChunk is running
> MyStepListener.afterStep is running
> INFO 63492 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 35ms
> JobListener.afterJob is running
> INFO 63492 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=listenerJob]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 57ms
> ~~~

另有的两块内容可以参考原始Demo

> * 在定义Job时、可以注入JavaMailSender，这样Job Listener可以在Job的不同阶段发送邮件
> * 可以用内存数据创建ItemReader和ItemWriter，来进行测试

### 3.7 Job Parameters

介绍：向Batch Job传递参数

原始Demo：`Learning Spring Batch - Working Files/Chapter 3/jobParameters`

配置方法

> 代码
>
> ```java
> @Bean
> @StepScope // Bean的生命周期是`StepScope`：就是说每个Step内，Bean对象时唯一的；在不同的Step中，Bean对象是不同的
> public Tasklet helloWorldTasklet(
>         // "#{jobParameters['message']}": SEPL Expression
>         @Value("#{jobParameters['message']}") String message
> ) {
>     return (stepContribution, chunkContext) -> {
>         System.out.println(this.hashCode() + ": " + message);
>         return RepeatStatus.FINISHED;
>     };
> }
> 
> @Bean
> public Step step1() {
>     return stepBuilderFactory.get("step1")
>             // 参数设为null，
>             // 是因为Spring并不会在定义tasklet的时候传参、而是在运行时进行注入
>             // 这里的null仅仅是占位用
>             .tasklet(helloWorldTasklet(null))
>             .build();
> }
> ```
>
> 运行（使用jdbc job repository），可以通过JVM命令行参数将传入（IDE使用run configuration传入）
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/004_spring_batch/private/demos/g03_job_parameters/target/
> $ java -jar g03_job_parameters-0.0.1-SNAPSHOT.jar message="hello"
> ...
> INFO 63222 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
> 1839168128: hello
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/004_spring_batch/private/demos/g03_job_parameters/target/
> $ java -jar g03_job_parameters-0.0.1-SNAPSHOT.jar message="hello"
> ERROR 63223 --- [           main] o.s.boot.SpringApplication               : Application run failed
> ...
> Caused by: org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException: A job instance already exists and is complete for parameters={message=hello}.  If you want to run this job again, change the parameters
> ...
> ~~~
>
> 其中Job Instance是根据Job Name和Job参数来唯一确定，因此相同的参数，运行第二次时，会抛异常，报告该Job Instance已经运行过一次

## 4 Input

### 4.1 ItemReader Interface

功能：通过实现`ItemReader<RecordType>`接口来定义一个bean

> 编写一个实现了`ItemReader<RecordType>`接口类之后，就可以使用这个类的对象定义一个bean
>
> 该接口实现`RecordType read()`方法，每次调用返回一条记录

`ItemREader<RecordType>`接口

> ```java
> public interface ItemReader<T> {
>     @Nullable
>     T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;
> }
> ```
>
> 它会被Spring Batch框架反复调用，直到返回null
>
> 返回null意味着通知框架，所有的input都已经读取完毕

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / inputInterfaces`

例子：

> 例如下面的`public class MyReader implements ItemReader<String>`它实现了接口要求的`public String read()`方法，然后进一步组装到`Step bean` 中
>
> ```java
> @Bean
> public MyReader myReader() {
>   	//构造时传入字符串，每次调用read都返回其中一个，全部都返回之后返回null告诉框架读取完毕
>   	//注意，如果MyReader如果不会return null，那么batch job就不会停止，它会无限运行下去
> 		return new MyReader(Arrays.asList("Foo", "Bar", "Baz"));
> }
> 
> @Bean
> public Step step1() {
> 		return stepBuilderFactory.get("step1")
>          .<String, String>chunk(2) // 每2条记录作为1个chunk
>          .reader(myReader())
>          .writer(list -> {
>             for (String curItem : list) {
>                System.out.println("curItem = " + curItem);
>             }
>          }).build();
> }
> ```
>
> 备注：传给`write()`方法的`lambda表达式`它生成了一个类，该类实现了`ItemWriter<String>`接口
>
> ```java
> public interface ItemWriter<T> {
>     void write(List<? extends T> var1) throws Exception;
> }
> ```

### 4.2 Reading From Database

功能：使用JDBC访问数据库，作为Step的Reader，演示两种Reader：`JdbcCursorItemReader`和`JdbcPagingItemReader`

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / databaseInput`

(1) `JdbcCursorItemReader`

> ```java
> @Bean
> public ItemReader<Customer> cursorItemReader() {
>     JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
>     reader.setSql("select id, firstName, lastName, birthdate from customer order by lastName, firstName");
>     reader.setDataSource(this.dataSource);
>     reader.setRowMapper(new CustomerRowMapper()); //Mapping each DB Item to POJO
>     return reader;
> }
> ```
>
> JdbcCursorItemReader：基于游标的ItemReader
>
> * 工作过程 ：(1) 打开游标（2）运行SQL（3）得到数据集合（4）遍历集合、每次返回游标所在位置的行
> * 遍历状态：靠游标的位置来实现
> * 非线程安全，如果被多线程访问，他们会操作同一个游标

(2) `JdbcPagingItemREader`

> ```java
> @Bean
> public ItemReader<Customer> pagingItemReader() {
> 	 // sort
>    Map<String, Order> sortKeys = new HashMap<>(1);
>    sortKeys.put("id", Order.ASCENDING);
> 
>    // query provider：用来生成返回1 page的SQL
>    MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
>    queryProvider.setSelectClause("id, firstName, lastName, birthdate");
>    queryProvider.setFromClause("from customer");
>    // 用来排序，也用来记录当前遍历到哪条记录，因此传入key需要能够区分每一条数据(unique key)
>    queryProvider.setSortKeys(sortKeys); 
> 
>    // reader
>    JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
>        reader.setDataSource(this.dataSource);
>        reader.setFetchSize(10); //每页10条，通常配成与chunk size相同的大小
>        reader.setRowMapper(new CustomerRowMapper());
>    reader.setQueryProvider(queryProvider);
>    return reader;
> }
> ```
>
> `JdbcPagingItemReader`：基于分页的ItemReader
>
> * 工作方式：根据JDBC的实现，每次生成 一个返回当前页的SQL，一页一页地返回数据；具体逻辑封装在传入的`QueryProvider`中  
> * 遍历状态：根据当前返回的最后一条数据的key来实现，因此key能够区分每一条数据 ，而执行的SQL也是需要根据key来排序
> * 多线程安全

其他 ：

> *  `CustomerRowMapper`实现 了`org.springframework.jdbc.core.RowMapper<Customer>`接口，它的`mapRow`方法从`ResultSet`中取出数据，生成 `Customer`（POJO）。一个`Customer`  对象对应于数据表中的一行
> * 参考：https://my.oschina.net/chkui/blog/3072495

### 4.3 Reading From XML

功能 ：读取xml文件的ItemReader

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / readingXML`

装配用于读取XML文件的`StaxEventItemReader`

> ```java
> @Bean
> public StaxEventItemReader<Customer> customerItemReader() {
>    // unmarshaller规则
>    Map<String, Class> aliases = new HashMap<>();
>    aliases.put("customer"/*xml tag*/, Customer.class /*Domain Object Class*/); 
> 
>    // unmarshaller
>    XStreamMarshaller unmarshaller = new XStreamMarshaller();
>    unmarshaller.setAliases(aliases);
> 
>    // ItemReader
>    StaxEventItemReader<Customer> reader = new StaxEventItemReader<>();
>    reader.setResource(new ClassPathResource("/data/customers.xml"));
>    reader.setFragmentRootElementName("customer"); //reader负责找到customer tag
>    reader.setUnmarshaller(unmarshaller); // unmarshaller负责生成Domain Object
> 
>    return reader;
> }
> ```

数据样本：`src/main/resources/data/customers.xml`

> ```xml
> <?xml version="1.0" encoding="UTF-8" ?>
> <customers>
>    <customer>
>       <id>1</id>
>       <firstName>Mufutau</firstName>
>       <lastName>Maddox</lastName>
>       <birthdate>2016-06-05 19:43:51PM</birthdate>
>    </customer>
>    <customer>
>       <id>2</id>
>       <firstName>Brenden</firstName>
>       <lastName>Cobb</lastName>
>       <birthdate>2016-01-06 13:18:17PM</birthdate>
>    </customer>
>    ...
> </customers>
> ```

pom.xml修改：`Spring Boot 2.4.1`（`Spring Batch 4.2.5`）创建项目时，需要增加两个依赖

> ```xml
> <!-- https://mvnrepository.com/artifact/org.springframework/spring-oxm -->
> <dependency>
>    <groupId>org.springframework</groupId>
>    <artifactId>spring-oxm</artifactId>
>    <version>5.3.2</version>
> </dependency>
> 
> <!-- https://mvnrepository.com/artifact/com.thoughtworks.xstream/xstream -->
> <dependency>
>    <groupId>com.thoughtworks.xstream</groupId>
>    <artifactId>xstream</artifactId>
>    <version>1.4.15</version>
> </dependency>
> ```

### 4.4 Reading Flat File (CSV)

功能：读取csv文件的ItemReader

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / flatFiles`

装配用于读取CSV文件的`FlatFileItemReader`

> ```java
> @Bean
> public FlatFileItemReader<Customer> customerItemReader() {
>    //（1） DelimitedLineTokenizer：将一行数据按照分隔符分成列映射到FieldSet中的各个field中
>    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
>    tokenizer.setNames(new String[] {"id", "firstName", "lastName", "birthdate"}); // 列名 
> 
>    // (2) DefaultLineMapper<Customer>：将一行数据转换成一个Domain Ojbect
>    DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
>    customerLineMapper.setLineTokenizer(tokenizer);// 把一个String转换成一个FieldSet
>    customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper()); 
>    customerLineMapper.afterPropertiesSet();
> 
>    // (3) FlatFileItemReader：组装处理csv文件用的ItemReader
>    FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
>    reader.setLinesToSkip(1);
>    reader.setResource(new ClassPathResource("/data/customer.csv"));
>    reader.setLineMapper(customerLineMapper);
>    return reader;
> }	
> ```

`CustomerFieldSetMapper`把FieldSet转换为Domain Object

> ```java
> public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {
>    @Override
>    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
>       return new Customer(fieldSet.readLong("id"),
>             fieldSet.readString("firstName"),
>             fieldSet.readString("lastName"),
>             fieldSet.readDate("birthdate", "yyyy-MM-dd HH:mm:ss"));
>    }
> }
> ```

数据样本：`src/main/resources/data/customer.csv`

> ```csv
> id,firstName,lastName,birthdate
> 1,Stone,Barrett,1964-10-19 14:11:03
> 2,Raymond,Pace,1977-12-11 21:44:30
> 3,Armando,Logan,1986-12-25 11:54:28
> ...
> ```

备注：

> * 对于不同的数据格式，可以在`org.springframework.batch.item.file.transform`包中找到不同的Tokenizor，例如`RegexLineTokenizer`，`FixedLengthTokenizer`
> * 除了`.setLinesToSkip`，`FlatFileItemReader`还有其他的配置，例如
>   * `.setComments`通过设置前缀来识别和跳过文件中的注释
>   * `.setRecordSeparatorPolicy`用来设置如何在文件中切分record
>   * `.setStrict`用来设置文件缺失是是job失败，还是忽略
>   * ……

### 4.5 Reading From Multiple Sources

功能：读取多个csv文件的ItemReader

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / multipleFlatFiles`

装配用于读取多个csv文件的`MultiResourceItemReader`

> ```java
> // Ant Based的通配符表达式，符合表达式的文件路径，会被解析成一组Resource
> @Value("classpath*:/data/customer*.csv")
> private Resource[] inputFiles;
> 
> @Bean
> public FlatFileItemReader<Customer> customerItemReader() {
>   // 参考上一小节或原始demo的代码，与上一小有差异的点列在本节的说明中
> }
> 
> @Bean
> public MultiResourceItemReader<Customer> multiResourceItemReader() {
>    MultiResourceItemReader<Customer> reader = new MultiResourceItemReader<>();
>    // 读取单个文件的任务代理给customerItemReader
>    reader.setDelegate(customerItemReader()); 
>    reader.setResources(inputFiles);
>    return reader;
> }
> ```
>
> 它是在用于读取单个`csv`文件的`FlatFileItemReader`基础上进行封装得到。
>
> * 负责处理单个文件的`customerItemReader ` 代码可以参考上一小节，其中
>
>   * 不需要调用`reader.setResource(new ClassPathResource("/data/customer.csv"))`，由外层的`MultiResourceItemReader`来调用
>   * 也不需要调用`reader.setLinesToSkip(1);`来跳过标题行，因为测试文件没有标题行
>
> * `reader.setDelegate`的参数是`ResourceAwareItemReaderItemStream`，因此也可以用于其他实现了该接口的类，例如4.2小节读取XML文件的`StaxEventItemReader`
>
>   ```java
>   public void setDelegate(ResourceAwareItemReaderItemStream<? extends T> delegate) {
>      this.delegate = delegate;
>   }
>   ```
>
> * 另外表示Domain Object的`Customer`类，可以让它实现`ResourceAware`接口，这样能够知道每一个Customer对象来自于哪个Resource
>
>   ```java
>   public class Customer implements ResourceAware {
>      private final long id;
>      private final String firstName;
>      private final String lastName;
>      private final Date birthdate;
>      private Resource resource;
>      ...
>      @Override
>      public void setResource(Resource resource) {
>         this.resource = resource;
>      }
>   }
>   ```

输入文件

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/004_spring_batch/private/demos/g04_multiple_flat_files/ 
> $ ls src/main/resources/data/
> customer1.csv customer2.csv customer3.csv customer4.csv customer5.csv
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/004_spring_batch/private/demos/g04_multiple_flat_files/ 
> $ head -n2 src/main/resources/data/customer1.csv 
> 1,Stone,Barrett,1964-10-19 14:11:03
> 2,Raymond,Pace,1977-12-11 21:44:30
> ~~~

### 4.6 ItemReader State

作用：维护Step内部文件处理的状态（State），可以提供更好的容错性

ItemReader State通过三部分来实现

> * `ItemStream`：开发者可以向框架传入当前的处理状态
> * `ExecutionContext`：维护state，从而能够让job instance重启时继续完成之前未完成的工作
> * `JobRepository`：持久化每个job instance的状态，存储在`BATCH_STEP_EXECUTION_CONTEXT`表

`ItemStream`接口要求实现3个方法：

> * `open()`：step启动时被调用、重启的step可以从context中加载state
> * `update()`：每个chunk被处理完之后被调用向context更新state，chunk是spring batch实现事务的方式
> * `close()`：step运行完毕时被调用

`BATCH_STEP_EXECUTION_CONTEXT`表中的state数据

> (1) 以4.3小节的`StaxEventItemReader`为例，存储`BATCH_STEP_EXECUTION_CONTEXT`表中的数据如下
>
> ~~~json
> {"@class":"java.util.HashMap","batch.taskletType":"org.springframework.batch.core.step.item.ChunkOrientedTasklet","StaxEventItemReader.read.count":1001,"batch.stepType":"org.springframework.batch.core.step.tasklet.TaskletStep"}
> ~~~
>
> (2) 以4.2小节的Demo为例，如果使用的是`JdbcPagingItemReader<Customer>`，存储在`BATCH_STEP_EXECUTION_CONTEXT`中的状态数据为
>
> ~~~json
> {
>     "@class": "java.util.HashMap",
>     "JdbcPagingItemReader.start.after": {
>         "@class": "java.util.LinkedHashMap",
>         "id": 1000
>     },
>     "JdbcPagingItemReader.read.count": 1001,
>     "batch.taskletType": "org.springframework.batch.core.step.item.ChunkOrientedTasklet",
>     "batch.stepType": "org.springframework.batch.core.step.tasklet.TaskletStep"
> }
> ~~~
>
> 这里`JdbcPagingItemReader`向Job Repository存入了两个状态：
>
> * `"JdbcPagingItemReader.start.after": {"id":1000}`：表示成功处理了1000条数据
> * `"JdbcPagingItemReader.read.count": 1001`：表示接下来去处理第1001条数据（这个字段在会跳过某些record的场景下有用）
>
> 这两个字段会被`JdbcPagingItemReader`用来生成获取下一页数据的SQL，当job instance失败重启时，也可以依据这两个字段从上一次执行完之后位置开始继续执行
>
> (3) `StaxEventItemWriter`和`FlatFileItemWriter`也是会向`BATCH_STEP_EXECUTION_CONTEXT`表中写入两个状态数据：
>
> * 已知成功写入的record数量
> * 已知成功写入的所有record之后的文件偏移量
>
> 这是因为写文件的“事务属性”无法通过step的chuck机制来保证，需要额外增加两个状态来保证写文件的原子性

另外还有一张表`BATCH_EXECUTION_CONTEXT`用来维护跨越多个Step的状态

### 4.7 ItemStream Interface

功能：自定义`ItemReader state`的存储

> 当job instance异常退出时，重启可以从state记录的位置开始继续运行

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / itemStream`

装配一个会保存状态的`ItemReader`

> ```java
> public class StatefulItemReader implements ItemStreamReader<String> {
>    // 用来表示当前读取状态的成员变量  
>    private int curIndex = -1;	
>    ......
>    
>    @Override
>    public String read() throws Exception {
>       //每次调用会返回一个Item
>       ......
>    }
> 
>    // 每次step启动时被调用
>    @Override
>    public void open(ExecutionContext executionContext) throws ItemStreamException {
>       if(executionContext.containsKey("curIndex")) {
>          // job重启时能够根据Context中state，从上次失败时的位置开始继续执行
>          this.curIndex = executionContext.getInt("curIndex");
>       } else {
>          this.curIndex = 0;
>          executionContext.put("curIndex", this.curIndex);
>       }
>    }
> 
>    // 每个chunk处理结束时被调用
>    @Override
>    public void update(ExecutionContext executionContext) throws ItemStreamException {
>       // 记录当前的状态到job repository
>       executionContext.put("curIndex", this.curIndex);
>    }
> 
>    // 所有数据都处理完时被调用
>    @Override
>    public void close() throws ItemStreamException {
>    }
> }
> 
> @Bean
> @StepScope
> public StatefulItemReader itemReader() {
> 		List<String> items = new ArrayList<>(100);
> 		for(int i = 1; i <= 100; i++) {
> 			items.add(String.valueOf(i));
> 		}
> 		return new StatefulItemReader(items);
> }
> ```

装配Step：

> * 对上面的例子，因为`StatefulItemReader`本身已经实现了`ItemStreamReader`接口并编写了存储状态的代码，因此使用普通的`.reader(itemReader())`就可以
>
> * 在某些比较复杂的场合，某些被组合在一个外层reader内部的StreamReader，会需要使用`.reader(outsideCompositeReader()).stream(streamItemReader())`来"manually registered into the step"，下面是一个例子（虽然是用在itemWriter()上但同样适用）：
>
>   https://docs.spring.io/spring-batch/docs/current/reference/html/readersAndWriters.html#delegatePatternAndRegistering

运行

> 处理完前41条数据时会触发异常，此时在`BATCH_STEP_EXECUTION_CONTEXT`表中可以找到该state当前的数据
>
> ~~~json
> {"@class":"java.util.HashMap","batch.taskletType":"org.springframework.batch.core.step.item.ChunkOrientedTasklet","curIndex":40,"batch.stepType":"org.springframework.batch.core.step.tasklet.TaskletStep"}
> ~~~
>
> 再次重启该batch job instance，它会从ExecutionContext中读取到`curIndex`=40，不再触发异常，并从数据41开始继续进行处理（数据41处理过两次），处理完所有数据后，state变为如下的值
>
> ~~~json
> {"@class":"java.util.HashMap","batch.taskletType":"org.springframework.batch.core.step.item.ChunkOrientedTasklet","curIndex":100,"batch.stepType":"org.springframework.batch.core.step.tasklet.TaskletStep"}
> ~~~
>
> 

## 5 Output

### 5.1 Interface ItemWrite

>

### 5.2 Writing to Database

>  

### 5.3 Writing Flat Files

>  

### 5.4 Writing to XML Files

> 

### 5.5 Writing to Multiple Destinations

> 

## 6 Processing

### 6.1 Interface: ItemProcessor 

> 

### 6.2 Filtering Items

> 

### 6.3 Validating Items

> 

### 6.4 CompositeItemProcessors

> 

## 7 Error Handling

### 7.1 Restart



### 7.2 Retry



### 7.3 Skip



### 7.4 Listeners



## 8 Scaling Jobs



### 8.1 Approaches



### 8.2 Multi-Threaded Step



### 8.3 AsyncItemProcessor / AsyncItemWriter



### 8.4 Local Partitioning 



### 8.5 Remote Chunking



## 9 Job Orchestration

### 9.1 Starting A Job



### 9.2 Stoping A Job



### 9.3 Scheduling A Job Using Spring Schedule



## 10 Spring Batch with Spring Integration

### 10.1 Launching Jobs Via Messages 



### 10.2 Informational Messages



## 

