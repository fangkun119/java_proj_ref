<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [7 Error Handling](#7-error-handling)
  - [7.1 Restart](#71-restart)
  - [7.2 Retry](#72-retry)
  - [7.3 Skip](#73-skip)
  - [7.4 Listeners](#74-listeners)
  - [7.5 SkipListeners](#75-skiplisteners)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

> 配套代码
>
> 1. 官网[O‘Reilly Learning Spring Batch](http://shop.oreilly.com/product/0636920044673.do)
>
>     访问官网注册账号后，可开通课程观看视屏并下载源代码，新用户免费可以使用10天
>
> 2. https://github.com/fangkun119/spring_batch_demos （Private Git Repository）
>
>     代码版本已更新到当前最新的Spring Batch 4.2.5（对应Spring Boot 2.4.1）

## 7 Error Handling

### 7.1 Restart

内容：模拟故障导致job instance失败 ，解决故障之后重启job instance的场景

> Batch Job重启时，它会
>
> * 跳过已经completed的Step，从上次失败的Step中开始  
> * 在重新执行上次失败的Step时，跳过已经处理完毕的chuck，重新处理发生失败chuck并继续处理后续chunk

原始Demo：`Learning Spring Batch - Working Files / Chapter 7 / restart`

步骤：

> 用在StepContext中设置key "problem_solved"来让代码模拟遇到故障，然后在故障解除后重启的场景
>
> ```java
> @Bean
> @StepScope 
> public Tasklet restartTasklet() {
> 	return new Tasklet() {
> 		@Override
> 		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
> 			// Step名称 
> 			String stepName = chunkContext.getStepContext().getStepName();
> 			// 读Step Context数据：这个API获得的数据是只读版本，向其中写数据会触发异常
> 			Map<String, Object> readOnlyContext 
> 				= chunkContext.getStepContext().getStepExecutionContext();
> 			if(false == readOnlyContext.containsKey("problem_solved")) {
> 				// 模拟故障场景
> 				System.out.println("exception in step " +  stepName);
> 				// 写Step Context：这个API返回的不是Map而是ExecutionContext对象，可以写数据 
> 				chunkContext
> 					.getStepContext()
> 					.getStepExecution().getExecutionContext()
> 					.put("problem_solved", true); 
> 				throw new RuntimeException("exception when executing step "  +  stepName);
> 			} else {
> 				// 模拟bug已经解决的场景
> 				System.out.println("problems has been solved in step " + stepName);
> 				return RepeatStatus.FINISHED;
> 			}
> 		}
> 	};
> }
> 
> @Bean
> public Step step1() {
> 	return stepBuilderFactory.get("step1").tasklet(restartTasklet()).build();
> }
> 
> @Bean
> public Step step2() {
> return stepBuilderFactory.get("step2").tasklet(restartTasklet()).build();
> }
> 
> @Bean
> public Job job() {
> return jobBuilderFactory.get("restart_application_demo")
>    .start(step1())
>    .next(step2())
>    .build();
> }
> ```

运行：

> 第一次运行：step1触发异常，job instance失败，但是step1会在StepContext中设置“problem_solved" key并持久化到jdbc  job repository中
>
> ~~~bash
> INFO 5153 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=restart_application_demo]] launched with the following parameters: [{}]
> INFO 5153 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
> exception in step step1
> ERROR 5153 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step step1 in job restart_application_demo
> java.lang.RuntimeException: exception when executing step step1
> 
> ......
> 
> INFO 5153 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 22ms
> INFO 5153 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=restart_application_demo]] completed with the following parameters: [{}] and the following status: [FAILED] in 49ms
> INFO 5153 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
> INFO 5153 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
> 
> Process finished with exit code 0
> ~~~
>
> 第二次运行：step1从StepContext中读取到”problem_solved"  key，进入模拟正常运行代码分支，不再触发异常。step1成功执行 ，但step2触发异常，job instance失败。同时step2在StepContext中设置了“problem_solved"  key。
>
> ~~~bash
> INFO 5170 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=restart_application_demo]] launched with the following parameters: [{}]
> INFO 5170 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
> problems has been solved in step step1
> INFO 5170 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 22ms
> INFO 5170 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step2]
> exception in step step2
> ERROR 5170 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step step2 in job restart_application_demo
> 
> java.lang.RuntimeException: exception when executing step step2
> 
> …………
> 
> INFO 5170 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step2] executed in 20ms
> INFO 5170 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=restart_application_demo]] completed with the following parameters: [{}] and the following status: [FAILED] in 105ms
> INFO 5170 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
> INFO 5170 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
> 
> Process finished with exit code 0
> ~~~
>
> 第三次运行：job instance发现step1之前已经运行成功，跳过step1，执行step2。step2从StepContext中读取到”problem_solved"  key，进入模拟程序正常运行的分支，step2运行 完毕
>
> ~~~bash
> INFO 5290 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=restart_application_demo]] launched with the following parameters: [{}]
> INFO 5290 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=9, version=3, name=step1, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
> INFO 5290 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=11, version=3, name=step2, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
> INFO 5290 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=restart_application_demo]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 55ms
> INFO 5290 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
> INFO 5290 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
> ~~~

