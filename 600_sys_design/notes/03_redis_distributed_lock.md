<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [用Redis实现分布式锁](#%E7%94%A8redis%E5%AE%9E%E7%8E%B0%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81)
  - [01 无分布式锁下的Bug复现](#01-%E6%97%A0%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E4%B8%8B%E7%9A%84bug%E5%A4%8D%E7%8E%B0)
  - [02 有缺陷的初级分布式锁：借助Redis命令的原子性](#02-%E6%9C%89%E7%BC%BA%E9%99%B7%E7%9A%84%E5%88%9D%E7%BA%A7%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%80%9F%E5%8A%A9redis%E5%91%BD%E4%BB%A4%E7%9A%84%E5%8E%9F%E5%AD%90%E6%80%A7)
    - [(a) 原理](#a-%E5%8E%9F%E7%90%86)
    - [(b) 方法](#b-%E6%96%B9%E6%B3%95)
    - [(c) 相关代码](#c-%E7%9B%B8%E5%85%B3%E4%BB%A3%E7%A0%81)
    - [(d) 缺陷](#d-%E7%BC%BA%E9%99%B7)
  - [03 有缺陷的修复方法](#03-%E6%9C%89%E7%BC%BA%E9%99%B7%E7%9A%84%E4%BF%AE%E5%A4%8D%E6%96%B9%E6%B3%95)
    - [(1) 为redis key设置过期时间](#1-%E4%B8%BAredis-key%E8%AE%BE%E7%BD%AE%E8%BF%87%E6%9C%9F%E6%97%B6%E9%97%B4)
    - [(2) 加锁的同时，在同一个原子操作中设置超时时间](#2-%E5%8A%A0%E9%94%81%E7%9A%84%E5%90%8C%E6%97%B6%E5%9C%A8%E5%90%8C%E4%B8%80%E4%B8%AA%E5%8E%9F%E5%AD%90%E6%93%8D%E4%BD%9C%E4%B8%AD%E8%AE%BE%E7%BD%AE%E8%B6%85%E6%97%B6%E6%97%B6%E9%97%B4)
    - [(3) 在value中填入uuid，只有加锁的线程才能解锁](#3-%E5%9C%A8value%E4%B8%AD%E5%A1%AB%E5%85%A5uuid%E5%8F%AA%E6%9C%89%E5%8A%A0%E9%94%81%E7%9A%84%E7%BA%BF%E7%A8%8B%E6%89%8D%E8%83%BD%E8%A7%A3%E9%94%81)
  - [04 能够解决一致性问题的方法：Watch Dog锁续命](#04-%E8%83%BD%E5%A4%9F%E8%A7%A3%E5%86%B3%E4%B8%80%E8%87%B4%E6%80%A7%E9%97%AE%E9%A2%98%E7%9A%84%E6%96%B9%E6%B3%95watch-dog%E9%94%81%E7%BB%AD%E5%91%BD)
  - [05 使用Redisson实现Watch Dog锁续命](#05-%E4%BD%BF%E7%94%A8redisson%E5%AE%9E%E7%8E%B0watch-dog%E9%94%81%E7%BB%AD%E5%91%BD)
    - [(1) Redisson常用功能](#1-redisson%E5%B8%B8%E7%94%A8%E5%8A%9F%E8%83%BD)
    - [(2) 分布式锁实现代码](#2-%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%AE%9E%E7%8E%B0%E4%BB%A3%E7%A0%81)
    - [(3) Redisson分布式锁原理](#3-redisson%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%8E%9F%E7%90%86)
    - [(4) Watch Dog锁续命的问题](#4-watch-dog%E9%94%81%E7%BB%AD%E5%91%BD%E7%9A%84%E9%97%AE%E9%A2%98)
      - [(a) 锁阻塞带来性能问题](#a-%E9%94%81%E9%98%BB%E5%A1%9E%E5%B8%A6%E6%9D%A5%E6%80%A7%E8%83%BD%E9%97%AE%E9%A2%98)
      - [(b) Redis主从架构切换时的锁key丢失问题](#b-redis%E4%B8%BB%E4%BB%8E%E6%9E%B6%E6%9E%84%E5%88%87%E6%8D%A2%E6%97%B6%E7%9A%84%E9%94%81key%E4%B8%A2%E5%A4%B1%E9%97%AE%E9%A2%98)
  - [06 锁key丢失问题：从CAP角度剖析Redis&Zookeeper锁架构](#06-%E9%94%81key%E4%B8%A2%E5%A4%B1%E9%97%AE%E9%A2%98%E4%BB%8Ecap%E8%A7%92%E5%BA%A6%E5%89%96%E6%9E%90rediszookeeper%E9%94%81%E6%9E%B6%E6%9E%84)
    - [(1) Redis：默认满足AP、可用性优先](#1-redis%E9%BB%98%E8%AE%A4%E6%BB%A1%E8%B6%B3ap%E5%8F%AF%E7%94%A8%E6%80%A7%E4%BC%98%E5%85%88)
    - [(2) Zookeeper：默认满足CP、一致性优先](#2-zookeeper%E9%BB%98%E8%AE%A4%E6%BB%A1%E8%B6%B3cp%E4%B8%80%E8%87%B4%E6%80%A7%E4%BC%98%E5%85%88)
    - [(3) 使用Redis还是Zookeeper](#3-%E4%BD%BF%E7%94%A8redis%E8%BF%98%E6%98%AFzookeeper)
    - [(4) 兼顾并发量和一致性的一种方法：Redlock（不是很推荐）](#4-%E5%85%BC%E9%A1%BE%E5%B9%B6%E5%8F%91%E9%87%8F%E5%92%8C%E4%B8%80%E8%87%B4%E6%80%A7%E7%9A%84%E4%B8%80%E7%A7%8D%E6%96%B9%E6%B3%95redlock%E4%B8%8D%E6%98%AF%E5%BE%88%E6%8E%A8%E8%8D%90)
  - [07 解决锁阻塞带来的性能问题：分段锁](#07-%E8%A7%A3%E5%86%B3%E9%94%81%E9%98%BB%E5%A1%9E%E5%B8%A6%E6%9D%A5%E7%9A%84%E6%80%A7%E8%83%BD%E9%97%AE%E9%A2%98%E5%88%86%E6%AE%B5%E9%94%81)
  - [08 Redis缓存与数据库（Cache Aside）双写不一致问题](#08-redis%E7%BC%93%E5%AD%98%E4%B8%8E%E6%95%B0%E6%8D%AE%E5%BA%93cache-aside%E5%8F%8C%E5%86%99%E4%B8%8D%E4%B8%80%E8%87%B4%E9%97%AE%E9%A2%98)
    - [(1) `写DB → 写缓存`的问题](#1-%E5%86%99db-%E2%86%92-%E5%86%99%E7%BC%93%E5%AD%98%E7%9A%84%E9%97%AE%E9%A2%98)
    - [(2) `写DB → 删除缓存`的问题](#2-%E5%86%99db-%E2%86%92-%E5%88%A0%E9%99%A4%E7%BC%93%E5%AD%98%E7%9A%84%E9%97%AE%E9%A2%98)
  - [09 分布式操作原子性的保证](#09-%E5%88%86%E5%B8%83%E5%BC%8F%E6%93%8D%E4%BD%9C%E5%8E%9F%E5%AD%90%E6%80%A7%E7%9A%84%E4%BF%9D%E8%AF%81)
    - [(1) 分析上面方案问题的本质：原子操作被破坏](#1-%E5%88%86%E6%9E%90%E4%B8%8A%E9%9D%A2%E6%96%B9%E6%A1%88%E9%97%AE%E9%A2%98%E7%9A%84%E6%9C%AC%E8%B4%A8%E5%8E%9F%E5%AD%90%E6%93%8D%E4%BD%9C%E8%A2%AB%E7%A0%B4%E5%9D%8F)
    - [(2) 保证原子性：分布式互斥锁及缺点](#2-%E4%BF%9D%E8%AF%81%E5%8E%9F%E5%AD%90%E6%80%A7%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%92%E6%96%A5%E9%94%81%E5%8F%8A%E7%BC%BA%E7%82%B9)
    - [(3) 保证原子性：分布式读写锁以及不足之处](#3-%E4%BF%9D%E8%AF%81%E5%8E%9F%E5%AD%90%E6%80%A7%E5%88%86%E5%B8%83%E5%BC%8F%E8%AF%BB%E5%86%99%E9%94%81%E4%BB%A5%E5%8F%8A%E4%B8%8D%E8%B6%B3%E4%B9%8B%E5%A4%84)
  - [10 读多写多场景](#10-%E8%AF%BB%E5%A4%9A%E5%86%99%E5%A4%9A%E5%9C%BA%E6%99%AF)
    - [(1) 借助为缓存设置TTL来改进"延迟双删"](#1-%E5%80%9F%E5%8A%A9%E4%B8%BA%E7%BC%93%E5%AD%98%E8%AE%BE%E7%BD%AEttl%E6%9D%A5%E6%94%B9%E8%BF%9B%E5%BB%B6%E8%BF%9F%E5%8F%8C%E5%88%A0)
    - [(2) 不使用缓存](#2-%E4%B8%8D%E4%BD%BF%E7%94%A8%E7%BC%93%E5%AD%98)
    - [(3) 中间件Canal](#3-%E4%B8%AD%E9%97%B4%E4%BB%B6canal)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


# 用Redis实现分布式锁

## 01 无分布式锁下的Bug复现

例子：`读库存 → 减库存`：并发错误，产生超卖问题

测试：使用JMeter + Ngix压测

> 说明：不是所有bug都能通过此方法复现，有的要极高并发，有的要在运行到某个环节发生系统卡顿等，有的要在两个操作当中出现特定操作才会复现

## 02 有缺陷的初级分布式锁：借助Redis命令的原子性

### (a) 原理

> 借助Redis命令原子性：`SETNX Key Value`，只有第一个执行该命令的线程可以执行成功

### (b) 方法

> 加分布式锁：如果加锁失败则退出
>
> 加锁成功执行操作，执行完毕后解锁（删除redis key）

### (c) 相关代码

> ~~~java
> try {
>     // lockKey相当于transaction id
> 	Boolean isSuccess = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, anyData); // 相当于jedis.setnx(...)
>     ...
> } finally {
> 	stringRedisTemplate.delete(lockKey);
> }
> ~~~

### (d) 缺陷

> 在执行finally之前该节点系统崩溃

## 03 有缺陷的修复方法

### (1) 为redis key设置过期时间

> ~~~java
> Boolean isSuccess = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, anyData); 
> if (isSuccess) {
>     stringRedisTemplate.expire(lockKay, 10, TimeUnit.SECONDES);
>     ...
> }
> ~~~
>
> 两个操作不是原子操作

### (2) 加锁的同时，在同一个原子操作中设置超时时间

> ~~~
> Boolean isSuccess = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, anyData, 10, TimeUnit.SECONDS); 
> ~~~
>
> 这个方法的缺陷是：当系统负载极大时
>
> * 会在事务执行完毕之前发生key过期导致锁失效，其他线程加锁成功。
> * 而事务执行完之后的delete又会删除其他线程使用的分布式锁
>
> 如此反复，导致大量线程逃脱分布式锁的控制，引起严重超卖

### (3) 在value中填入uuid，只有加锁的线程才能解锁

> ~~~java
> try {
> 	Boolean isSuccess = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 10, TimeUnit.SECONDS); 
>     ...
> } finally {
>     // 通过引入了uuid的限制，只有加锁的线程自己才能解锁
>     if (uuid.equals(stringRedisTemplate.opsForValue().get(lockKey))) {
>         // 位置(1)
>         stringRedisTemplate.delete(lockKey)
>     }
> }
> ~~~
>
> 这个方法的缺陷是：`uuid检查`和`delete lockKey`不是原子操作，当系统负载高出现卡顿时，可能有如下问题
>
> * 在位置(1)的地方（执行delete之前），`redis key expire`导致锁失效，另一个线程此时加锁
> * 随后执行delete操作，将其他线程的锁删除

## 04 能够解决一致性问题的方法：Watch Dog锁续命

方法

> 在uuid来保证只有同一个线程才能解锁
>
> 在一个分线程中，每隔1/3的life time，用uuid检查任务线程是否仍然持有这把锁，如果仍然持有，则重置该锁的life time

类库

> 同样需要处理原子操作等很多问题，因此首选仍然是市面上提供的封装，例如[Redisson](http://github.com/redisson/redisson/wiki/)

相比`Jedis`、在分布式场景`Redisson`使用的更多

## 05 使用Redisson实现Watch Dog锁续命

### (1) Redisson常用功能

> * **[Distributed objects](https://github.com/redisson/redisson/wiki/6.-distributed-objects)**：[分布式对象](https://github.com/redisson/redisson/wiki/6.-分布式对象)
> * **[Distributed collections](https://github.com/redisson/redisson/wiki/7.-distributed-collections)**：[分布式集合](https://github.com/redisson/redisson/wiki/7.-分布式集合)
> * **[Distributed locks and synchronizers](https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers)**：[分布式锁和同步器](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器)
> * **[Distributed services](https://github.com/redisson/redisson/wiki/9.-distributed-services)**：[分布式服务](https://github.com/redisson/redisson/wiki/9.-分布式服务)

### (2) 分布式锁实现代码

Step 1：添加依赖

> ~~~xml
> <dependency>
>     <groupId>org.redisson</groupId>
>     <artifactId>redisson</artifactId>
>     <version>3.6.5</version>
> </dependency>
> ~~~

Step 2：Redisson客户端

> ~~~java
> @Bean
> public Redisson redisson() {
> 	// 以单机架构的Redis为例代码如下，Redisson也支持集群、主从、哨兵等各种Redis架构
>     Config config = new Config();
>     config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(0);
>     return (Redisson) Redisson.create(config);
> }
> ~~~

Step 3：使用Redisson客户端实现分布式锁

> ~~~java
> @Autowired
> private Redisson redisson;
> 
> @RequestMapping("/deduc_stock")
> public String deducStock() {
>     RLock redissonLock = redisson.getLock(lockKey);
>     try {
>         redissonLock.lock();
>         ...
>     } finally {
>         redissonLock.unlock();
>     }
> }
> ~~~

### (3) Redisson分布式锁原理

> `redissonLock.lock()`背后的操作
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/sysdesign/sysdesign_understand_redisson_lock.jpg" width="800" /></div>

lock操作

> 类似之前的的`stringRedisTemplate.opsForValue().setIfAbsent(lockKey,uuid,30,TimeUnit.SECONDS)`，只不过使用的是HSET，其中：
>
> * Hash的Redis Key用来表示Transaction Id
> * 向Hash中存入的Member Name为标识加锁线程的Global Thread Id
> * 该Hash的过期时间（锁的租约期）为一个指定的时间，默认为30秒
>
> 上述所有操作封装在Lua脚本中以保证原子执行，仅在Redis Key不存在时才会执行

交互过程：

> * 线程2未获取锁，重复尝试等待加锁能够成功
>
> * 线程1加锁成功，后台线程会每间10秒钟检查Redis查看锁是否还存在（被线程1持有），如果存在则延长锁的过期时间（锁续命），以确保线程1的事务不论执行多久，都在锁的保护中
>
> * 如果节点崩溃，则后台线程也会崩溃，最终锁到期销毁自动解锁

### (4) Watch Dog锁续命的问题

#### (a) 锁阻塞带来性能问题

> 上面的例子中，线程2未能获取锁，一直处于轮询状态
>
> 这样的浪费性能，无法提供高负载服务

#### (b) Redis主从架构切换时的锁key丢失问题

> 主节点设置key成功 → 客户端认为加锁成功 → 主节点在同步到副节点之前故障、副节点成为主节点 → 新的主节点没有这个key而同时另一个线程执行加锁操作、导致超卖问题

## 06 锁key丢失问题：从CAP角度剖析Redis&Zookeeper锁架构

### (1) Redis：默认满足AP、可用性优先

> 主节点写成功立刻返回Response

### (2) Zookeeper：默认满足CP、一致性优先

> 主节点写成功后、会先向其他节点同步，至少同步到半数以上的节点，才会返回Response。Zookeeper如果有节点宕机、重新选举时，一定会保证有这个新设置的key的follower成为leader。因此主从切换时不会发生key丢失

### (3) 使用Redis还是Zookeeper

> 对并发量强需求，可以容忍少量一致性问题：Redis
>
> 要求强一致性：Zookeeper

### (4) 兼顾并发量和一致性的一种方法：Redlock（不是很推荐）

> 一个不是特别好的方法：使用Redlock
>
> RedLock底层的实现思想与Zookeeper类似，只是使用Redis实现，超过一半redis节点加锁成功才算加锁成功（当有节点宕机时、因为已经有一半以上的节点加锁成功，代表着如果另一个线程想加锁，在集齐一半以上节点的过程中一定会遇到锁冲突，从而加锁失败）
>
> 这个方法并不推荐，还有其他一些细节问题有待商榷

## 07 解决锁阻塞带来的性能问题：分段锁

> 分布式锁与高并发是相违背的，因为它将并发请求串行化了，提升并发能力的方式与ConcurrentHashMap的设计类似，采用分段锁

分段锁

> 例如200个库存，将其分成10份，每份20个库存，并发度由1变为10
>
> 但是对于一次要减5个库存这样的请求，需要对请求进行拆分

## 08 Redis缓存与数据库（Cache Aside）双写不一致问题

### (1) `写DB → 写缓存`的问题

> 写线程1：更新DB（A）→ 更新缓存（B）
>
> 写线程2：更新DB → 更新缓存
>
> 在`（A）`和`（B）`之间发生`写线程2的两个操作`时，写线程2更新的值，会被操作B的旧数据覆盖
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/sysdesign/sysdesign_cache_aside_write_cache.jpg" width="400" /></div>

### (2) `写DB → 删除缓存`的问题

该方法及问题

> 写线程2：更新DB → 删除缓存
>
> 读线程3（Cache Miss）：读DB得到数据（A） →  用读到的数据填充缓存（B）
>
> 在`（A）`和`（B）`之间发生`写线程2的两个操作`时，会导致读线程用错误的旧数据覆盖DB
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/sysdesign/sysdesign_cache_aside_write_del.jpg" width="500" /></div>

修补方法1： `内存队列`的问题

> 将DB操作（增删改查）放入队列，串行执行：
>
> * 影响性能
> * 有多少key要求多少队列，队列内存放各种增删改查的命令，太过复杂，不切实际

修补方法2： `延迟双删`的问题

> 上面的线程2，删除缓存后，sleep一段时间（例如50秒），然后再次删除缓存
>
> * 不能彻底解决问题，两次删除之间间隔多长时间很难确定
> * 所有写请求都需要sleep，影响系统吞吐

## 09 分布式操作原子性的保证

### (1) 分析上面方案问题的本质：原子操作被破坏

> 对DB和Cache的访问没能封装在原子操作中

### (2) 保证原子性：分布式互斥锁及缺点

方法：在对同一个key执行读、写操作之前，加一把分布式锁

问题：

> * 当对同一个key存在密集操作时，并行变串行，降低性能
> * 因为是同一个key，无法使用分段锁进行优化

### (3) 保证原子性：分布式读写锁以及不足之处

方法：在读多写少的场景下、读与读之间不会发生互斥

代码：Redisson同样提供了分布式读写锁封装

> 读操作
>
> ~~~java
> RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockKey);
> RLock readLock = readWriteLock.readLock();
> try {
>     readLock.lock();
>     ...
> } finally {
>     readLock.unlock();
> }
> ~~~
>
> 写操作
>
> ~~~java
> RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockKey);
> RLock writeLock = readWriteLock.writeLock();
> try {
>     writeLock.lock();
>     ...
> } finally {
>     writeLock.unlock();
> }
> ~~~

实现原理：点入Redisson的源码可以看到具体的实现

> 与互斥锁相同，读写锁仍然用Redis Hashmap来实现分布式锁，Hashmap的Redis Key就是lockKey，但是member_name，member_value有所差异
>
> 以加读锁的Lua脚本为例、代码如下
>
> * `KEY[1] = getName()`：用`Lock Key`作为HashMap的`Redis Key`
> * `KEY[2] = getReadWriteTimeoutNamePrefix(threadId)`：用来控制超时的key前缀
> * `ARGV[1] = internalLockLeaseTime`：锁的租约时长
> * `ARGV[2] = getLockName(threadId)`：读锁的锁名，用来记录持有读锁的“线程”
> * `ARGV[3] = getWriteLockName(threadId)`：写锁的锁名，用来记录持有写锁的“线程”
>
> ~~~lua
> local mode = redis.call('hget', KEYS[1], 'mode');
> -- HashMap中不存在'mode'这个member，说明是第一次加锁
> if (mode == false) then 
>     redis.call('hset',    KEYS[1], 			'mode' , 'read');	-- 锁设置为读模式
>     redis.call('hset',    KEYS[1], 			ARGV[2], 1);		-- 该线程对读锁的持有数置设为1
>     redis.call('set',     KEYS[2] .. ':1',  1);					-- 设置"超时前缀:1"以及TTL
>     redis.call('pexpire', KEYS[2] .. ':1', 	ARGV[1]);			-- 
>     redis.call('pexpire', KEYS[1], 			ARGV[1]);			-- 设置整个锁的TTL
>     return nil;
> end;
> -- HashMap中的'mode'为'read'，或者值虽然是'write'但是是当前线程加的写锁：这种情况容许继续加读锁
> if (mode == 'read') or (mode == 'write' and redis.call('hexists', KEYS[1], ARGV[3]) == 1) then 
>     local ind = redis.call('hincrby', KEYS[1], ARGV[2], 1);		-- 该线程对读锁的持有数量加1
>     redis.call('set', 	  KEYS[2] .. ':' .. ind,  1);			-- 设置"超时前缀:${ind}"以及TTL
>     redis.call('pexpire', KEYS[2] .. ':' .. ind,  ARGV[1]);		-- 
>     redis.call('pexpire', KEYS[1], 				  ARGV[1]);		-- 重置整个锁的TTL
>     return nil;
> end;
> return redis.call('pttl', KEYS[1]); -- 返回锁剩余的TTL
> ~~~

不足之处

> 不能应对读多写多的场景，因为写入量大，大量的写锁互斥依然会产生性能问题

## 10 读多写多场景

### (1) 借助为缓存设置TTL来改进"延迟双删"

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/sysdesign/sysdesign_cache_aside_write_del.jpg" width="500" /></div>
>
> 线程3更新缓存之后，为缓存设置超时时间（TTL），使其到期自动失效，以减小缓存与DB不一致的时间
>
> 1. 相比让写线程延迟双删，不会影响写吞吐
> 2. 上图中`飘红`的位置会发生数据不一致，但在于类似秒杀等应用，Redis的主要作用缓存库存量以拦截抢库存的流量，可以容忍一定的不一致。
>
> 在后续的购买和下单环节使用DB时会再次进行检查以保证一致性

### (2) 数据分片（Partitioning）

### (3) 使用cache through模式

对比Cache Aside和Cache Through，其中箭头是数据流动方向

Cache Aside: 

> `Client ↔ { DB , Cache }`

Cache Through:

> `Client ← Cache ← DB`
>
> 写操作通过消息队列控制负载、直接写入DB，再通过DB刷新到Cache中

### (4) 不使用缓存

### (5) 中间件Canal

> todo