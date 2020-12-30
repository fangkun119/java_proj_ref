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
> return new Tasklet() {
>    @Override
>    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
>       // Step名称 
>       String stepName = chunkContext.getStepContext().getStepName();
>       // 读Step Context数据：这个API获得的数据是只读版本，向其中写数据会触发异常
>       Map<String, Object> readOnlyContext = chunkContext
>           .getStepContext().getStepExecutionContext();
>       if(false == readOnlyContext.containsKey("problem_solved")) {
>          // 模拟故障场景
>          System.out.println("exception in step " +  stepName);
>          // 写Step Context：这个API返回的不是Map而是ExecutionContext对象，可以写数据 
>          chunkContext.getStepContext().getStepExecution()
>              .getExecutionContext().put("problem_solved", true); 
>          throw new RuntimeException("exception when executing step "  +  stepName);
>       } else {
>          // 模拟bug已经解决的场景
>          System.out.println("problems has been solved in step " + stepName);
>          return RepeatStatus.FINISHED;
>       }
>    }
> };
> }
> 
> @Bean
> public Step step1() {
> return stepBuilderFactory.get("step1").tasklet(restartTasklet()).build();
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
>       .start(step1())
>       .next(step2())
>       .build();
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
> 				throw new RuntimeException("process failed and can not retry");
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

### 7.3 Skip

内容：

原始Demo：`Learning Spring Batch - Working Files / Chapter 7 / skip`

步骤：



### 7.4 Listeners

内容：

原始Demo：`Learning Spring Batch - Working Files / Chapter 7 / listeners`

步骤：



###  7.5 SkipRetryListeners

内容：

原始Demo：`Learning Spring Batch - Working Files / Chapter 7 / skipRetryListeners`

步骤：

