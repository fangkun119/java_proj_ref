<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Rate Limiter/时间序列/连接管理/Error Handling/性能/调试及Redis协议](#rate-limiter%E6%97%B6%E9%97%B4%E5%BA%8F%E5%88%97%E8%BF%9E%E6%8E%A5%E7%AE%A1%E7%90%86error-handling%E6%80%A7%E8%83%BD%E8%B0%83%E8%AF%95%E5%8F%8Aredis%E5%8D%8F%E8%AE%AE)
  - [1. Rate Limiter](#1-rate-limiter)
    - [1.1 介绍](#11-%E4%BB%8B%E7%BB%8D)
    - [1.2 实现](#12-%E5%AE%9E%E7%8E%B0)
      - [(1) 接口](#1-%E6%8E%A5%E5%8F%A3)
      - [(2) Fixed Window Rate Limiter](#2-fixed-window-rate-limiter)
      - [(3) Sliding Window Rate Limiter](#3-sliding-window-rate-limiter)
  - [2. 使用 RedisTimeSeires Module处理时间序列数据](#2-%E4%BD%BF%E7%94%A8-redistimeseires-module%E5%A4%84%E7%90%86%E6%97%B6%E9%97%B4%E5%BA%8F%E5%88%97%E6%95%B0%E6%8D%AE)
    - [2.1 介绍](#21-%E4%BB%8B%E7%BB%8D)
      - [(1) RedisTimeSeries Module](#1-redistimeseries-module)
      - [(2) 文档](#2-%E6%96%87%E6%A1%A3)
      - [(3) `Tuple`（又称sample）](#3-tuple%E5%8F%88%E7%A7%B0sample)
      - [(4) 命令：`TS.ADD`、`TS.RANGE`、`TS.RANGE ... ABGREGATION ...`](#4-%E5%91%BD%E4%BB%A4tsaddtsrangetsrange--abgregation-)
      - [(5) Java代码](#5-java%E4%BB%A3%E7%A0%81)
  - [3. 连接管理](#3-%E8%BF%9E%E6%8E%A5%E7%AE%A1%E7%90%86)
    - [3.1 介绍](#31-%E4%BB%8B%E7%BB%8D)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3)
      - [(2) 用途](#2-%E7%94%A8%E9%80%94)
    - [3.2 命令：`CLIENT SETNAME | GETNAME | LIST`，`SLOWLOG`](#32-%E5%91%BD%E4%BB%A4client-setname--getname--listslowlog)
    - [3.3 Java代码：设置client name以及connection配置](#33-java%E4%BB%A3%E7%A0%81%E8%AE%BE%E7%BD%AEclient-name%E4%BB%A5%E5%8F%8Aconnection%E9%85%8D%E7%BD%AE)
    - [3.4 连接数配置](#34-%E8%BF%9E%E6%8E%A5%E6%95%B0%E9%85%8D%E7%BD%AE)
      - [(1) 如何决定max connection的配置值](#1-%E5%A6%82%E4%BD%95%E5%86%B3%E5%AE%9Amax-connection%E7%9A%84%E9%85%8D%E7%BD%AE%E5%80%BC)
      - [(2) Redis最多能接收多少连接](#2-redis%E6%9C%80%E5%A4%9A%E8%83%BD%E6%8E%A5%E6%94%B6%E5%A4%9A%E5%B0%91%E8%BF%9E%E6%8E%A5)
      - [(3) 确定应用程序可以部署多少个节点](#3-%E7%A1%AE%E5%AE%9A%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F%E5%8F%AF%E4%BB%A5%E9%83%A8%E7%BD%B2%E5%A4%9A%E5%B0%91%E4%B8%AA%E8%8A%82%E7%82%B9)
      - [(4) 原则](#4-%E5%8E%9F%E5%88%99)
  - [4. Error Handling： `JedisException`](#4-error-handling-jedisexception)
    - [(1) `JedisConnectionException`](#1-jedisconnectionexception)
    - [(2) `JedisDataException`](#2-jedisdataexception)
    - [(3) `JedisClusterException`](#3-jedisclusterexception)
  - [5. 性能](#5-%E6%80%A7%E8%83%BD)
    - [5.1 Latency的构成](#51-latency%E7%9A%84%E6%9E%84%E6%88%90)
    - [5.2 使用Redis Pipeline最小化latency](#52-%E4%BD%BF%E7%94%A8redis-pipeline%E6%9C%80%E5%B0%8F%E5%8C%96latency)
    - [5.3 命令的时间复杂度](#53-%E5%91%BD%E4%BB%A4%E7%9A%84%E6%97%B6%E9%97%B4%E5%A4%8D%E6%9D%82%E5%BA%A6)
      - [(1) 各个时间复杂度的命令](#1-%E5%90%84%E4%B8%AA%E6%97%B6%E9%97%B4%E5%A4%8D%E6%9D%82%E5%BA%A6%E7%9A%84%E5%91%BD%E4%BB%A4)
      - [(2) 对性能构成危险的`O(N)`命令](#2-%E5%AF%B9%E6%80%A7%E8%83%BD%E6%9E%84%E6%88%90%E5%8D%B1%E9%99%A9%E7%9A%84on%E5%91%BD%E4%BB%A4)
      - [(3) 注意`Transaction`和`Lua Script`的执行效率](#3-%E6%B3%A8%E6%84%8Ftransaction%E5%92%8Clua-script%E7%9A%84%E6%89%A7%E8%A1%8C%E6%95%88%E7%8E%87)
  - [6. Debugging](#6-debugging)
    - [(1) 检查expired keys](#1-%E6%A3%80%E6%9F%A5expired-keys)
    - [(2) 在Redis CLI测试出问题的命令](#2-%E5%9C%A8redis-cli%E6%B5%8B%E8%AF%95%E5%87%BA%E9%97%AE%E9%A2%98%E7%9A%84%E5%91%BD%E4%BB%A4)
    - [(3) 监控发送给Redis的命令](#3-%E7%9B%91%E6%8E%A7%E5%8F%91%E9%80%81%E7%BB%99redis%E7%9A%84%E5%91%BD%E4%BB%A4)
    - [(4) Lua脚本Debug](#4-lua%E8%84%9A%E6%9C%ACdebug)
  - [7. Redis协议](#7-redis%E5%8D%8F%E8%AE%AE)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3-1)
    - [(2) 例子](#2-%E4%BE%8B%E5%AD%90)
    - [(3) 查看Redis协议的数据包](#3-%E6%9F%A5%E7%9C%8Bredis%E5%8D%8F%E8%AE%AE%E7%9A%84%E6%95%B0%E6%8D%AE%E5%8C%85)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Rate Limiter/时间序列/连接管理/Error Handling/性能/调试及Redis协议

## 1. Rate Limiter

### 1.1 介绍

功能：跟踪一定时间内的请求数量，阻止用户过量发送请求，保护系统资源

常用方法：（1）`Fixed Window`；（2）`Sliding Window`

> Fixed Window：实现简单、但是精确度不够
>
> Sliding Window：精确、但实现较复杂、需要更多的内存空间和计算消耗

### 1.2 实现

#### (1) 接口

> 代码：[/src/main/java/com/redislabs/university/RU102J/dao/RateLimiter.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/RateLimiter.java)
>
> ```java
> public interface RateLimiter {
> 	void hit(String name) throws RateLimitExceededException;
> }
> ```

#### (2) Fixed Window Rate Limiter

> 代码：[/src/main/java/com/redislabs/university/RU102J/dao/RateLimiterFixedDaoRedisImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/RateLimiterFixedDaoRedisImpl.java)
>
> ~~~java
> @Override
> public void hit(String name) throws RateLimitExceededException {
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		// key命名：limiter:${name}:${window_id}:${max-hits}
> 		// 例如：limiter:get_profile:0:5，表示get_profile api在第0个window最多容许5次访问
> 		String key = getKey(name);
> 		// 更新访问计数器
> 		Pipeline pipeline = jedis.pipelined();
> 		Response<Long> hits = pipeline.incr(key);
> 		pipeline.expire(key, expiration); // key可以自动过期
> 		pipeline.sync();
> 		// 检查是否超时
> 		if (hits.get() > maxHits) {
> 			throw new RateLimitExceededException();
> 		}
> 	}
> }
> ~~~
>
> 该方法的优点
>
> 1. 节省内存，只需要占用一个Redis Key
> 2. CPU节省：`O(1)`的时间复杂度
> 3. Redis访问次数少：只有1个round-trip

#### (3) Sliding Window Rate Limiter

> 代码：[/src/main/java/com/redislabs/university/RU102J/dao/RateLimiterSlidingDaoRedisImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/RateLimiterSlidingDaoRedisImpl.java)
>
> ~~~java
> @Override
> public void hit(String name) throws RateLimitExceededException {
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		// Key的格式：limiter:windowsize_ms:name:maxHits
>         // 例如：limiter:500:get_sites:50
> 		String key = KeyHelper.getKey("limiter:" + windowSizeMS + ":" + name + ":" + maxHits);
> 		long now = ZonedDateTime.now().toInstant().toEpochMilli();
> 		// 执行操作
> 		Transaction t = jedis.multi();
> 		String member = now + "-" + Math.random(); 		// 加一个随机数防止被去重
> 		t.zadd(key, now, member); 						// score是当前时间的毫秒数
> 		t.zremrangeByScore(key, 0, now - windowSizeMS); // 删除滑到窗口外的member
> 		Response hits = t.zcard(key);					// 计算当前还有多少个memeber
> 		t.exec();
> 		// 检查是否超过rate limiter限制
> 		if (null != hits.get() && (Long)hits.get() > maxHits) {
> 			throw new RateLimitExceededException();
> 		}
> 	}
> }
> ~~~

## 2. 使用 RedisTimeSeires Module处理时间序列数据

### 2.1 介绍

#### (1) RedisTimeSeries Module

> Redis支持Module扩展，例如
>
> * 支持全文搜索的RedisSearch Module
> * 支持图搜索的GraphSearch
>
> 而RedisTimeSeries Module则为Redis增加了时间序列数据的存储和处理能力
>
> 本节对其简单介绍

#### (2) 文档

> [RedisTimeSeries documentation](https://oss.redislabs.com/redistimeseries/)

#### (3) `Tuple`（又称sample）

> * 由一个timestamp in milliseconds和一个measurement of type float/double组成
>* 支持使用`max retention`配置来自动设置旧sample的清理（不需要显示地使用expire命令）
> * 支持范围查找
> * 支持聚合查询、包括：`average`、`sum`、`min`、`max`、...

####  (4) 命令：`TS.ADD`、`TS.RANGE`、`TS.RANGE ... ABGREGATION ...`

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_timeseries_cmd_example.jpg" width="600" /></div>

#### (5) Java代码

> 使用JRedisTimeSeries Lib：[https://github.com/RedisTimeSeries/JRedisTimeSeries](https://github.com/RedisTimeSeries/JRedisTimeSeries)

接口：[/src/main/java/com/redislabs/university/RU102J/dao/MetricDao.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/MetricDao.java)

> ```java
> public interface MetricDao {
> 	void insert(MeterReading reading);
> 	List<Measurement> getRecent(Long siteId, MetricUnit unit, ZonedDateTime time,Integer limit);
> }
> ```

实现：

> 使用Sorted Set：[/src/main/java/com/redislabs/university/RU102J/dao/MetricDaoRedisTSImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/MetricDaoRedisTSImpl.java)
>
> 使用TimeSeries：[/src/main/java/com/redislabs/university/RU102J/dao/MetricDaoRedisZsetImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/MetricDaoRedisZsetImpl.java)

## 3. 连接管理

### 3.1 介绍

#### (1) 文档

> - [ SLOWLOG](https://redis.io/commands/slowlog)
> - [ CLIENT SETNAME](https://redis.io/commands/client-setname)
> - [ CLIENT GETNAME](https://redis.io/commands/client-getname)
> - [ CLIENT LIST](https://redis.io/commands/client-list)

#### (2) 用途

> 给Client命名，可以有助于debug，例如使用SLOWLOG命令分析慢查询，发现连接泄漏等
>
> 命名通常包含如下信息：(1) Hostname (2) Application Name (3) Process ID 
>
> 例如：`app25.example.com-redisolar-41992`

### 3.2 命令：`CLIENT SETNAME | GETNAME | LIST`，`SLOWLOG`

> ~~~bash
> # 客户端命令
> 127.0.0.1:6379> CLIENT SETNAME ru102j
> OK
> 127.0.0.1:6379> CLIENT GETNAME
> "ru102j"
> # 当前连接到redis的客户端
> 127.0.0.1:6379> CLIENT LIST
> id=20 addr=127.0.0.1:50868 laddr=127.0.0.1:6379 fd=8 name= age=324914 idle=271449 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=0 argv-mem=0 obl=0 oll=0 omem=0 tot-mem=17440 events=r cmd=bitcount user=default redir=-1
> id=33 addr=127.0.0.1:55265 laddr=127.0.0.1:6379 fd=9 name=ru102j age=267311 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=26 qbuf-free=45024 argv-mem=10 obl=0 oll=0 omem=0 tot-mem=62490 events=r cmd=client user=default redir=-1
> # 查看SLOW LOG
> 127.0.0.1:6379> SLOWLOG GET 1
> 1) 1) (integer) 3
>    2) (integer) 1617085903
>    3) (integer) 10626
>    4) 1) "CLIENT"
>       2) "LIST"
>    5) "127.0.0.1:55265"
>    6) "ru102j"
> # 帮助命令
> 127.0.0.1:6379> SLOWLOG HELP
>  1) SLOWLOG <subcommand> [<arg> [value] [opt] ...]. Subcommands are:
>  2) GET [<count>]
>  3)     Return top <count> entries from the slowlog (default: 10). Entries are
>  4)     made of:
>  5)     id, timestamp, time in microseconds, arguments array, client IP and port,
>  6)     client name
>  7) LEN
>  8)     Return the length of the slowlog.
>  9) RESET
> 10)     Reset the slowlog.
> 11) HELP
> 12)     Prints this help.   
> ~~~

### 3.3 Java代码：设置client name以及connection配置

> 创建`JedisPool`、`JedisSentinelPool`、`JedisCluster`对象时都可以传入client name

例子：[/src/main/java/com/redislabs/university/RU102J/examples/ConnectionExamples.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/examples/ConnectionExamples.java)

> ```java
> public static JedisPool getPool(String host, Integer port,
> 		int maxConnections, int timeout, String password) {
> 	JedisPoolConfig config = new JedisPoolConfig
> 	// 查看JedisPoolConfig及基类代码，可以看到下面两个配置的默认值都是8
> 	config.setMaxTotal(maxConnections);
> 	config.setMaxIdle(maxConnections);
> 	return new JedisPool(config, host, port, timeout, password);
> }
> ```

### 3.4 连接数配置

#### (1) 如何决定max connection的配置值

> 一个方法是，根据应用程序并发的线程数
>
> 例如本章例子中、DropWizard的线程数配置为 8 ~ 1024，但是观察生产环境实际运行状况，峰值线程数加上buffer基本稳定在80左右，因此max connection配置为80

#### (2) Redis最多能接收多少连接

> 默认10000，超过后将会拒绝新的连接

#### (3) 确定应用程序可以部署多少个节点

> 10000 / 80 = 125
>
> 最多可以部署125个节点

#### (4) 原则

> 每个线程一个连接（最佳实践），总连接数不超过Redis的上限
>
> 要做重组的压力测试和线上数据估算

## 4. Error Handling： `JedisException`

> 当遇到错误时，Jedis抛出JedisException，它是RuntimeException（unchecked exception）不强制要求外部代码取捕捉这些异常。但有时候，知道是哪种JedisException（即JedisException的哪个子类）能够更完善地处理这些错误。以下是这些JedisException的类型：

### (1) `JedisConnectionException`

> Redis连接失败，通常由Server故障或者网络原因引起

### (2) `JedisDataException`

>通常在不能执行某个Redis命令时产生，例如
>
>* Redis内存不足
>* 命令参数的数据类型不对，例如对embstr进行increment操作

### (3) `JedisClusterException`

> 参考[ JedisCluster documentation at javadoc.io](https://static.javadoc.io/redis.clients/jedis/2.9.0/redis/clients/jedis/JedisCluster.html)

## 5. 性能

### 5.1 Latency的构成

> 1. 网络延时
> 2. 命令的时间复杂度以数据的Cardinality
> 3. 原子操作和阻塞

### 5.2 使用Redis Pipeline最小化latency

适用场景：

> 运行多条命令，但是不需要立即得到每条命令的返回

性能收益：

> 降低Round Trips的数量
>
> 减少了Redis Server的上下文切换
>
> 减少系统调用

当然有时候需要原子保证，会用到Redis Transaction

### 5.3 命令的时间复杂度

#### (1) 各个时间复杂度的命令

> 在redis.io网站上可以查到每条命令的时间复杂度
>
> `O(1)`时间复杂度的命令
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_cmd_time_complexity_o1.jpg" width="350" /></div>
>
> `O(log(n))`时间复杂度的命令
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_cmd_time_complexity_ologn.jpg" width="350" /></div>
>
> 几乎从来不在生产环境运行的`O(n)`时间复杂度的命令：`KEYS`
>
> 另外在大lists或大hashes上也从来不使用`LRANGE`和`HGETALL`等

#### (2) 对性能构成危险的`O(N)`命令

> 参考：[ Redis Security at redis.io (disabling commands)](https://redis.io/topics/security#disabling-of-specific-commands)

#### (3) 注意`Transaction`和`Lua Script`的执行效率

> 出于原子性的需要，他们同样会阻塞其他的Redis命令
>
> 不需要事务属性时，可以考虑使用不会阻塞其他命令的redis pipeline

## 6. Debugging

> 对于使用Jedis编写的程序，常用的Debug技巧如下

### (1) 检查expired keys

> 应用程序依赖已经expired的key会产生问题
>
> * 检查使用了[EXPIRE](https://redis.io/commands/expire)命令的key，确保TTL（Time To Live）设置足够
> * 检查 [maxmemory-policy setting](https://redis.io/topics/lru-cache#eviction-policies) ，确保key没有因为内存达到max limit而开始被去驱逐（evicted）

### (2) 在Redis CLI测试出问题的命令

> 如果命令在Redis CLI同样出问题，那说明是执行的命令不当导致

### (3) 监控发送给Redis的命令

> 使用Redis的[MONITOR](https://redis.io/commands/monitor)命令，可以查看当前所有发送给Redis的命令，从而发现是哪些命令有问题

### (4) Lua脚本Debug

> Lua脚本Debug比较复杂，Redis提供了” [fully-featured debugger](https://redis.io/topics/ldb) “用于对Lua的调试

## 7. Redis协议

### (1) 文档

> [https://redis.io/topics/protocol](https://redis.io/topics/protocol)

### (2) 例子

执行命令：`lpush test hello`

命令会被解析成以下的数据放入发送给Redis的数据包中

> ~~~txt
> *3\r\n		// 表示命令由3个元素主城
> $5\r\n		// 表示第一个元素是5个字符组成的字符串
> lpush\r\n
> $4\r\n		// 表示第二个元素是4个字符组成的字符串
> test\r\n
> $5\r\n		// 表示第三个元素是5个字符组成的字符串
> hello\r\n
> ~~~

命令执行后，返回结果时1，会被解析成以下的数据，放入Redis返回的数据包中

> ~~~txt
> :1\t\n		// ":"表示返回值为integer类型
> ~~~

### (3) 查看Redis协议的数据包

> 使用[ngrep](https://en.wikipedia.org/wiki/Ngrep)或者[Wireshark](https://www.wireshark.org/)，例如
>
> ~~~bash
> ngrep -W byline -d lo0 -t '' 'port 6379'
> ~~~