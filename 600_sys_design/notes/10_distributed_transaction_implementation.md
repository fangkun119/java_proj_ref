[TOC]

# 分布式事务

## 01 分布式事务实现方法理解

### (1) 例子及问题描述

例子背景：客户端 → 订单系统 → 库存系统

> 订单系统
>
> ~~~java
> @Transactional
> Order createOrder() {
>     runSomeLocalSQL(); 		// 创建订单
>     runRemoteMethod();		// 远程调用库存系统，减库存
>     runOtherSqlButError();	// 执行其他操作，假设在这个环节发生错误
> }
> ~~~
>
> 库存系统
>
> ~~~java
> @Transactional
> Oeder deducStock() {
>     runSomeLocalSQL(); // 减库存
> }
> ~~~
>
> 订单系统执行`runOtherSql()`时出错，如何让库存系统也回滚
>
> 很多系统是通过非实时的方式、自动化Job等方式来修正数据，并不能实时保持数据正确

`@Transactional`注解的工作原理

> Step 1 建立连接：Connection conn = dataSource.getConnection();
>
> Step 2 开启事务：conn.setAutoCommit(false);
>
> `Step 3` 执行方法内的代码
>
> Step 4 提交事务或回滚事务：conn.commit() 或 conn.rollBack()

在上面的操作步骤中，在`runOtherSqlButError()`之前库存系统已经提交了本地事务，那么能否让库存系统来等待订单系统的操作呢？

> 库存系统执行完上面的`Step 3`之后，进入等待状态
>
> * 订单系统操作全部成功时，库存系统得到success通知，也提交本地事务
>
> * 订单系统操作故障发生回滚时，库存系统得到rollback通知，也回滚本地事务

### (2) 要实现的两个功能：操作封装；事务管理

想实现这个特性，需要解决以下问题

> `事务管理`：调用链很长时（例如：`系统A`→`系统B`→...→`系统E`），事务操作会变得复杂
>
> `本地事务操作`： 事务操作封装在Spring提供的`@Transactional`操作中，如何改变这个注解下本地事务的行为，来配合全局事务
>
> `全局事务封装`：如何添加注解，来操作全局事务

### (3) 事务管理

需要一个全局的事务管理者，为每个全局事务存储如下内容

> `GlobalTrxId`：全局事务的ID
>
> `list<SubTrxId>`：全局事务内部各个分支事务的ID

以之前的`客户端 → 订单系统 → 库存系统`的例子为例

> 订单系统：createOrder创建了一个全局事务，注册在事务管理器中
>
> 订单系统、库存系统：的本地事务成为这个全局事务的分支事务，添加到列表中
>
> 随着调用链的进行
>
> * 新的分支事务不断加入
> * 每个分支事务执行成功：只能将`list<SubTrxId>`中对应的分支事务状态置为成功
>
> 所有分支事务都成功才会触发提交，任何一个分支事务失败都会触发回滚

### (4) 本地事务操作

问题：如何让@Transaction注解的方法，在执行完之后不会立即提交，而是等待事务管理器发来提交命令之后再提交。

Spring的@Transactional注解提供的操作

> 步骤 1 建立连接：Connection conn = dataSource.getConnection();
>
> 步骤 2 开启事务：conn.setAutoCommit(false);
>
> 步骤 3 执行方法内的代码
>
> 步骤 4 提交事务或回滚事务：conn.commit() 或 conn.rollBack()

Spring的源码无法修改，因此考虑用AOP修改DataSource的代码

> 在`步骤1`中、让 `Connection conn = DataSource.getConnection()` 返回包含了处理分布式事务功能的Connection的子类。
>
> 假设命名为GlobalTrxDBConnection，AOP代码如下
>
> ~~~java
> @Aspect
> @Component
> public class DstbtTrxDataSourceAspect implements Ordered {
>     @Around("execution(* javax.sql.DataSource.getConnection(..))")
>     public Connection around(ProceedingJoinPoint point) throws Throwable {
>         Connection connection = point.procceed(); 
>         return new DstbtTrxDBConnection(connection);
>     }
>     @Override
>     public int getOrder() { return DistbtTrxOrder.DstbtTrxDataSourceAspect; /*比较低的值*/ }
> }
> ~~~

GlobalTrxDBConnection

> ~~~java
> public class DstbtTrxDBConnection implements Connections {
> 	private Connection connection;
>     public DstbtTrxDBConnection(Connection connection) {
>         this.connection = connection;
>     }
>     @override
>     public statement createStatement() throws SQLException {
>         return connection.createStatement();
>     }
>     ...
>     @override
> 	public void commit() throws SQLException {
>         /*
>         * 这里添加等待事务协调器的操作
>         */ 
>         connection.commit();
>     }
> 	...
> }
> ~~~

### (5) 全局事务封装

目标：编写一个`@DistributedTransactional`注解，启动全局事务

> ~~~java
> @Transactional
> @DistributedTransactional
> Order createOrder() {
>     runSomeLocalSQL(); 		// 创建订单
>     runRemoteMethod();		// 远程调用库存系统，减库存
>     runOtherSqlButError();	// 执行其他操作，假设在这个环节发生错误
> }
> ~~~

编写注解

> ~~~java
> @Target({ElementType.METHOD})
> @Retention(RetentionPolicy.RUNTIME)
> public @interface DistributedTransactional {
>     
> }
> ~~~

编写注解的配套切面

> ~~~java
> @Aspect
> @Component
> public class DistributedTransactional implements Ordered {
>     @Around("@annotation(com.myproj.annotation.DistributedTransactional)")
>     public void invoke(ProceedingJoinPoint point) {
>         // 创建全局分布式事务
>         String distbtTrxId = DistbtTrxManager.createDistbtTrx();
>         // 执行分布式事务
>         try {
> 			// 执行切点的方法，
> 			// * 对应上面用@Transactional注解的createOrder()方法
> 			// * 会启动本地事务切面的代码中的4步操作
> 			// * 包括：建立连接 → 开启本地事务 → 执行操作 → 等待commit或rollBack命令
>     	    point.proceed(); 
>         	// 执行成功，分布式事务状态置为commited，事务管理器会发送命令要求所有参与方提交事务
> 	        DistbtTrxManager.updateTrx(distbtTrxId, TrxStatus.commit);
>         } catch (Throwable throwable) {
>             // 执行失败，分布式事务状态置为回滚，事务管理器会发送命令要求所有参与方回滚
>             DistbtTrxManager.updateTrx(distbtTrxId, TrxStatus.rollback);
>             throwable.printStackTrace();
>         }
>     }
>     
>     @Override
>     public int getOrder() { return DistbtTrxOrder.DistributedTransactional; /*比较高的值*/  }
> } 
> ~~~
>
> 相比`DistbtTrxOrder.DstbtTrxDataSourceAspect`，上面`DistbtTrxOrder.DistributedTransactional`的值更高，在两个注解同时使用的情况下，也会优先被执行

### (6) 本地事务的commit和rollback

> 

## 2. 支持分布式事务的框架

> Seata、Icn、Sega、Zookeeper、Redis、TCC、……

