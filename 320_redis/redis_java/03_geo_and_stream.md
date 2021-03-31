<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Geo查询以及Redis Stream](#geo%E6%9F%A5%E8%AF%A2%E4%BB%A5%E5%8F%8Aredis-stream)
  - [1. 内容介绍](#1-%E5%86%85%E5%AE%B9%E4%BB%8B%E7%BB%8D)
  - [2. Site Capacity Leaderboards](#2-site-capacity-leaderboards)
    - [2.1 介绍](#21-%E4%BB%8B%E7%BB%8D)
    - [2.2 代码](#22-%E4%BB%A3%E7%A0%81)
      - [(a) `CapacityDao`](#a-capacitydao)
      - [(b) `CapacityDaoRedisImpl`](#b-capacitydaoredisimpl)
    - [2.3 Sorted Set性能](#23-sorted-set%E6%80%A7%E8%83%BD)
  - [3. Geospatial数据](#3-geospatial%E6%95%B0%E6%8D%AE)
    - [3.1 介绍](#31-%E4%BB%8B%E7%BB%8D)
      - [(1) 相关文档](#1-%E7%9B%B8%E5%85%B3%E6%96%87%E6%A1%A3)
      - [(2) 本章节功能](#2-%E6%9C%AC%E7%AB%A0%E8%8A%82%E5%8A%9F%E8%83%BD)
    - [3.2 Geospatial数据](#32-geospatial%E6%95%B0%E6%8D%AE)
    - [3.3 Geospatial存储及简单查询](#33-geospatial%E5%AD%98%E5%82%A8%E5%8F%8A%E7%AE%80%E5%8D%95%E6%9F%A5%E8%AF%A2)
      - [(1) `SiteGeoDao extends SiteDao`](#1-sitegeodao%C2%A0extends%C2%A0sitedao)
      - [(2) `SiteGeoDaoRedisImpl`](#2-sitegeodaoredisimpl)
    - [3.4 Geospatial组合其他查询条件](#34-geospatial%E7%BB%84%E5%90%88%E5%85%B6%E4%BB%96%E6%9F%A5%E8%AF%A2%E6%9D%A1%E4%BB%B6)
      - [(1) Use Case](#1-use-case)
      - [(2) 性能考虑点](#2-%E6%80%A7%E8%83%BD%E8%80%83%E8%99%91%E7%82%B9)
        - [(a) 命令的时间复杂度：GEORADIUS、ZSCORE](#a-%E5%91%BD%E4%BB%A4%E7%9A%84%E6%97%B6%E9%97%B4%E5%A4%8D%E6%9D%82%E5%BA%A6georadiuszscore)
        - [(b) Latency和Round Trip考虑](#b-latency%E5%92%8Cround-trip%E8%80%83%E8%99%91)
      - [(3) 解决方法](#3-%E8%A7%A3%E5%86%B3%E6%96%B9%E6%B3%95)
        - [(a) 使用Pipeline来减少Round Trip到2次](#a-%E4%BD%BF%E7%94%A8pipeline%E6%9D%A5%E5%87%8F%E5%B0%91round-trip%E5%88%B02%E6%AC%A1)
        - [(b) 使用Lua Script来减少Round Trip到1次](#b-%E4%BD%BF%E7%94%A8lua-script%E6%9D%A5%E5%87%8F%E5%B0%91round-trip%E5%88%B01%E6%AC%A1)
      - [(4) 代码](#4-%E4%BB%A3%E7%A0%81)
  - [4. Redis Stream](#4-redis-stream)
    - [4.1 介绍](#41-%E4%BB%8B%E7%BB%8D)
      - [(2) Redis Stream](#2-redis-stream)
      - [(3) 命令 ：`XADD`、](#3-%E5%91%BD%E4%BB%A4-xadd)
      - [(4) Stream Size](#4-stream-size)
    - [4.2 使用Stream实现Meter Reading Feed（测量数据信息流）](#42-%E4%BD%BF%E7%94%A8stream%E5%AE%9E%E7%8E%B0meter-reading-feed%E6%B5%8B%E9%87%8F%E6%95%B0%E6%8D%AE%E4%BF%A1%E6%81%AF%E6%B5%81)
      - [(1) 功能](#1-%E5%8A%9F%E8%83%BD)
      - [(2) DAO Interface](#2-dao-interface)
      - [(3) DAO实现](#3-dao%E5%AE%9E%E7%8E%B0)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Geo查询以及Redis Stream

## 1. 内容介绍

包括以下4部分：

> (1) 使用Sorted Set存储存储各个Site Capacity
>
> (2) 使用Reids Geo存储Site的地理位置信息，用来展现dashboard
>
> (3) 结合Redis Geo和Lua来根据查询条件以及地理位置，来进行查询
>
> (4) 使用Redis Stream来同步各个系统之间的数据

## 2. Site Capacity Leaderboards

### 2.1 介绍 

相关参考

> [ Sorted Set commands at Redis.io](https://redis.io/commands#sorted_set)

功能：

> 每分钟更新一次各个Site的电量信息
>
> 在报表页面上用两个柱状图显示电量最多、最少的Sites

数据存储：使用Sorted Site

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_site_capacity_leaderboard_storage.jpg" width="500" /></div>

### 2.2 代码

#### (a) `CapacityDao`

>[/src/main/java/com/redislabs/university/RU102J/dao/CapacityDao.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/CapacityDao.java)
>
>~~~java
>public interface CapacityDao {
>	void update(MeterReading reading);
>	CapacityReport getReport(Integer limit);
>	Long getRank(Long siteId);
>}
>~~~

#### (b) `CapacityDaoRedisImpl`

> [/src/main/java/com/redislabs/university/RU102J/dao/CapacityDaoRedisImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/CapacityDaoRedisImpl.java)

更新各个site的电量

> ~~~java
> // 更新
> @Override
> public void update(MeterReading reading) {
> 	// Redis Key："sites:capacity:ranking"
> 	String capacityRankingKey = RedisSchema.getCapacityRankingKey();
> 	// 请求携带的数据
> 	Long siteId = reading.getSiteId();
> 	double currentCapacity = reading.getWhGenerated() - reading.getWhUsed();
> 	// 存储
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		jedis.zadd(capacityRankingKey, currentCapacity, String.valueOf(siteId));
> 	}
> }
> ~~~

Global Capacity Leader Board：查询所有site中电量最低、最高的几个site

> ```java
> // 查询：使用ZRANGE和ZREVRANGE
> @Override
> public CapacityReport getReport(Integer limit) {
> 	// 返回结果
> 	CapacityReport report;
> 	// Redis Key："sites:capacity:ranking"
> 	String key = RedisSchema.getCapacityRankingKey();
> 	// 查询
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		// 使用ZRANGE、ZREVRANGE查询电量最低、最高的limit个SITE
> 		Pipeline p = jedis.pipelined();
> 		Response<Set<Tuple>> lowCapacity = p.zrangeWithScores(key, 0, limit-1);
> 		Response<Set<Tuple>> highCapacity = p.zrevrangeWithScores(key, 0, limit-1);
> 		p.sync();
> 		// 类型转换
> 		List<SiteCapacityTuple> lowCapacityList = lowCapacity.get().stream()
> 			.map(SiteCapacityTuple::new)
> 			.collect(Collectors.toList());
> 		List<SiteCapacityTuple> highCapacityList = highCapacity.get().stream()
> 			.map(SiteCapacityTuple::new)
> 			.collect(Collectors.toList());
> 		// 返回
> 		report = new CapacityReport(highCapacityList, lowCapacityList);
> 	}
> 	return report;
> }
> ```

细粒度的Capacity Leader Board

> 例如City，Region中电量最高、最低的几个Site
>
> 使用key name命名来解决，例如：`sites:capacity:ranking:${city}`

查询一个site的电量排名（rank）

> ```java
> @Override
> public Long getRank(Long siteId) {
> 	Long rank = null;
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		rank = jedis.zrevrank(RedisSchema.getCapacityRankingKey(), siteId.toString());
> 	}
> 	return rank;
> }
> ```

### 2.3 Sorted Set性能

`ZRANGE`：`O(log(n) + m)`

> 对于小set（n）小range（m）不成问题
>
> 但是对于大set或者大range，则需要注意性能问题
>
> 可以考虑使用ZREM系列命令（例如ZREMRANGEBYRANK命令）来控制sorted set的大小

## 3. Geospatial数据

### 3.1 介绍

#### (1) 相关文档

> | **Wikipedia articles**                            | **Documentation at redis.io**                     |
> | ------------------------------------------------- | ------------------------------------------------- |
> | [ Geohash](https://en.wikipedia.org/wiki/Geohash) | [ GEOADD](https://redis.io/commands/geoadd)       |
> |                                                   | [ GEORADIUS](https://redis.io/commands/georadius) |

#### (2) 本章节功能

> 主页是一个表示各个site的地图，和一个使用经纬度坐标进行搜索的搜索框
>
> 本小节使用Redis GEO命令，实现相关的后端功能

### 3.2 Geospatial数据

> 对经度纬度进行GeoHash编码，编码结果（11位字母或数组数组的字符、可通过前缀匹配找到相近的地点）转换成52bit整数，存储在Sorted Hash中。例子如下：
>
> ~~~bash
> 127.0.0.1:6379> GEOADD sites:geo -122.147019 37.670738 56
> (integer) 1
> 127.0.0.1:6379> GEOADD sites:geo -122.007419 37.5506959 101
> (integer) 1
> 127.0.0.1:6379> GEORADIUS sites:geo -122.271111 37.804363 1 km
> (empty array)
> 127.0.0.1:6379> GEORADIUS sites:geo -122.271111 37.804363 20 km
> 1) "56"
> 127.0.0.1:6379> GEORADIUS sites:geo -122.271111 37.804363 50 km
> 1) "101"
> 2) "56"
> 127.0.0.1:6379> GEORADIUS sites:geo -122.271111 37.804363 50 km WITHDIST WITHCOORD
> 1) 1) "101"
>    2) "36.5363"
>    3) 1) "-122.00741976499557495"
>       2) "37.55069631985877265"
> 2) 1) "56"
>    2) "18.4402"
>    3) 1) "-122.14701801538467407"
>       2) "37.67073817924390511"
> ~~~

### 3.3 Geospatial存储及简单查询

#### (1) `SiteGeoDao extends SiteDao`

> [/src/main/java/com/redislabs/university/RU102J/dao/SiteDao.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/SiteDao.java)
>
> ~~~java
> public interface SiteDao {
>    	void insert(Site site);
>    	Site findById(long id);
>    	Set<Site> findAll();
> }
> ~~~
>
> [/src/main/java/com/redislabs/university/RU102J/dao/SiteGeoDao.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/SiteGeoDao.java)
>
> ```java
> public interface SiteGeoDao extends SiteDao {
> 	Set<Site> findByGeo(GeoQuery query);
> }
> ```

#### (2) `SiteGeoDaoRedisImpl`

> [/src/main/java/com/redislabs/university/RU102J/dao/SiteGeoDaoRedisImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/SiteGeoDaoRedisImpl.java)

insert

> ```java
> @Override
> public void insert(Site site) {
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		// 向”sites:info:${site_id}"存入site相关的信息
> 		String key = RedisSchema.getSiteHashKey(site.getId());
> 		jedis.hmset(key, site.toMap());
> 		if (site.getCoordinate() == null) {
> 			throw new IllegalArgumentException("Coordinate required for Geo insert.");
> 		}
> 		// 向“site:geo"存储地理位置：把GeoHash作为score，"sites:info:${site_id}"作为member
> 		Double longitude = site.getCoordinate().getGeoCoordinate().getLongitude();
> 		Double latitude = site.getCoordinate().getGeoCoordinate().getLatitude();
> 		jedis.geoadd(RedisSchema.getSiteGeoKey(), longitude, latitude, key);
> 	}
> }
> ```

findAll

> 有问题的代码版本：
>
> ```java
> @Override
> public Set<Site> findAll() {
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		// 从"site:geo"中查出所有的member，格式为"site:info:${site_id}“ 
> 		Set<String> keys = jedis.zrange(RedisSchema.getSiteGeoKey(), 0, -1);
> 		Set<Site> sites = new HashSet<>(keys.size());
> 		for (String key : keys) {
> 			// 为每一个"site:info:${site_id}”，查出该站点的详细信息
> 			Map<String, String> site = jedis.hgetAll(key);
> 			if (!site.isEmpty()) {
> 				sites.add(new Site(site));
> 			}
> 		}
> 		return sites;
> 	}
> }
> ```
>
> 这份代码有两个问题
>
> 1. 对于大的Sorted Set，建议使用`ZSCAN`通过游标遍历，而不是使用`ZRANGE`阻塞遍历
> 2. 代码频繁调用`HGETALL`，造成重复的Redis调用等待，应当用pipeline来合并成一次Redis调用
>
> 修改后的版本如下
>
> ```java
> @Override
> public Set<Site> findAll() throws JedisDataException {
> 	List<Object> siteInfoRestList = null;
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		// 根据地理位置查询site key列表
> 		Set<String> keys = jedis.zrange(RedisSchema.getSiteGeoKey(), 0, -1);
> 		// 在Redis Pipeline中，根据site key列表查询Site详细信息
> 		Pipeline pipeline = jedis.pipelined();
> 		for (String key : keys) {
> 			pipeline.hgetAll(key);
> 		}
> 		// exec
> 		siteInfoRestList = pipeline.syncAndReturnAll();
> 	}
> 	// 组装返回结果
> 	Set<Site> sites = new HashSet<>();
> 	checkOrThrowException(siteInfoRestList);
> 	for (Object resp : siteInfoRestList) {
> 		sites.add(new Site((Map<String, String>) resp));
> 	}
> 	return sites;
> }
> ```

findByGeo

> 查询条件：GeoQuery
>
> ~~~java
> public class GeoQuery {
> 	private Coordinate coordinate;		// 经度纬度
> 	private Double radius;				// 半径
> 	private GeoUnit radiusUnit;			// 半径的代码：m、km、ft、mi
> 	private boolean onlyExcessCapacity;	// 是否只查询有富余电量的site
> 	...
> }
> ~~~
>
> 查询：findByGeo → findSitesByGeo
>
> findByGeo
>
> ~~~java
> public Set<Site> findByGeo(GeoQuery query) {
> 	return (query.onlyExcessCapacity()) ? findSitesByGeoWithCapacity(query) : findSitesByGeo(query);
> }
> ~~~
>
> findSitesByGeo
>
> ~~~java
> private Set<Site> findSitesByGeo(GeoQuery query) {
> 	// 查询条件
> 	Coordinate coord = query.getCoordinate();
> 	Double radius = query.getRadius();
> 	GeoUnit radiusUnit = query.getRadiusUnit();
> 	// 查询
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		// 使用GEORADIUS命令在"site:geo"中查询符合地理位置要求的"site:info:${site_id}”
> 		List<GeoRadiusResponse> radiusResponses =
> 			jedis.georadius(RedisSchema.getSiteGeoKey(), coord.getLng(),
> 				coord.getLat(), radius, radiusUnit);
> 		// 为每一个siteid，在"site:info:${site_id}”中查找该站点的详细信息
> 		// 同样该方法有太多的Redis阻塞调用时延，应该使用pipeline来优化
> 		return radiusResponses.stream()
> 			.map(response -> jedis.hgetAll(response.getMemberByString()))
> 			.filter(Objects::nonNull)
> 			.map(Site::new).collect(Collectors.toSet());
> 	}
> }
> ~~~
>
> findSitesByGeoWithCapacity：将在下一小节中介绍

### 3.4 Geospatial组合其他查询条件

#### (1) Use Case

> 返回指定地理半径内，电量大于阈值的site

#### (2) 性能考虑点

##### (a) 命令的时间复杂度：GEORADIUS、ZSCORE

GEORADIUS的时间复杂度：`O(n + log(m))`

> `n`：查询半径范围内member的数量
>
> `m`：存储在底层sorted set中的总的member的数量

ZSCORE的时间复杂度：`O(1)`

##### (b) Latency和Round Trip考虑

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_geo_latency_and_roundtrip.jpg" width="350" /></div>

#### (3) 解决方法

##### (a) 使用Pipeline来减少Round Trip到2次

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_geo_roundtrip_pipeline_approach.jpg" width="380" /></div>
>
> 只能减少到两次，因为ZSCORE命令的执行内容要在GEORADIUS执行后才能确定、无法把他们预先与GEORADIUS放在同一个管道中，一起发送给Redis执行

##### (b) 使用Lua Script来减少Round Trip到1次

> Lua脚本中的命令可以以编程的方式在运行的时候决定如何执行，因此可以把所有的执行逻辑放在脚本中
>
> 优点是：
>
> * 只有1次Round Trip
> * 不会返回用不到的中间结果
> * 封装成脚本、可重用性好
>
> 缺点是：
>
> * 原子运行，会阻塞其他命令执行
> * 如果返回数据量大，使用pipeling更合适

#### (4) 代码

> ```java
> private Set<Site> findSitesByGeoWithCapacity(GeoQuery query) {
> 	Set<Site> results = new HashSet<>();
> 	// 查询条件
> 	Coordinate coord = query.getCoordinate();
> 	Double radius = query.getRadius();
> 	GeoUnit radiusUnit = query.getRadiusUnit();
> 	// 查询
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		// 在半径指定范围内的site key
> 		List<GeoRadiusResponse> radiusResponses = jedis.georadius(
> 			RedisSchema.getSiteGeoKey(), coord.getLng(), coord.getLat(), radius, radiusUnit);
> 
> 		// 查询site信息（site数量高时也应当改用pipeline来查询）
> 		Set<Site> sites = radiusResponses.stream()
> 			.map(response -> jedis.hgetAll(response.getMemberByString()))
> 			.filter(Objects::nonNull)
> 			.map(Site::new)
> 			.collect(Collectors.toSet());
> 
> 		// 查询site当前的capacity
> 		Pipeline pipeline = jedis.pipelined();
> 		Map<Long, Response<Double>> scores = new HashMap<>(sites.size());
> 		for (Site site : sites) {
> 			Response<Double> score = pipeline.zscore(RedisSchema.getCapacityRankingKey(), String.valueOf(site.getId()));
> 			scores.put(site.getId(), score);
> 		}
>         pipeline.sync();
> 
> 		// 根据当前capacity进行过滤
> 		for (Site site : sites) {
> 			if (scores.get(site.getId()).get() >= capacityThreshold) {
> 				results.add(site);
> 			}
> 		}
> 	}
> 	return results;
> }
> ```

## 4. Redis Stream

### 4.1 介绍

#### (1) Redis Stream

> Redis Stream是一种实现为append-only log的数据结构（不是消息队列）
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_redis_stream_ds.jpg" width="380" /></div>
>
> 使用Redis Stream需要对它有深入的理解，本节focus在Demo项目中用到的部分：`在系统中显示最近的meter redaing事件`

#### (2) 命令 ：`XADD`、`XRANGE`、`XREVRANGE`、……

`XADD key [NOMKSTREAM] [MAXLEN|MINID [=|~] threshold [LIMIT count]] *|ID field value`

> 向stream发送entry
>
> `key`：stream name
>
> `ID`|`*`：entry ID，值为`*`时表示让redis自动生成ID
>
> `field value`：entry内容，是1个或多个`field value`对
>
> `[MAXLEN|MINID [=|~] threshold`：MAXLEN参数用来限制stream的大小，可以用`=`将其配为绝对数值，也可以用`~`将其配为一个大约的数值

 `XRANGE key start end [COUNT count]`

> 读取stream，按照从oldest到newest的顺序
>
> `start`、`end`：值为`+`时表示是newest，值为`-`是表示lowest

`XREVRANGE key end start [COUNT count]`

> 读取stream，按照从newest到oldest的顺序

命令行例子

> ~~~bash
> 127.0.0.1:6379> XADD test:stream * siteID 1 tempC 18.0
> "1617013292561-0" # 命令返回entry ID
> 127.0.0.1:6379> XREVRANGE test:stream + - COUNT 1
> 1) 1) "1617013292561-0"
>    2) 1) "siteID"
>       2) "1"
>       3) "tempC"
>       4) "18.0"
> ~~~

Jedis例子

> 代码：[/src/test/java/com/redislabs/university/RU102J/examples/StreamsTest.java](https://github.com/fangkun119/ru102j/blob/master/src/test/java/com/redislabs/university/RU102J/examples/StreamsTest.java)
>
> ```java
> @Test
> public void testStream() {
> 	// 用一个Map来存储entry中的field、value对 
> 	Map<String, String> entry = new HashMap<>();
> 	entry.put("siteId", "1");
> 	entry.put("tempC", "18.0");
> 	// 向stream存入entry
> 	Long maxStreamEntries = numberOfSolarSites * measurementsPerHour * hoursPerDay * maxDays;    
> 	StreamEntryID id = jedis.xadd(
> 		/*stream name      */ streamKey, 
> 		/*entry id         */ StreamEntryID.NEW_ENTRY, 
> 		/*entry            */ entry, 
> 		/*maxLen           */ maxStreamEntries, 
> 		/*approximateLength*/ true
> 	);
> 	// 从stream取出最近的entry
> 	List<StreamEntry> results = jedis.xrevrange(
> 		/* stream name      */ streamKey, 
> 		/* stream name      */ null, 
> 		/* stream name      */ null, 
> 		/* count            */ 1
> 	);
> 	// 检查结果
> 	assertThat(results.size(), is(1));
> 	assertThat(results.get(0).getID(), is(id));
> 	assertThat(results.get(0).getFields(), is(entry));
> }
> ```

#### (4) Stream Size

> 理论上讲：stream的体积应当是可以无限的，但是Redis内存有限，需要一种方式来控制stream size
>
> 因此XADD命令一个参数用来限制stream的长度，值可以是绝对值、也可以是一个大约的阈值

### 4.2 使用Stream实现Meter Reading Feed（测量数据信息流）

#### (1) 功能

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_feed_stream_usecase.jpg" width="500" /></div>

#### (2) DAO Interface

> 代码：[/src/main/java/com/redislabs/university/RU102J/dao/FeedDao.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/FeedDao.java)
>
> ~~~java
> public interface FeedDao {
> 	void insert(MeterReading meterReading);
> 	List<MeterReading> getRecentGlobal(int limit);
> 	List<MeterReading> getRecentForSite(long siteId, int limit);
> }
> ~~~

#### (3) DAO实现

> 代码：[/src/main/java/com/redislabs/university/RU102J/dao/FeedDaoRedisImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/FeedDaoRedisImpl.java)

两个用于查询的方法，提供给Resource层保留为REST端口，给web端调用

Stream的数据结构阻止方式使得它可以很方便提取最近的limit条数据

> ~~~java
> @Override
> public List<MeterReading> getRecentGlobal(int limit) {
> 	// stream key："sites:feed"
> 	return getRecent(RedisSchema.getGlobalFeedKey(), limit);
> }
> 
> @Override
> public List<MeterReading> getRecentForSite(long siteId, int limit) {
> 	// stream key: "sites:feed:${site_id}"
> 	return getRecent(RedisSchema.getFeedKey(siteId), limit);
> }
> 
> public List<MeterReading> getRecent(String key, int limit) {
> 	List<MeterReading> readings = new ArrayList<>(limit);
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		List<StreamEntry> entries = jedis.xrevrange(key, null, null, limit);
> 		for (StreamEntry entry : entries) {
> 			readings.add(new MeterReading(entry.getFields()));
> 		}
> 		return readings;
> 	}
> }
> ~~~

发送entry到Stream

> ```java
> @Override
> public void insert(MeterReading meterReading) {
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		Transaction t = jedis.multi();
> 		jedis.xadd(
> 			RedisSchema.getGlobalFeedKey(), StreamEntryID.NEW_ENTRY, meterReading.toMap(),
> 			globalMaxFeedLength, true);
> 		jedis.xadd(
> 			RedisSchema.getFeedKey(meterReading.getSiteId()),
> 			StreamEntryID.NEW_ENTRY, meterReading.toMap(),
> 			globalMaxFeedLength, true);
> 		t.exec();
> 	}
> }
> ```