### 7.2 Retry

内容：

> 有些场景，故障原因非常简单，比如网络状况不好、下游服务宕机等，而根据程序的逻辑恢复也非常简单，只需要重试即可。
>
> * 在这种场景下，可以使用Spring Batch的自动重试
> * spring batch容许指定异常类型来识别这种靠重试可以解决的故障

原始Demo：`Learning Spring Batch - Working Files / Chapter 7 / retry`

步骤：

(1) Processor 业务逻辑，外部代码捕捉到CustomRetryableException代表该故障可以通过重试来修复

> ```java
> public class RetryItemProcessor implements ItemProcessor<String, String> {
> 	private boolean enableRetry = false;
> 	private int attemptCount = 0;
> 
> 	@Override
> 	public String process(String item) throws Exception {
> 		// 假定item 42会有导致ItemProcessor发送异常的故障，在重试5次之后该故障消失
> 		boolean hasError = item.equalsIgnoreCase("42") && attemptCount <= 5;
> 		// (1) 发生故障时
> 		if (hasError) {
> 			if (enableRetry) {
> 				// 开启retry功能，抛出CustomRetryableException
> 				attemptCount++;
> 				System.out.println("process item " + item + " failed, will retry");
> 				throw new CustomRetryableException("process failed, attempt: " + attemptCount);
> 			} else {
> 				// 未开启retry功能，抛出其他异常
> 				throw new Exception("process failed and can not retry");
> 			}
> 		}
> 		// (2) 没发生故障时，正常处理
> 		String result = String.valueOf(Integer.valueOf(item) * -1);
> 		System.out.println("process：" + item + " --> " + result);
> 		return result;
> 	}
> 
> 	// 是否开启retry
> 	public void setEnableRetry(boolean enableRetry) {
> 		this.enableRetry = enableRetry;
> 	}
> }
> ```

(2) Writer业务逻辑

> 也Processor类似，假定`item  -84`在写入时会出现故障
>
> * 如果已经开启retry，会抛出CustomRetryableException
> * 如果没有开启retry，会抛出其他Exception

(3) 装配`RetryItemProcessor`和`RetryItemWriter`，设置容许触发retry的Exception类，并指定retry上限

> ```java
> @Bean
> @StepScope
> public RetryItemProcessor processor(@Value("#{jobParameters['retry']}")String retry) {
>    	RetryItemProcessor processor = new RetryItemProcessor();
>    	boolean enableRetry = StringUtils.hasText(retry)
>    		&& Arrays.stream(retry.split(",")).anyMatch(param -> param.equalsIgnoreCase("processor"));
>    	processor.setEnableRetry(enableRetry);
>    	return processor;
>    }
> 
> @Bean
> @StepScope
> public RetryItemWriter writer(@Value("#{jobParameters['retry']}")String retry) {
> 	RetryItemWriter writer = new RetryItemWriter();
>    	boolean enableRetry = StringUtils.hasText(retry) 
>    		&& Arrays.stream(retry.split(",")).anyMatch(param -> param.equalsIgnoreCase("writer"));
>    	writer.setEnableRetry(enableRetry);
>    	return writer;
>    }
>    
> @Bean
> public Step step1() {
> return stepBuilderFactory.get("step")
> 	.<String, String>chunk(10)
>    	.reader(reader())
>    	.processor(processor(null))
>    	.writer(writer(null))
>    	// 调用`falultTolerent()`将返回一个能使用retry等方法的Builder
>    	.faultTolerant() 
>    	// 捕获CustomRetryableException时重试，捕获其他Exception会导致job fail
>    	.retry(CustomRetryableException.class)
>    	// 这个step最多retry 15次
>    	.retryLimit(15)
>    	.build();
>    }
>    ```

