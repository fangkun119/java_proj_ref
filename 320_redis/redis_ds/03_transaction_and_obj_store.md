<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Transaction及对象存储](#transaction%E5%8F%8A%E5%AF%B9%E8%B1%A1%E5%AD%98%E5%82%A8)
  - [1. Transaction](#1-transaction)
    - [1.1 Transaction介绍](#11-transaction%E4%BB%8B%E7%BB%8D)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3)
      - [(2) Transaction](#2-transaction)
    - [1.2. Transaction使用](#12-transaction%E4%BD%BF%E7%94%A8)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3-1)
      - [(2) 命令：`MULTI`、`EXEC`、`DISCARD`](#2-%E5%91%BD%E4%BB%A4multiexecdiscard)
      - [(3) Demo 1：事务的隔离性](#3-demo-1%E4%BA%8B%E5%8A%A1%E7%9A%84%E9%9A%94%E7%A6%BB%E6%80%A7)
      - [(4) Demo 2：使用DISCARD命令取消事务](#4-demo-2%E4%BD%BF%E7%94%A8discard%E5%91%BD%E4%BB%A4%E5%8F%96%E6%B6%88%E4%BA%8B%E5%8A%A1)
      - [(5) Demo 3：不容许嵌套MULTI命令](#5-demo-3%E4%B8%8D%E5%AE%B9%E8%AE%B8%E5%B5%8C%E5%A5%97multi%E5%91%BD%E4%BB%A4)
    - [1.3 错误处理和事务回滚](#13-%E9%94%99%E8%AF%AF%E5%A4%84%E7%90%86%E5%92%8C%E4%BA%8B%E5%8A%A1%E5%9B%9E%E6%BB%9A)
      - [(1) Redis事务中对错误命令的处理方式](#1-redis%E4%BA%8B%E5%8A%A1%E4%B8%AD%E5%AF%B9%E9%94%99%E8%AF%AF%E5%91%BD%E4%BB%A4%E7%9A%84%E5%A4%84%E7%90%86%E6%96%B9%E5%BC%8F)
        - [(a) 语法错误（Syntax Error，例如参数数量不符合命令要求）](#a-%E8%AF%AD%E6%B3%95%E9%94%99%E8%AF%AFsyntax-error%E4%BE%8B%E5%A6%82%E5%8F%82%E6%95%B0%E6%95%B0%E9%87%8F%E4%B8%8D%E7%AC%A6%E5%90%88%E5%91%BD%E4%BB%A4%E8%A6%81%E6%B1%82)
        - [(b) 执行错误（Operation Error，例如在一个使用TEXT编码的数据上执行INCR命令）](#b-%E6%89%A7%E8%A1%8C%E9%94%99%E8%AF%AFoperation-error%E4%BE%8B%E5%A6%82%E5%9C%A8%E4%B8%80%E4%B8%AA%E4%BD%BF%E7%94%A8text%E7%BC%96%E7%A0%81%E7%9A%84%E6%95%B0%E6%8D%AE%E4%B8%8A%E6%89%A7%E8%A1%8Cincr%E5%91%BD%E4%BB%A4)
        - [(c) 系统错误（System Error，例如Redis内存不足引发命令执行错误）](#c-%E7%B3%BB%E7%BB%9F%E9%94%99%E8%AF%AFsystem-error%E4%BE%8B%E5%A6%82redis%E5%86%85%E5%AD%98%E4%B8%8D%E8%B6%B3%E5%BC%95%E5%8F%91%E5%91%BD%E4%BB%A4%E6%89%A7%E8%A1%8C%E9%94%99%E8%AF%AF)
      - [(2) Redis不提供回滚机制和嵌套事务](#2-redis%E4%B8%8D%E6%8F%90%E4%BE%9B%E5%9B%9E%E6%BB%9A%E6%9C%BA%E5%88%B6%E5%92%8C%E5%B5%8C%E5%A5%97%E4%BA%8B%E5%8A%A1)
    - [1.4 乐观锁（Optimistic Concurrency Control）](#14-%E4%B9%90%E8%A7%82%E9%94%81optimistic-concurrency-control)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3-2)
      - [(2) 命令和使用：`WATCH`、`UNWATCH`](#2-%E5%91%BD%E4%BB%A4%E5%92%8C%E4%BD%BF%E7%94%A8watchunwatch)
      - [(3) 例子](#3-%E4%BE%8B%E5%AD%90)
  - [2. 对象（Domain Object）存储](#2-%E5%AF%B9%E8%B1%A1domain-object%E5%AD%98%E5%82%A8)
    - [2.1 介绍](#21-%E4%BB%8B%E7%BB%8D)
      - [(1) 两种存储Domain Object的方法](#1-%E4%B8%A4%E7%A7%8D%E5%AD%98%E5%82%A8domain-object%E7%9A%84%E6%96%B9%E6%B3%95)
      - [(2) 使用Redis Hash存储Domain Object](#2-%E4%BD%BF%E7%94%A8redis-hash%E5%AD%98%E5%82%A8domain-object)
      - [(3) 文档](#3-%E6%96%87%E6%A1%A3)
    - [2.2 存储Simple Object](#22-%E5%AD%98%E5%82%A8simple-object)
    - [2.3 存储复杂对象](#23-%E5%AD%98%E5%82%A8%E5%A4%8D%E6%9D%82%E5%AF%B9%E8%B1%A1)
      - [(1) 背景和方法](#1-%E8%83%8C%E6%99%AF%E5%92%8C%E6%96%B9%E6%B3%95)
      - [(2) 方法1：将字段打平到一个Hash中（使用字段层次化命名）](#2-%E6%96%B9%E6%B3%951%E5%B0%86%E5%AD%97%E6%AE%B5%E6%89%93%E5%B9%B3%E5%88%B0%E4%B8%80%E4%B8%AAhash%E4%B8%AD%E4%BD%BF%E7%94%A8%E5%AD%97%E6%AE%B5%E5%B1%82%E6%AC%A1%E5%8C%96%E5%91%BD%E5%90%8D)
      - [(2) 方法2：每个对象使用一个Hash（使用Hash层次化命名）](#2-%E6%96%B9%E6%B3%952%E6%AF%8F%E4%B8%AA%E5%AF%B9%E8%B1%A1%E4%BD%BF%E7%94%A8%E4%B8%80%E4%B8%AAhash%E4%BD%BF%E7%94%A8hash%E5%B1%82%E6%AC%A1%E5%8C%96%E5%91%BD%E5%90%8D)
      - [(3) 方法3： 在方法2的基础上，用Set类维护对象关系](#3-%E6%96%B9%E6%B3%953-%E5%9C%A8%E6%96%B9%E6%B3%952%E7%9A%84%E5%9F%BA%E7%A1%80%E4%B8%8A%E7%94%A8set%E7%B1%BB%E7%BB%B4%E6%8A%A4%E5%AF%B9%E8%B1%A1%E5%85%B3%E7%B3%BB)
  - [3. 例子：票务库存管理](#3-%E4%BE%8B%E5%AD%90%E7%A5%A8%E5%8A%A1%E5%BA%93%E5%AD%98%E7%AE%A1%E7%90%86)
    - [3.1 功能](#31-%E5%8A%9F%E8%83%BD)
    - [3.2 实现](#32-%E5%AE%9E%E7%8E%B0)
      - [(1) 数据存储](#1-%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8)
      - [(2) 仅考虑锁定库存](#2-%E4%BB%85%E8%80%83%E8%99%91%E9%94%81%E5%AE%9A%E5%BA%93%E5%AD%98)
      - [(3) 加入付款操作：库存锁定→付款（成功或失败）](#3-%E5%8A%A0%E5%85%A5%E4%BB%98%E6%AC%BE%E6%93%8D%E4%BD%9C%E5%BA%93%E5%AD%98%E9%94%81%E5%AE%9A%E2%86%92%E4%BB%98%E6%AC%BE%E6%88%90%E5%8A%9F%E6%88%96%E5%A4%B1%E8%B4%A5)
      - [(4) 处理超时付款](#4-%E5%A4%84%E7%90%86%E8%B6%85%E6%97%B6%E4%BB%98%E6%AC%BE)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Transaction/对象存储/库存管理例子

## 1. Transaction

### 1.1 Transaction介绍

#### (1) 文档

>  Articles at redis.io
>
> * [ Documentation for Transaction commands](https://redis.io/commands#transactions)
> * [ How client connections are accepted and commands processed](https://redis.io/topics/clients)
>
> Articles at Wikipedia
>
> * [ Database Transactions](https://en.wikipedia.org/wiki/Database_transaction)
> * [ ACID](https://en.wikipedia.org/wiki/ACID)

#### (2) Transaction

> 在Transaction中执行一组命令，具有如下性质：
>
> 1. 隔离性：不受Transaction以外其他命令的影响
> 2. 原子性：Redis会保证事务中所有命令要么全部执行、要么全部不执行
>
> 关于部分命令执行失败的场景，本章后半部分会讨论rollback相关的事宜

### 1.2. Transaction使用

#### (1) 文档

> * [Documentation for Transaction commands](https://redis.io/commands#transactions)
> * [Documentation for Client Handling](https://redis.io/topics/clients)

#### (2) 命令：`MULTI`、`EXEC`、`DISCARD`

`MULTI`：用于指定一个transaction的开始

`EXEC`：执行事务队列中的命令

`DISCARD`：放弃事务队列中的任何命令

#### (3) Demo 1：事务的隔离性

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/reids_ds_transaction_demo.jpg" width="500" /></div>
>
> Client 1：将某个key对应的value设置为”Sold Out“，执行get时拿到的仍然是”Sold out“
>
> Client 2：创建transaction，在事务中将key的值设置为100，并加1，然后再get；事务执行后get到的值时101

上面例子的命令行演示如下

> client 1：
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_01/
> $ redis-cli
> 127.0.0.1:6379> set event:Judo "Sold Out"
> OK
> ~~~
>
> client 2:
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_02/
> $ redis-cli
> 127.0.0.1:6379> multi
> OK
> 127.0.0.1:6379(TX)> set event:Judo 100
> QUEUED
> 127.0.0.1:6379(TX)> incr event:Judo
> QUEUED
> ~~~
>
> client 1:
>
> ~~~bash
> 127.0.0.1:6379> get event:Judo
> "Sold Out"
> ~~~
>
> client 2:
>
> ~~~bash
> 127.0.0.1:6379(TX)> get event:Judo
> QUEUED
> 127.0.0.1:6379(TX)> exec
> 1) OK
> 2) (integer) 101
> 3) "101"
> 127.0.0.1:6379>
> ~~~

#### (4) Demo 2：使用DISCARD命令取消事务

> ~~~bash
> 127.0.0.1:6379> get event:Judo
> "101"
> 127.0.0.1:6379> multi
> OK
> 127.0.0.1:6379(TX)> get event:Judo
> QUEUED
> 127.0.0.1:6379(TX)> incrby event:Judo 20
> QUEUED
> 127.0.0.1:6379(TX)> discard
> OK
> 127.0.0.1:6379> get event:Judo
> "101"
> ~~~

#### (5) Demo 3：不容许嵌套MULTI命令

> ~~~bash
> 127.0.0.1:6379> multi
> OK
> 127.0.0.1:6379(TX)> set foo 123
> QUEUED
> 127.0.0.1:6379(TX)> multi
> (error) ERR MULTI calls can not be nested
> ~~~

### 1.3 错误处理和事务回滚

#### (1) Redis事务中对错误命令的处理方式

执行EXEC命令在事务中执行一组Redis命令时，如果其中有命令出错，不同的错误方式处理方法不同

##### (a) 语法错误（Syntax Error，例如参数数量不符合命令要求）

> Redis可以检查出这些命令，整个事务都不会执行

##### (b) 执行错误（Operation Error，例如在一个使用TEXT编码的数据上执行INCR命令）

> 这类错误只有在命令执行时才能发现，命令执行失败后，Redis会继续执行事务中后续的命令

##### (c) 系统错误（System Error，例如Redis内存不足引发命令执行错误）

> Redis通过内部safeguard机制来保证不存在partial writes和inconsistencies状态

#### (2) Redis不提供回滚机制和嵌套事务

> Redis不提供回滚机制，以保证吞吐量和低延时，认为执行错误和系统错误应当在pre-production阶段被解决
>
> Redis也不支持嵌套事务，因为Redis通过命令队列的方式来支持事务以使得Redis的实现简单清晰

### 1.4 乐观锁（Optimistic Concurrency Control）

> 使用乐观锁访问某个Redis Key，当命令停留在在Transaction队列中还没有被原子执行时，如果key的数据被其他请求所修改，乐观锁会让这个事务不被执行、并让客户端得到通知

#### (1) 文档

> [ Wikipedia article on Optimistic concurrency control](https://en.wikipedia.org/wiki/Optimistic_concurrency_control)

#### (2) 命令和使用：`WATCH`、`UNWATCH`

命令

> `WATCH <key> [<key> ...]` ：用来声明对一个或多个key使用乐观锁
>
> `UNWATCH <key> [<key> ...]`：解锁

使用过程如下

> `WATCH [WATCH ... ]` → `EXEC` → `自动UNWATCH`
>
> * 可以使用多个WATCH命令，来对多个key使用乐观锁，命令之间是累积关系（后面的WATCH不会覆盖前面的WATCH）
> * WATCH只为当前的client/connection获取乐观锁，底层数据被修改时，也这有这个client/connection的事务会失败，不会影响其他的事务
> * EXEC命令执行的事务执行结束之后，会自动UNWATCH，不需要显式地执行UNWATCH命令

#### (3) 例子

场景

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redis_ds_watch_example.jpg" width="500" /></div>

命令行演示：Happy Flow、没有client 2影响

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_01/
> $ redis-cli
> 127.0.0.1:6379> set event:Judo 100
> OK
> 127.0.0.1:6379> get event:Judo
> "100"
> 127.0.0.1:6379> watch event:Judo
> OK
> 127.0.0.1:6379> multi
> OK
> 127.0.0.1:6379(TX)> set event:Judo 200
> QUEUED
> 127.0.0.1:6379(TX)> exec
> 1) OK
> ~~~

命令行演示：Unhappy Flow、Client 2导致乐观锁失败

> Client 1：
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_01/
> $ redis-cli
> 127.0.0.1:6379> set event:Judo 100
> OK
> 127.0.0.1:6379> get event:Judo
> "100"
> 127.0.0.1:6379> watch event:Judo
> OK
> 127.0.0.1:6379> multi
> OK
> 127.0.0.1:6379(TX)> incr event:Judo
> QUEUED
> ~~~
>
> Client 2：
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_02/
> $ redis-cli
> 127.0.0.1:6379> decr event:Judo
> (integer) 199
> ~~~
>
> Client 1：
>
> ~~~bash
> 127.0.0.1:6379(TX)> exec
> (nil) # nil表示事务的command queue没有被执行
> 127.0.0.1:6379> get event:Judo
> "199" # client设置的值
> ~~~

## 2. 对象（Domain Object）存储

### 2.1 介绍

#### (1) 两种存储Domain Object的方法

> Domian Object可以序列化成Json然后用String来存储，但是这样访问Object中的字段会非常低效
>
> 另一种方法是本章介绍的`使用Hash来存储Domain Object`。

#### (2) 使用Redis Hash存储Domain Object

> 因为Hash的value只能是Redis String、不容许嵌套。因此每个Hash只能存储一个Domain Object。本章先介绍Simple Domain Object的存储、再介绍Nested Domain Object的存储。

#### (3) 文档

> [Documentation for Hash commands at redis.io](https://redis.io/commands#hash)
>
> [Wikipedia article on Glob style wildcards](https://en.wikipedia.org/wiki/Glob_(programming))

### 2.2 存储Simple Object

例子

> ~~~json
> __events__ = [{
> 				'sku': "123-ABC-723",
> 				'name': "Men's 100m Final",
> 				'disabled_access': True,
> 				'medal_event': True,
> 				'venue': "Olympic Stadium",
> 				'category': "Track & Field"
> 			},{
> 				'sku': "737-DEF-911",
> 				'name': "Women's 4x100m Heats",
> 				'disabled_access': True,
> 				'medal_event': False,
> 				'venue': "Olympic Stadium",
> 				'category': "Track & Field"
> 			},{
> 				'sku': "320-GHI-921",
> 				'name': "Womens Judo Qualifying",
> 				'disabled_access': False,
> 				'medal_event': False,
> 				'venue': "Nippon Budokan",
> 				'category': "Martial Arts"
> 			}]
> ~~~

将上述三个对象，存储到hash中，可以使用HGET、HSET、HMGET、HGETALL、HEXISTS、HSETNX、HINCRBY、HINCRBYFLOAT、HDEL、HLEN、HKEYS、HCVALS、……等命令操作这些对象和对象中的字段

> HGETALL是一个会阻塞的命令；HSCAN是非阻塞命令，采用游标的方式获取数据
>
> 另外要注意字段的数据量，一个field最大容许存储512MB的数据，虽然很少会存储这样量级

采用结构化的key命名，可以区分这些对象，上面的三个对象为例，可以使用如下的key

> `events:123-ABC-723`
>
> `events:737-DEF-911`
>
> `events:320-GHI-921`

想取出一个对象中特定的field，可以使用HSCAN命令结合match通配符的方式

> 例如取出对象中"available:"为前缀的field
>
> ~~~bash
> redis:6373 > hscan events:123-ABC-723 0 match available:*
> 1)  "0"
> 2)	1) "available:vip"
> 	2) "100"
> 	3) "available:assigned"
> 	4) "2000"
> 	5) "available:general"
> 	6) "4000"
> ~~~

### 2.3 存储复杂对象

#### (1) 背景和方法

背景（Domain Model）

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_cpxobj_domain_model.jpg" width="350" /></div>
>
> 对象之间存在关联，一个Event被删除时，与它相关的TicketTier、Event-TicketTier都要被删除

数据的JSON表示如下，是有嵌套关系的3个对象

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/javads_cpxobj_json.jpg" width="350" /></div>

存储方式

> * Multiple Hashes：每个Object一个Hash
>
> * Multiple Hashes + Sets：将对象之前关系存储在Sets中，简化查找签到的子对象
>
> * Flattening Relationship into a Single Hash：使用层次化命名将几个对象中的字段打平；所有对象的字段存储在同一个Hash中

#### (2) 方法1：将字段打平到一个Hash中（使用字段层次化命名）

用JSON表示、形式如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/javads_cpxobj_json.jpg" width="350" /></div>

对应的Redis命令如下：字段名使用了层次化命名（例如：available*）

> ~~~bash
> 127.0.0.1:6379> hmset event:123-ABC-723 available:general:qty 20000 available:general:price 25.0
> OK
> 127.0.0.1:6379> hmget event:123-ABC-723 available:general:qty available:general:price
> 1) "20000"
> 2) "25.0"
> 127.0.0.1:6379> hscan event:123-ABC-723 0 match available:general:*
> 1) "0"
> 2) 1) "available:general:qty"
>    2) "20000"
>    3) "available:general:price"
>    4) "25.0"
> 127.0.0.1:6379> hincrby event:123-ABC-723 available:general:qty -1
> (integer) 19999
> ~~~

**该方法优点如下**

> * 原子更新
> * 原子删除
> * 没有事务
> * 自动封装（不需要额外进行CASCADE DELETE on foreign keys）

**缺点：**关系维护开销

> * 删除一个子对象，需要通过删除一批字段来实现
> * 更换一个子对象，需要更换一批字段
> * 可能会flat出一个非常大的Hash

#### (2) 方法2：每个对象使用一个Hash（使用Hash层次化命名）

用JSON表示，数据如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_cpxobj_sephash.jpg" width="400" /></div>

对应的redis命令如下，需要对Hash使用层次化命名

> ~~~bash
> 127.0.0.1:6379> del event:123-ABC-723
> (integer) 1
> 127.0.0.1:6379> hmset event:123-ABC-723 name "Men's 100m Final" 'disable_access' True
> OK
> 127.0.0.1:6379> hmset event:123-ABC-723:available:general qty 20000 price 25.00
> OK
> 127.0.0.1:6379> hmget event:123-ABC-723:available:general qty price
> 1) "20000"
> 2) "25.00"
> 127.0.0.1:6379> hincrby event:123-ABC-723:available:general qty -1
> (integer) 19999
> ~~~

该方法的优缺点

> 参考下面的方法3

#### (3) 方法3： 在方法2的基础上，用Set类维护对象关系

> 上面的对象关系维护，是使用命名规则来隐含进行的
>
> 方法3在此基础之上，将对象关系记录在Set中

添加关系记录的redis命令如下

> ~~~bash
> 127.0.0.1:6379> sadd event:123-ABC-723:available event:123-ABC-723:available:general
> (integer) 1
> 127.0.0.1:6379> smembers event:123-ABC-723:available
> 1) "event:123-ABC-723:available:general"
> ~~~

**优点**

(1) 可以支持更加灵活的对象关系，让符合对象的数据结构可扩展

> 例如，再增加一个赛事VIP可用票务的信息
>
> ~~~bash
> 127.0.0.1:6379> hmset event:123-ABC-723:available:vip qty 1000 price 250.00
> OK
> 127.0.0.1:6379> hmget event:123-ABC-723:available:vip qty price
> 1) "1000"
> 2) "250.00"
> 127.0.0.1:6379> sadd event:123-ABC-723:available event:123-ABC-723:available:vip
> (integer) 1
> 127.0.0.1:6379> smembers event:123-ABC-723:available
> 1) "event:123-ABC-723:available:vip"
> 2) "event:123-ABC-723:available:general"
> ~~~

(2) 数据独立存储

(3) 可以单独为每个子对象设置TTL（Time To Live）

> 例如：某种票限时售卖

**缺点**

(1) 需要维护多个Key，增加复杂度

(2) 需要事务封装，但是如果使用Redis Cluster，相关的Hash和Set必须位于同一个Sharding分片下才能使用事务

## 3. 例子：票务库存管理

### 3.1 功能

功能点1：购票

> 每个Ticket只能被购买一次，在购买过程中Ticket被锁定，如果Ticket购买没有完成，要放回到Ticket Pool中，顾客可以购买（同一个赛事、即event）的多张票。过程如下：
>
> (1) 锁定库存
>
> (2) 信用卡授权和付款
>
> (3) 购买成功、或归还库存

功能点2：查看已购的票

### 3.2 实现

代码：

> [https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc02-inventory-control/inventory.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc02-inventory-control/inventory.py)

#### (1) 数据存储

> 使用之前的`方法1 —— 将字段打平到一个Hash中`来存储3个赛事的信息
>
> ~~~python
> events = [{'sku': "123-ABC-723",
> 		 'name': "Men's 100m Final",
> 		 'disabled_access': "True",
> 		 'medal_event': "True",
> 		 'venue': "Olympic Stadium",
> 		 'category': "Track & Field",
> 		 'capacity': 60102,
> 		 'available:General': 20000,
> 		 'price:General': 25.00
> 		},
> 		{'sku': "737-DEF-911",
> 		 'name': "Women's 4x100m Heats",
> 		 'disabled_access': "True",
> 		 'medal_event': "False",
> 		 'venue': "Olympic Stadium",
> 		 'category': "Track & Field",
> 		 'capacity': 60102,
> 		 'available:General': 10000,
> 		 'price:General': 19.50
> 		},
> 		{'sku': "320-GHI-921",
> 		 'name': "Womens Judo Qualifying",
> 		 'disabled_access': "False",
> 		 'medal_event': "False",
> 		 'venue': "Nippon Budokan",
> 		 'category': "Martial Arts",
> 		 'capacity': 14471,
> 		 'available:General': 5000,
> 		 'price:General': 15.25
> 		}
> ~~~
>
> 存储数据：每个对象存储在一个Hash中，再使用一个Set来存储这个Hash的key
>
> ~~~python
> def create_events(event_array, available=None, price=None, tier="General"):
> 	""" Create events from the array of passed event details. Provides overrides
> for number of available tickets, price and ticket tier."""
> 	e_set_key = keynamehelper.create_key_name("events")
> 	for event in event_array:
> 		# Override the availability & price if provided
> 		if available != None:
> 			event['available:' + tier] = available
> 		if price != None:
> 			event['price:' + tier] = price
> 		e_key = keynamehelper.create_key_name("event", event['sku'])
> 		redis.hmset(e_key, event)
> 		redis.sadd(e_set_key, event['sku'])
> ~~~

#### (2) 仅考虑锁定库存

时序图

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_ticket_inventory_ctrl_seq_diagram.jpg" width="500" /></div>
>
> 需要确认库存，避免超卖（下一章将用bit store来实现座位锁定，当前这章focus在数据正确）

代码

> ~~~python
> # Part One - Check availability and Purchase
> def check_availability_and_purchase(customer, event_sku, qty, tier="General"):
> 	"""Check if there is sufficient inventory before making the purchase"""
> 	# 封装在一个Redis Pipeline中执行
> 	p = redis.pipeline()
> 	try: 
> 		# 用乐观锁，保证读取到的数据，在pipeline执行期间没有被其他请求修改
> 		e_key = keynamehelper.create_key_name("event", event_sku)
> 		redis.watch(e_key)
> 		# 获取可用票数、票价
> 		available = int(redis.hget(e_key, "available:" + tier))
> 		price = float(redis.hget(e_key, "price:" + tier))
> 		# 检查库存
> 		if available >= qty:
> 			# 减少库存
> 			p.hincrby(e_key, "available:" + tier, -qty)
> 			# 创建订单
> 			order_id = generate.order_id()
> 			purchase = {'order_id': order_id, 'customer': customer,
> 									'tier': tier, 'qty': qty, 'cost': qty * price,
> 									'event_sku': event_sku, 'ts': int(time.time())}
> 			so_key = keynamehelper.create_key_name("sales_order", order_id)
> 			p.hmset(so_key, purchase)
> 			# 执行pipe line中的所有操作
> 			p.execute()
> 		else:
> 			print("Insufficient inventory, have {}, requested {}".format(available,																															 qty))
> 	except WatchError:
> 		print("Write Conflict check_availability_and_purchase: {}".format(e_key))
> 	finally:
> 		# 确保pipeline总是被释放
> 		p.reset()
> 	print("Purchase complete!")
> ~~~

测试：代码中的`test_check_and_purchase`方法

#### (3) 加入付款操作：库存锁定→付款（成功或失败）

因为涉及到信用卡支付，`锁定库存`和`付款购买`无法在一个事务中完成，需要拆分成两个事务

时序图

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_reversation_seq_diagram.jpg" width="600" /></div>

代码

> ~~~python
> # Part Two - Reserve stock & Credit Card auth
> def reserve(customer, event_sku, qty, tier="General"):
> 	"""First reserve the inventory and perform a credit authorization. If successful
> then confirm the inventory deduction or back the deducation out."""
> 	# 在pipeline中执行
> 	p = redis.pipeline()
> 	try:
> 		# 乐观锁加锁
> 		e_key = keynamehelper.create_key_name("event", event_sku)        
> 		redis.watch(e_key)
> 		# 检查库存
> 		available = int(redis.hget(e_key, "available:" + tier))
> 		if available >= qty:
> 			order_id = generate.order_id()
> 			ts = int(time.time())
> 			price = float(redis.hget(e_key, "price:" + tier))
> 			# 锁定库存，用Hash来存储库存锁定信息
> 			p.hincrby(e_key, "available:" + tier, -qty)
> 			p.hincrby(e_key, "held:" + tier, qty)
> 			hold_key = keynamehelper.create_key_name("ticket_hold", event_sku)
> 			p.hsetnx(hold_key, "qty:" + order_id, qty)
> 			p.hsetnx(hold_key, "tier:" + order_id, tier)
> 			p.hsetnx(hold_key, "ts:" + order_id, ts)
> 			# 执行pipeline
> 			p.execute()
> 	except WatchError:
> 		print("Write Conflict in reserve: {}".format(e_key))
> 	finally:
> 		# 保证pipe line总能够释放
> 		p.reset()
> 
> 	# 信用卡授权
> 	if creditcard_auth(customer, qty * price):
> 		try:
> 			purchase = {'order_id': order_id, 'customer': customer,
> 				'tier': tier, 'qty': qty, 'cost': qty * price,
> 				'event_sku': event_sku, 'ts': int(time.time())}
> 			# 用乐观锁锁定该
> 			redis.watch(e_key)
> 			# 删除库存锁定信息
> 			p.hdel(hold_key, "qty:" + order_id,)
> 			p.hdel(hold_key, "tier:" + order_id)
> 			p.hdel(hold_key, "ts:" + order_id)
> 			# 更新销售量
> 			p.hincrby(e_key, "held:" + tier, -qty)
> 			# 添加订单id到订单集合中（so: sales order）
> 			so_key = keynamehelper.create_key_name("sales_order", order_id)
> 			p.hmset(so_key, purchase)
> 			# 执行pipeline
> 			p.execute()
> 		except WatchError:
> 			print("Write Conflict in reserve: {}".format(e_key))
> 		finally:
> 			p.reset()
> 		print("Purchase complete!")
> 	else:
> 		# 授权失败时归还库存
> 		print("Auth failure on order {} for customer {} ${}".format(order_id, customer, price * qty))
> 		backout_hold(event_sku, order_id)
> ~~~

测试：

> `test_reserve`方法

#### (4) 处理超时付款

> 直接用测试用例来演示
>
> ~~~python
> # Part Three - Expire Reservation
> def create_expired_reservation(event_sku, tier="General"):
> 	"""Test function to create a set of reservation that will shortly expire"""
> 	cur_t = time.time()
> 	# 测试用的库存信息
> 	tickets = {'available:' + tier: 485, 'held:' + tier: 15}
> 	# 测试用的锁定信息：
>    	# * 前缀：锁定数量、类型、锁定时间
>    	# * 后缀：订单ID
> 	holds = {
> 		'qty:VPIR6X': 3, 'tier:VPIR6X': tier, 'ts:VPIR6X': int(cur_t - 16),
> 		'qty:B1BFG7': 5, 'tier:B1BFG7': tier, 'ts:B1BFG7': int(cur_t - 22),
> 		'qty:UZ1EL0': 7, 'tier:UZ1EL0': tier, 'ts:UZ1EL0': int(cur_t - 30)
> 	}
>    	# 存入库存信息和锁定信息
> 	k = keynamehelper.create_key_name("ticket_hold", event_sku)
> 	redis.hmset(k, holds)
> 	k = keynamehelper.create_key_name("event", event_sku)
> 	redis.hmset(k, tickets)
> 
> def expire_reservation(event_sku, cutoff_time_secs=30):
> 	""" Check if any reservation has exceeded the cutoff time. If any have, then
> backout the reservation and return the inventory back to the pool."""
>    	# 计算超时时间
> 	cutoff_ts = int(time.time()-cutoff_time_secs)
> 	# 描锁定的库存，将超时的库存归还
>    	# 如果库存锁定量比较大，可以使用Sorted Hash来存储，用ZRANGE来扫描
> 	e_key = keynamehelper.create_key_name("ticket_hold", event_sku)
> 	for field in redis.hscan_iter(e_key, match="ts:*"):
> 		if int(field[1]) < cutoff_ts:
> 			(_, order_id) = field[0].split(":")
> 			backout_hold(event_sku, order_id)
> ~~~
>
> 代码中的`test_expired_res`会调用上面两个函数，模拟超时付款处理过程

