<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [1. Redis数据结构](#1-redis%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84)
  - [(1) 基础数据结构](#1-%E5%9F%BA%E7%A1%80%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84)
  - [(2) 集合操作](#2-%E9%9B%86%E5%90%88%E6%93%8D%E4%BD%9C)
  - [(3) 事务和object存储](#3-%E4%BA%8B%E5%8A%A1%E5%92%8Cobject%E5%AD%98%E5%82%A8)
  - [(4) Bit数据和消息订阅发布](#4-bit%E6%95%B0%E6%8D%AE%E5%92%8C%E6%B6%88%E6%81%AF%E8%AE%A2%E9%98%85%E5%8F%91%E5%B8%83)
  - [(5) 地理位置存储和Lua Script](#5-%E5%9C%B0%E7%90%86%E4%BD%8D%E7%BD%AE%E5%AD%98%E5%82%A8%E5%92%8Clua-script)
- [2. Redis Java编程](#2-redis-java%E7%BC%96%E7%A8%8B)
  - [(1) Redis客户端；DAO编写](#1-redis%E5%AE%A2%E6%88%B7%E7%AB%AFdao%E7%BC%96%E5%86%99)
  - [(2) Jedis Pipeline；Jedis Transaction](#2-jedis-pipelinejedis-transaction)
  - [(3) 地理位置信息（Geospetial）； Redis Stream介绍](#3-%E5%9C%B0%E7%90%86%E4%BD%8D%E7%BD%AE%E4%BF%A1%E6%81%AFgeospetial-redis-stream%E4%BB%8B%E7%BB%8D)
  - [(4) RateLimiter；时间序列；连接管理；Exception处理；性能；Debug方法；Redis协议介绍](#4-ratelimiter%E6%97%B6%E9%97%B4%E5%BA%8F%E5%88%97%E8%BF%9E%E6%8E%A5%E7%AE%A1%E7%90%86exception%E5%A4%84%E7%90%86%E6%80%A7%E8%83%BDdebug%E6%96%B9%E6%B3%95redis%E5%8D%8F%E8%AE%AE%E4%BB%8B%E7%BB%8D)
- [3. Redis设计与实现（todo）](#3-redis%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%AE%9E%E7%8E%B0todo)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 1. Redis数据结构

### (1) 基础数据结构

笔记链接：[redis_ds/01_ds_overview.md](redis_ds/01_ds_overview.md)

内容：

> 1. Redis Key、Key过期时间设置
> 2. 四种数据结构（String、Hash、Set、SortedSet）及操作命令

### (2) 集合操作

笔记链接：[redis_ds/02_collection_set_ops.md](redis_ds/02_collection_set_ops.md)

内容：

> 1. 常用Pattern（1）：固定体积的集合（Capped Collection）
> 2. 常用Pattern（2）：根据domain object属性进行组合查询（Faceted Search）的三种方法
> 3. Redis命令的时间复杂度

### (3) 事务和object存储

笔记链接：[redis_ds/03_transaction_and_obj_store.md](redis_ds/03_transaction_and_obj_store.md)

内容：

> 1. Transaction命令及使用
> 2. Tranction中命令出错时的处理方法
> 3. 常用Pattern（3）：乐观锁
> 4. 常用Pattern（4）：简答对象存储；嵌套对象存的的三种方法
> 5. Demo：票务预订时的票务锁定及付款操作流程

### (4) Bit数据和消息订阅发布

笔记链接：[redis_ds/04_bitmap_and_subscribe_publish.md](redis_ds/04_bitmap_and_subscribe_publish.md)

内容：

> 1. bit数据的两种使用方式：(1) 当做BITFIELDS来使用；(2) 当做bit array来操作
> 2. Demo：使用BITFIELD实现订票系统座位预订的功能
> 3. Redis消息订阅发布机制、特点、限制和性能注意事项
> 4. 根据channel name进行消息订阅和发布
> 5. 根据channel name pattern进行消息订阅和发布
> 6. Demo：票务系统销售实时分析

### (5) 地理位置存储和Lua Script

笔记链接：[redis_ds/05_geospacial_and_lua.md](redis_ds/05_geospacial_and_lua.md)

内容：

> 1. Redis对Geo Hash的存储方式、命令；以及Geo Hash的特性
> 2. Demo：根据位置、距离和比赛类型、地铁线路查找比赛场馆
> 3. 使用Lua脚本封装server端计算逻辑：命令、脚本样例、传参、原子属性、类型映射、注意事项、原子性被破坏的问题
> 4. Lua Script管理
> 5. Demo：使用Lua Script改写票务系统购票流程

## 2. Redis Java编程

> 代码：[https://github.com/fangkun119/ru102j](https://github.com/fangkun119/ru102j)

### (1) Redis客户端；DAO编写

笔记链接：[redis_java/01_redis_client_dao.md](redis_java/01_redis_client_dao.md)

内容：

> 1. 开发环境设置
> 2. Redis Client类库及Jedis介绍
> 3. Jedis类型映射关系、Domain Objects编写、DAO接口及实现
> 4. 注意事项：(1) 线程安全 (2) Server类型 (3) 最大连接数 (4) 连接及连接池释放 (5) Redis命令的时间复杂度

### (2) Jedis Pipeline；Jedis Transaction

笔记链接：[redis_java/02_datamodel_pipeling_transactions_lua.md](redis_java/02_datamodel_pipeling_transactions_lua.md)

内容：

> 1. Demo：太阳能电板数据的收集和查询（DAO接口及实现）
> 2. Demo：太阳能电板数据每日汇总（Lua Script、Jedis Pipeline、Jedis Transaction）

### (3) 地理位置信息（Geospetial）； Redis Stream介绍

笔记链接：[redis_java/03_geo_and_stream.md](redis_java/03_geo_and_stream.md)

内容：

> 1. Demo ：太阳能电池板电量Leader Board功能的实现
>
> 2. Demo ：太阳能电池板地理位置相关的查询
>
>     简单查询：根据经纬度左边搜索附近的太阳能电池板
>
>     组合查询：查询指定地理半径内、电量值大于阈值的太阳能电池板
>
>     地理位置查询相关的命令、性能考虑点、解决方法
>
> 3. Redis Stream数据结构及命令：`XADD`、`XRABNGE`、`XREVRANGE`
>
> 4. Demo：使用Redis Stream实现太阳能电池板数据信息流（Meter Reading Feed）

### (4) RateLimiter；时间序列；连接管理；Exception处理；性能；Debug方法；Redis协议介绍

笔记链接：[redis_java/04_ratelimiter_timeseries_error_conn_debug_protocal.md](redis_java/04_ratelimiter_timeseries_error_conn_debug_protocal.md)

内容：

> 1. 实现Rate Limiter：固定窗口Rate Limiter；滑动窗口Rate Limiter
> 2. RedisTimeSeires Module介绍：用于处理时间序列数据
> 3. 连接管理：命令，Java代码，如何确定应用所需的连接数、节点数
> 4. Exception处理：三种JedisException
> 5. 性能：Latency构成；减小Latency；各命令的时间复杂度；需要小心的命令；注意事项
> 6. Debugging：Tips及方法
> 7. Redis协议：介绍、例子、数据包查看方法

## 3. Redis设计与实现（todo）

> [http://redisbook.com/index.html](http://redisbook.com/index.html)
>
> TODO