测试：不传入命令行参数，让step跑其他Exception时，会触发异常，job失败

> ~~~
> -28
> -29
> process：30 --> -30
> process：31 --> -31
> process：32 --> -32
> process：33 --> -33
> process：34 --> -34
> process：35 --> -35
> process：36 --> -36
> process：37 --> -37
> process：38 --> -38
> process：39 --> -39
> begin writing items: 
> -30
> -31
> -32
> -33
> -34
> -35
> -36
> -37
> -38
> -39
> process：40 --> -40
> process：41 --> -41
> process：40 --> -40
> process：41 --> -41
> ERROR 7672 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step step in job job
> 
> ...
> 
> INFO 7672 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step] executed in 223ms
> INFO 7672 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=job]] completed with the following parameters: [{}] and the following status: [FAILED] in 301ms
> ~~~

测试：传入命令行参数`retry=processor`

> 可以看到在ItemProcessor处理数据时，发生了5次chunk重试，直到第6次业务逻辑不再抛出异常
>
> 但是会在writing的时候job失败
>
> ~~~
> -38
> -39
> process：40 --> -40
> process：41 --> -41
> process item 42 failed, will retry
> process：40 --> -40
> process：41 --> -41
> process item 42 failed, will retry
> process：40 --> -40
> process：41 --> -41
> process item 42 failed, will retry
> process：40 --> -40
> process：41 --> -41
> process item 42 failed, will retry
> process：40 --> -40
> process：41 --> -41
> process item 42 failed, will retry
> process：40 --> -40
> process：41 --> -41
> process item 42 failed, will retry
> process：40 --> -40
> process：41 --> -41
> process：42 --> -42
> process：43 --> -43
> process：44 --> -44
> process：45 --> -45
> process：46 --> -46
> process：47 --> -47
> process：48 --> -48
> process：49 --> -49
> begin writing items: 
> -40
> -41
> -42
> -43
> -44
> -45
> -46
> -47
> -48
> -49
> 
> ......
> 
> begin writing items: 
> -80
> -81
> -82
> -83
> process：80 --> -80
> ERROR 7785 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step step in job job
> ~~~

测试：传入命令行参数`retry=processor,writer`，可以看到程序在5次chunk重试，直到业务逻辑不再抛出异常时执行成功

> ~~~bash
> begin writing items: 
> -80
> -81
> -82
> -83
> writing item -84 failed, will retry
> process：80 --> -80
> process：81 --> -81
> process：82 --> -82
> process：83 --> -83
> process：84 --> -84
> process：85 --> -85
> process：86 --> -86
> process：87 --> -87
> process：88 --> -88
> process：89 --> -89
> begin writing items: 
> -80
> -81
> -82
> -83
> -84
> -85
> -86
> -87
> -88
> -89
> ~~~

### 7.3 Skip

内容：跳过处理失败的Item

原始Demo：`Learning Spring Batch - Working Files / Chapter 7 / skip`

步骤：

(1) Processor业务逻辑：

> 与7.2小节类似，假定item 42会引发故障：
>
> * 如果开启enableSkip，会抛出`CustomSkipableException`
> * 如果未开启enableSkip，会抛出其他异常
>
> ```java
> public class SkipItemProcessor implements ItemProcessor<String, String> {
>    private boolean enableSkip = false;
> 
>    @Override
>    public String process(String item) throws Exception {
>       // 假定item 42会有导致ItemProcessor发送异常的故障
>       boolean hasError = item.equalsIgnoreCase("42");
>       // (1) 发生故障时
>       if (hasError) {
>          if (enableSkip) {
>             // 开启retry功能时抛出CustomSkipException
>             System.out.println("process item " + item + " failed, skip");
>             throw new CustomSkipException("process failed，skip");
>          } else {
>             // 未开启retry功能时抛出其他异常
>             throw new Exception("process failed and can not retry");
>          }
>       }
>       // (2) 没发生故障时，正常处理
>       String result = String.valueOf(Integer.valueOf(item) * -1);
>       System.out.println("process：" + item + " --> " + result);
>       return result;
>    }
> 
>    public void setEnableSkip(boolean enableSkip) {
>       this.enableSkip = enableSkip;
>    }
> }
> ```

