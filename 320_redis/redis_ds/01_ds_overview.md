<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Redis基础数据结构](#redis%E5%9F%BA%E7%A1%80%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84)
  - [1. Redis Key](#1-redis-key)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3)
    - [(2) 说明](#2-%E8%AF%B4%E6%98%8E)
    - [(3) 命令](#3-%E5%91%BD%E4%BB%A4)
      - [(a) `EXISTS`](#a-exists)
      - [(b) `SET`](#b-set)
      - [(c) `GET`](#c-get)
      - [(d) `KEYS`, `SCAN`](#d-keys-scan)
      - [(e) `DEL`, ` UNLINK`](#e-del--unlink)
  - [2. KEY的过期时间（Key Expiration）](#2-key%E7%9A%84%E8%BF%87%E6%9C%9F%E6%97%B6%E9%97%B4key-expiration)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3-1)
    - [(2) 说明](#2-%E8%AF%B4%E6%98%8E-1)
    - [(3) 命令](#3-%E5%91%BD%E4%BB%A4-1)
      - [(a) 设置KEY的过期时间：`EXPIRE`, `EXPIREAT`, `PEXPR`, `PEXPIREAT`](#a-%E8%AE%BE%E7%BD%AEkey%E7%9A%84%E8%BF%87%E6%9C%9F%E6%97%B6%E9%97%B4expire-expireat-pexpr-pexpireat)
      - [(b) 查看KEY当前的TTL（Time To Live）：`TTL`, `PTTL`](#b-%E6%9F%A5%E7%9C%8Bkey%E5%BD%93%E5%89%8D%E7%9A%84ttltime-to-livettl-pttl)
      - [(c) 删除过期时间设定：`PERSIST`](#c-%E5%88%A0%E9%99%A4%E8%BF%87%E6%9C%9F%E6%97%B6%E9%97%B4%E8%AE%BE%E5%AE%9Apersist)
      - [(d) 执行`SET`命令时，使用`PX`或`EX`参数设置KEY的过期时间](#d-%E6%89%A7%E8%A1%8Cset%E5%91%BD%E4%BB%A4%E6%97%B6%E4%BD%BF%E7%94%A8px%E6%88%96ex%E5%8F%82%E6%95%B0%E8%AE%BE%E7%BD%AEkey%E7%9A%84%E8%BF%87%E6%9C%9F%E6%97%B6%E9%97%B4)
      - [(d) 例子](#d-%E4%BE%8B%E5%AD%90)
  - [3. String](#3-string)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3-2)
    - [(2) 说明](#2-%E8%AF%B4%E6%98%8E-2)
    - [(3) 命令](#3-%E5%91%BD%E4%BB%A4-2)
      - [(a) `SET`、`GET`](#a-setget)
      - [(b) `INCR`、`DECR`、`INCRBY`、`DECRBY`、`INCRBYFLOAT`、`DECRBYFLOAT`](#b-incrdecrincrbydecrbyincrbyfloatdecrbyfloat)
      - [(c) 获得数据类型、数据编码：`TYPE`，`OBJECT`](#c-%E8%8E%B7%E5%BE%97%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E6%95%B0%E6%8D%AE%E7%BC%96%E7%A0%81typeobject)
  - [4. Hash [`H`]](#4-hash-h)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3-3)
    - [(2) 说明](#2-%E8%AF%B4%E6%98%8E-3)
    - [(3) 应用场景举例](#3-%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF%E4%B8%BE%E4%BE%8B)
    - [(4) 命令](#4-%E5%91%BD%E4%BB%A4)
      - [(a) `HSET`](#a-hset)
      - [(b) `HEXISTS`、`HGET` 、`HDEL`、`HINCRBY`、`HINCRBYFLOAT`](#b-hexistshget-hdelhincrbyhincrbyfloat)
      - [(c) `HGETALL`、`HSCAN`、`HMGET`、`HKEYS`、`HVALS`](#c-hgetallhscanhmgethkeyshvals)
    - [(5) Redis JSON](#5-redis-json)
  - [5. List [`L`]](#5-list-l)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3-4)
    - [(2) 说明](#2-%E8%AF%B4%E6%98%8E-4)
    - [(3) 应用场景举例](#3-%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF%E4%B8%BE%E4%BE%8B-1)
    - [(4) 命令](#4-%E5%91%BD%E4%BB%A4-1)
      - [(a) `LPUSH`、`RPUSH`、`LPOP`、`RPOP`](#a-lpushrpushlpoprpop)
      - [(b) `LLEN`、`LRANGE`、`LINDEX`](#b-llenlrangelindex)
      - [(c) `LINSERT`、`LSET`、`LREM`、`LTRIM`](#c-linsertlsetlremltrim)
      - [(d) 例子](#d-%E4%BE%8B%E5%AD%90-1)
  - [6. Set [`S`]](#6-set-s)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3-5)
    - [(2) 说明](#2-%E8%AF%B4%E6%98%8E-5)
    - [(3) 应用场景举例](#3-%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF%E4%B8%BE%E4%BE%8B-2)
    - [(4) 命令](#4-%E5%91%BD%E4%BB%A4-2)
      - [(a) `SADD`、`SMEMEBERS`、`SSCAN`、`SISMEMBER`、`SREM`、`SPOP`](#a-saddsmemeberssscansismembersremspop)
      - [(b) 例子](#b-%E4%BE%8B%E5%AD%90)
      - [(c) `SDIFF`、`SINTER`、`SUNION`](#c-sdiffsintersunion)
      - [(d) `SDIFFSTORE`、`SINTERSTORE`、`SUNIONSTORE`：后续章节介绍](#d-sdiffstoresinterstoresunionstore%E5%90%8E%E7%BB%AD%E7%AB%A0%E8%8A%82%E4%BB%8B%E7%BB%8D)
      - [例子](#%E4%BE%8B%E5%AD%90)
  - [7. Sorted Set [`Z`]](#7-sorted-set-z)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3-6)
    - [(2) 说明](#2-%E8%AF%B4%E6%98%8E-6)
    - [(3) 应用场景举例](#3-%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF%E4%B8%BE%E4%BE%8B-3)
    - [(4) 命令和例子](#4-%E5%91%BD%E4%BB%A4%E5%92%8C%E4%BE%8B%E5%AD%90)
      - [(a) `ZADD`](#a-zadd)
      - [(b) `ZRANGE`、 `ZREVRANGE`](#b-zrange-zrevrange)
      - [(c) `ZRANGEBYSCORE`、`ZREVRANGEBYSCORE`、`ZRANGEBYLEX`](#c-zrangebyscorezrevrangebyscorezrangebylex)
      - [(d) `ZRANK`、`ZREVRANK`、`ZSCORE`、 `ZOUNT`](#d-zrankzrevrankzscore-zount)
      - [(e) `ZREM`、`ZREMRANGEBYLEX`、`ZREMRANGEBYRANK`、`ZREMRANGEBYSCORE`](#e-zremzremrangebylexzremrangebyrankzremrangebyscore)
      - [(f) 例子](#f-%E4%BE%8B%E5%AD%90)
      - [(g) `ZINTERSTORE`、`ZUNIONSTORE`](#g-zinterstorezunionstore)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Redis基础数据结构

文档：[https://redis.io/topics/data-types](https://redis.io/topics/data-types)

实验环境设置：[https://university.redislabs.com/courses/course-v1:redislabs+RU101+2021_02/courseware](https://university.redislabs.com/courses/course-v1:redislabs+RU101+2021_02/courseware)

> 1. Run the Docker container
>`$ docker run --rm --name redis-lab -p:8888:8888 redisuniversity/ru101-lab`
> 2. Point your browser to
>`localhost:8888/entry.html`

## 1. Redis Key

### (1) 文档

> Key Command: [https://redis.io/commands#generic](https://redis.io/commands#generic)
>
> Glob Style Wildcards: [https://en.wikipedia.org/wiki/Glob_(programming)](https://en.wikipedia.org/wiki/Glob_(programming))

### (2) 说明

Key是redis数据的主键，具备如下特性

> 1. 全局唯一
>
> 2. 任何数据结构（字符串、整数、浮点数、二进制数据）都可以用作key
>
>     ~~~txt
>     "Foo"，42, 3.1415, 0xff
>     ~~~
>
> 3. 最大512MB（512 megabytes，当前版本redis，以后还可能增加，当然不推荐使用体积大的数据作为key）
>
> 4. 大小写敏感（因为是binary sequence）

在使用方面

> 1. Key Name太短可读性查、太长增加内存占用，需要权衡
>
> 2. `name spacing`以及`logical database的局限`
>
>     * Redis通过logical database的概念来实现类似name spacing的功能（默认的logical database是database 0，仅在相同logical databas中key不可以重名）。
>     * 然而redis cluster以及很多工具、框架都只支持database 0，因此之后所有的例子，都跑在database 0之上，只是知道有logical database这个概念即可
>
> 3. 实践当中更多是通过`key命名规范`来起到类似"naming spacing"的作用
>
>     例如：给key加字符串前缀等
>
>     ~~~txt
>     "user:1000:followers"
>     ~~~
>
>     尤其对于跨团队项目、微服务等，key的命名规范对于避免key冲突特别重要

### (3) 命令

> 使用Redis CLI可以对key进行命令行操作，常用命令如下（`[]`表示可选参数）

#### (a) `EXISTS`

> 检查一个key是否已经存在
>
> `EXISTS key [key, ....]`
>
> ~~~bash
> redis:6379 > exists inventory:100-meters-womens-final
> (integer) 0
> ~~~
>
> 返回1：如果key已经存在
>
> 返回0：如果key不存在
>
> 对于只有在key不存在时才希望设置value的场景，可以先使用exists检查，再执行set命令
>
> 但两条命令，会带来数据不一致的隐患。另一个方法是使用SET命令的`NX`参数

#### (b) `SET`

> `SET $key $val [EX <seconds>][PX <milliseconds>|EX <seconds>][NX|XX]`
>
> 为一个key设置value，不论key是否已经存在，都会把value设置为参数中传入的值
>
> ~~~bash
> redis:6379 > set customer:1000 fred
> redis:6379 > set customer:1500 jane
> ~~~
>
> `NX`参数：表示只是key不存在才执行操作
>
> `XX`参数：表示只有key存在才执行操作
>
> ~~~bash
> redis:6379 > set inventory:100-meters-womens-final 1000 NX # key不存在才执行
> OK
> redis:6379 > set inventory:100-meters-womens-final 1001 NX # key已经存在，因此不能执行操作
> (nil) 
> redis:6379 > get inventory:100-meters-womens-final 0    XX # key存在才执行（覆盖）
> redis:6379 > get inventory:100-meters-womens-final
> "0"
> ~~~
>
> `PX <milliseconds>`参数：表示key在多少毫秒后过期
>
> `EX <seconds>`参数：表示key在多少秒后过期
>
> ~~~bash
> redis:6379 > set seat-hold Row:A:Seat:4 PX 50000
> redis:6379 > set seat-hold Row:A:Seat:4 EX 50
> ~~~

#### (c) `GET`

> `GET key`
>
> 返回这个key对应的value，返回的值通常包裹在双引号中
>
> ~~~bash
> redis:6379 > get customer:1000
> ~~~

#### (d) `KEYS`, `SCAN`

> 遍历所有符合某种pattern的Key
>
> * `KEYS`（不建议对生产环境使用）：简单方便、但是会阻塞直到获得所有的KEY
> * `SCAN`（建议在生产环境使用它）：使用游标来迭代，只阻塞至获得一小批KEY，返回一个slot reference，之后使用后续命令继续遍历
>
> `KEYS $key_pattern`
>
> ~~~bash
> redis:6379 > keys customer:1*         # *表示通配符
> ~~~
>
> `SCAN <slot> [MATCH pattern][COUNT count]`
>
> ~~~bash
> redis:6379 > scan 0 Match customer:1*     # 输入一个初始slot值为0
> 1) "14336"
> 2) (empty list or set)
> redis:6379 > scan 14336 Match customer:1* # 使用Redis返回的slot id继续查询
> 1) "14848"
> 2) (empty list or set)
> redis:6379 > scan 14848 Match customer:1* # 使用Redis返回的slot id继续查询
> 1）"12291"
> 2) 1) "customer:1500"
>    2) "customer:1000"
> redis:6379 > scan 12291 Match customer:1* # 全部遍历完时redis返回的slot值会为0
> 1) "0"
> 2) (empty list or set)
> ~~~

#### (e) `DEL`, ` UNLINK`

> 删除key
>
> * `DEL`：删除key以及与key、value相关的内存，是一个阻塞操作
> * `UNLINK`：将key unlink，随后在异步操作中释放与key、value相关的内存，返回值是删除的key的数量
>
> `DEL key [key ...]`
>
> `UNLINK key [key ...]`
>
> ~~~bash
> redis:6379 > unlink customer:1000
> (integer) 1
> redis:6379 > get customer:1000
> (nil)
> ~~~

## 2. KEY的过期时间（Key Expiration）

### (1) 文档

> 各条命令的文档链接
>
> | **Set Expiration**                                | **Inspect Expiration**                  | **Remove Expiration**                         |
> | ------------------------------------------------- | --------------------------------------- | --------------------------------------------- |
> | [ EXPIRE](https://redis.io/commands/expire)       | [ PTTL](https://redis.io/commands/pttl) | [ PERSIST](https://redis.io/commands/persist) |
> | [ EXPIREAT](https://redis.io/commands/expireat)   | [ TTL](https://redis.io/commands/ttl)   |                                               |
> | [ PEXPIRE](https://redis.io/commands/pexpire)     |                                         |                                               |
> | [ PEXPIREAT](https://redis.io/commands/pexpireat) |                                         |                                               |

### (2) 说明

KEY的过期时间

> 可以被设为milliseconds、seconds、UNIX timestamp
>
> 可以在创建redis key时设置、也可以之后再设置、也可以被移除

KEY的TTL

> 在设置KEY的过期时间之后，可以随时查看这个KEY还剩下多长时间就会过期

### (3) 命令

#### (a) 设置KEY的过期时间：`EXPIRE`, `EXPIREAT`, `PEXPR`, `PEXPIREAT`

> `EXPIRE <key> <seconds>`：设置过期时间为指定秒之后
>
> `EXPIREAT <key> <timestamp>`：设置过期时间为一个以秒为单位的UNIX时间戳
>
> `PEXPIRE <key> <milliseconds>`：设置过期时间为指定毫秒之后
>
> `PEXPIREAT <key> <milliseconds-timestamp>`：设置过期时间为一个一毫秒为单位的UNIX时间戳

#### (b) 查看KEY当前的TTL（Time To Live）：`TTL`, `PTTL`

> `TTL <key> `：以秒为单位查看
>
> `PTTL <key>`：以毫秒为单位查看

#### (c) 删除过期时间设定：`PERSIST`

> `PERSIST <key>`

#### (d) 执行`SET`命令时，使用`PX`或`EX`参数设置KEY的过期时间

> `SET <key> <value> EX <seconds>`：设置过期时间为指定的秒数
>
> `SET <key> <value> PX <milliseconds>`：设定过期时间为指定的毫秒数

#### (d) 例子

> ~~~bash
> redis:6379 > set seat-hold Row:A:Seat:4 EX 50
> redis:6379 > ttl seat-hold
> (integer) 42
> redis:6379 > persist seat   # 删除过期时间设置
> (integer) 1 
> redis:6379 > ttl seat-hold  # 没有过期时间的key，ttl命令返回-1
> (integer) -1 # 
> redis:6379 > set seat-hold Row:A:Seat:4 PX 50000
> redis:6379 > get seat-hold
> "Row:A:Seat:4"
> redis:6379 > pexpire seat-hold 1 #过期时间改为1毫秒后
> (integer) 1
> redis:6379 > get seat-hold #再执行get命令，发现key已经因为过期而被remove
> (nil)
> ~~~

## 3. String

### (1) 文档

> String Commands：https://redis.io/commands#string
>
> Type Command：[TYPE command](https://redis.io/commands/type)
>
> Object Command：[OBJECT command](https://redis.io/commands/object)

### (2) 说明

String是Redis的基本存储类型

> 可以用来存储Text、Float、Integers、二进制数据……（用编码来区分）
>
> 在执行操作之前，会先检查数据的编码、看一下它到底是哪种数据

如果存储的是数字时、还可以对它做递增和递减操作，也可以用于实现整数计数器

> 递增和递减操作会按照value的编码（INT、FLOAT）来进行和返回

### (3) 命令

#### (a) `SET`、`GET`

`SET <key> <value>`

> ~~~bash
> redis:6379 > set user:101:time-zone UTC-8
> OK
> redis:6379 > get user:101:time-zone
> UTC-8
> ~~~
>
> <value>可以是上面说的各种类型
>
> 同样可以使用EX、PX、NX、XX等参数来SET命令对于过期时间、KEY已经存在或不存在时的处理方法，见上一章SET / GET命令

#### (b) `INCR`、`DECR`、`INCRBY`、`DECRBY`、`INCRBYFLOAT`、`DECRBYFLOAT`

> * `INCR <key>` value加1
> * `INCRBY <key> <num_to_incr>`：value值增加`<num_to_incr>`（可以是负数）
> * `INCRBYFLOAT <key> <float_to_incr>`：value值增加`<float_to_incr>`（可以是负数）
> * `DECR <key>`
> * `DECRBY <key> <num_to_decr>`
> * `DECRBYFLAT <key> <float_to_decr>`

(1) 这些命令在KEY不存在时会自动创建一个KEY然后再递增（或递减）

(2) 使用INCRBY时，可以指定计数器递增的数量，如果是负数则会有递减的效果

> ~~~bash
> redis:6379 > EXITS user:23:visit-count 			# key不存储在
> (integer) 0
> redis:6379 > INCR user:23:visit-count  			# 新建一个值为0的value，并加1
> (integer) 1
> redis:6379 > INCRBY user:23:credit-balance 30  	# 加30
> (integer) 70
> redis:6379 > INCRBY user:23:credit-balance -20 	# 减20
> (integer) 50
> ~~~

(3) 在命令执行时，会按照编码执行并返回结果，下面例子中编码是INT数据类型是STRING

> ~~~bash
> redis:6379 > set inventory:4x100m-womens-final 1000
> OK
> redis:6379 > get inventory:4x100m-womens-final
> "1000"
> redis:6379 > decrby inventory:4x100m-womens-final 1
> (integer) 999 # 在decrby时，使用INT编码执行操作并返回结果
> redis:6379 > get inventory:4x100m-womens-final
> "999"		  # 但存储中的value类型不变、仍然是STRING
> redis:6379 > type inventory:4x100m-womens-final
> String
> ~~~

(4) 当编码与命令不兼容时，会报错

> ~~~bash
> redis:6397 > set inventory:4x100m-womens-final "sold out"
> OK
> redis:6379 > decrby inventory:4x100m-womens-final 10
> (error) ERR value is not an integer or out of range 
> ~~~

(5) 另外3条`DECR`相关的命令也类似，只是它们用于递减而不是递增

#### (c) 获得数据类型、数据编码：`TYPE`，`OBJECT`

命令

> * `TYPE <key>`：获得value的数据类型
>
> * `OBJECT <key>`：获得value的编码

encoding会取决于设置了什么样的value，随着value的变化encoding也会变化

> ~~~bash
> redis:6379 > set inventory:4x100m-womens-final 999 # value设为999
> OK
> redis:6379 > get inventory:4x100m-womens-final
> "999"
> redis:6379 > type inventory:4x100m-womens-final    # TYPE是STRING
> String
> redis:6397 > object inventory:4x100m-womens-final  # 编码是INT
> INT
> redis:6397 > set inventory:4x100m-womens-final "sold out" # value改为“sold out"
> OK
> redis:6397 > type inventory:4x100m-womens-final    # TYPE仍然是STRING
> String
> redis:6397 > object inventory:4x100m-womens-final  # 编码变成了一种用于TEXT的编码
> "embstr"
> ~~~

## 4. Hash [`H`]

### (1) 文档

> [ Documentation for Hash commands at redis.io](https://redis.io/commands#hash)
> [ Wikipedia article on Glob style wildcards](https://en.wikipedia.org/wiki/Glob_(programming))

### (2) 说明

Redis的Hash

> 1. 用于存储filed-value对
> 2. 单层结构：底层field-value类型是String，不可以是List、Set、Sorted Set、Hash，不支持嵌套
> 3. 支持针对单个filed-value对的操作、动态添加和删除filed-value对（schemeless）
> 4. 紧凑和高效的内存结构（extremely memory efficcient and compact structure）、支持handreds of millions数量级的keys存储、低开销性能状况可预测

可以动态地向hash添加和删除field

### (3) 应用场景举例

1 . 在Rate Limiter中记录各个end point在当前时间周期剩余的访问量余额

> * 在每个时间片开始时，创建Hash并设置每个AP的I配额
> * Hash的Expiration Time被设为该时间片的长度，时间片用完时Hash被释放
> * 每被访问一次，API的访问配额减1

2 . 用于session cache

> * 相比String，使用Hash存储，可以独立操作Session中的具体字段
> * 为每个Session创建一个Hash，并设置Expiration Time，超时时Session对象会被自动删除

### (4) 命令

#### (a) `HSET`

> `HSET <key> <field> <value> [<field> <value>, ...]`：创建或更新Hash
>
> `HSETNX <key> <field> <value> [<field> <value>, ...]`：仅在Hash不存在时创建Hash
>
> ~~~bash
> redis:6379> hset event:judo capacity 12000 location "Nipp Budokan" ticket_price:gold 100 availablility:gold 8000
> (integer) 4
> ~~~
>
> 数据结构：`key : {field : value}`
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redis_hash_key_field_value.jpg" width="400" /></div>
>
> key被删除时，整个hash中所有的`field:value`对都会被删除

#### (b) `HEXISTS`、`HGET` 、`HDEL`、`HINCRBY`、`HINCRBYFLOAT`

> `HEXISTS <key> <field>`：检查一个field在hash中是否存在
>
> `HGET <key> <field>`：从hash中获取某个field的值
>
> `HDEL <key> <field> [<field> ...]`：从hash中删除某个field
>
> `HINCRBY <key> <field> <increment_int>`：将hash中某个数值编码的field增加指定的值
>
> `HINCRBYFLOAT <key> <field> <increment_float>`：将hash中某个浮点数编码的field增加指定的值
>
> ~~~bash
> redis:6379> hexists event:judo capacity   # 返回1表示存在、0表示不存在
> (integer) 1
> redis:6379> hget event:judo timezone      # 不存在时返回nil，存在时返回具体的value
> (nil)
> redis:6379> hincrby event:judo availability:gold -10 # 将名为"availabity:gold"的field值减10
> (integer) 7990
> redis:6379> hset event:judo timezone JST  # HSET除了创建Hash，也能添加/更新Hash中的field:value对，动态更新Hash的Schema
> (integer) 1
> redis:6379> hdel event:judo timezone      # HSET也可以删除field:value对
> (integer) 1 
> ~~~
>

Encoding的使用与String类型相同

#### (c) `HGETALL`、`HSCAN`、`HMGET`、`HKEYS`、`HVALS`

> `HSCAN <key> <cursor> [MATCH <pattern>][COUNT <count>]`：以游标的方式遍历hash中的field
>
> `HMGET <key> <field> [<field> ...]`：返回hash中多个field
>
> `HGETALL <key>`: 阻塞返回hash中所有的field（不建议在生产环境使用）
>
> `HKEYS <key>`：返回所有的field names（不建议在生产环境使用）
>
> `HVALS <key>`：返回所有的values（不建议在生产环境使用）
>
> ~~~bash
> redis:6379> hset event:judo availability:silver 2000
> redis:6379> # `0`：初始游标设为0
> redis:6379> # `availibity:*``：Glob Style Wildcard通配符语法
> redis:6379> hscan event:judo 0 match availibity:* 
> 1）"0"
> 2) 	1) "availability:gold"
>    	2) "7990"
>    	1) "availibity:silver"
>    	2) "2000"
> ~~~

### (5) Redis JSON

> 如果想要使用Redis存储JSON，可以使用RedisJSON module（[https://oss.redislabs.com/redisjson/](https://oss.redislabs.com/redisjson/)）

## 5. List [`L`]

### (1) 文档

> [ Documentation for List commands at redis.io](https://redis.io/commands#list)
> [ Wikipedia article on Double Linked Lists](https://en.wikipedia.org/wiki/Linked_list#Doubly_linked_list)

### (2) 说明

> 1. Strings类型的列表，可以用来实现队列和堆栈（元素类型不可以是String以外的类型、即不容许嵌套）
> 2. 容许有重复值出现
> 3. 可以从左侧、右侧加入和取出元素；也可以插入到相对于某个其他元素的位置
> 4. 出于简洁考虑，直接用LEFT和RIGHT称呼列表两端的元素
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redis_ds_list.jpg" width="400" /></div>
>
> 5. 需要强调Redis List是使用双向链表实现的，对链表遍历的越深，时间开销越大

### (3) 应用场景举例

1 . 信息流中最近的帖子，聊天软件中最近的消息

> 使用`LPUSH`存入新的消息
>
> 使用`LRANGE`提取最近N条消息
>
> 使用`LTRIM`消息列表，只保留前N条消息

2 . 消息队列

> 使用`LPOP`和`RPUSH`

### (4) 命令

#### (a) `LPUSH`、`RPUSH`、`LPOP`、`RPOP`

> * `LPUSH <key> <value> [<value>, ...]`：从左端添加元素
> * `RPUSH <key> <value> [<value>, ...]`：从右端添加元素
> * `LPOP <key>`：从左端取出元素
> * `RPOP <key>`：从右端取出元素

#### (b) `LLEN`、`LRANGE`、`LINDEX`

> * `LLEN <key>`：链表长度
> * `LRANGE <key> <first_idx> <last_idx>`：`[first_idx, last_idx]`区间内的元素，整数index表示从左向右，负数index表示从右向左，0表示最左的元素，-1表示最右的元素（链表很长时会影响性能）
> * `LINDEX <key> <index>`：返回位于`<index>`位置的元素

#### (c) `LINSERT`、`LSET`、`LREM`、`LTRIM`

> * `LINSERT <key> BEFORE|AFTER <pivot> <value>`：在另一个元素之前插入
> * `LSET <key> <index> <value>`：将位于`<index>`位置的元素设置为指定值
> * `LREM <key> <count> <value>`：删除值为某个值的、指定数量的元素
> * `LTRIM <key> <first_idx> <last_idx>`：修剪列表，只保留`[first_idx, last_idx]`区间内的元素

#### (d) 例子

> ~~~bash
> redis:6379> lpush order:4x100-womens-final jane:4 bill:8 charlie:6
> (integer) 3
> redis:6379> llen orders:4x100m-womens-final
> (integer) 3
> redis:6379> lrange orders:4x100m-woemns-final 0 -1 
> 1) "charlie:6"
> 2) "bill:8"
> 3) "jane:4"
> redis:6379> rpush waitlist:basketball-mens-squal brain:2 kate:7 kevin:9
> (integer) 3
> redis:6379> lrange waitlist:basketball-mens-qual 0 -1
> 1) "brain:2"
> 2) "kate:7"
> 3) "kevin:9"
> redis:6379> rpop orders:4x100m-women-final
> "jane:4"
> redis:6379> lpop waitlist:basketball-means-qual
> "brain:2"
> ~~~

## 6. Set [`S`]

### (1) 文档

> [Documentation for Set commands at redis.io](https://redis.io/commands#set)

### (2) 说明

> * 只能存储String类型的数据（不容许嵌套）
> * 支持集合操作

### (3) 应用场景举例

标签云

> ~~~bash
> sadd wrench tool metal
> sadd coin currency metal
> sscan wrench match * # wrench的所有tag
> sinter wrench coin # 共同tag
> sunion wrench coin # 标签云合并
> ~~~

每个url在近期某天的独立访客

> ~~~bash
> add about.html:20180210 jim jane john # 添加访客
> sscan about.html:20180210 match * # 访客列表
> expire about.html:20180210 86400 # 设置过期时间
> ~~~

统计近期每个时间片、或多个时间片的用户

> ~~~bash
> redis:6379> sadd players:online:1000 42
> (integer) 1
> redis:6379> sadd players:online:1005 42
> (integer) 1
> redis:6379> expireat players:online:1000 1588241100
> (integer) 1
> redis:6379> expireat players:online:1005 1588241400
> (integer) 1
> redis:6379> sismember players:online:1000 42
> (integer) 1
> ~~~

### (4) 命令

#### (a) `SADD`、`SMEMEBERS`、`SSCAN`、`SISMEMBER`、`SREM`、`SPOP`

> `SADD <key> <member> [<member> ...]`：向set加入元素
>
> `SMEMBERS <key>`：返回set中所有的元素（阻塞操作，除非集合很小，否则不建议在生产环境中使用）
>
> `SSCAN <key> <cursor> [MATCH <pattern>][COUNT <count>]`：使用游标返回set中的元素，可以通过pattern来进行挑选
>
> `SISMEMBER <key> <member>`：检查是否在set中
>
> `SREM <key> <member> [<member> ...]`：删除一组元素
>
> `SPOP <key> [<count>]`：随机删除并返回指定数量的元素，count的默认值是1

#### (b) 例子

> ~~~bash
> redis:6379> sadd venues "Olympic Stadium" "Nippon Budokan" "Tokyo Stadium"
> (integer) 3
> redis:6379> sscan venues 0 match *
> 1) "0"
> 2) 1) "Nippon Budokan"
>    2) "Tokyo Stadium"
>    3) "Olympic Stadium"
> redis:6379> sadd venues "Olympic Stadium" # 对已经存在的元素，重复加入会返回0
> (integer) 0
> redis:6379> sismember venues "Nippon Budokan"
> (integer) 1
> redis:6379> sismember venues "Sth Not Exist" 
> (integer) 0
> redis:6379> sadd venues "Eiffel Tower"
> redis:6379> srem venues "Eiffel Tower"
> redis:6379> sscan venues 0 match *
> 1) "0"
> 2) 1) "Nippon Budokan"
>    2) "Tokyo Stadium"
>    3) "Olympic Stadium"
> redis:6379> spop venues 2
> 1) "Nippon Budokan"
> 2) "Olympic Stadium"
> ~~~

#### (c) `SDIFF`、`SINTER`、`SUNION`

>`SIDFF <basic_set> <other_set> [<other_set> ...]`：返回1个set与其他set的差集
>
>`SINTER <set> <set> [<set> ...]`：返回set的交集
>
>`SUNION <set> <set> [<set> ...]` ：返回set的并集
>
>注意
>
>1. 这些操作会受到机器大小的影响，不要将它们用在大的set上
>2. 传给它们的参数都必须是set，否则会报错

#### (d) `SDIFFSTORE`、`SINTERSTORE`、`SUNIONSTORE`：后续章节介绍

#### 例子

> ~~~bash
> redis:6379> sadd "venue-subway:Makuhari Messe" "Keiyo Line"
> redis:6379> sadd "venue-subway:Tokyo Tatsumi International Swimming Center"
> "Keiyo Line" "Rinkai Line" "Yurakucho Line"
> (integer) 3
> redis:6379> sinter "venue-subway:Makuhari Messe" "venue-subway:Tokyo Tatsumi International Swimming Center"
> 1) "Keiyo Line"
> ~~~

## 7. Sorted Set [`Z`]

### (1) 文档

> [ Documentation for Sorted Set commands at redis.io](https://redis.io/commands#sorted_set)

### (2) 说明

> * 与set一样会去重、元素（member）类型是String（不容许是List、Set、……，不容许嵌套）
> * 每个元素与一个Float类型的score关联、用于保持元素的顺序
> * 可以根据value、position（index）、score、lexigraphically进行操作
> * 同样提供集合间操作例如union、intersection、……，但是与set不同的是
>     * sorted set只支持将计算结果存储在另一个sorted set中，而不能直接返回
>     * sorted set只能计算交集、并集，不能计算差集

### (3) 应用场景举例

> 应用范围例如：priority queues、low-latency leaderboards、secondary indexing等

游戏中积分Leader Board（展示top 10 players，显示一个player的rank，显示一个player的score ）

> ~~~bash
> redis:6379> zadd leaders:exp 0 42 # zadd key score memeber
> (integer) 1
> redis:6379> zadd leaders:exp 0 391 
> 							 0 127
> 							 0 268
> 							 0 637
> 							 0 722
> 							 0 971
> 							 0 662
> 							 0 37
> 							 0 175
> 							 0 21
> 							 0 444
> (integer) 11
> redis:6379> zincrby leader:exp 300 42 # zincrby key increment memeber
> "300"
> ...
> redis:6379> zrange leader:exp 
> redis:6379> zrevrange leader:exp 0 9 withscores # withscores: output score
> 1) "42"   # memeber
> 2) "300"  # score
> 3) "21"   # memeber
> 4) "290"  # score
> ...
> 19) "268" # memeber
> 20) "170" # score
> redis:6379> zrank leader:exp 42
> 1) 11     # 从0开始计数、按score从小到大，rank是11
> redis:6379> zrevrank leader:exp 42
> 1) 0      # score最高
> redis:6379> zscore leader:exp 42
> 1) "300"  # score是300
> ~~~

实现Priority Queue

> ~~~bash
> redis:6379> zadd pq 1 p1-item1 1 p1-item2 2 p2-item2 3 p3-item1 # 入堆
> redis:6379> zrange pq 0 0 # peek top priority element
> redis:6379> zrem pq p1-tiem1 # 删除指定元素（priority queue以外的功能）
> ...
> ~~~

### (4) 命令和例子

#### (a) `ZADD`

>`ZADD <key> [NX|XX] [CH] [INCR] <score> <memeber> [<score> <memeber> ...]`
>
>* memeber的score相同时，根据memeber的字典序（Lex Order）来决定顺序
>
>* `NX`：仅在memeber不存在时才加入给memeber
>
>* `XX`：仅在memeber已经存在时、更新这个memeber的score 
>
>* `CH`：
>
>    设置该选项时、返回changed原书数量
>
>    未设置时按默认方式返回有所added or changed的元素数量
>
>* `INCR`：用来为单个(score, memeber)对增加score

#### (b) `ZRANGE`、 `ZREVRANGE`

>`ZRANGE <key> <first_position> <last_position> [WITHSCORES]`：按score从小到大排序、返回指定指定下标区间段内的元素
>
>`ZREVRANGE <key> <first_position> <last_position> [WITHSCORES]`：按score从大到小排序、返回指定指定下标区间段内的元素
>
>* `WITHSCORES`：设置该选项时、返回memeber和score；没设置该选项时、只返回member
>* `<first_position>`/`<last_position>`：类似python数组下标，但是是排序后的下标；从0开始计数，-1表示倒数第1，-2表示倒数第2，……

#### (c) `ZRANGEBYSCORE`、`ZREVRANGEBYSCORE`、`ZRANGEBYLEX`

>`ZRANGEBYSCORE <key> <min_score> <max_score> [WITHSCORES][LIMIT offset_count]`：按score从小到大排序、返回指定score区间段内的元素
>
>`ZREVRANGEBYSCORE <key> <min_score> <max_score> [WITHSCORES][LIMIT offset_count]`：按score从大到小排序、返回指定score区间段内的元素
>
>`ZRANGEBYLEX <key> <min> <max> [LIMIT offset_count]`：按照member的字典序排序，返回

#### (d) `ZRANK`、`ZREVRANK`、`ZSCORE`、 `ZOUNT`

>`ZRANK <key> <member>`：返回元素的position（从0开始计数的下标）
>
>`ZERVRANK <key> <memeber>`：返回元素的反向position（从-1开始反向计数的下标：最后一个是-1、其次是-2，……）
>
>`ZSCORE <key> <member>`：返回元素的score
>
>`ZOUNT <key> <min_score> <max_score>`：返回`[min_score, max_score]`之间的元素数量

#### (e) `ZREM`、`ZREMRANGEBYLEX`、`ZREMRANGEBYRANK`、`ZREMRANGEBYSCORE`

> `ZREM <key> <member> [<member> ...]`：根据元素名称删除一个或多个元素
>
> `ZREMRANGEBYLEX`：根据元素名称的字典序删除指定范围内的元素
>
> `ZREMRANGEBYRANK`：根据元素排序后的下标删除指定范围内的元素
>
> `ZREMRANGEBYSCORE`：根据元素的score删除指定范围内的元素

#### (f) 例子

> ~~~bash
> redis:6379> zadd "subway:Keiyo Line" 1 Tokyo
> (integer) 1
> redis:6379> zadd "subway:Keiyo Line" 5 Shin-Kiba 3 Etchujima 2 Hatchobori 4 Shiomi
> (integer) 4
> redis:6379> zrange "subway:Keiyo Line" 0 -1
> 1) "Tokyo"
> 2）"Hatchobori"
> 3）"Etchujima"
> 4）"Shiomi"
> 5）"Shin-Kiba"
> redis:6379> zadd "subway:Keiyo Line" 10 Nishi-Funabashi
> (integer) 1
> redis:6379> zadd "subway:Keiyo Line" 10 Futamat
> (integer) 1 
> redis:6379> zrange "subway:Keiyo Line" 0 -1
> 1) "Tokyo"
> 2）"Hatchobori"
> 3）"Etchujima"
> 4）"Shiomi"
> 5）"Shin-Kiba"
> 6) "Futamatashimmachi" # score相同时，安装memeber的字典序排序
> 7) "Nishi-Funabashi"
> redis:6379> zrange "subway:Keiyo Line" 0 -1 withscores
> 1) "Tokyo"
> 2) 1
> 3）"Hatchobori"
> 4) 2
> 5）"Etchujima"
> 6) 3
> 7）"Shiomi"
> 8) 4
> 9）"Shin-Kiba"
> 10) 5
> 11) "Futamatashimmachi" # score相同时，安装memeber的字典序排序
> 12) 10
> 13) "Nishi-Funabashi"
> 14) 10
> redis:6379> zscore "subway:Keiyo Line" Tokyo
> "1"
> redis:6379> zrank "subway:Keiyo Line" Tokyo
> (integer) 0
> redis:6379> zcount "subway:Keiyo Line" 5 10
> (integer) 3
> redis:6379> zrangebyscore "subway:Keiyo Line" 2 5
> 1) "Hatchobori"
> 2) "Etchujima"
> 3) "Shiomi"
> 4) "Shin-Kiba"
> redis:6379> zrem "subway:Keiyo Line" Shin-Kiba
> (integer) 1
> redis:6379> zrem 
> ~~~

#### (g) `ZINTERSTORE`、`ZUNIONSTORE`

> `ZINTERSTORE <dest> <num_keys> <key> [<key> ...][WIEIGHTS weight [weight]][AGGREGATE SUM|MIN|MAX]`：计算集合交集
>
> `ZUNION <dest> <num_keys> <key> [<key> ...][WIEIGHTS weight [weight]][AGGREGATE SUM|MIN|MAX]`：计算集合并集

