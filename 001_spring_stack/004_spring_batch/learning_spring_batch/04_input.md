<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [4 Input](#4-input)
  - [4.1 ItemReader Interface](#41-itemreader-interface)
  - [4.2 Reading From Database](#42-reading-from-database)
  - [4.3 Reading From XML](#43-reading-from-xml)
  - [4.4 Reading Flat File (CSV)](#44-reading-flat-file-csv)
  - [4.5 Reading From Multiple Sources](#45-reading-from-multiple-sources)
  - [4.6 ItemReader State](#46-itemreader-state)
  - [4.7 ItemStream Interface](#47-itemstream-interface)
  - [4.8 JsonItemReader](#48-jsonitemreader)

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

## 	4 Input

### 4.1 ItemReader Interface

功能：通过实现`ItemReader<RecordType>`接口来定义一个ItermReader bean

> 编写一个实现了`ItemReader<RecordType>`接口类之后，就可以使用这个类的对象定义一个bean
>
> 该接口实现`RecordType read()`方法，每次调用返回一条记录

`ItemReader<RecordType>`接口

> ```java
> public interface ItemReader<T> {
>  	@Nullable
>  	T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;
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
>    	//构造时传入字符串，每次调用read都返回其中一个，全部都返回之后返回null告诉框架读取完毕
>    	//注意，如果MyReader如果不会return null，那么batch job就不会停止，它会无限运行下去
>  	return new MyReader(Arrays.asList("Foo", "Bar", "Baz"));
> }
> 
> @Bean
> public Step step1() {
>  	return stepBuilderFactory.get("step1")
>    		.<String, String>chunk(2) // 每2条记录作为1个chunk
>    		.reader(myReader())
>    		.writer(list -> {
>    			for (String curItem : list) {
>    			System.out.println("curItem = " + curItem);
>    		}
>    	}).build();
> }
> ```
>
> 备注：传给`write()`方法的`lambda表达式`它生成了一个类，该类实现了`ItemWriter<String>`接口
>
> ```java
> public interface ItemWriter<T> {
>    	void write(List<? extends T> var1) throws Exception;
> }
> ```

### 4.2 Reading From Database

功能：使用JDBC访问数据库，作为Step的Reader，演示两种Reader：`JdbcCursorItemReader`和`JdbcPagingItemReader`

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / databaseInput`

(1) `JdbcCursorItemReader`

> ```java
> @Bean
> public ItemReader<Customer> cursorItemReader() {
>    	JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
>    	reader.setSql("select id, firstName, lastName, birthdate from customer order by lastName, firstName");
>    	reader.setDataSource(this.dataSource);
>    	reader.setRowMapper(new CustomerRowMapper()); //Mapping each DB Item to POJO
>    	return reader;
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
> 	// sort
>    	Map<String, Order> sortKeys = new HashMap<>(1);
>    	sortKeys.put("id", Order.ASCENDING);
> 
>    	// query provider：用来生成返回1 page的SQL
>    	MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
>    	queryProvider.setSelectClause("id, firstName, lastName, birthdate");
>    	queryProvider.setFromClause("from customer");
>    	// JdbcPagingItemReader会为每一页生成一个SQL，为了保证这些SQL获取的数据之间连贯一致，必须进行排序
>        // 同时JdbcPagingItemReader也根据sorting key来判断下一页从哪里开始，因此也需要这个key是unique key
>    	queryProvider.setSortKeys(sortKeys); 
> 
>    	// reader
>    	JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
>    		reader.setDataSource(this.dataSource);
>    		reader.setFetchSize(10); //每页10条，通常配成与chunk size相同的大小
>    		reader.setRowMapper(new CustomerRowMapper());
>    	reader.setQueryProvider(queryProvider);
>    	return reader;
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
> *  参考：https://my.oschina.net/chkui/blog/3072495

### 4.3 Reading From XML

功能 ：读取xml文件的ItemReader

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / readingXML`

装配用于读取XML文件的`StaxEventItemReader`

> ```java
> @Bean
> public StaxEventItemReader<Customer> customerItemReader() {
>    	// unmarshaller规则
>    	Map<String, Class> aliases = new HashMap<>();
>    	aliases.put("customer"/*xml tag*/, Customer.class /*Domain Object Class*/); 
> 
>    	// unmarshaller
>    	XStreamMarshaller unmarshaller = new XStreamMarshaller();
>    	unmarshaller.setAliases(aliases);
> 
>    	// ItemReader
>    	StaxEventItemReader<Customer> reader = new StaxEventItemReader<>();
>    	reader.setResource(new ClassPathResource("/data/customers.xml"));
>    	reader.setFragmentRootElementName("customer"); //reader负责找到customer tag
>    	reader.setUnmarshaller(unmarshaller); // unmarshaller负责生成Domain Object
> 
>    	return reader;
> }
> ```

数据样本：`src/main/resources/data/customers.xml`

> ```xml
> <?xml version="1.0" encoding="UTF-8" ?>
> <customers>
>     <customer>
>            <id>1</id>
>            <firstName>Mufutau</firstName>
>            <lastName>Maddox</lastName>
>            <birthdate>2016-06-05 19:43:51PM</birthdate>
>     </customer>
>     <customer>
>            <id>2</id>
>            <firstName>Brenden</firstName>
>            <lastName>Cobb</lastName>
>            <birthdate>2016-01-06 13:18:17PM</birthdate>
>     </customer>
>     ...
> </customers>
> ```

pom.xml修改：`Spring Boot 2.4.1`（`Spring Batch 4.2.5`）创建项目时，需要增加两个依赖

> ```xml
> <!-- https://mvnrepository.com/artifact/org.springframework/spring-oxm -->
> <dependency>
>     <groupId>org.springframework</groupId>
>     <artifactId>spring-oxm</artifactId>
>     <version>5.3.2</version>
> </dependency>
> 
> <!-- https://mvnrepository.com/artifact/com.thoughtworks.xstream/xstream -->
> <dependency>
>     <groupId>com.thoughtworks.xstream</groupId>
>     <artifactId>xstream</artifactId>
>     <version>1.4.15</version>
> </dependency>
> ```

### 4.4 Reading Flat File (CSV)

功能：读取csv文件的ItemReader

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / flatFiles`

装配用于读取CSV文件的`FlatFileItemReader`

> ```java
> @Bean
> public FlatFileItemReader<Customer> customerItemReader() {
>    	//（1） DelimitedLineTokenizer：将一行数据按照分隔符分成列映射到FieldSet中的各个field中
>    	DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
>    	tokenizer.setNames(new String[] {"id", "firstName", "lastName", "birthdate"}); // 列名 
> 
>    	// (2) DefaultLineMapper<Customer>：将一行数据转换成一个Domain Ojbect
>    	DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
>    	customerLineMapper.setLineTokenizer(tokenizer);// 把一个String转换成一个FieldSet
>    	customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper()); 
>    	customerLineMapper.afterPropertiesSet();
> 
>    	// (3) FlatFileItemReader：组装处理csv文件用的ItemReader
>    	FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
>    	reader.setLinesToSkip(1);
>    	reader.setResource(new ClassPathResource("/data/customer.csv"));
>    	reader.setLineMapper(customerLineMapper);
>    	return reader;
> }	
> ```

`CustomerFieldSetMapper`把FieldSet转换为Domain Object

> ```java
> public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {
>    	@Override
>    	public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
>    		return new Customer(fieldSet.readLong("id"),
>    			fieldSet.readString("firstName"),
>    			fieldSet.readString("lastName"),
>    			fieldSet.readDate("birthdate", "yyyy-MM-dd HH:mm:ss"));
>    	}
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
>     * `.setComments`通过设置前缀来识别和跳过文件中的注释
>     * `.setRecordSeparatorPolicy`用来设置如何在文件中切分record
>     * `.setStrict`用来设置文件缺失是是job失败，还是忽略
>     * ……

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
>    	// 参考上一小节或原始demo的代码，与上一小有差异的点列在本节的说明中
> }
> 
> @Bean
> public MultiResourceItemReader<Customer> multiResourceItemReader() {
>    	MultiResourceItemReader<Customer> reader = new MultiResourceItemReader<>();
>    	// 读取单个文件的任务代理给customerItemReader
>    	reader.setDelegate(customerItemReader()); 
>    	reader.setResources(inputFiles);
>    	return reader;
> }
> ```
>
> 它是在用于读取单个`csv`文件的`FlatFileItemReader`基础上进行封装得到。
>
> * 负责处理单个文件的`customerItemReader ` 代码可以参考上一小节，其中
>
>     * 不需要调用`reader.setResource(new ClassPathResource("/data/customer.csv"))`，由外层的`MultiResourceItemReader`来调用
>     * 也不需要调用`reader.setLinesToSkip(1);`来跳过标题行，因为测试文件没有标题行
>
> * `reader.setDelegate`的参数是`ResourceAwareItemReaderItemStream`，因此也可以用于其他实现了该接口的类，例如4.2小节读取XML文件的`StaxEventItemReader`
>
>     ```java
>     public void setDelegate(ResourceAwareItemReaderItemStream<? extends T> delegate) {
>     	this.delegate = delegate;
>     }
>     ```
>
> * 另外表示Domain Object的`Customer`类，可以让它实现`ResourceAware`接口，这样能够知道每一个Customer对象来自于哪个Resource
>
>     ```java
>     public class Customer implements ResourceAware {
>     	private final long id;
>     	private final String firstName;
>     	private final String lastName;
>     	private final Date birthdate;
>     	private Resource resource;
>     	...
>     	@Override
>     	public void setResource(Resource resource) {
>     		this.resource = resource;
>     	}
>     }
>     ```

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

**作用**：维护Step内部文件处理的状态（State），可以提供更好的容错性

ItemReader State通过三部分来实现

> * `ItemStream`：开发者可以向框架传入当前的处理状态
> * `ExecutionContext`：维护state，从而能够让job instance重启时继续完成之前未完成的工作
> * `JobRepository`：持久化每个job instance的状态，存储在`BATCH_STEP_EXECUTION_CONTEXT`表

**`ItemStream`接口**

> 该接口要求实现3个方法
>
> * `open()`：step启动时被调用、重启的step可以从context中加载state
> * `update()`：每个chunk被处理完之后被调用向context更新state，chunk是spring batch实现事务的方式
> * `close()`：step运行完毕时被调用

**`BATCH_STEP_EXECUTION_CONTEXT`表中的状态数据**

(1) 以4.3小节的`StaxEventItemReader`为例，存储`BATCH_STEP_EXECUTION_CONTEXT`表中的数据如下

> ~~~json
>{"@class":"java.util.HashMap","batch.taskletType":"org.springframework.batch.core.step.item.ChunkOrientedTasklet","StaxEventItemReader.read.count":1001,"batch.stepType":"org.springframework.batch.core.step.tasklet.TaskletStep"}
> ~~~
> 

(2) 以4.2小节的Demo为例，如果使用的是`JdbcPagingItemReader<Customer>`，存储在`BATCH_STEP_EXECUTION_CONTEXT`中的状态数据为

> ~~~json
>{
> 	"@class": "java.util.HashMap",
> 	"JdbcPagingItemReader.start.after": {
> 		"@class": "java.util.LinkedHashMap",
>		"id": 1000
> 	},
>	"JdbcPagingItemReader.read.count": 1001,
> 	"batch.taskletType": "org.springframework.batch.core.step.item.ChunkOrientedTasklet",
> 	"batch.stepType": "org.springframework.batch.core.step.tasklet.TaskletStep"
>  }
>  ~~~
>    
>    这里`JdbcPagingItemReader`向Job Repository存入了两个状态：
>  
>  * `"JdbcPagingItemReader.start.after": {"id":1000}`：表示成功处理了1000条数据
>  * `"JdbcPagingItemReader.read.count": 1001`：表示接下来去处理第1001条数据（这个字段在会跳过某些record的场景下有用）
>  
> 这两个字段会被`JdbcPagingItemReader`用来生成获取下一页数据的SQL，当job instance失败重启时，也可以依据这两个字段从上一次执行完之后位置开始继续执行
> 

 **`StaxEventItemWriter`和`FlatFileItemWriter`**

> 也会向`BATCH_STEP_EXECUTION_CONTEXT`表中写入两个状态数据
>
> * 已知成功写入的record数量
> * 已知成功写入的所有record之后的文件偏移量
> 
>这是因为写文件的“事务属性”无法通过step的chuck机制来保证，需要额外增加两个状态来保证写文件的原子性

另外还有一张表`BATCH_EXECUTION_CONTEXT`用来维护跨越多个Step的状态

### 4.7 ItemStream Interface

功能：自定义`ItemReader state`的存储

> 当job instance异常退出时，重启可以从state记录的位置开始继续运行

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / itemStream`

装配一个会保存状态的`ItemReader`

> ```java
> public class StatefulItemReader implements ItemStreamReader<String> {
>    	// 用来表示当前读取状态的成员变量  
>    	private int curIndex = -1;	
>    	......
> 
>    	@Override
>    	public String read() throws Exception {
>    		//每次调用会返回一个Item
>    		......
>    	}
> 
>    	// 每次step启动时被调用
>    	@Override
>    	public void open(ExecutionContext executionContext) throws ItemStreamException {
>    		if(executionContext.containsKey("curIndex")) {
>    			// job重启时能够根据Context中state，从上次失败时的位置开始继续执行
>    			this.curIndex = executionContext.getInt("curIndex");
>    		} else {
>    			this.curIndex = 0;
>    			executionContext.put("curIndex", this.curIndex);
>    		}
>    	}
> 
>    	// 每个chunk处理结束时被调用
>    	@Override
>    	public void update(ExecutionContext executionContext) throws ItemStreamException {
>    		// 记录当前的状态到job repository
>    		executionContext.put("curIndex", this.curIndex);
>    	}
> 
>    	// 所有数据都处理完时被调用
>    	@Override
>    		public void close() throws ItemStreamException {
>    	}
> }
> 
> @Bean
> @StepScope
> public StatefulItemReader itemReader() {
> 	List<String> items = new ArrayList<>(100);
> 	for(int i = 1; i <= 100; i++) {
> 		items.add(String.valueOf(i));
> 	}
> 	return new StatefulItemReader(items);
> }
> ```

装配Step：

> * 对上面的例子，因为`StatefulItemReader`本身已经实现了`ItemStreamReader`接口并编写了存储状态的代码，因此使用普通的`.reader(itemReader())`就可以
>
> * 在某些比较复杂的场合，某些被组合在一个外层reader内部的StreamReader，会需要使用`.reader(outsideCompositeReader()).stream(streamItemReader())`来"manually registered into the step"，下面是一个例子（虽然是用在itemWriter()上但同样适用）：
>
>     https://docs.spring.io/spring-batch/docs/current/reference/html/readersAndWriters.html#delegatePatternAndRegistering

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

### 4.8 JsonItemReader

> https://spring.io/blog/2018/05/31/spring-batch-4-1-0-m1-released