## 6 Processing

> 用来处理每个record的业务逻辑

### 6.1 Interface: ItemProcessor 

内容：用于对item数据做加工的processor

> * `public interface ItemProcessor<I, O>`：输出和输出的类型可以不一样

原始Demo：`Learning Spring Batch - Working Files / Chapter 6 / itemProcessorInterface`

步骤：

(1) 组装processor bean

> processor的业务逻辑：UpperCaseItemProcessor.java
>
> ```java
> public class UpperCaseItemProcessor implements ItemProcessor<Customer, Customer> {
> @Override
> public Customer process(Customer item) throws Exception {
>    // 对每一个Item，都会调用一次process方法
>    // 为了保证幂等性，不要影响Reader的读取
>    return new Customer(item.getId(),
>          item.getFirstName().toUpperCase(),
>          item.getLastName().toUpperCase(),
>          item.getBirthdate());
> }
> }
> ```
>
> 在@configuration类中装配processor bean：JobConfiguration.java
>
> ```java
> @Bean
> public UpperCaseItemProcessor itemProcessor() {
> return new UpperCaseItemProcessor();
> }
> ```

(2) 注入到step中 

> 在@configuration类中装配step bean
>
> ```java
> @Bean
> public Step step1() throws Exception {
> return stepBuilderFactory.get("step1")
>       .<Customer, Customer>chunk(10)
>       .reader(pagingItemReader())
>       .processor(itemProcessor())
>       .writer(customerItemWriter())
>       .build();
> }
> ```

查看效果

> 输出：
>
> ~~~bash
> INFO 2496 --- [           main] .j.s.g.ItemProcessorInterfaceApplication : No active profile set, falling back to default profiles: default
> >> Output Path: /var/folders/nb/n2wl0lms2g57q00_t5qd2nsc0000gn/T/customerOutput12962825073639398888.xml
> ~~~
>
> 查看文件：可以看到firstName和lastName的值 都已经被改成了大写 
>
> ~~~bash
> $ cat /var/folders/nb/n2wl0lms2g57q00_t5qd2nsc0000gn/T/customerOutput12962825073639398888.xml
> <?xml version="1.0" encoding="UTF-8"?><customers><customer><id>1</id><firstName>STONE</firstName><lastName>BARRETT</lastName><birthdate class="sql-date">1964-10-19</birthdate></customer><customer><id>2</id><firstName>RAYMOND</firstName><lastName>PACE</lastName>…………
> ~~~

### 6.2 Filtering Items

内容：用于过滤item的processor

原始Demo：`Learning Spring Batch - Working Files / Chapter 6 / filteringItemProcessor`

步骤：

(1) 组装processor bean

> 业务逻辑
>
> ```java
> public class FilteringItemProcessor implements ItemProcessor<Customer, Customer> {
> @Override
> public Customer process(Customer item) throws Exception {
>    if(item.getId() % 2 == 0) {
>       return null; //返回null表示过滤
>    } else {
>       return item;
>    }
> }
> }
> ```
>
> 装配成bean
>
> ```java
> @Bean
> public FilteringItemProcessor itemProcessor() {
> return new FilteringItemProcessor();
> }
> ```

(2) 将itemProcessor装配到step中：与6.1小节相同  

查看效果 

