<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [01 常见数据库并发量](#01-%E5%B8%B8%E8%A7%81%E6%95%B0%E6%8D%AE%E5%BA%93%E5%B9%B6%E5%8F%91%E9%87%8F)
- [02 并发请求处理](#02-%E5%B9%B6%E5%8F%91%E8%AF%B7%E6%B1%82%E5%A4%84%E7%90%86)
  - [(1) 使用数据库事务处理并发请求](#1-%E4%BD%BF%E7%94%A8%E6%95%B0%E6%8D%AE%E5%BA%93%E4%BA%8B%E5%8A%A1%E5%A4%84%E7%90%86%E5%B9%B6%E5%8F%91%E8%AF%B7%E6%B1%82)
  - [(2) 借助UPDATE语句自带行锁](#2-%E5%80%9F%E5%8A%A9update%E8%AF%AD%E5%8F%A5%E8%87%AA%E5%B8%A6%E8%A1%8C%E9%94%81)
  - [(3) 分布式事务](#3-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1)
  - [(4) 使用Redis实现分布式锁](#4-%E4%BD%BF%E7%94%A8redis%E5%AE%9E%E7%8E%B0%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81)
  - [(5) 使用Redis进行缓存（cache aside）](#5-%E4%BD%BF%E7%94%A8redis%E8%BF%9B%E8%A1%8C%E7%BC%93%E5%AD%98cache-aside)
- [03 使用Redis拦截DB请求](#03-%E4%BD%BF%E7%94%A8redis%E6%8B%A6%E6%88%AAdb%E8%AF%B7%E6%B1%82)
  - [(1) 负载分担](#1-%E8%B4%9F%E8%BD%BD%E5%88%86%E6%8B%85)
  - [(2) 防止过量请求集中到达DB](#2-%E9%98%B2%E6%AD%A2%E8%BF%87%E9%87%8F%E8%AF%B7%E6%B1%82%E9%9B%86%E4%B8%AD%E5%88%B0%E8%BE%BEdb)
    - [(a) 拦截过量请求](#a-%E6%8B%A6%E6%88%AA%E8%BF%87%E9%87%8F%E8%AF%B7%E6%B1%82)
      - [数据预热](#%E6%95%B0%E6%8D%AE%E9%A2%84%E7%83%AD)
      - [原子操作（Lua脚本）](#%E5%8E%9F%E5%AD%90%E6%93%8D%E4%BD%9Clua%E8%84%9A%E6%9C%AC)
    - [(b) 控制请求频次](#b-%E6%8E%A7%E5%88%B6%E8%AF%B7%E6%B1%82%E9%A2%91%E6%AC%A1)
- [04 数据库本地事务](#04-%E6%95%B0%E6%8D%AE%E5%BA%93%E6%9C%AC%E5%9C%B0%E4%BA%8B%E5%8A%A1)
  - [(1) ACID](#1-acid)
  - [(2) InnoDB对于本地事务的实现](#2-innodb%E5%AF%B9%E4%BA%8E%E6%9C%AC%E5%9C%B0%E4%BA%8B%E5%8A%A1%E7%9A%84%E5%AE%9E%E7%8E%B0)
- [98 流量控制](#98-%E6%B5%81%E9%87%8F%E6%8E%A7%E5%88%B6)
- [99 工具](#99-%E5%B7%A5%E5%85%B7)
  - [(1) 开发环境并发压测](#1-%E5%BC%80%E5%8F%91%E7%8E%AF%E5%A2%83%E5%B9%B6%E5%8F%91%E5%8E%8B%E6%B5%8B)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 01 常见数据库并发量

> 以实际环境压测为准，下面是大致参考
>
> * MySQL等普通的关系型数据库：1000 QPS
> * MongoDB等硬盘性NoSQL：10000 QPS
> * Redis等内存型数据库：100000 QPS

## 02 并发请求处理

### (1) 使用数据库事务处理并发请求

> ~~~sql
> -- 事务开始
> START TRANSACTION
> -- 查询并锁定相关数据行（使用FOR UPDATE）
> SELECT ... WHERE ... FOR UPDATE
> -- 更新这些行
> UPDATE ... WHERE ...
> -- 事务提交
> COMMIT
> ~~~
>
> 使用TRANSACTION保证原子性
>
> 使用FOR UPDATE锁定相关的行，使其他的FOR UPDATE查询无法拿到这些行

### (2) 借助UPDATE语句自带行锁

> ~~~sql
> -- 仅在库存大于0时扣减库存
> UPDATE `stock_info` SET stock = stock - 1 WHERE commodity_id = 189 AND seckill_id = 28 AND stock > 0;
> ~~~

### (3) 分布式事务

> 2PC（两阶段提交）；3PC（三阶段提交）；TCC（Try-Confirm-Cancel）；消息事务（最终一致性）； Sega事务；Seata事务
>
> 参考：[11_distributed_transaction_approach.md](11_distributed_transaction_approach.md)

### (4) 使用Redis实现分布式锁

> [03_redis_distributed_lock.md](03_redis_distributed_lock.md)

### (5) 使用Redis进行缓存（cache aside）

> [03_redis_distributed_lock.md](03_redis_distributed_lock.md)
>
> * 08 Redis缓存与数据库双写不一致问题
> * 09 分布式操作原子性保证
> * 10 读多写多场景

## 03 使用Redis拦截DB请求

> 数据库性能与Redis性能差距巨大，Redis是MySQL的1000倍，短暂的缓存穿透仍然有可能让数据库崩溃

### (1) 负载分担

> 考虑三个问题：
>
> 1. 哪些请求落在Redis上
> 2. 哪些请求落在后台的数据库上
> 3. 落在后台数据库的请求以怎样的方式到达

### (2) 防止过量请求集中到达DB

#### (a) 拦截过量请求

##### 数据预热

> 对于突发高并发请求的场景，需要提前对Redis预热

##### 原子操作（Lua脚本）

> 写操作开启拦截生效之前，读到旧操作，导致请求大量涌入DB，应当将读写操作合并在一个原子操作中

#### (b) 控制请求频次

> 可考虑使用消息队列削峰填谷，需要考虑消息队列投递失败的情况

## 04 数据库本地事务

### (1) ACID

`A`：原子性（Atomicity）

> 一组操作：要么同时成功；要么同时失败

`C`：一致性（Consistency）

> 事务执行前、执行后，数据库都必须处于一致性状态。
>
> `原子性`/`隔离性`/`持久性`都是为了实现`一致性`而服务。例如只具有`原子性`而不具备`隔离性`，一个事务的执行结果，同样会受另一个事务的影响，两个事务操作顺序编排的差异会产生不同的最终结果。

`I`：隔离性（Isolation）

> 在并发环境中，不同的事务同时操作相同的数据时，每个事务都有各自完整的数据空间
>
> 四个数据库事务隔离级别
>
> * Read Committed
> * Read UnConmitted
> * Reapeatble Read
> * Serializable

`D`：持久性（Durability）

> 只要事务成功结束，它读数据库所做的更新必须永久保存下来。即使发生系统崩溃，重新启动数据库，也仍然能恢复到事务成功结束时的状态

### (2) InnoDB对于本地事务的实现

参与方：

> * 应用程序（AP：Application Threads）
> * 资源管理器（RM：Resource Manager）

原理：通过日志和锁来实现

> 持久性：通过Redo Log持久化来实现
>
> 原子性、一致性：通过Undo log来实现
>
> 隔离性：通过数据库锁机制实现

过程：

> 1. 开始会话
>
> 2. 开始事务
>
> 3. 执行操作（写入Redo Log，Undo Log）
>
>     操作1
>
>     操作2
>
>     ...
>
>     操作n
>
> 4. 提交事务或回滚事务（加锁，应用Redo Log或Undo Log）
>
> 5. 结束会话

## 98 流量控制

> (1) 前端资源静态化（CDN）
>
> (2) 前端限流（阻止频繁点击等）
>
> (3) 降级至繁忙页（影响用途体验）
>
> (4) 阻止恶意请求（验证码机制）
>
> (5) 限流机制（Rate Limiter：防止恶意刷屏或爬虫）
>
> (6) 黑名单机制

## 99 工具

### (1) 开发环境并发压测

用Ngix代理多个后台服务，使用JMeter压测Ngix

`Ngix`：

`JMeter`：Test Plan → Thread Group → HTTP Request → Aggregate Report

> Thread Group
>
> * Number of Threads：线程数
> * Ramp Up Period：多长时间内发完请求，模拟高并发可以配为0
> * Loop Count：循环压几次
>
> HTTP Request
>
> Aggregate Report

