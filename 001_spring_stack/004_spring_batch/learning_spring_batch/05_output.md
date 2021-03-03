<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [5 Output](#5-output)
  - [5.1 Interface ItemWriter](#51-interface-itemwriter)
  - [5.2 Writing to Database](#52-writing-to-database)
  - [5.3 Writing Flat Files](#53-writing-flat-files)
  - [5.4 Writing to XML Files](#54-writing-to-xml-files)
  - [5.5 Writing to Multiple Destinations](#55-writing-to-multiple-destinations)
- [](#)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 5 Output

### 5.1 Interface ItemWriter

内容：通过实现`ItemWriter`接口来装配一个`ItemWriter` bean

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / itemWriter`

实现`ItemWriter`接口

> ```java
> public class SysOutItemWriter implements ItemWriter<String> {
>    // 在一个chunk被处理完时被调用
>    @Override
>    public void write(List<? extends String> items /*一个chunck的所有item*/) throws Exception {
>         // 打印日志观察write方法何时被调用
>         System.out.println("The size of this chunk was: " + items.size());
>         // 打印Item观察每次调用传给writer哪些数据
>         for (String item : items) {
>             System.out.println(">> " + item);
>         }
>    }
> }
> ```

装配

> ```java
> @Bean
> public SysOutItemWriter itemWriter() {
>     return new SysOutItemWriter();
> }
> 
> @Bean
> public Step step() {
>     return stepBuilderFactory.get("item_writer_demo_step")
>         .<String, String>chunk(10)  //每10条数据一个chunk
>         .reader(itemReader())
>         .writer(itemWriter())
>         .build();
> }
> ```

运行日志中可以看到，每个chunk（10条数据）挑用一次 `SysOutItemWriter`的`write`方法，而传给write方法的`List`就是当前chuck的所有数据

> ~~~bash
> The size of this chunk was: 10
> >> 71
> >> 72
> >> 73
> >> 74
> >> 75
> >> 76
> >> 77
> >> 78
> >> 79
> >> 80
> The size of this chunk was: 10
> >> 81
> >> 82
> >> 83
> >> 84
> >> 85
> >> 86
> >> 87
> >> 88
> >> 89
> >> 90
> ~~~
>
> 输入数据是用`ListItemReader<Integer>`构造的测试数据，数据内容为1到100

### 5.2 Writing to Database

内容：装配能够像数据库写数据的`ItemWriter` bean

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / databaseOutput`

装配`JdbcBatchItemWriter`

> ```java
> @Bean
> public JdbcBatchItemWriter<Customer> customerItemWriter() {
>   // JdbcBatchItemWriter在一个jdbc batch update中写入所有数据
>   // 线程安全，write方法在一个事务中被执行
>    JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
> 
>   // 设置DataSource、SQL模板、SQL模板参数Provider
>    itemWriter.setDataSource(this.dataSource);
>    itemWriter.setSql(
>       "INSERT INTO CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
>    itemWriter.setItemSqlParameterSourceProvider(
>       new BeanPropertyItemSqlParameterSourceProvider());
> 
>   // 检查必须的properties是否都已经设置，并返回ItemWriter
>    itemWriter.afterPropertiesSet();
>    return itemWriter;
> }
> 
> @Bean
> public Step step1() {
>     return stepBuilderFactory.get("step1")
>         .<Customer, Customer>chunk(10) //每10条记录一个chunk
>         .reader(customerItemReader())
>         .writer(customerItemWriter())
>         .build();
> }
> ```
>
> `Customer`是普通的domain object
>
> 除了`JdbcBatchItemWriter`，还有`JpaItemWriter`，`HibernateItemWriter`，其他开源的lib也提供自己的ItemWriter，例如Mybatis提供的`MyBatisItemWriter`

### 5.3 Writing Flat Files

内容：装配能够写普通文件的`ItemWriter` bean

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / flatFileOutput`

`FlatFileItemWriter`：

> 在一个chunck的最后时刻执行写操作，在job instance失败的情况下，如何fault tolerance，见后续的第7小节

在@Configuration类中装配Item Writer Bean

> ```java
> @Bean
> public FlatFileItemWriter<Customer> customerItemWriter() throws Exception {
>     // file resource generated from template
>     String customerOutputPath = File.createTempFile("customerOutput_", ".out").getAbsolutePath();
>     System.out.println(">> Output Path: " + customerOutputPath);
>     Resource resource = new FileSystemResource(customerOutputPath);
> 
>     // item reader
>     FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
>     // (1) aggregator：决定Domain Object如何map到写文件的格式中
>     // itemWriter.setLineAggregator(new PassThroughLineAggregator<>());
>     itemWriter.setLineAggregator(new CustomerLineAggregator());
>     // (2) 设置resource
>     itemWriter.setResource(new FileSystemResource(customerOutputPath));
>     // (3) 检查是否所有的必需的properties都已经设置
>     itemWriter.afterPropertiesSet();
>     return itemWriter;
> }
> ```

`PassThroughLineAggregator`

> 如果使用`PassThroughLineAggregator`，将直接调用domain object的`toString()`方法来输出

`CustomerLineAggregator()`

> 自定义输出格式，例如下面的代码，使用`jackson.databind.ObjectMapper`将domain object序列化成Json
>
> ```java
> package com.javaprojref.springbatch.g05_flat_file_output.domain;
> 
> import com.fasterxml.jackson.core.JsonProcessingException;
> import com.fasterxml.jackson.databind.ObjectMapper;
> import org.springframework.batch.item.file.transform.LineAggregator;
> 
> public class CustomerLineAggregator implements LineAggregator<Customer> {
>     private ObjectMapper objectMapper = new ObjectMapper();
> 
>     @Override
>     public String aggregate(Customer item) {
>         try {
>             return objectMapper.writeValueAsString(item);
>         } catch (JsonProcessingException e) {
>             throw new RuntimeException("Unable to serialize Customer", e);
>         }
>  }
> }
> ```

### 5.4 Writing to XML Files

内容：装配能够写XML文件的`ItemWriter` bean

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / xmlFileOutput`

StaxEventItemWriter：

> 在一个chunck的最后时刻执行写操作，在job instance失败的情况下，如何fault tolerance，见后续的第7小节

装配`ItemWriter`

> ```java
> @Bean
> public StaxEventItemWriter<Customer> customerItemWriter() throws Exception {
>     // XML标签、与Domain Object的映射关系
>     Map<String, Class> aliases = new HashMap<>();
>     aliases.put("customer", Customer.class); 
> 
>     // marshaller：使用了XMLStreamMarshaller
>     // 也可以使用任何实现org.springframework.oxm.Marshaller接口的类
>     XStreamMarshaller marshaller = new XStreamMarshaller();
>     marshaller.setAliases(aliases);
> 
>     // 输出文件、文件名根据模板生成
>     String customerOutputPath 
>         = File.createTempFile("customerOutput", ".xml").getAbsolutePath();
>     System.out.println(">> Output Path: " + customerOutputPath);
>     Resource outputResource = new FileSystemResource(customerOutputPath);
> 
>     // Item Writer
>     StaxEventItemWriter<Customer> itemWriter = new StaxEventItemWriter<>();
>     itemWriter.setRootTagName("customers"); // root element名称
>     itemWriter.setMarshaller(marshaller);
>     itemWriter.setResource(outputResource);
>     itemWriter.afterPropertiesSet(); //检查属性设置
> 		return itemWriter;
> }
> ```
>
> 其中的Customer是普通的POJO

### 5.5 Writing to Multiple Destinations

内容：装配能够向多个Destination写数据的`ItemWriter` bean

原始Demo：`Learning Spring Batch - Working Files / Chapter 4 / writingMultipleDestinations`

步骤：

(1) 装配一个`StaxEventItemWriter<Customer>` bean，与5.4节的一样

(2) 装配一个`FlatFileItemWriter<Customer>` bean，与5.3节的一样

(3.A) 如果想让每条数据都输出两份，一份输出到`(1)`一份输出到`(2)`

> 装配一个CompositeItemWriter
>
> ```java
> @Bean
> public CompositeItemWriter<Customer> itemWriter() throws Exception {
>  List<ItemWriter<? super Customer>> writers = new ArrayList<>(2);
> 
>  writers.add(xmlItemWriter());
>  writers.add(jsonItemWriter());
> 
>  CompositeItemWriter<Customer> itemWriter = new CompositeItemWriter<>();
> 
>  itemWriter.setDelegates(writers);
>  itemWriter.afterPropertiesSet();
> 
>  return itemWriter;
> }
> ```
>
> 装配Step，因为`CompositeItemWriter`实现了`ItemStream`接口，因此不需要像（3.B）那样调用`.stream`来显示注册
>
> ~~~java
> @Bean
> public Step step1() throws Exception {
> return stepBuilderFactory.get("step1")
>    .<Customer, Customer>chunk(10)
>    .reader(pagingItemReader())
>    .writer(itemWriter())
>    // .stream(xmlItemWriter())
>    // .stream(jsonItemWriter())
>    .build();
> }
> ~~~

(3.B) 如果想让数据一部分输出到`(1)`，一部分输出到`(2)`

>  编写一个`Classifier<Customer, ItemWriter<? super Customer>>`类，用来决定将每个`Customer`交给哪个ItemWriter来处理
>
>  ```java
>  public class CustomerClassifier implements Classifier<Customer, ItemWriter<? super Customer>> {
>      private ItemWriter<Customer> evenItemWriter;
>    private ItemWriter<Customer> oddItemWriter;
>  
>    public CustomerClassifier(
>      ItemWriter<Customer> evenItemWriter, ItemWriter<Customer> oddItemWriter) {
>    this.evenItemWriter = evenItemWriter;
>    this.oddItemWriter = oddItemWriter;
>      } 
>  
>    @Override
>    public ItemWriter<? super Customer> classify(Customer customer) {
>      return customer.getId() % 2 == 0 ? evenItemWriter : oddItemWriter;
>    }
>  }
>  ```
>
>  装配`ClassifierCompositeItemWriter<Customer>`：它使用`(3)`提供的`Classifier`将`(1)`和`(2)`提供的`ItemWriter<Customer>`组合在一起
>
>  ```java
>  @Bean
>  public ClassifierCompositeItemWriter<Customer> itemWriter() throws Exception {
>    ClassifierCompositeItemWriter<Customer> itemWriter = new ClassifierCompositeItemWriter<>();
>    itemWriter.setClassifier(new CustomerClassifier(xmlItemWriter(), jsonItemWriter()));
>      return itemWriter;
>  }
>  ```
>
>  注册被代理的Item Writer为`ItemStream``
>
>  * `StaxEventItemWriter`和`FlatFileItemWriter`都实现了`ItemStream`，都能够在写数据时保存当前的state到job repository。
>  * 然而在这个例子中，他们被`ClassifierCompositeItemWriter`代理，同时这个writer又没有实现`ItemStream`接口，框架无法感知到这两个`ItemStream`。因此需要显式地使用`.stream`方法来注册他们。
>
>  ```java
>  @Bean
>  public Step step1() throws Exception {
>      return stepBuilderFactory.get("step1")
>            .<Customer, Customer>chunk(10)
>            .reader(pagingItemReader())
>            .writer(itemWriter())
>            .stream(xmlItemWriter())
>            .stream(jsonItemWriter())
>            .build();
>  }
>  ```

## 