> 日志：
>
> ~~~bash
> INFO 2562 --- [           main] .j.s.g.FilteringItemProcessorApplication : No active profile set, falling back to default profiles: default
> >> Output Path: /var/folders/nb/n2wl0lms2g57q00_t5qd2nsc0000gn/T/customerOutput14479604712625325353.out
> ~~~
>
> 查看文件：id为偶数的都被过滤了
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/004_spring_batch/private/demos/g04_multiple_flat_files/ 
> $ cat  /var/folders/nb/n2wl0lms2g57q00_t5qd2nsc0000gn/T/customerOutput14479604712625325353.out | head 
> {"id":1,"firstName":"Stone","lastName":"Barrett","birthdate":-164188800000}
> {"id":3,"firstName":"Armando","lastName":"Logan","birthdate":535737600000}
> {"id":5,"firstName":"Cassandra","lastName":"Moses","birthdate":-419760000000}
> {"id":7,"firstName":"Upton","lastName":"Morrow","birthdate":97516800000}
> {"id":9,"firstName":"Sybill","lastName":"Nolan","birthdate":-584611200000}
> {"id":11,"firstName":"Ingrid","lastName":"Jackson","birthdate":-388915200000}
> {"id":13,"firstName":"Xaviera","lastName":"Gillespie","birthdate":-140688000000}
> {"id":15,"firstName":"Fatima","lastName":"Combs","birthdate":296928000000}
> {"id":17,"firstName":"Felicia","lastName":"Vinson","birthdate":-316771200000}
> {"id":19,"firstName":"Ramona","lastName":"Acosta","birthdate":-237542400000}
> ~~~

### 6.3 Validating Items

内容：对ItemReader读入的数据进行业务检查

>  将`Validating`放在 processor中可以使其与数据读取解耦 

原始Demo：`Learning Spring Batch - Working Files / Chapter 6 / itemValidation`

步骤：

(1) 组装processor bean

> 业务逻辑
>
> ```java
> public class CustomerValidator implements Validator<Customer> {
> @Override
> public void validate(Customer value) throws ValidationException {
>    // 打印customer id以便在日志中观察输出处理情况
>    System.out.println(value.getId() + "\t: "  + value.getFirstName());
>    // 假定name以'A'开始的都是异常数据
>    if(value.getFirstName().startsWith("A")) {
>       throw new ValidationException("First names that begin with A are invalid: " + value);
>    }
> }
> }
> ```
>
> 装配成bean
>
> ```java
> @Bean
> public ValidatingItemProcessor<Customer> itemProcessor() {
> 	ValidatingItemProcessor<Customer> customerValidatingItemProcessor =
>       new ValidatingItemProcessor<>(new CustomerValidator());
> 
>  // 设为false时，遇到ValidationException，job instance会抛异常并，标记为fail
> 	// 设为true时，遇到ValidationException，会跳过这条数据，继续执行
> 	customerValidatingItemProcessor.setFilter(true);
> 
> 	return customerValidatingItemProcessor;
> }
> ```

(2) 将itemProcessor装配到step中：与6.1小节相同  

程序输出（`customerValidatingItemProcessor.setFilter(false)`，使用`mem-based job repository`）

> ~~~bash
> INFO 4050 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
> 1	: Stone
> 2	: Raymond
> 3	: Armando
> ERROR 4050 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step step1 in job job
> 
> org.springframework.batch.item.validator.ValidationException: First names that begin with A are invalid: Customer{id=3, firstName='Armando', lastName='Logan', birthdate=1986-12-24}
> ...
> INFO 4050 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 413ms
> INFO 4050 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=job]] completed with the following parameters: [{}] and the following status: [FAILED] in 423ms
> 
> Process finished with exit code 0
> ~~~

程序输出（`customerValidatingItemProcessor.setFilter(true)`时，使用`mem-based job repository`）

