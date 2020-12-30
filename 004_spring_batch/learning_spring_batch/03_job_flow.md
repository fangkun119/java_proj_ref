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
>         .start(step1()) 
>         .next(step2()) 
>         .next(step3()) 
>         .build();			
>        // 等价于
>      // return jobBuilderFactory.get("transitionJobNext")
>         //      .start(step1()).on("COMPLETED").to(step2())
>        //      // 理解为有向图中的一条边，表示从<step2,COMPLETED>到<step3>的依赖关系
>         //      .from(step2()).on("COMPLETED").to(step3()) 
>         //      .from(step3()).on("COMPLETED").end()
>         //      .build();
> }
> ~~~

基于任务依赖有向图的配置

> ```java
> @Bean
> public Job transitionJobSimpleNext() {
>     // from(),on(),to()：增加一条任务依赖，相当于向任务依赖有向图（DAG）中添加一条有向边
>    //		from()	：用来指定基于哪个step的执行结果
>    //		on()		：用来指定基于这个step的哪种执行结果
>    //		to()		：用来指定满足from(),on()条件时，接下来执行哪个step
>    // 整条有向边的起点是`(from(),on())`二元组所表示的状态，终点是`to()`
>     return jobBuilderFactory.get("transitionJobNext")
>         // 从step1开始，step1成功后执行step2
>         .start(step1()).on("COMPLETED").to(step2())
>         // 在step2、3之间加入instance stop
>         // 程序会在step2成功后退出，下次执行从step3开始继续（需要jdbc job repository）
>         // 使用场景例如需要在两个step之间执行人工操作等场景
>         .from(step2()).on("COMPLETED").stopAndRestart(step3()) 
>         .from(step3()).end()
>         .build();
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
>          .next(innerStep2())
>          .end();
>     return flowBuilder.build();
> }
> ```

外层代码可以把Flow和外层的Step组合配置在Job中，例如

> ```java
> @Bean
> public Job flowBLastJob(@Qualifier("flowB") Flow flowB) {
>     return jobBuilderFactory.get("flowBLastJob")
>         .start(outStep1())
>         .next(outStep2())
>         .on("COMPLETED").to(flowB) //对于flow没有next()这样的快捷方式
>         .end()
>         .build();
> }
> ```

也可以将个Flow组合成一个更复杂的Flow

> ```java
> @Bean
> public Job parallelFlowsJob(@Qualifier("flowA") Flow flowA, @Qualifier("flowB") Flow flowB) {
>     FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("parallelFlow");
>     Flow parallelFlow = flowBuilder
>         .split(new SimpleAsyncTaskExecutor()).add(flowA, flowB)
>         .end();
>     return jobBuilderFactory.get("splitJob")
>         .start(outStep1())
>         .next(outStep2())
>         .on("COMPLETED").to(parallelFlow)
>         .end()
>         .build();
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
>  		.end();
> return jobBuilderFactory.get("splitJob")
>      .start(outStep1())
>      .next(outStep2())
>      .on("COMPLETED").to(parallelFlow)
>      .end()
>      .build();
> ```
>
> 方法2：`start()`后面使用`split`，创建一个并行执行的flow
>
> ```java
> @Bean
> public Job job() {
>     return jobBuilderFactory.get("job")
>         // 如果在to()或者next()之后创建并发流，应当使用方法1
>         // 方法2无法让flow2执行，即无法创建并发的flow
>         .start(flow1()).split(new SimpleAsyncTaskExecutor()).add(flow2())
>         .end()
>         .build();
> }
> ```

(2) 在`flow`中复用`tasklet`的代码

> 定义tasklet
>
> ```java
> public static class DemoTasklet implements Tasklet {
>  @Override
>  public RepeatStatus execute(
>      StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
>      System.out.println(String.format(
>          "%s has been executed on thread %s",
>          chunkContext.getStepContext().getStepName(), Thread.currentThread().getName()));
>      // 用来查看执行step的DemoTasklet对象是不是同一个对象
>      System.out.println("Tasklet hashcode: " + this.hashCode());
>      return RepeatStatus.FINISHED;
>  }
> }
> 
> // 如果不设置StepScope，这个bean会是单例的
> // 意味着全局共享同一个DemoTasklet对象，包括并发运行的两个Flow
> @Bean
> @StepScope 
> public Tasklet tasklet() {
>  return new DemoTasklet();
> }
> ```
>
> 使用tasklet
>
> ```java
> @Bean
> public Flow flow1() {
>  return new FlowBuilder<Flow>("flow1")
>      .start(stepBuilderFactory.get("step1").tasklet(tasklet()).build())
>      .build();
> }
> 
> @Bean
> public Flow flow2() {
>  return new FlowBuilder<Flow>("flow2")
>      .start(stepBuilderFactory.get("step2").tasklet(tasklet()).build())
>      .next( stepBuilderFactory.get("step3").tasklet(tasklet()).build())
>      .build();
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
>  @Override
>  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
>      // 如果需要基于前一个Step的执行状态来做判断，可以使用stepExecution.getExitStatus()
>      switch (getXYZStatus()) {
>          case XYZStatus.X:
>          case XYZStatus.Y:   
>              return new FlowExecutionStatus("GO_STEP_A");
>          case XYZStatus.Z:
>          default:
>              return new FlowExecutionStatus("GO_STEP_B");          
>      }
>  }
> }
> 
> @Bean
> public JobExecutionDecider stepABDecider() {
>  return new StepABDecider();
> }
> ```

