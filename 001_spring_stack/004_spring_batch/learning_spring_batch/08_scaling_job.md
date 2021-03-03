<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [8 Scaling Jobs](#8-scaling-jobs)
  - [8.1 Approaches](#81-approaches)
  - [8.2 Multi-Threaded Step](#82-multi-threaded-step)
  - [8.3 AsyncItemProcessor / AsyncItemWriter](#83-asyncitemprocessor--asyncitemwriter)
  - [8.4 Local Partitioning](#84-local-partitioning)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 8 Scaling Jobs

### 8.1 Approaches

Spring  Batch提供了4种方法来增强处理数据的能力

1 `Multi-Threaded Step`

> 每个Chunk有自己的线程， 这样根据线程数量，可以有多个chunk同时被处理

2 `AsyncItemProcessor / AsyncItemWriter`

> 有时processor会阻塞在一些等待操作中 （例如IO型等待），此时可以使用该方法 
>
> * ItemProcessor返回一个`Future`类型的对象，并在另一个线程执行操作
> * ItemWriter阻塞在`Future`对象上，当Processor执行完毕时，unwrape Future对象执行写入操作
>
> 这样processor可以并发执行 ，同时writer也不用等待所有processor任务都完成，进而在单一JVM中扩展processor的处理能力

3 `Local / Remote Partitioning` 

> 数据分成多个分区，各个分区之间并行处理，以一个master-slaves的模式来扩展batch job处理数据的能力 
>
> * 既可以以多线程的方式，每个线程处理自己分到的partition
> * 也可以以remote slave的方式，由多个JVM来处理

4 `Remote Chunking`

> remote master-slaves模式，master读取数据并将数据分发给远程的slave JVMs来进行processing和writing

### 8.2 Multi-Threaded Step

内容：支持多线程的Step，该Step处理的每个chunck都会交给其中一个thread来处理

文档 ：

> * https://docs.spring.io/spring-batch/docs/current/reference/html/index-single.html#multithreadedStep
> * https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#multithreadedStep

原始Demo：`Learning Spring Batch - Working Files / Chapter 8 / multithreadedStep`

步骤：

(1) 装配Item Reader：

> 与4.2节的`pagingItemReader()`基本相同、除了需要`reader.setSaveState(false)`
>
> ```java
> @Bean
> public JdbcPagingItemReader<Customer> pagingItemReader() {
>     ...
>     
>     // jdbc paging item reader
>     JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
>     
>     ...
> 	
>     // 根据该API源代码注释：
>     //     always set it to false, 
>     //     if the reader is being used in a concurrent environment
>     reader.setSaveState(false); //不向job repository保存状态
>     return reader;
> }
> ```

(2) 装配Item Writer：

> 与5.2节的`customerItemWriter()`相同 

(3) 装配Step：

> ```java
> @Bean
> public Step step1() throws Exception {
>    // async executor
>    SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
>    executor.setConcurrencyLimit(2);
> 
>    // build the step with executor
>    return stepBuilderFactory.get("step1")
>          .<Customer, Customer>chunk(1000)
>          .reader(pagingItemReader())
>          .writer(customerItemWriter())
>          .taskExecutor(executor)
>          .build();
> }
> ```

### 8.3 AsyncItemProcessor / AsyncItemWriter

内容：

> 有时processor会阻塞在一些等待操作中 （例如IO型等待），此时可以使用该方法 
>
> * ItemProcessor返回一个`Future`类型的对象，并在另一个线程执行操作
> * ItemWriter阻塞在`Future`对象上，当Processor执行完毕时，unwrape Future对象执行写入操作
>
> 这样processor可以并发执行 ，同时writer也不用等待所有processor任务都完成，进而在单一JVM中扩展processor的处理能力

文档 ：

> * https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#scalabilityParallelSteps
>
> * https://docs.spring.io/spring-batch/docs/current/reference/html/index-single.html#scalabilityParallelSteps

原始Demo：`Learning Spring Batch - Working Files / Chapter 8 / asyncItemProcessorItemWriter`

步骤：

(1) 把`itemProcessor`代理给`asyncItemProcessor`

> ```java
> @Bean
> public ItemProcessor itemProcessor() {
>    return new ItemProcessor<Customer, Customer>() {
>       @Override
>       public Customer process(Customer item) throws Exception {
>          //模拟程序运行过程中遇到的慢速操作阻塞、例如等待下游返回
>          Thread.sleep(new Random().nextInt(10));
>          //执行process操作
>          return new Customer(item.getId(),
>                item.getFirstName().toUpperCase(),
>                item.getLastName().toUpperCase(),
>                item.getBirthdate());
>       }
>    };
> }
> 
> @Bean
> public AsyncItemProcessor asyncItemProcessor() throws Exception {
>    AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor();
>    asyncItemProcessor.setDelegate(itemProcessor()); //注入itemProcessor bean
>    asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
>    asyncItemProcessor.afterPropertiesSet(); //检查属性设置是否完整
>    return asyncItemProcessor;
> }
> ```

(2) 把`customerItemWriter`代理给`asyncItemWriter`

> ```java
> @Bean
> public JdbcBatchItemWriter customerItemWriter() {
>    ... //与5.2节的customerItemWriter()相同
> }
> 
> @Bean
> public AsyncItemWriter asyncItemWriter() throws Exception {
>    AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
>    asyncItemWriter.setDelegate(customerItemWriter()); //注入customerItemWriter bean
>    asyncItemWriter.afterPropertiesSet();  //检查属性设置是否完整
>    return asyncItemWriter;
> }
> ```

(3) 装配step

> ```java
> @Bean
> public Step step1() throws Exception {
>    return stepBuilderFactory.get("step1")
>          .chunk(1000)
>          .reader(pagingItemReader())
>          .processor(asyncItemProcessor()) 
>          .writer(asyncItemWriter())
>          .build();
> }
> ```

### 8.4 Local Partitioning

内容：

> 数据分成多个分区，各个分区之间并行处理，以一个master-slaves的模式来扩展batch job处理数据的能力 
>
> * master负责将数据分成多个partition
> * slave负责处理分配到的partition
>
> 下面是在单个JVM中以多线程方式实现master-slaves的方式

文档 ：

> * https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#partitioning
>
> * https://docs.spring.io/spring-batch/docs/current/reference/html/index-single.html#partitioning

原始Demo：

> * `Learning Spring Batch - Working Files / Chapter 8 / localPartitioning`

Local Partitioning的实现步骤：

(1) 实现Partitioner类，装配partitioner bean

> 一个实现`org.springframework.batch.core.partition.support.Partitioner`接口的类
>
> 这个类需要`@override`一个`partition`方法，以ExecutionContext以<key,value>的 
>
> ```java
> public ColumnRangePartitioner implements Partitioner {
>     // 根据某张表，某一列，按取值范围分成partitionCount个分区
>     @Override
>     public Map<String,ExecutionContext> partition(int gridSize /*分多少个区*/) {
>         ......
>         // 存储返回结果：Map<String, ExecutionContext>
>         // * key	: "partition${number}"
>         // * value	: ExecutionContext{
>         //					"minValue":${min_column_val_of_current_partition}, 
>         //					"maxValue":${max_column_val_of_current_partition}}
>         Map<String, ExecutionContext> result
>             = new HashMap<String, ExecutionContext>();
> 
>     }
> }
> ```
>
> 装配：
>
> ~~~java
> @Bean
> public ColumnRangePartitioner partitioner() {
>    ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();
>    ... //设置ColumnRangePartitioner.partitio(...)方法用到的各种属性 
>    return columnRangePartitioner;
> }
> ~~~

(2) 根据分配配置来为ItemReader分配任务

> 在后面的步骤(3)中使用`PartitionStepBuilder`，框架会在运行时将上面`partitioner bean`的`partition()` 方法返回的ExecutionConext注入到ItemReader中
>
> ```java
> @Bean
> @StepScope
> public JdbcPagingItemReader<Customer> pagingItemReader(
>       @Value("#{stepExecutionContext['minValue']}")Long minValue,
>       @Value("#{stepExecutionContext['maxValue']}")Long maxValue) {
>    // print partitioner parameters
>    System.out.println("reading " + minValue + " to " + maxValue);
> 
>    // sort keys：给query provider用作分页依据
>    ...
> 
>    // query provider: 用来生成获取每一页数据的SQL
>    MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
>    queryProvider.setSelectClause("id, firstName, lastName, birthdate");
>    queryProvider.setFromClause("from customer");
>    queryProvider.setWhereClause("where id >= " + minValue + " and id < " + maxValue);
>    queryProvider.setSortKeys(sortKeys);
> 
>    // jdbc item reader
>    ...
>    return reader;
> }
> ```

(3) 装配Step

> ```java
> // slave step：与普通的step没有差别，除了reader bean可以有参数注入以外
> @Bean
> public Step slaveStep() {
>    return stepBuilderFactory.get("slaveStep")
>          .<Customer, Customer>chunk(1000)
>          .reader(pagingItemReader(null, null) /*null是这个bean的占位参数*/)
>          .writer(customerItemWriter() /*与5.2节的customerItemWriter相同*/)
>          .build();
> }
> 
> // master step
> @Bean
> public Step step1() throws Exception {
>    SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
>    executor.setConcurrencyLimit(4); // 最多4个线程
>    
>    return stepBuilderFactory.get("step1")
>          // partitioner方法返回PartitionStepBuilder，用来设置分区相关的设置
>          .partitioner(slaveStep().getName(), partitioner()) 
>          // 分区相关的设置
>          .step(slaveStep()) 	 // partiation参数注入给slaveStep bean
>          .gridSize(4) 			 // 分4个区
>          .taskExecutor(executor) // 各分区异步执行
>          .build();
> }
> ```





