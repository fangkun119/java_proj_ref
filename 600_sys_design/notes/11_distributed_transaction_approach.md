<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [分布式事务解决方案](#%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88)
  - [1. 基于XA规范的分布式事务：2PC、3PC](#1-%E5%9F%BA%E4%BA%8Exa%E8%A7%84%E8%8C%83%E7%9A%84%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A12pc3pc)
    - [(1) 2PC：两阶段提交](#1-2pc%E4%B8%A4%E9%98%B6%E6%AE%B5%E6%8F%90%E4%BA%A4)
      - [(a) 事务执行过程](#a-%E4%BA%8B%E5%8A%A1%E6%89%A7%E8%A1%8C%E8%BF%87%E7%A8%8B)
      - [(b) 优缺点](#b-%E4%BC%98%E7%BC%BA%E7%82%B9)
    - [(2) 3PC：三阶段提交](#2-3pc%E4%B8%89%E9%98%B6%E6%AE%B5%E6%8F%90%E4%BA%A4)
      - [(a) 事务执行过程](#a-%E4%BA%8B%E5%8A%A1%E6%89%A7%E8%A1%8C%E8%BF%87%E7%A8%8B-1)
    - [(3) 优缺点](#3-%E4%BC%98%E7%BC%BA%E7%82%B9)
  - [2. 基于业务层的方案：TCC（Try-Confirm-Cancel）](#2-%E5%9F%BA%E4%BA%8E%E4%B8%9A%E5%8A%A1%E5%B1%82%E7%9A%84%E6%96%B9%E6%A1%88tcctry-confirm-cancel)
    - [(1) 背景](#1-%E8%83%8C%E6%99%AF)
    - [(2) 事务执行过程](#2-%E4%BA%8B%E5%8A%A1%E6%89%A7%E8%A1%8C%E8%BF%87%E7%A8%8B)
    - [(3) 优缺点及适用范围](#3-%E4%BC%98%E7%BC%BA%E7%82%B9%E5%8F%8A%E9%80%82%E7%94%A8%E8%8C%83%E5%9B%B4)
    - [(4) 参考实现：ByteTCC](#4-%E5%8F%82%E8%80%83%E5%AE%9E%E7%8E%B0bytetcc)
  - [4. 应用消息队列 + 消息表：实现最终一致性（BASE）](#4-%E5%BA%94%E7%94%A8%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97--%E6%B6%88%E6%81%AF%E8%A1%A8%E5%AE%9E%E7%8E%B0%E6%9C%80%E7%BB%88%E4%B8%80%E8%87%B4%E6%80%A7base)
    - [(1) 本地消息表](#1-%E6%9C%AC%E5%9C%B0%E6%B6%88%E6%81%AF%E8%A1%A8)
    - [(2) 参考实现：RocketMQ](#2-%E5%8F%82%E8%80%83%E5%AE%9E%E7%8E%B0rocketmq)
  - [5. SAGA](#5-saga)
    - [(1) 原理](#1-%E5%8E%9F%E7%90%86)
    - [(2) 参考实现：servicecomb](#2-%E5%8F%82%E8%80%83%E5%AE%9E%E7%8E%B0servicecomb)
  - [6. 参考实现：Seata](#6-%E5%8F%82%E8%80%83%E5%AE%9E%E7%8E%B0seata)
    - [(1) TCC-AT模式](#1-tcc-at%E6%A8%A1%E5%BC%8F)
      - [(a) 事务执行过程](#a-%E4%BA%8B%E5%8A%A1%E6%89%A7%E8%A1%8C%E8%BF%87%E7%A8%8B-2)
      - [(b) 写隔离](#b-%E5%86%99%E9%9A%94%E7%A6%BB)
      - [(b) 读隔离](#b-%E8%AF%BB%E9%9A%94%E7%A6%BB)
    - [(2) TCC-MT模式](#2-tcc-mt%E6%A8%A1%E5%BC%8F)
    - [(3) SEGA模式](#3-sega%E6%A8%A1%E5%BC%8F)
    - [(4) 详细信息参考](#4-%E8%AF%A6%E7%BB%86%E4%BF%A1%E6%81%AF%E5%8F%82%E8%80%83)
  - [附录：CAP和BASE](#%E9%99%84%E5%BD%95cap%E5%92%8Cbase)
    - [(1) CAP](#1-cap)
    - [(2) BASE](#2-base)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 分布式事务解决方案

## 1. 基于XA规范的分布式事务：2PC、3PC

> 基于 XA 协议实现的分布式事务，角色分为两部分：事务管理器和本地资源管理器。
>
> * 本地资源管理器：往往由数据库实现（例如 Oracle、MYSQL 5.5+等）都实现了 XA 接口
> * 事务管理器：则作为一个全局的调度者

### (1) 2PC：两阶段提交

#### (a) 事务执行过程

概括

> 角色：事务协调器，事务参与方
>
> 过程：分支事务本地执行 → 汇报汇总以决定是全局提交还是回滚

两阶段

> 阶段一：协调器请求`参与方`在本地事务中进行预执行（precommit）
>
> * 锁定要执行的行
> * 执行操作
> * 记录undo和redo日志
> * 向事务协调器汇报结果
>
> 阶段二：事务协调器确认预执行情况，要求所有参与方提交或回滚本地事务
>
> * 如果是全部提交：`参与方`提交事务，解除本地锁
> * 如果是全部回滚：`参与方`放弃事务，解除本地锁

#### (b) 优缺点

优点

> * 尽量保证了数据一致性
> * 实现成本低：很多数据库（例如Oracle，MySQL 5.5以上）本身就支持XA协议，可以用作参与者

缺点

> * 单点故障：事务管理器是单点
> * 同步阻塞：参与者执行完阶段1之后，处于阻塞状态，要等待阶段2的命令才能释放资源，无法用于高并发状态
> * 数据不一致的可能性仍然存在：例如阶段2的commit请求没有发送到所有的参与者，部分参与者处于阻塞状态，同时数据也处于不一致状态

### (2) 3PC：三阶段提交

#### (a) 事务执行过程

概括

> 角色：事务协调器，事务参与方
>
> 过程：CanCommit锁定资源 → PreCommit执行操作 → DoCommit全局提交或回滚

三阶段

> 阶段一：协调器询问参与方是否可以提交，需要全部参与者返回SUCCESS ACK
>
> * 参与方获得本地锁
> * 参与方汇报结果：成功、失败、**超时未能汇报**
>
> 阶段二：协调器请求参与方进行预执行（PreCommit）
>
> * 参与方在各自的本地事务中执行操作、并记录undo和redo日志
> * 参与方汇报结果：成功、失败、**超时未能汇报**
>
> 阶段三：协调器确认事务
>
> * 如果参与方存在PreCommit失败：协调器通知所有参与者回滚
>     * 参与者回滚，释放本地锁
> * 如果参与方全部PreCommit成功：协调器通知所有参与者提交COMMIT本地事务
>     * 参与者提交事务，释放本地锁

### (3) 优缺点

优点

> 1. 阻塞问题解决：引入了参与者超时机制，当参与者无法联系到协调者时，会超时并释放本地锁，而不是一直阻塞
> 2. 增加了一个询问准备阶段：提高了对一致性的保证

缺点

> 执行开销仍然比较大

## 2. 基于业务层的方案：TCC（Try-Confirm-Cancel）

### (1) 背景

> 解决了2PC、3PC的几个缺点：
>
> * 解决了协调者端点的问题，改由业务发起方来完成这个部分任务，而业务活动管理器也变成多点
> * 解决同步阻塞问题：引入超时机制，并且不会锁定整个资源，将资源转换为业务逻辑的形式，锁粒度变小
> * 数据一致性：由业务活动管理器控制
>
> 原始论文："Life beyond Distributed Transactions:an Apostate’s Opinion", by Pat Helland, 2007

### (2) 事务执行过程

角色

> * 主业务服务：发起并完成整个业务活动
> * 从业务服务：提供TCC业务操作
> * 业务活动管理器：控制业务活动的一致性
>     * 分布式事务执行时，登记事务内的各个TCC业务操作
>     * 分布式事务提交时，对各个TCC业务操作进行确认（Confirm）
>     * 分布式事务取消时，对各个TCC操作进行取消（Cancel）

过程

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/sysdesign/sysdesign_tcc_steps.jpg" width="400" /></div>
>
> Try阶段：尝试执行，完成业务检查（一致性）和业务资源预留（准隔离性），但不会提交
>
> * 例如A向B购买一个100元的商品
> * A的余额需要大于100元，B的商品库存需要大于1
>
> Confirm阶段：真正执行
>
> * 只使用Try阶段预留的业务资源
> * 操作需要满足幂等性，因为失败后有可能会进行重试
>
> Cancel阶段：取消执行
>
> * 释放Try阶段预留的业务资源
> * 操作需要满足幂等性

另一个例子：商品购买

> Try阶段：锁定库存
>
> Confirm：库存扣减
>
> Cancel：释放库存

### (3) 优缺点及适用范围

> * 不存在资源阻塞问题，每个方法都直接进行提交或回滚补偿
>
> * 适用于要求强隔离性、严格要求一致性的场景，执行时间较短的业务
>
> * 实现较复杂（一个操作需要拆分成三个方法）、可复用性低

### (4) 参考实现：ByteTCC

> [https://github.com/liuyangming/ByteTCC/](https://github.com/liuyangming/ByteTCC/)

## 4. 应用消息队列 + 消息表：实现最终一致性（BASE）

### (1) 本地消息表

方法

> 将需要分布式处理的任务，通过消息日志的方式来异步执行
>
> 1. 消息日志：存储在本地文本、数据库、或者消息队列中
>
> 2. 通过业务规则自动或基于事件触发来发起重试
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/sysdesign/sysdesign_distribute_trx_local_msg_table.jpg" width="500" /></div>
>
> 原始论文：["Base: An Acid Alternative"](https://queue.acm.org/detail.cfm?id=1394128)

例子：购买商品

> 1. 将扣款和扣减库存操作写入同一个事务
> 2. 定时任务轮询本地事务表，向商品服务器发送消息
> 3. 商品服务器扣减库存，更新事务表中对应消息的状态
> 4. 商品服务器直接（或通过定时任务）通知扣款服务器
> 5. 扣款服务器进行扣款，并更新事务表中的事务状态
> 6. 备注：(1) 消息可能重复发送、需要进行重复检查 (2) 有重试的可能，执行的操作需要保证幂等性

### (2) 参考实现：RocketMQ

事务执行过程如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/sysdesign/sysdesign_roketmq_trx.jpg" width="600" /></div>

正常情况：

> * 阶段一：生产者发送Prepared Message到消息队列，并得到一个功能类似句柄的`消息地址`
>
> * 阶段二：生产者执行本地事务
>
> * 阶段三：生产者通过`消息地址`来修改消息的状态，使接受者能够收到这个消息

异常情况：

> * 生产者本地事务失败：阶段三不会发生，因此消费者无法接收到消息
> * 消费者接收消息超时：消息集群会自动进行重试，因此Consumer在处理消息时需要保证幂等性

同样以商品购买为例，事务执行过程如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/sysdesign/sysdesign_disttrx_base_seqdiagram.jpg" width="500" /></div>
>
> 1. 订单系统向消息队列发送预备扣减库存的消息，消息队列保存消息并返回ACK（不会立即发往库存系统），而订单系统则可以得到消息队列的`回调接口`
> 2. 订单系统（收到消息队列的ACK之后）执行本地事务（下单操作）
>     * 如果本地事务执行成功：通过回调接口通知消息队列，将消息发往库存系统，执行库存扣减操作
>     * 如果本地事务执行失败：通过回调接口通知消息队列进行回滚
> 3. 库存系统收到库存扣减的消息后，执行本地事务
>     * 如果库存系统扣减失败，会进行重试
>     * 对应于超过重试次数分布式事务，会由启动定时任务来执行补偿操作（回滚）

## 5. SAGA

### (1) 原理

> 将长事务拆分为多个本地短事务，由Sega事务协调器处理。
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/upload/sysdesign_disttrx_sega.png" width="300" /></div>
>
> 每个短事务都有两种操作：T（事务操作），C（补偿操作：失败回滚时使用）
>
> * 正常情况：按正向顺序执行事务操作 T<sub>1</sub>，T<sub>2</sub>，……，T<sub>n</sub>
> * 异常情况：按反向顺序执行补偿操作进行回滚：T<sub>1</sub>，T<sub>2</sub>，……，T<sub>j</sub>，C<sub>j</sub>，C<sub>2</sub>，……，C<sub>1</sub>
>
> 原版的Sega模式中，没有进行事务隔离，因此会被其他事务影响。解决如下：
>
> * 华为对Sega的实现中，在业务层加入了Session以及锁机制，来保证串行化操作资源
> * 也可以在业务逻辑的设计层面，引入类似资金冻结这样的步骤，来隔离这部分接下来要用的资源

### (2) 参考实现：servicecomb

> 华为的servicecomb

## 6. 参考实现：Seata

> 从两阶段提交演变而来的分布式事务解决方案，提供了`TCC`（`AT`，`MT`），`SAGA`，`XA`等事务模式，下面是`AT`模式的介绍

### (1) TCC-AT模式

#### (a) 事务执行过程

角色：

> `TM`（Transaction Manager）：事务管理者，业务代码中用来开启/提交/回滚`全局事务`（在调用服务的方法中用注解开启事务）
>
> `TC`（Transaction Coordinator）：全局事务提交者、用来协调全局事务与分支事务的状态，驱动分支事务提交或回滚
>
> `RM`（Resource Manager）：资源管理者，管理分支事务（Branch Transaction），与TC进行协调、注册分支事务并汇报事务状态，驱动分支事务的提交或回滚

回滚日志记录表：`UNDO_LOG`表

> 每个应用分布式事务的业务库中，都需要创建这张表，用来将业务数据在更新前后祖泽成回滚日志

执行过程

> 阶段1：执行各分支事务并记录Redo和Undo日志
>
> * RM执行分支事务，包括：
>     * RM`获取本地锁`（启动本地事务）
>     * 将分支事务之前前/执行后的数据镜像，生成成Redo和Undo日志，写入UNDO_LOG表中
>     * 向TC注册，为要修改的记录`申请全局锁`（一段时间拿不到会触发事务本地回滚）
>     * `RM释放本地锁`（提交本地事务）
>
> 阶段2：根据各分支事务的决议做提交或回滚
>
> * 如果决议是全局提交，TC向各个RM发送提交请求，而RM执行如下操作
>     * RM将请求放入异步任务队列，并立刻返回Success Response给TC
>     * RM的异步任务，批量将队列请求中Branch ID对应的UNDO_LOG删除
>     * 释放全局锁
> * 如果决议是全局回滚：TC向各个RM发送回滚命令
>     * RM重新获得本地锁（如果拿不到则继续重试）
>     * RM根据UNDO LOG反向生成回滚SQL并执行，完成分支事务的回滚
>     * RM释放本地锁
>     * 释放全局锁

#### (b) 写隔离

> 阶段1的本地事务，提交前要先（在一定重试范围之内）拿到全局锁，否则会放弃本地事务。以写事务Trx1、Trx2修改相同的资源（相同的本地锁）为例
>
> * 都成功的情况下：
>     * 都先（在本地事务中）执行所有分支事务的写操作，执行成功，并生成Redo和Undo日志
>     * 先获得本地锁的先提交（全局锁在本地锁解锁之前获取），以异步的方式删除UNDO_LOG表中对应的日志
> * Trx1失败时、Trx2正在执行的情况：
>     * 都先（在本地事务中）执行所有分支事务的写操作，并生成Redo和Undo日志。Trx1只有部分成功，Trx2全部成功。
>     * 如果Trx2先拿到本地锁
>         * Trx2也会先拿到全局锁（全局锁在本地锁解锁之前获取）
>         * Trx2会先执行本地事务并且先提交全局事务
>         * Trx1要等待Trx2释放全局锁之后才能执行补偿操作进行回滚
>         * 上述过程实现了写隔离、执行顺序是Trx2 → Trx1（失败放弃）
>     * 如果Trx1先拿到本地锁
>         * Trx1也会先拿到全局锁并释放本地锁
>         * Trx2得到本地锁开始执行分支事务
>         * Trx1获得全局锁，开始回滚，因为回滚要重新获得本地锁因此Trx1陷入重试状态
>         * Trx2试图获得全局锁提交事务，但全局锁被Trx1占用无法提交，等待超时之后Trx2本地回滚分支事务，释放本地锁
>         * Trx1重新获得本地锁，使用UNDO LOG进行回滚，随后释放全局锁
>         * 上述过程实现了写隔离、执行顺序是Trx1（失败放弃）→Trx2（等待全局锁超时放弃）

#### (b) 读隔离

读隔离级别

> 前提条件：数据库本地执行的分支事务的隔离级别是Read Committed或以上
>
> 默认全局事务隔离级别：Read Uncommitted
>
> 通过Seata的SELECT FOR UPDATE语句的代理的隔离级别：Read Committed（会消耗性能）

原理：Trx1使用SELECT FOR UPDATE读数据，Trx2更新了同一行数据

> Trx1先获得本地锁的情况
>
> * Trx1读取数据；Trx2阻塞在本地锁上
> * Trx1获取全局锁开始提交、同时释放本地锁；Trx2更新数据
> * TC的决议是全局提交、因此全局事务成功，Trx1的UNDO LOG被删除
> * 上述过程实现了Read Comitted，顺序是：Trx1（读到之前的数据）→ Trx2写入新数据
>
> Trx2先获得本地锁，并且事务成功的情况
>
> * Trx2写数据；Trx1阻塞在本地锁上
> * Trx2获取全局锁开始提交，提交本地事务、释放本地锁；Trx1获取本地锁读取到Trx2刚刚写入的事务
> * Trx1尝试获取全局锁、但全局锁被Trx2占有，因此Trx1陷入重试
> * Trx2提交完毕，释放全局锁；Trx1获取全局锁，完成全局事务并释放全局锁
> * 上说过程实现了Read Comitted，顺序是：Trx2（更新数据）→ Trx1（读到Trx2提交的数据）
>
> Trx2先获得本地锁，但是事务失败发生回滚
>
> * 本质上，SELECT FOR UPDATE语句代理，使SELECT的分布式处理方式与上面`(a)`当中的写操作相同。
> * 在这种情况下，Trx2会回滚，而Trx1也会失败

### (2) TCC-MT模式

> 上面的AT模式，其实是Seata为TCC提供的一种实现
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/upload/sysdesign_seata_tcc.png" width="800" /></div>
>
> TCC分为两阶段：阶段1是Prepare行为；阶段二是Commit或RollBack行为
>
> 有两种行为模式：
>
> * AT模式：Automatic Branch Transaction Mode
> * MT模式：Manual Branch Transaction Mode
>
> AT模式：
>
> - 一阶段 prepare 行为：在本地事务中，一并提交业务数据更新和相应回滚日志记录。
> - 二阶段 commit 行为：马上成功结束，**自动** 异步批量清理回滚日志。
> - 二阶段 rollback 行为：通过回滚日志，**自动** 生成补偿操作，完成数据回滚。
>
> MT模式：
>
> - 一阶段 prepare 行为：调用 **自定义** 的 prepare 逻辑。
> - 二阶段 commit 行为：调用 **自定义** 的 commit 逻辑。
> - 二阶段 rollback 行为：调用 **自定义** 的 rollback 逻辑。

### (3) SEGA模式

> 使用场景：
>
> - 业务流程长、业务流程多
> - 参与者包含其它公司或遗留系统服务，无法提供 TCC 模式要求的三个接口
>
> 优势：
>
> - 一阶段提交本地事务，无锁，高性能
> - 事件驱动架构，参与者可异步执行，高吞吐
> - 补偿服务易于实现
>
> 缺点：
>
> - 不保证隔离性、 在极端情况下可能由于脏写无法完成回滚操作（应对方案见[用户文档](http://seata.io/zh-cn/docs/user/saga.html)）

### (4) 详细信息参考

> [http://seata.io/zh-cn/docs/overview/what-is-seata.html](http://seata.io/zh-cn/docs/overview/what-is-seata.html)
>
> [https://github.com/seata/seata-samples](https://github.com/seata/seata-samples)

## 附录：CAP和BASE

### (1) CAP

> `P（分区容错性）`：当出现网络分区后（例如某个节点的网络出现故障），整个分布式系统仍然能够继续工作
>
> `A（可用性）`：非故障节点在合理时间内返回合理的响应
>
> * 合理时间：请求不会被无限阻塞，在合理时间内返回
> * 合理响应：返回正确的计算结果
>
> `C（一致性）`：读操作能返回最新的写操作
>
> * 强一致性：某个节点更新了数据，其他节点都能读取到这个最新的数据

### (2) BASE

> `BA：Basically Available，基本可用`：分布式系统出现故障时、容许损失部分功能的可用性，但核心功能仍然可用
>
> `S：Soft State，软状态`：容许系统中存在中间状态（短暂的不一致）并且不会影响可用性
>
> `E：Eventually Consistent，最终一致性`：经过一段时间后，所有节点的数据都将达到一致