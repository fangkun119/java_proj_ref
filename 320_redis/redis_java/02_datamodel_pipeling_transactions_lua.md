<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Data Model Pipeline：数据接收/存储/事务/LUA脚本](#data-model-pipeline%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6%E5%AD%98%E5%82%A8%E4%BA%8B%E5%8A%A1lua%E8%84%9A%E6%9C%AC)
  - [1. 项目背景](#1-%E9%A1%B9%E7%9B%AE%E8%83%8C%E6%99%AF)
  - [2. 数据接收](#2-%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6)
    - [2.1 Domain Object](#21-domain-object)
    - [2.2 操作演示](#22-%E6%93%8D%E4%BD%9C%E6%BC%94%E7%A4%BA)
    - [2.3 处理Rest操作的类](#23-%E5%A4%84%E7%90%86rest%E6%93%8D%E4%BD%9C%E7%9A%84%E7%B1%BB)
  - [2. 使用Sorted Set存储仪表数据](#2-%E4%BD%BF%E7%94%A8sorted-set%E5%AD%98%E5%82%A8%E4%BB%AA%E8%A1%A8%E6%95%B0%E6%8D%AE)
    - [2.1 Use Case及Redis数据结构](#21-use-case%E5%8F%8Aredis%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84)
      - [(1) Use Case](#1-use-case)
      - [(2) 数据结构及Redis Key](#2-%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E5%8F%8Aredis-key)
    - [2.2 数据存储](#22-%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8)
      - [(1) DAO接口](#1-dao%E6%8E%A5%E5%8F%A3)
      - [(2) DAO实现](#2-dao%E5%AE%9E%E7%8E%B0)
        - [`getRecent`方法](#getrecent%E6%96%B9%E6%B3%95)
        - [`insertMetric`方法](#insertmetric%E6%96%B9%E6%B3%95)
  - [3. Site粒度天级别统计](#3-site%E7%B2%92%E5%BA%A6%E5%A4%A9%E7%BA%A7%E5%88%AB%E7%BB%9F%E8%AE%A1)
    - [3.1 Use Case](#31-use-case)
    - [3.2 Java代码](#32-java%E4%BB%A3%E7%A0%81)
      - [(1) Domain Object](#1-domain-object)
      - [(2) DAO接口](#2-dao%E6%8E%A5%E5%8F%A3)
      - [(3) DAO实现：有问题的版本](#3-dao%E5%AE%9E%E7%8E%B0%E6%9C%89%E9%97%AE%E9%A2%98%E7%9A%84%E7%89%88%E6%9C%AC)
    - [3.3 原子性：使用`lua脚本`实现类似“存储过程”的功能](#33-%E5%8E%9F%E5%AD%90%E6%80%A7%E4%BD%BF%E7%94%A8lua%E8%84%9A%E6%9C%AC%E5%AE%9E%E7%8E%B0%E7%B1%BB%E4%BC%BC%E5%AD%98%E5%82%A8%E8%BF%87%E7%A8%8B%E7%9A%84%E5%8A%9F%E8%83%BD)
      - [(1) Lua文档](#1-lua%E6%96%87%E6%A1%A3)
      - [(2) lua脚本例子](#2-lua%E8%84%9A%E6%9C%AC%E4%BE%8B%E5%AD%90)
      - [(3) 例子](#3-%E4%BE%8B%E5%AD%90)
    - [3.4 多命令封装：使用`Jedis Pipeline`](#34-%E5%A4%9A%E5%91%BD%E4%BB%A4%E5%B0%81%E8%A3%85%E4%BD%BF%E7%94%A8jedis-pipeline)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3)
      - [(2) 例子](#2-%E4%BE%8B%E5%AD%90)
    - [3.5 事务：让`Jedis Pipeline`在原子操作中执行](#35-%E4%BA%8B%E5%8A%A1%E8%AE%A9jedis-pipeline%E5%9C%A8%E5%8E%9F%E5%AD%90%E6%93%8D%E4%BD%9C%E4%B8%AD%E6%89%A7%E8%A1%8C)
      - [(1) 例子](#1-%E4%BE%8B%E5%AD%90)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Data Model Pipeline：数据接收/存储/事务/LUA脚本

## 1. 项目背景

本章目标是将仪表（meter）中的数据实时注入到系统中并在dashboard中显示。应用场景如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_ch2_projbg.jpg" width="500" /></div>
>
> 太阳能面板（panel）中的数据被仪表（meter）采集，每分钟向server推送一次，输入内容如图（siteid，timestamp，watt-hours-used, watt-hours-generated, temperature）

## 2. 数据接收

### 2.1 Domain Object

> 代码：[/src/main/java/com/redislabs/university/RU102J/api/MeterReading.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/api/MeterReading.java)
>
> 与上一章的Site Domain Object相同，类似普通的POJO
>
> (1) 使用jackson注解使其可以进行JSON序列化/反序列化，用于通信协议
>
> (2) 提供`toMap()`以及使用`Map<String,String>`为参数的构造函数，用来与redis的Hash类型进行转换
>
> (3) 标准的`equals()`、`hashCode()`、`toString()`方法

### 2.2 操作演示

通过REST API接收数据，并存入Redis

(1) 启动redis

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ redis-server
> ...
> ... ready to accept connections
> ~~~

(2) 数据初始化

> 如果是第一次构建项目时，在`mvn clean package`之后使用`java -jar target/redisolar-1.0.jar load --flush true`来向Redis存入初始数据

(3) 启动程序

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ java -jar target/redisolar-1.0.jar server config.yml
> ...
> ~~~

(4) 使用POST Man向`127.0.0.1:8081/api/meterReadings`发送POST请求

> Headers中增加：`Content-Type=application/json`；Body为
>
> ~~~json
> [{
>	 "siteId":1,
>	 "dateTime":"2020-01-01T00:00:00.927Z",
>	 "whUsed":2.5,
>	 "whGenerated":3.0,
>	 "tempC":22.5
> },{
>	 "siteId":1,
>	 "dateTime":"2020-01-01T00:01:00.927Z",
>	 "whUsed":2.4,
>	 "whGenerated":3.1,
>	 "tempC":22.0
> }]
> ~~~

### 2.3 处理Rest操作的类

> 代码：[/src/main/java/com/redislabs/university/RU102J/resources/MeterReadingResource.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/resources/MeterReadingResource.java)
>
> 使用Dropwizard REST框架实现
>
> ~~~java
> @Path("/meterReadings") // REST BASE路径
> @Produces(MediaType.APPLICATION_JSON)
> @Consumes(MediaType.APPLICATION_JSON)
> public class MeterReadingResource {
> 	final static private Integer MAX_RECENT_FEEDS = 1000;
>	final static private Integer DEFAULT_RECENT_FEEDS = 100;
> 
>	// 在RedisSolarApplication初始化时传入，为了focus在Redis演示上，没有使用任何容器和框架
>	private final SiteStatsDao siteStatsDao;
>	private final MetricDao metricDao;
>	private final CapacityDao capacityDao;
>	private final FeedDao feedDao;
>	public MeterReadingResource(SiteStatsDao siteStatsDao, MetricDao metricDao,
>			CapacityDao capacityDao, FeedDao feedDao) {
>		this.siteStatsDao = siteStatsDao;
>		this.metricDao = metricDao;
>		this.capacityDao = capacityDao;
>		this.feedDao = feedDao;
>	}
> 
>	// 处理POST请求，每个site并不能保证严格每分钟发送1次，可能会延迟，积累一些后一起发送，因此参数是List
>	@POST 
>	public Response addAll(List<MeterReading> readings) {
>		for (MeterReading reading : readings) {
>			add(reading);
>		}
>		return Response.accepted().build();
>	}
> 
>	public Response add(MeterReading reading) {
>		metricDao.insert(reading);
>		siteStatsDao.update(reading);
>		capacityDao.update(reading);
>		feedDao.insert(reading);
>		return Response.accepted().build();
>	}
> 	
>	// 其他方法
> 	...
> }
> ~~~
>

## 2. 使用Sorted Set存储仪表数据

> [Sorted Set commands at Redis.io](https://redis.io/commands#sorted_set)

### 2.1 Use Case及Redis数据结构

#### (1) Use Case

> * 存储很多的MeterReading数据：insert
> * 需要根据siteId进行支持range查询

#### (2) 数据结构及Redis Key

数据结构：Sorted Set

Redis Key： 

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_metric_redis_key.jpg" width="300" /></div>

Sorted Set内存储的数据（以temperature为例）：

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_metric_store_tempeture.jpg" width="400" /></div>
>
> score: minute index
>
> element: ${temperature}:${minute_index}
>
> 这样就可以按时间排序，同时不同时间相同温度的数据不会互相覆盖

### 2.2 数据存储

#### (1) DAO接口

> [/src/main/java/com/redislabs/university/RU102J/dao/MetricDao.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/MetricDao.java)
>
> ~~~java
> public interface MetricDao {
>	 void insert(MeterReading reading);
>	 List<Measurement> getRecent(Long siteId, MetricUnit unit,
>								 ZonedDateTime time,Integer limit);
> }
> ~~~

#### (2) DAO实现

##### `getRecent`方法

> [src/main/java/com/redislabs/university/RU102J/dao/MetricDaoRedisZsetImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/MetricDaoRedisZsetImpl.java)
>
> ~~~java
> // ...
> private List<Measurement> getMeasurementsForDate(Long siteId, ZonedDateTime date, MetricUnit unit, Integer count) {
>	// 返回的Measurement列表
>	List<Measurement> measurements = new ArrayList<>();
>	try (Jedis jedis = jedisPool.getResource()) {
>		// 生成Redis Key
>		// * 格式：metric:[unit-name]:[year-month-day]:[site-id]
>		// * 例子：metrics:whU:2020-01-01:1
>		String metricKey = RedisSchema.getDayMetricKey(siteId, unit, date);
>		// 查最近的count个Measurement，因此要倒序，使用ZREVRANGWITHSCORE命令
>		Set<Tuple> metrics = jedis.zrevrangeWithScores(metricKey, 0, count - 1);
>		for (Tuple minuteValue : metrics) {
>			// 提取查到的Value，格式为[measurement]:[minute]
>			// 静态函数fromZSetValue将Redis返回的String解析为MeasurementMinute对象
>			MeasurementMinute mm = MeasurementMinute.fromZSetValue(minuteValue.getElement());
>			// 从数据中提取日期
> 			ZonedDateTime dateTime = getDateFromDayMinute(date, mm.getMinuteOfDay());
>			// 填充到要返回的Measurement中
>			measurements.add(new Measurement(siteId, unit, dateTime, mm.getMeasurement()));
>		}
> 	}
>    // 再次倒序，使其变为按时间从小到大排
>	Collections.reverse(measurements);
>	return measurements;
> }
>// ...
>~~~
> 
> [/src/main/java/com/redislabs/university/RU102J/api/Measurement.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/api/Measurement.java)
> 
>~~~java
> public class Measurement {
>	public Long siteId;
> 	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
> 	public ZonedDateTime dateTime;
> 	public Double value;
> 	public MetricUnit metricUnit;
>	...
>}
>~~~
>
> [src/main/java/com/redislabs/university/RU102J/api/MetricUnit.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/api/MetricUnit.java)
> 
>~~~java
> public enum MetricUnit {
>	WHGenerated("whG"),
> 	WHUsed("whU"),
> 	TemperatureCelsius("tempC");
>	...
>}
>~~~
>

##### `insertMetric`方法

> ~~~java
> private void insertMetric(Jedis jedis, long siteId, double value, MetricUnit unit,
> 		ZonedDateTime dateTime) {
> 	String metricKey = RedisSchema.getDayMetricKey(siteId, unit, dateTime);
> 	Integer minuteOfDay = getMinuteOfDay(dateTime);
> 	MeasurementMinute mm = new MeasurementMinute(Double.valueOf(value), Integer.valueOf(minuteOfDay));
> 	jedis.zadd(metricKey, minuteOfDay, mm.toString());
> }
> ~~~

## 3. Site粒度天级别统计

### 3.1 Use Case

> 对上一小节分钟粒度的数据进行聚合到天级粒度，生成报表， 内容包含每个site的：
>
> * `last reporting time`
> * `meter readings per day`
> * `max watt-hours generated in this day`
>
> 实现这些将会用到Lua脚本和Redis事务

### 3.2 Java代码

#### (1) Domain Object

> 代码：[/src/main/java/com/redislabs/university/RU102J/api/SiteStats.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/api/SiteStats.java)
>
> ~~~java
> public class SiteStats {
> 	private ZonedDateTime lastReportingTime;
> 	private Long meterReadingCount;
> 	private Double maxWhGenerated;
> 	private Double minWhGenerated;
> 	private Double maxCapacity;
> 	...
> }
> ~~~

#### (2) DAO接口

> [/src/main/java/com/redislabs/university/RU102J/dao/SiteStatsDao.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/SiteStatsDao.java)
>
> ~~~java
> public interface SiteStatsDao {
>     SiteStats findById(long siteId);
>     SiteStats findById(long siteId, ZonedDateTime day);
>     void update(MeterReading reading);
> }
> ~~~

#### (3) DAO实现：有问题的版本

问题：

> * 在“Compare And Set”时，不具备原子性
> * 在”更新多个字段时“时，要与Redis多轮交互
> * 不具备事务属性

以下是这个有问题版本的代码，之后的三节依次解决上面的三个问题

> [/src/main/java/com/redislabs/university/RU102J/dao/SiteStatsDaoRedisImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/SiteStatsDaoRedisImpl.java)
>
> ~~~java
> public class SiteStatsDaoRedisImpl implements SiteStatsDao {
> 	private final int weekSeconds = 60 * 60 * 24 * 7;    
> 	private final JedisPool jedisPool;
> 	private final CompareAndUpdateScript compareAndUpdateScript;
> 	public SiteStatsDaoRedisImpl(JedisPool jedisPool) {
> 		this.jedisPool = jedisPool;
> 		this.compareAndUpdateScript = new CompareAndUpdateScript(jedisPool);
> 	}
> 
>     // 查询：返回指定siteId在当前日期的Site Stat信息
> 	@Override
> 	public SiteStats findById(long siteId) {
> 		return findById(siteId, ZonedDateTime.now());
> 	}
> 	@Override
> 	public SiteStats findById(long siteId, ZonedDateTime day) {
> 		try (Jedis jedis = jedisPool.getResource()) {
> 			String key = RedisSchema.getSiteStatsKey(siteId, day);
> 			Map<String, String> fields = jedis.hgetAll(key);
> 			if (fields == null || fields.isEmpty()) {
> 				return null;
> 			}
> 			return new SiteStats(fields);
> 		}
> 	}
> 
> 	// 更新数据
> 	@Override
> 	public void update(MeterReading reading) {
> 		try (Jedis jedis = jedisPool.getResource()) {
> 			// 提取siteId, day；生成redis key
> 			Long siteId = reading.getSiteId();
> 			ZonedDateTime day = reading.getDateTime();
> 			String key = RedisSchema.getSiteStatsKey(siteId, day);
> 			// 执行更新操作
> 			updateBasic(jedis, key, reading);
> 		}
> 	}
> 
> 	// 这个版本还没有使用事务，多线程更新同一个key会导致数据不一致
> 	private void updateBasic(Jedis jedis, String key, MeterReading reading) {
> 		// 计算reporting time
> 		String reportingTime = ZonedDateTime.now(ZoneOffset.UTC).toString();
> 		// 设置或更新hash内的字段：
> 		// * "lastReportingTime"
> 		jedis.hset(key, SiteStats.reportingTimeField, reportingTime);
> 		// * "meterReadingCount"
> 		jedis.hincrBy(key, SiteStats.countField, 1);
> 		// * "maxWhGenerated"
> 		String maxWh = jedis.hget(key, SiteStats.maxWhField);
> 		if (maxWh == null || reading.getWhGenerated() > Double.valueOf(maxWh)) {
> 			jedis.hset(key, SiteStats.maxWhField, String.valueOf(reading.getWhGenerated()));
> 		}
> 		// * "minWhGenerated"
> 		String minWh = jedis.hget(key, SiteStats.minWhField);
> 		if (minWh == null || reading.getWhGenerated() < Double.valueOf(minWh)) {
> 			jedis.hset(key, SiteStats.minWhField, String.valueOf(reading.getWhGenerated()));
> 		}
> 		// * "maxCapacity"
> 		String maxCapacity = jedis.hget(key, SiteStats.maxCapacityField);
> 		if (maxCapacity == null || getCurrentCapacity(reading) > Double.valueOf(maxCapacity)) {
> 			jedis.hset(key, SiteStats.maxCapacityField, String.valueOf(getCurrentCapacity(reading)));
> 		}
> 		// 设置这个key(siteid,date)所存储hash的过期时间：一周后过期
> 		jedis.expire(key, weekSeconds);
>     }
> 
>     // Challenge #3
>     private void updateOptimized(Jedis jedis, String key, MeterReading reading) {
>         // START Challenge #3
>         // END Challenge #3
>     }
> 
>     private Double getCurrentCapacity(MeterReading reading) {
>         return reading.getWhGenerated() - reading.getWhUsed();
>     }
> } 
> ~~~

### 3.3 原子性：使用`lua脚本`实现类似“存储过程”的功能

> 可以使用LUA脚本编写Redis数据处理逻辑，然后由Jedis在一个原子操作中执行，提供类似“存储过程”的功能

#### (1) Lua文档

> [ Lua Scripting docs at Redis.io](https://redis.io/commands/eval)

#### (2) lua脚本例子

> [/src/main/resources/lua/updateIfLowest.lua](https://github.com/fangkun119/ru102j/blob/master/src/main/resources/lua/updateIfLowest.lua)
>
> ~~~lua
> -- Lua script to compare set if lower
> -- key: the name a Redis string storing a number
> -- new: a number which, if smaller, will replace the existing number
> -- 读取两个参数并存储在本地变量中
> local key = KEYS[1]
> local new = ARGV[1]
> -- 执行查询-比较-更新操作
> -- 其中tonumber用来把String转换成数字以用于比较
> local current = redis.call('GET', key)
> if (current == false) or (tonumber(new) < tonumber(current)) then
>   redis.call('SET', key, new)
>   return 1
> else
>   return 0
> end
> ~~~

#### (3) 例子

步骤如下：

> 1. 为每个lua脚本编写一个class
> 2. class在初始化加载lua script脚本，并缓存脚本签名（使用`EVALSHA`命令）以便后续执行脚本
> 3. 提供一个java接口用来调用脚本

例子：[/src/main/java/com/redislabs/university/RU102J/examples/UpdateIfLowestScript.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/examples/UpdateIfLowestScript.java)

> ~~~java
> public class UpdateIfLowestScript {
> 	private final Jedis jedis;
> 	private final String sha;
> 	// Lua脚本
> 	private final static String script =
> 		"local key = KEYS[1] " +
> 		"local new = ARGV[1] " +
> 		"local current = redis.call('GET', key) " +
> 		"if (current == false) or " +
> 		"   (tonumber(new) < tonumber(current)) then " +
> 		"  redis.call('SET', key, new) " +
> 		"  return 1 " +
> 		"else " +
> 		"  return 0 " +
> 		"end";
> 	// 类对象构造时，加载Lua脚本，并保存脚本签名
> 	public UpdateIfLowestScript(Jedis jedis) {
> 		this.jedis = jedis;
> 		this.sha = jedis.scriptLoad(script);
> 	}
> 	// 函数被调用时会执行Lua脚本
> 	public boolean updateIfLowest(String key, Integer newValue) {
> 		// 以列表的形式传参，参数名分别是KEYS, ARGV
> 		List<String> keys = Collections.singletonList(key);
> 		List<String> args = Collections.singletonList(String.valueOf(newValue));
> 		Object response = jedis.evalsha(sha, keys, args);
> 		return (Long)response == 1;
> 	}
> }
> ~~~

测试：[/src/test/java/com/redislabs/university/RU102J/examples/UpdateIfLowestScriptTest.java](https://github.com/fangkun119/ru102j/blob/master/src/test/java/com/redislabs/university/RU102J/examples/UpdateIfLowestScriptTest.java)

### 3.4 多命令封装：使用`Jedis Pipeline`

Jedis Pipeline以串行的方式封装所有command，然后统一发送给redis执行，具有如下优点：

> * 将多个操作封装在一轮Redis交互中，减少调用开销，减少等待的时延
> * 减少系统调用（read()、write()）

但是只使用Jedis Pipeline，不能保证事务属性，事务属性将在下一小节介绍

#### (1) 文档

> [Pipelining docs at Redis.io](https://redis.io/topics/pipelining)

#### (2) 例子

> [/src/test/java/com/redislabs/university/RU102J/examples/MultiKeyTest.java](https://github.com/fangkun119/ru102j/blob/master/src/test/java/com/redislabs/university/RU102J/examples/MultiKeyTest.java)
>
> ~~~java
> @Test
> public void testPipeline() {
> 	Long siteId = 1L;
> 	Pipeline p = jedis.pipelined();
> 	// 并不会执行
> 	Response<Long> hsetResponse = p.hset(statusKey, "available", "true");
> 	Response<Long> expireResponse = p.expire(statusKey, 1000);
> 	Response<Long> saddResponse = p.sadd(availableKey, String.valueOf(siteId));
> 	// 发送命令给Redis
>     // sync: 不保证三条命令执行具有原子性
> 	p.sync();
> 	// 检查结果
> 	assertThat(hsetResponse.get(), is(1L));
> 	assertThat(expireResponse.get(), is(1L));
> 	assertThat(saddResponse.get(), is(1L));
> }
> ~~~

### 3.5 事务：让`Jedis Pipeline`在原子操作中执行

> jedis Pipeline要加上原子属性，才能执行事务
>
> * 事务属性保证命令执行，不会收到其他“线程”/“客户端”命令的干扰
> * 但是事务在执行时，会block其他命令

#### (1) 例子

> 方法：
>
> ```java
> @Test
> public void testTransaction() {
> 	Long siteId = 1L;
> 	Transaction t = jedis.multi();
> 
> 	Response<Long> hsetResponse = t.hset(statusKey, "available", "true");
> 	Response<Long> expireResponse = t.expire(statusKey, 1000);
> 	Response<Long> saddResponse = t.sadd(availableKey, String.valueOf(siteId));
> 
>     // 将sync替换为exec
> 	// exec：保证三条命令执行具有原子性
> 	t.exec();
> 
> 	assertThat(hsetResponse.get(), is(1L));
> 	assertThat(expireResponse.get(), is(1L));
> 	assertThat(saddResponse.get(), is(1L));
> }
> ```