(2) Writer业务逻辑

> 与processor类似，假定`Item -84`会引发故障
>
> * 如果开启enableSkip，会抛出`CustomSkipableException`
> * 如果未开启enableSkip，会抛出其他异常

(3) 装配`SkipItemProcessor`和`SkipItemWriter`

> ```java
> @Bean
> @StepScope
> public SkipItemProcessor processor(@Value("#{jobParameters['skip']}") String skip) {
>    	SkipItemProcessor processor = new SkipItemProcessor();
>    	boolean enableSkip = StringUtils.hasText(skip)
>    		&& Arrays.stream(skip.split(",")).anyMatch(param -> param.equalsIgnoreCase("processor"));
>    	processor.setEnableSkip(enableSkip);
>    	return processor;
>    }
> 
> @Bean
> @StepScope
> public SkipItemWriter writer(@Value("#{jobParameters['skip']}") String skip) {
> 	SkipItemWriter writer = new SkipItemWriter();
>    	boolean enableSkip = StringUtils.hasText(skip) 
>            && Arrays.stream(skip.split(",")).anyMatch(param -> param.equalsIgnoreCase("processor"));
>    	writer.setEnableSkip(enableSkip);
>    	return writer;
>    }
>    
> @Bean
> public Step step1() {
> return stepBuilderFactory.get("step")
> 	.<String, String>chunk(10)
>    	.reader(reader())
>    	.processor(processor(null))
>    	.writer(writer(null))
>    	.faultTolerant()
>    	.skip(CustomSkipException.class)
>    	.skipLimit(15)
>    	.build();
>    }
>    ```

运行：命令行参数`skip=processor,writer`

> 对于ItemProcessor阶段的故障，直接跳过出错的`Item 42`
>
> 对于ItemWriter阶段的故障
>
> * 虽说会跳过出错`Item -84`，但是`Item -84`所在的整个chunk都要重写。
> * 并且与之前一次写入整个chunck不同，skip时，会一个record一个record地重写，这是因为框架需要找到是哪个item导致了chunk写失败
>
> ~~~bash
> process：40 --> -40
> process：41 --> -41
> process item 42 failed, skip
> process：40 --> -40
> process：41 --> -41
> 
> ...
> 
> process：88 --> -88
> process：89 --> -89
> begin writing items: 
> -80
> -81
> -82
> -83
> writing item -84 failed, skip
> process：80 --> -80
> begin writing items: 
> -80
> process：81 --> -81
> begin writing items: 
> -81
> process：82 --> -82
> begin writing items: 
> -82
> process：83 --> -83
> begin writing items: 
> -83
> process：84 --> -84
> begin writing items: 
> writing item -84 failed, skip
> process：85 --> -85
> begin writing items: 
> -85
> process：86 --> -86
> begin writing items: 
> -86
> process：87 --> -87
> begin writing items: 
> -87
> process：88 --> -88
> begin writing items: 
> -88
> process：89 --> -89
> begin writing items: 
> -89
> process：90 --> -90
> process：91 --> -91
> process：92 --> -92
> process：93 --> -93
> process：94 --> -94
> process：95 --> -95
> process：96 --> -96
> process：97 --> -97
> process：98 --> -98
> process：99 --> -99
> begin writing items: 
> -90
> -91
> -92
> -93
> -94
> -95
> -96
> -97
> -98
> -99
> ~~~

### 7.4 Listeners

内容：设置监听器来让框架在程序运行的特定阶段调用预设的回调函数

原始Demo：`Learning Spring Batch - Working Files / Chapter 7 / listeners`

步骤：与3.6节相同

###  7.5 SkipListeners

内容：使用之前的`Skip`容错机制，同时设置监听器监听`Skip`事件的发生

原始Demo：`Learning Spring Batch - Working Files / Chapter 7 / skipRetryListeners`

步骤：

(1) processor跳过错误数据的处理逻辑

> ```java
> public class SkipItemProcessor implements ItemProcessor<String, String> {
> 	@Override
> 	public String process(String item) throws Exception {
> 		// 假定item 42会有导致ItemProcessor发送异常的故障
> 		boolean hasError = item.equalsIgnoreCase("42");
> 		if (hasError) {
> 			// (1) 发生故障时
> 			System.out.println("process item " + item + " skip");
> 			throw new CustomSkipException("process failed，item: " + item);
> 		} else {
> 			// (2) 没发生故障时，正常处理
> 			String result = String.valueOf(Integer.valueOf(item) * -1);
> 			System.out.println("process：" + item + " --> " + result);
> 			return result;
> 		}
> 	}
> }
> ```