> ~~~bash
> INFO 4059 --- [           main] c.j.s.g.ItemValidationApplication        : No active profile set, falling back to default profiles: default
> >> Output Path: /var/folders/nb/n2wl0lms2g57q00_t5qd2nsc0000gn/T/customerOutput4756479172819217761.out
> ...
> INFO 4059 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
> 1	: Stone
> 2	: Raymond
> 3	: Armando
> 4	: Latifah
> ...
> 999	: Indigo
> 1000	: Raphael
> INFO 4059 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 3s595ms
> INFO 4059 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=job]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 3s615ms
> Process finished with exit code 0
> ~~~
>
> job instance运行成功，但是以firstName以"A"开头的数据，都被忽略跳过 
>
> ~~~bash
> $ head -10 /var/folders/nb/n2wl0lms2g57q00_t5qd2nsc0000gn/T/customerOutput4756479172819217761.out
> {"id":1,"firstName":"Stone","lastName":"Barrett","birthdate":-164188800000}
> {"id":2,"firstName":"Raymond","lastName":"Pace","birthdate":250617600000}
> {"id":4,"firstName":"Latifah","lastName":"Barnett","birthdate":-329644800000}
> {"id":5,"firstName":"Cassandra","lastName":"Moses","birthdate":-419760000000}
> {"id":7,"firstName":"Upton","lastName":"Morrow","birthdate":97516800000}
> {"id":8,"firstName":"Melodie","lastName":"Velasquez","birthdate":-526636800000}
> {"id":9,"firstName":"Sybill","lastName":"Nolan","birthdate":-584611200000}
> {"id":10,"firstName":"Glenna","lastName":"Little","birthdate":-515923200000}
> {"id":11,"firstName":"Ingrid","lastName":"Jackson","birthdate":-388915200000}
> {"id":12,"firstName":"Duncan","lastName":"Castaneda","birthdate":285696000000}
> ~~~

### 6.4 CompositeItemProcessors

内容：将多个processor组装在一起

> 工作原理类似`chained processors`，前一个processor的输出作为下一个processor的输入 

原始Demo：`Learning Spring Batch - Working Files / Chapter 6 / compositeItemProcessor`

步骤：

(1) `UpperCaseItemProcessor`的业务逻辑：同6.1小节

(2) `FilteringItemProcessor`的业务逻辑：同6.2小节

(3) 装配`CompositeItemProcessors`

> ```java
> @Bean
> public CompositeItemProcessor<Customer, Customer> itemProcessor() throws Exception {
> CompositeItemProcessor<Customer, Customer> processor = new CompositeItemProcessor<>();
> processor.setDelegates(Arrays.asList(
>             new FilteringItemProcessor(), new UpperCaseItemProcessor()));
> processor.afterPropertiesSet();
> return processor;
> }
> ```

(3) 将itemProcessor装配到step中：与6.1小节相同  

查看效果

> 日志 
>
> ~~~bash
> INFO 2655 --- [           main] .j.s.g.CompositeItemProcessorApplication : No active profile set, falling back to default profiles: default
> >> Output Path: /var/folders/nb/n2wl0lms2g57q00_t5qd2nsc0000gn/T/customerOutput13477426665290581876.out
> ~~~
>
> 查看文件 
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/004_spring_batch/private/demos/g04_multiple_flat_files/ 
> $ head -n10 /var/folders/nb/n2wl0lms2g57q00_t5qd2nsc0000gn/T/customerOutput13477426665290581876.out
> {"id":1,"firstName":"STONE","lastName":"BARRETT","birthdate":-164188800000}
> {"id":3,"firstName":"ARMANDO","lastName":"LOGAN","birthdate":535737600000}
> {"id":5,"firstName":"CASSANDRA","lastName":"MOSES","birthdate":-419760000000}
> {"id":7,"firstName":"UPTON","lastName":"MORROW","birthdate":97516800000}
> {"id":9,"firstName":"SYBILL","lastName":"NOLAN","birthdate":-584611200000}
> {"id":11,"firstName":"INGRID","lastName":"JACKSON","birthdate":-388915200000}
> {"id":13,"firstName":"XAVIERA","lastName":"GILLESPIE","birthdate":-140688000000}
> {"id":15,"firstName":"FATIMA","lastName":"COMBS","birthdate":296928000000}
> {"id":17,"firstName":"FELICIA","lastName":"VINSON","birthdate":-316771200000}
> {"id":19,"firstName":"RAMONA","lastName":"ACOSTA","birthdate":-237542400000}
> ~~~

## 