[TOC]

## 01 常见数据库并发量

> 以实际环境压测为准，下面是大致参考
>
> * MySQL：1000 QPS
> * Redis：100000 QPS

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

### (3) 用三阶段提交实现分布式事务

> 角色：事务协调器，事务参与方（backing service）
>
> 阶段一：协调器询问参与方是否可以提交，需得到全部的ACK确认
>
> 阶段二：协调器请求参与方进行预执行
>
> * 参与方在本地事务中执行操作，并记录undo和redo日志
> * 参与方向协调器汇报执行结果：成功或失败
>
> 阶段三：协调器确认事务
>
> * 如果有参与方失败：协调器告诉参与方回滚
> * 如果参与方全部成功：
>     * 协调器通知所有参与方COMMIT本地事务
>     * 如果有一个参与方提交失败，协调器要求参与方全部回滚

### (4) 使用Redis实现分布式锁

> [03_redis_distributed_lock.md](03_redis_distributed_lock.md)

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