(2) writer跳过错误数据的处理逻辑

> ```java
> public class SkipItemWriter implements ItemWriter<String> {
> 	@Override
> 	public void write(List<? extends String> items) throws Exception {
> 		System.out.println("begin writing items: ");
> 		for (String item : items) {
> 			// 假定item -84会有导致ItemWriter发送异常的故障
> 			boolean hasError = item.equalsIgnoreCase("-84");
> 			if (hasError) {
> 				System.out.println("writing item " + item + " failed, skip");
>    				throw new CustomSkipException("write fail, item: " + item);
> 			} else {
> 				// 没有故障
> 				System.out.println(item);
> 			}
> 		}
> 	}
> }
> ```

(2) skip listener

> ```java
> public class CustomSkipListener implements SkipListener {
>    	@Override
>    	public void onSkipInRead(Throwable t) {
>    		//拿不到record，但是可以在Reader代码中提供一些信息
>    	}
> 
>    	@Override
>    	public void onSkipInWrite(Object item, Throwable t) {
>    		System.out.println(">> Skipping " + item + " because writing it caused the error: " + t.getMessage());
>    	}
> 
>    	@Override
>    	public void onSkipInProcess(Object item, Throwable t) {
>    		System.out.println(">> Skipping " + item + " because processing it caused the error: " + t.getMessage());
>    	}
> }
> ```

(3) 装配

> ```java
> @Bean
> @StepScope
> public SkipItemProcessor processor() {
>    	return new SkipItemProcessor();
> }
> 
> @Bean
> @StepScope
> public SkipItemWriter writer() {
>    	return new SkipItemWriter();
> }
> 
> @Bean
> public Step step1() {
>    return stepBuilderFactory.get("step")
>          .<String, String>chunk(10)
>          .reader(reader())
>          .processor(processor())
>          .writer(writer())
>          .faultTolerant() //返回FaultStepBuilder以提供skip等方法
>          .skip(CustomSkipException.class)
>          .skipLimit(15) //skip超过15个item时会job fail
>          .listener(new CustomSkipListener())
>          .build();
> }
> ```

运行：

> 对于ItemProcessor的故障，可以看到日志">> Skipping 42 because processing it caused the error: process failed，item: 42"，说明Listener中的回调函数被执行
>
> ~~~bash
> process：40 --> -40
> process：41 --> -41
> process item 42 skip
> process：40 --> -40
> process：41 --> -41
> process：43 --> -43
> process：44 --> -44
> process：45 --> -45
> process：46 --> -46
> process：47 --> -47
> process：48 --> -48
> process：49 --> -49
> begin writing items: 
> -40
> -41
> -43
> -44
> -45
> -46
> -47
> -48
> -49
> >> Skipping 42 because processing it caused the error: process failed，item: 42
> ~~~
>
> 对于ItemWriter的故障，同样可以看到”>> Skipping -84 because writing it caused the error: write fail, item: -84“，说明Listener的回调函数被执行
>
> ~~~bash
> process：85 --> -85
> process：86 --> -86
> process：87 --> -87
> process：88 --> -88
> process：89 --> -89
> begin writing items: 
> -80
> -81
> -82
> -83
> writing item -84 failed, skip
> process：80 --> -80
> begin writing items: 
> -80
> process：81 --> -81
> begin writing items: 
> -81
> process：82 --> -82
> begin writing items: 
> -82
> process：83 --> -83
> begin writing items: 
> -83
> process：84 --> -84
> begin writing items: 
> writing item -84 failed, skip
> process：85 --> -85
> begin writing items: 
> -85
> >> Skipping -84 because writing it caused the error: write fail, item: -84
> process：86 --> -86
> begin writing items: 
> -86
> process：87 --> -87
> begin writing items: 
> -87
> process：88 --> -88
> begin writing items: 
> -88
> process：89 --> -89
> begin writing items: 
> -89
> process：90 --> -90
> process：91 --> -91
> process：92 --> -92
> process：93 --> -93
> process：94 --> -94
> process：95 --> -95
> ~~~