## 1. Redis数据结构

### (1) 基础数据结构

笔记链接：[redis_ds/01_ds_overview.md](redis_ds/01_ds_overview.md)

内容：

> 1. Redis Key、Key过期时间设置
> 2. 四种数据结构（String、Hash、Set、SortedSet）及操作命令

### (2) 集合操作

笔记链接：[redis_ds/02_collection_set_ops.md](redis_ds/02_collection_set_ops.md)

内容：

> 1. 常用Pattern（1）：固定集合（capped collection）
> 2. 常用Pattern（2）：Faceted Search（根据domain object属性进行组合查询）
> 3. Redis命令的时间复杂度

### (3) 事务和object存储

> todo

### (4) BitFields

> todo

### (5) 地理位置存储（geospacial）

> todo

## 2. Redis Java编程

### (1) Redis客户端及DAO编写

笔记链接：[redis_java/01_redis_client_dao.md](redis_java/01_redis_client_dao.md)

内容：

> 1. 开发环境设置
> 2. Redis Client类库及Jedis介绍
> 3. Jedis类型映射关系、DAO、Domain Objects编写
> 4. 注意事项：(1) 线程安全 (2) Server类型 (3) 最大连接数 (4) 连接及连接池释放 (5) Redis命令的时间复杂度

### (2) Data Model Pipeline及事务

> todo

### (3) GEO Streams

> todo

### (4) RateLimiter、时间序列、error connection、debug

> todo