使用`decider()`，其实就是把`decider()`当做一个节点，作为任务依赖有向图中一条边的起点或终点

> ```java
> @Bean
> public Job job() {
>  return jobBuilderFactory.get("job")
>         // "起始节点"执行成功后进入"判断节点"，调用一次"decide()"
>      .start(startStep()).next(stepABDecider()) 				
>         // "判断节点"返回"GO_STEP_A"时执行"stepA()"
>      .from(stepABDecider()).on("GO_STEP_A").to(stepA())		
>         // "判断节点"返回"GO_STEP_B"时执行"stepB()"
>      .from(stepABDecider()).on("GO_STEP_B").to(stepB())
>         // 对于"stepA()"，不论执行结果如何，都跳转到"判断节点"进行判断
>      .from(stepA()).on("*").to(stepABDecider())
>      .end()
>      .build();
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
>  // 把childJob包装在一个step中，这样它可以与普通的step和flow组合
>  Step childJobStep = new JobStepBuilder(new StepBuilder("childJobStep"))
>      .job(childJob)
>      // 在child job的层级、需要显示地指定、不能自动注入
>      .launcher(jobLauncher).repository(jobRepository).transactionManager(transactionManager)
>      .build();
> 
>  // 定义Parent Job，其中的step1只是parent job中的一个普通step
>  return jobBuilderFactory.get("parentJob")
>      .start(step1())
>      .next(childJobStep)
>      .build();
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
>      @Override
>      public void beforeChunk(ChunkContext context) {
>          System.out.println("MyChunkListener.beforeChunk is running");
>      }
> 
>      @Override
>      public void afterChunk(ChunkContext context) {
>          System.out.println("MyChunkListener.afterChunk is running");
>      }
> 
>      @Override
>      public void afterChunkError(ChunkContext context) {
>          System.out.println("MyChunkListener.afterChunkError is running");
>      }
> }
> ```
>
> 定义StepListener
>
> ```java
> public class MyStepListener implements org.springframework.batch.core.StepExecutionListener {
>      @Override
>      public void beforeStep(StepExecution stepExecution) {
>          System.out.println("MyStepListener.beforeStep is running");
>      }
> 
>      @Override
>      public ExitStatus afterStep(StepExecution stepExecution) {
>          System.out.println("MyStepListener.afterStep is running");
>          return stepExecution.getExitStatus();
>      }
> }
> ```
>
> 使用ChunkListener和StepListener
>
> ```java
> @Bean
> public Step step1() {
>  return stepBuilderFactory.get("step1")
>          .<String, String>chunk(2) // 每2条记录作为一个chunk，输入输出类型都是String
>          .reader(reader()) 								// 设置reader
>          .writer(writer()) 								// 设置writer
>          .listener(new MyStepListener())  	// 设置step listener
>          .listener(new MyChunkListener()) 	// 设置chunk listener
>          .build();
> }
> ```

(2) 定义和使用Job Listener

> 定义Job Listener
>
> ```java
> public class MyJobListener implements JobExecutionListener {
>      @Override
>      public void beforeJob(JobExecution jobExecution) {
>          String jobName = jobExecution.getJobInstance().getJobName();
>          System.out.println("jobListener.beforeJob is running");
>      }
> 
>      @Override
>      public void afterJob(JobExecution jobExecution) {
>          System.out.println("JobListener.afterJob is running");
>      }
> }
> ```
>
> 使用Job Listener
>
> ```java
> @Bean
> public Job listenerJob() {
>  return jobBuilderFactory.get("listenerJob")
>          .start(step1())
>          .listener(new MyJobListener())
>          .build();
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
>      // "#{jobParameters['message']}": SEPL Expression
>      @Value("#{jobParameters['message']}") String message
> ) {
>      return (stepContribution, chunkContext) -> {
>          System.out.println(this.hashCode() + ": " + message);
>          return RepeatStatus.FINISHED;
>      };
> }
> 
> @Bean
> public Step step1() {
>  	return stepBuilderFactory.get("step1")
>          // 参数设为null，
>          // 是因为Spring并不会在定义tasklet的时候传参、而是在运行时进行注入
>          // 这里的null仅仅是占位用
>          .tasklet(helloWorldTasklet(null))
>          .build();
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