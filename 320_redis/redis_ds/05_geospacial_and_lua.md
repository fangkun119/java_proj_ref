<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [地理位置和Lua Script](#%E5%9C%B0%E7%90%86%E4%BD%8D%E7%BD%AE%E5%92%8Clua-script)
  - [1. 地理位置（Geospacial）](#1-%E5%9C%B0%E7%90%86%E4%BD%8D%E7%BD%AEgeospacial)
    - [1.1 介绍](#11-%E4%BB%8B%E7%BB%8D)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3)
      - [(2) Geospacial](#2-geospacial)
    - [1.2 存储Geospatial](#12-%E5%AD%98%E5%82%A8geospatial)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3-1)
      - [(2) Geo Hash](#2-geo-hash)
      - [(3) 命令](#3-%E5%91%BD%E4%BB%A4)
        - [(a) `GEOADD`](#a-geoadd)
        - [(b) `GEOHASH`](#b-geohash)
        - [(c) `GEOPOS`](#c-geopos)
        - [(d) `ZRANGE、ZREM等Sorted Set命令`](#d-zrangezrem%E7%AD%89sorted-set%E5%91%BD%E4%BB%A4)
    - [1.3 搜索Geospatial Objects](#13-%E6%90%9C%E7%B4%A2geospatial-objects)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3-2)
      - [(2) 命令](#2-%E5%91%BD%E4%BB%A4)
        - [(a) `GEODIST`](#a-geodist)
        - [(b) `GEORADIUS`](#b-georadius)
        - [(c) `GEORADIUSBYMEMBER`](#c-georadiusbymember)
      - [(3) 注意事项：限制返回结果和搜索半径](#3-%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9%E9%99%90%E5%88%B6%E8%BF%94%E5%9B%9E%E7%BB%93%E6%9E%9C%E5%92%8C%E6%90%9C%E7%B4%A2%E5%8D%8A%E5%BE%84)
    - [1.4 典型应用场景](#14-%E5%85%B8%E5%9E%8B%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF)
  - [2. 例子：体育馆（Venues）查找](#2-%E4%BE%8B%E5%AD%90%E4%BD%93%E8%82%B2%E9%A6%86venues%E6%9F%A5%E6%89%BE)
    - [2.1 介绍](#21-%E4%BB%8B%E7%BB%8D)
    - [2.2 根据位置查找](#22-%E6%A0%B9%E6%8D%AE%E4%BD%8D%E7%BD%AE%E6%9F%A5%E6%89%BE)
    - [2.3 查找指定距离内某场比赛的场馆](#23-%E6%9F%A5%E6%89%BE%E6%8C%87%E5%AE%9A%E8%B7%9D%E7%A6%BB%E5%86%85%E6%9F%90%E5%9C%BA%E6%AF%94%E8%B5%9B%E7%9A%84%E5%9C%BA%E9%A6%86)
    - [2.4 查找地铁线沿线的场馆](#24-%E6%9F%A5%E6%89%BE%E5%9C%B0%E9%93%81%E7%BA%BF%E6%B2%BF%E7%BA%BF%E7%9A%84%E5%9C%BA%E9%A6%86)
  - [3. 存储过程（Lua Script）](#3-%E5%AD%98%E5%82%A8%E8%BF%87%E7%A8%8Blua-script)
    - [3.1 介绍](#31-%E4%BB%8B%E7%BB%8D)
    - [3.2 运行Script](#32-%E8%BF%90%E8%A1%8Cscript)
      - [(1) 命令：`EVAL`](#1-%E5%91%BD%E4%BB%A4eval)
      - [(2) 在Lua Script中执行Redis命令：`redis.call(...)`，`redis.pcall(...)`](#2-%E5%9C%A8lua-script%E4%B8%AD%E6%89%A7%E8%A1%8Credis%E5%91%BD%E4%BB%A4rediscallredispcall)
      - [(3) 参数传递](#3-%E5%8F%82%E6%95%B0%E4%BC%A0%E9%80%92)
      - [(4) 原子属性](#4-%E5%8E%9F%E5%AD%90%E5%B1%9E%E6%80%A7)
      - [(5) Lua脚本的编写的注意事项](#5-lua%E8%84%9A%E6%9C%AC%E7%9A%84%E7%BC%96%E5%86%99%E7%9A%84%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9)
      - [(4) 数据类型映射](#4-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E6%98%A0%E5%B0%84)
      - [(5) Lua脚本返回结果给Redis客户端](#5-lua%E8%84%9A%E6%9C%AC%E8%BF%94%E5%9B%9E%E7%BB%93%E6%9E%9C%E7%BB%99redis%E5%AE%A2%E6%88%B7%E7%AB%AF)
    - [3.3 管理Script](#33-%E7%AE%A1%E7%90%86script)
      - [(1) `EVAL`执行过程](#1-eval%E6%89%A7%E8%A1%8C%E8%BF%87%E7%A8%8B)
      - [(2) 脚本管理命令：`SCRIPT LOAD/EVALSHA/EXISTS/FLUSH/KILL/DEBUG`](#2-%E8%84%9A%E6%9C%AC%E7%AE%A1%E7%90%86%E5%91%BD%E4%BB%A4script-loadevalshaexistsflushkilldebug)
      - [(3) Lua脚本原子性被破坏的问题](#3-lua%E8%84%9A%E6%9C%AC%E5%8E%9F%E5%AD%90%E6%80%A7%E8%A2%AB%E7%A0%B4%E5%9D%8F%E7%9A%84%E9%97%AE%E9%A2%98)
        - [(a) 脚本执行时间超过阈值时会打破原子性](#a-%E8%84%9A%E6%9C%AC%E6%89%A7%E8%A1%8C%E6%97%B6%E9%97%B4%E8%B6%85%E8%BF%87%E9%98%88%E5%80%BC%E6%97%B6%E4%BC%9A%E6%89%93%E7%A0%B4%E5%8E%9F%E5%AD%90%E6%80%A7)
        - [(b) 应对办法](#b-%E5%BA%94%E5%AF%B9%E5%8A%9E%E6%B3%95)
        - [(c) 脚本编写注意事项](#c-%E8%84%9A%E6%9C%AC%E7%BC%96%E5%86%99%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9)
    - [3.4 典型使用场景](#34-%E5%85%B8%E5%9E%8B%E4%BD%BF%E7%94%A8%E5%9C%BA%E6%99%AF)
  - [4. 例子：使用Lua Script操作票务库存](#4-%E4%BE%8B%E5%AD%90%E4%BD%BF%E7%94%A8lua-script%E6%93%8D%E4%BD%9C%E7%A5%A8%E5%8A%A1%E5%BA%93%E5%AD%98)
    - [4.1 使用Python管理Lua Script](#41-%E4%BD%BF%E7%94%A8python%E7%AE%A1%E7%90%86lua-script)
    - [4.2 功能和数据模型](#42-%E5%8A%9F%E8%83%BD%E5%92%8C%E6%95%B0%E6%8D%AE%E6%A8%A1%E5%9E%8B)
      - [(1) 功能](#1-%E5%8A%9F%E8%83%BD)
      - [(2) 数据模型](#2-%E6%95%B0%E6%8D%AE%E6%A8%A1%E5%9E%8B)
        - [(a) Purchase](#a-purchase)
        - [(b) Event](#b-event)
        - [(c) Ticket holds](#c-ticket-holds)
    - [4.3 代码](#43-%E4%BB%A3%E7%A0%81)
      - [(1) 购买流程](#1-%E8%B4%AD%E4%B9%B0%E6%B5%81%E7%A8%8B)
        - [(a) 用例描述](#a-%E7%94%A8%E4%BE%8B%E6%8F%8F%E8%BF%B0)
        - [(b) 状态转换](#b-%E7%8A%B6%E6%80%81%E8%BD%AC%E6%8D%A2)
        - [(c) Lua脚本](#c-lua%E8%84%9A%E6%9C%AC)
        - [(d) 测试](#d-%E6%B5%8B%E8%AF%95)
      - [(2) 锁定赛票](#2-%E9%94%81%E5%AE%9A%E8%B5%9B%E7%A5%A8)
        - [(a) Lua脚本](#a-lua%E8%84%9A%E6%9C%AC)
      - [(3) 完整购票流程](#3-%E5%AE%8C%E6%95%B4%E8%B4%AD%E7%A5%A8%E6%B5%81%E7%A8%8B)
    - [4.4 要点](#44-%E8%A6%81%E7%82%B9)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 地理位置和Lua Script

## 1. 地理位置（Geospacial）

### 1.1 介绍

#### (1) 文档

> [ Documentation for Geospatial commands at redis.io](https://redis.io/commands#geo)
> [ Wikipedia article on the Haversine formula](https://en.wikipedia.org/wiki/Haversine_formula)

#### (2) Geospacial

> 存储了地理位置的经度（Longitude）和维度（Latitude）
>
> 可以根据距离和半径查找

### 1.2 存储Geospatial

#### (1) 文档

> | **Wikipedia articles**                                       | **Geospatial Standards**                                     | **Documentation at redis.io**                |
> | ------------------------------------------------------------ | ------------------------------------------------------------ | -------------------------------------------- |
> | [Haversine Formula](https://en.wikipedia.org/wiki/Haversine_formula) | [EPSG:900913](https://epsg.io/900913)                        | [GEOADD](https://redis.io/commands/geoadd)   |
> | [Geo hash](https://en.wikipedia.org/wiki/Geohash)            | [EPSG:3785](http://spatialreference.org/ref/epsg/popular-visualisation-crs-mercator/) | [GEOHASH](https://redis.io/commands/geohash) |
> |                                                              |                                                              | [GEOPOS](https://redis.io/commands/geopos)   |

#### (2) Geo Hash

编码

> GeoHash由Gustavo Niemeyer创建、它将地理位置编码在一个由字符和数字组成的短字符串中
>
> 在Redis中，GeoHash
>
> * 由`字符和数字`组成的段字符串转换为52 bit Integer来保存，但可以相互转换
> * 根据经度维度计算而得到，为了与各类系统兼容，经度用X表示、维度用Y表示
>
> ~~~bash
> 127.0.0.1:6379> geoadd geopoints 139.640072 35.443311 "Yokohama Stadium"
> (integer) 1 
> 127.0.0.1:6379> geohash geopoints "Yokohama Stadium"
> 1) "xn739s6b380"    # 字符和数字组成的短字符串
> 127.0.0.1:6379> zscore geopoints "Yokohama Stadium"
> "4171216862175648"  # 底层是52 bit Integer
> ~~~

距离查询

> 通常GeoHash是一个11位的字符串，缩减字符串可以减少数据量，但是也会降低经度，例如
>
> * [http://geohash.org/xn77h1fbep0](http://geohash.org/xn77h1fbep0)
> * [http://geohash.org/xn77h1f](http://geohash.org/xn77h1f)
> * [http://geohash.org/xn7](http://geohash.org/xn7)
>
> 地址相近的GeoHash具有相同的前缀、因此可以用前缀匹配相近的地理位置

计算精度

> GeoHash假设地球是标准的半球（其实不是），将经纬度网格映射到标准半球上来计算距离。
>
> 在赤道区域误差只有60厘米，而在两极误差会显著增大，使其不适合用于靠近两极的地域。
>
> 因此GeoHash有对于经纬度的限制、具体是：经度`[-180, 180]`；维度`[-85.05112878, 85.05112879]`，超过该范围Redis命令会返回Fail

#### (3) 命令

##### (a) `GEOADD`

`GEOADD key [NX|XX] [CH] longitude latitude member [longitude latitude member ...]`

> 功能：将1至N个地理位置（格式为`经度 维度 member`）存储在底层是sorted set的数据结构中
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_geohash_geoadd.jpg" width="500" /></div>
>
> 说明：如上图、GEOADD命令
>
> * 根据`经度、维度`计算出来的52 bit integer GeoHash值被当做Sorted Set的浮点数Score
>
> * 参数`member`被当做Sorted Set的Member来存储
>
> * 因为底层使sorted set，因此可以使用sorted set的命令查询底层数据
>
> 例子：
>
> ~~~bash
> 127.0.0.1:6379> geoadd geopoints 139.75 35.69333 "Nippon Budokan"
> (integer) 1
> 127.0.0.1:6379> geoadd geopoints 139.76632 35.666754 "Olympic Stadium"
> (integer) 1
> 127.0.0.1:6379> geoadd geopoints 139.640072 35.443311 "Yokohama Stadium"
> (integer) 1
> 127.0.0.1:6379> zrange geopoints 0 -1 withscores
> 1) "Yokohama Stadium"
> 2) "4171216862175648"
> 3) "Olympic Stadium"
> 4) "4171232605494985"
> 5) "Nippon Budokan"
> ~~~
>
> 对于一个移动的物体，可以多次使用geoadd命令来更新它的地理位置，例如
>
> ~~~bash
> 127.0.0.1:6379> geoadd geopoints 139.75 35.69333 "My Car"
> (integer) 1
> 127.0.0.1:6379> geohash geopoints "My Car"
> 1) "xn77h1fbep0"
> 127.0.0.1:6379> geoadd geopoints 139.640072 35.443311 "My Car"
> (integer) 0
> 127.0.0.1:6379> geohash geopoints "My Car"
> 1) "xn739s6b380"
> ~~~

##### (b) `GEOHASH`

 `GEOHASH key member [member ...]`

> 根据member name返回对应地理位置的GEOHASH值
>
> ~~~bash
> 127.0.0.1:6379> zrange geopoints 0 0 withscores
> 1) "Yokohama Stadium"
> 2) "4171216862175648"
> 127.0.0.1:6379> geohash geopoints "Yokohama Stadium"
> 1) "xn739s6b380"
> 127.0.0.1:6379> zscore geopoints "Yokohama Stadium"
> "4171216862175648"
> ~~~

##### (c) `GEOPOS`

 `GEOPOS key member [member ...]`

> 根据member name返回对应地理位置的经度和维度
>
> ~~~bash
> 127.0.0.1:6379> geopos geopoints "Yokohama Stadium"
> 1) 1) "139.64007407426834106"
>    2) "35.44331140493625298"
> ~~~

##### (d) `ZRANGE、ZREM等Sorted Set命令`

> 存储GeoHash被用作Score存储在底层数据结构Sorted Set中，因此Sorted Set的命令都可以使用
>
> 但是要注意集合操作的聚合函数只能用MIN和MAX（例如`ZINTERSTORE AGGREGATE MIN`、`ZUNIONSTORE AGGREGATE MIN`等），不能使用SUM

### 1.3 搜索Geospatial Objects

#### (1) 文档

> - [ GEODIST](https://redis.io/commands/geodist)
> - [ GEORADIUS](https://redis.io/commands/georadius)
> - [ GEORADIUSBYMEMBER](https://redis.io/commands/georadiusbymember)

#### (2) 命令

##### (a) `GEODIST`

 ` GEODIST key member1 member2 [m|km|ft|mi]`

> 计算member1和member2的距离，默认的距离单位是米（`m`）
>
> 可以指定其他单位：`km`-千米；`ft`-英尺feet；`mi`-英里mile
>
> ~~~bash
> 127.0.0.1:6379> geodist geopoints "Yokohama Stadium" "Nippon Budokan"
> "29533.7542"
> 127.0.0.1:6379> geodist geopoints "Yokohama Stadium" "Nippon Budokan" mi
> "18.3515"
> 127.0.0.1:6379> geodist geopoints "Yokohama Stadium" "Nippon Budokan" m
> "29533.7542"
> ~~~

##### (b) `GEORADIUS`

 ` GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD][WITHDIST][WITHHASH][COUNT count][ASC|DESC][STORE key][STOREDIST key]`

> 查询某个`<经度、维度>`指定半径内的member，这些member需要被存储在同一个key的sorted set下
>
> * `m|km|ft|mi`：半径的单位可以指定为：`m`-米；`km`-千米；`ft`-英尺feet；`mi`-英里mile
> * `WITHCOORD`：返回结果中携带member的经度、维度
> * `WITHDIST`：返回结果中携带member到`<longitude, latitude>`的距离
> * `WITHHASH`：返回结果中携带member的GeoHash
> * `COUNT`：返回多少个member
> * `ASC|DESC`：ASC-按距离升序排序；DESC-按距离降序排序
> * `STORE key`：将查询出来的memeber name及其GeoHash一起写入到另一个sorted set中，这个sorted set同样可以被当做Geo Spaicial Sorted Set使用`GEO*`命令来查询
> * `STOREDIST key`：将查询出来的memeber name及距离一起写入到另一个sorted set中，因为存储的是距离而不是GeoHash，因此它只能当做普通的sorted set来使用
>
> ~~~bash
> 127.0.0.1:6379> georadius geopoints 139.818943 35.648532 30 km withdist
> 1) 1) "Olympic Stadium"
>    2) "5.1696"
> 2) 1) "Nippon Budokan"
>    2) "7.9771"
> 3) 1) "Yokohama Stadium"
>    2) "27.9832"
> 127.0.0.1:6379> georadius geopoints 139.818943 35.648532 30 km count 1 desc
> 1) "Yokohama Stadium" # 距离最远的   
> ~~~

##### (c) `GEORADIUSBYMEMBER`

`GEORADIUSBYMEMBER key member radius m|km|ft|mi [WITHCOORD][WITHDIST][WITHHASH][COUNT count][ASC | DESC][STORE key][STOREDEST key]`

> 与GEORADIUS相同，只是它是查询某一个member为中心（而不是以某个经纬度为中心）指定半径内member
>
> ~~~bash
> 127.0.0.1:6379> georadiusbymember geopoints "Yokohama Stadium" 17 mi withcoord
> 1) 1) "Yokohama Stadium" # 自身也会被返回
>    2) 1) "139.64007407426834106"
>       2) "35.44331140493625298"
> 2) 1) "Olympic Stadium"
>    2) 1) "139.76632028818130493"
>       2) "35.66675467929545817"
> 127.0.0.1:6379> geodist geopoints "Yokohama Stadium" "Nippon Budokan" mi
> "18.3515" # 另一个memeber因为距离大于17，没有被返回
> ~~~

#### (3) 注意事项：限制返回结果和搜索半径

> 1. 限制返回结果数量：任何返回大量结果的命令，都会带来大量的开销，因此需要用`COUNT`参数限制返回结果的数量
> 2. 限制搜索半径大小：即使限制了返回结果，大的搜索半径仍然可能导致大的latency

### 1.4 典型应用场景

场景1：Location based services

> 查找附近的酒店、咖啡厅、商场，或者几类场馆的组合

场景2：Location based Advertising Targeting

> 展示用户附近商家的优惠信息

Redis的优势：

> 1. real-time access
> 2. high change rates

## 2. 例子：体育馆（Venues）查找

> 代码：[https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc05-finding-venues/finding_venues.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc05-finding-venues/finding_venues.py)

### 2.1 介绍

Use Cases: 

> * 查找某个位置附近的场馆
> * 查找某个地铁站附近的场馆
> * 查找某个位置附近、举办某种比赛的场馆
> * 查找某条地铁沿线的场馆

### 2.2 根据位置查找

相关文档

> - [ GEOADD](https://redis.io/commands/geoadd)
> - [ GEORADIUS](https://redis.io/commands/georadius)
> - [ GEORADIUSBYMEMBER](https://redis.io/commands/georadiusbymember)

数据

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_geo_demo_data.jpg" width="400" /></div>

将场馆数据存入Redis

> ~~~python
> def create_venue(venue):
> 	"""Create key and geo entry for passed venue"""
> 	key = keynamehelper.create_key_name("geo", "venues")
> 	redis.geoadd(key, venue['geo']['long'], venue['geo']['lat'], venue['venue'])
> 
> def test_venue_search():
> 	"""Test 1 - geo searches around a venue"""
> 	print("\n==Test 1 - geo searches around a venue")
> 	create_venue(olympic_stadium)
> 	create_venue(nippon_budokan)
> 	create_venue(makuhari_messe)
> 	create_venue(saitama_super_arena)
> 	create_venue(international_stadium)
> 	create_venue(isc)
>     ...
> ~~~

搜索某个位置附近的场馆

> ~~~python
> geo_key = keynamehelper.create_key_name("geo", "venues")
> print(redis.georadius(geo_key, 139.771977, 35.668024, 5, "km", withdist=True))
> ~~~

搜索某个地铁站附近的其他场馆

> ~~~python
> 	print("== Find venues within 25km of 'Olympic Stadium'")
> 	print(redis.georadiusbymember(geo_key,"Olympic Stadium", 25, "km", withdist=True))
> ~~~

### 2.3 查找指定距离内某场比赛的场馆

相关文档

> - [ GEOADD](https://redis.io/commands/geoadd)
> - [ GEORADIUS](https://redis.io/commands/georadius)

新增数据

> 在上一章场馆的地理位置数据基础上，新增赛事数据，同一个比赛有可能在多个场馆巨型
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_geo_demo_event_data.jpg" width="400" /></div>

将数据存入Redis

> 比赛（event）用作key，存储这个event所有场馆的位置
>
> ~~~python
> def create_event_locations(venue):
> 	"""Create geo entry for venues"""
> 	p = redis.pipeline()
> 	for i in range(len(venue['events'])):
> 		event, _ = venue['events'][i]
> 		key = keynamehelper.create_key_name("geo", "events", event)
> 		p.geoadd(key, venue['geo']['long'], venue['geo']['lat'], venue['venue'])
> 	p.execute()
> def test_event_search():
> 	"""Test 2 - geo searches around events"""
> 	print("\n==Test 2 - geo searches around events")
> 	create_event_locations(olympic_stadium)
> 	create_event_locations(nippon_budokan)
> 	create_event_locations(makuhari_messe)
> 	create_event_locations(saitama_super_arena)
> 	create_event_locations(international_stadium)
> 	create_event_locations(isc)
>     ...
> ~~~

查找某个地理位置附近、指定比赛的场馆

> ~~~python
> 	print("== Find venues for 'Football' within 25km of 'Shin-Yokohama Station'")
> 	geo_key = keynamehelper.create_key_name("geo", "events", "Football")
> 	print(redis.georadius(geo_key, 139.606396, 35.509996, 25, "km", withdist=True))
> ~~~

### 2.4 查找地铁线沿线的场馆

相关文档

> - [ GEOADD](https://redis.io/commands/geoadd)
> - [ GEODIST](https://redis.io/commands/geordist)
> - [ GEORADIUS](https://redis.io/commands/georadius)
> - [ GEORADIUSBYMEMBER](https://redis.io/commands/georadiusbymember)

数据

> 将地铁线路作为后缀，以`geo:${subway_line}作为key创建sorted set
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_geo_demo_subway_stadium_data.jpg" width="400" /></div>

将数据存入Redis

> ~~~python
> def create_event_transit_locations(venue):
> 	"""Create geo entries for transit stops for the passed venue"""
> 	p = redis.pipeline()
> 	for i in range(len(venue['transit'])):
> 		key = keynamehelper.create_key_name("geo", "transits", venue['transit'][i])
> 		p.geoadd(key, venue['geo']['long'], venue['geo']['lat'], venue['venue'])
> 	p.execute()
> 
> def test_transit_search():
> 	"""Test 3 - geo searched around transit"""
> 	print("\n==Test 3 - geo searched around transit")
> 	create_event_transit_locations(olympic_stadium)
> 	create_event_transit_locations(nippon_budokan)
> 	create_event_transit_locations(makuhari_messe)
> 	create_event_transit_locations(saitama_super_arena)
> 	create_event_transit_locations(international_stadium)
> 	create_event_transit_locations(isc)
>     ...
> ~~~

查询

> ~~~python
> print("== Find venues 5km from 'Tokyo Station' on the 'Keiyo Line'")
> geo_key = keynamehelper.create_key_name("geo", "transits", "Keiyo Line")
> print(redis.georadius(geo_key, 139.771977, 35.668024, 5, "km", withdist=True))
> 
> print("""== Find the distance between 'Makuhari Messe' and 'Tokyo Tatsumi International Swimming Center' on the 'Keiyo Line'""")
> print(redis.geodist(geo_key, "Makuhari Messe", "Tokyo Tatsumi International Swimming Center", "km"))
> 
> print("== Find venues within 20km of 'Makuhari Messe' on the 'Keiyo Line'")
> # Note: This only works if the member we are search for is on the
> # "Keiyo Line". For example, "Olympic Statdium" is not
> # on the "Keiyo Line" so would return zero results.
> print(redis.georadiusbymember(geo_key, "Makuhari Messe", 20, "km", withdist=True))
> ~~~

## 3. Lua Script

### 3.1 介绍

Lua是一种脚本语言：运行速度快、轻量级、功能强大、容易与各种技术集成

Lua脚本可以用作Redis Server Side Script，用编程语言封装一组Redis命令（不同命令可以作用在不同key上）和计算逻辑，发送给Redis。Redis在服务端执行这些命令、省去了中间步骤中请求响应的网络延时，起到类似存储过程的作用

本章focus在

> * 如何调用Lua脚本
> * 如何向Lua脚本传递Keys和参数列表
> * 原子操作
> * 数据类型映射
> * Lua脚本管理

Lua性能高效功能强大，但是也要注意脚本内容，不当的脚本编写可能会影响所有正在访问Redis的客户端

### 3.2 运行Script

#### (1) 命令：`EVAL`

`EVAL script numkeys key [key ...] arg [arg ...]`

> `script`：将Lua脚本以String的形式传入
>
> `key [key ...]`：key列表
>
> `arg [arg ...]`：脚本参数列表

例子

> ~~~bash
> 127.0.0.1:6379> hset hash-key field1 hello field2 world
> (integer) 2
> 127.0.0.1:6379> eval "return redis.call('HGET', KEYS[1], ARGV[1])" 1 hash-key field2
> "world"
> ~~~

#### (2) 在Lua Script中执行Redis命令：`redis.call(...)`，`redis.pcall(...)`

> `redis.call(...)`：call函数调用的命令执行错误时，直接让EVAL命令失败
>
> `redis.pcall(...)`：pcall函数调用的命令错误时，返回一个error return，以便让Lua脚本以编程的方式继续处理这个错误信息

#### (3) 参数传递

命令`EVAL script numkeys key [key ...] arg [arg ...]`中的

>  `key [key ...]`：会被放入Lua脚本的`KEYS`数组中、
>
>  `arg [arg ...]`：会被放入Lua脚本的`ARGV`数组中

#### (4) 原子属性

> Redis把EVAL命令看做一个原子命令，不会与其他命令交错执行
>
> 但是也会有一些例外，将在后续小节中介绍

#### (5) Lua脚本的编写的注意事项

注意事项1：以参数的形式传入key和arg，不要写死在Lua脚本中

> 提高脚本的灵活性和可复用性
>
> EVAL命令知道脚本运行的上下文，使用参数传递，可以让调用方与Lua脚本解耦

注意事项2：Lua的数组下标从1开始

> Redis命令中的索引下标是从0开始计数的（例如ZRANGE命令）
>
> 而Lua脚本数组的下标是从1开始计数
>
> 代码编写时需要注意两者之间的转换

注意事项3：浮点数截断（保留精度需要使用String类型）

> Lua不区分Float和Integer，当脚本中的Lua Number传给`redis.call(...)`或`redis.pcall(...)`时，会被截断为整数，丢失精度
>
> 如果想保留精度，需要使用String类型

#### (4) 数据类型映射

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_lua_redis_datamapping.jpg" width="1024" /></div>

#### (5) Lua脚本返回结果给Redis客户端

> 返回Lua Number
>
> ~~~bash
> 127.0.0.1:6379> eval "return 42" 0
> (integer) 42 # 常量
> 127.0.0.1:6379> eval "local val=42 return val" 0
> (integer) 42 # 本地变量
> ~~~
>
> 对于String类型的`redis.call`的执行结果，脚本返回Lua String
>
> ~~~ bash
> 127.0.0.1:6379> set int-key 42
> OK
> 127.0.0.1:6379> eval "local val=redis.call('GET', KEYS[1]) return val" 1 int-key
> "42"
> 127.0.0.1:6379> get int-key
> "42"
> 127.0.0.1:6379> type int-key
> string
> 127.0.0.1:6379> object encoding int-key
> "int"
> ~~~
>
> 对于返回列表的`redis.call`的执行结果，脚本返回Lua Array
>
> ~~~bash
> 127.0.0.1:6379> HMGET hash-key field1 field2
> 1) "hello"
> 2) "world"
> 127.0.0.1:6379> eval "return redis.call('HMGET', KEYS[1], ARGV[1], ARGV[2])" 1 hash-key field1 field2
> 1) "hello"
> 2) "world"
> ~~~
>
> 返回Lua Table的情况
>
> ~~~bash
> # redis.call返回结果被转成Lua Table
> 127.0.0.1:6379> eval "return redis.call('HSCAN', KEYS[1], ARGV[1])" 1 hash-key 0
> 1) "0"
> 2) 1) "field1"
>    2) "hello"
>    3) "field2"
>    4) "world"
> 127.0.0.1:6379> hscan hash-key 0
> 1) "0"
> 2) 1) "field1"
>    2) "hello"
>    3) "field2"
>    4) "world"
> # 创建一个Lua Table并返回
> 127.0.0.1:6379> eval "return {KEYS[1], {ARGV[1], ARGV[2]}}" 1 hash-key field1 field2
> 1) "hash-key"
> 2) 1) "field1"
>    2) "field2"
> ~~~

### 3.3 管理Script

#### (1) `EVAL`执行过程

> Step 1：开启原子操作
>
> Step 2：编译Lua脚本
>
> Step 3：执行脚本
>
> Step 4：返回结果
>
> Step 5：缓存脚本编译结果

#### (2) 脚本管理命令：`SCRIPT LOAD/EVALSHA/EXISTS/FLUSH/KILL/DEBUG`

`SCRIPT LOAD script`

> 将Lua脚本发送给Redis，Redis将脚本编译结果缓存在服务器，并返回脚本的SHA签名

`EVALSHA sha1 numkeys key [key ...] arg [arg ...]`

> 与EVAL命令的功能和参数格式相似，但是它不需要上传Lua Script，而是直接传入脚本SHA签名，执行之前使用`SCRIPT LOAD`命令缓存在服务器上的脚本

例子

> ~~~bash
> 127.0.0.1:6379> script load "local val=redis.call('GET', KEYS[1]) return val"
> "dd47ad79bb7b6d7d2b8e0607c344d134412e84e0"
> 127.0.0.1:6379> evalsha "dd47ad79bb7b6d7d2b8e0607c344d134412e84e0" 1 int-key
> "42"
> ~~~

`SCRIPT EXISTS sha1 [sha1 ...]`

> 传入SHA1签名，检查对应的Lua Script在redis服务器上是否存在

` SCRIPT FLUSH [ASYNC|SYNC]`

> 删除redis server上缓存的所有脚本

`SCRIPT KILL`

> 删除当前正在阻塞执行的Lua Script（由EVAL或EVALSHA执行）

`SCRIPT DEBUG YES|SYBNC|NO` （不要在生产环境使用）

> 设置接下来脚本的运行方式，可以开启交互式DEBUG模式，但注意**不要在生产环境使用**，它会让redis不接收其他命令

#### (3) Lua脚本原子性被破坏的问题

##### (a) 脚本执行时间超过阈值时会打破原子性

> 执行Lua脚本，与普通Redis命令一样，会单独执行。执行期间其他命令被阻塞在队列中
>
> 但是特殊之处在于，对于Lua脚本Redis有`max execution threshold`的执行限制（默认5秒钟），超时时脚本执行会被暂时中断，切换给其他的redis命令。

##### (b) Redis提供的解决方法

> 1. 监控`long running script`并报警
> 2. 让Redis在Lua Script超时中断时，只执行管理命令（Administration Commands），不执行数据访问命令（例如Get命令等）。数据访问命令会返回Busy Response给调用它的客户端。
> 3. 对于脚本只包含读操作，执行超时的时候，可以使用`SCRIPT KILL`命令来结束这条命令
> 4. 如果脚本中有写命令，唯一的办法是使用`SHUTDOWN NOSAVE`，这条命令会关闭Redis，丢弃当前正在执行脚本的所有写入数据，以及上一次flush之后所有新的写入数据

##### (c) 脚本编写注意事项

> 1. 脚本要小：不仅是脚本的命令数量、还包括命令的时间复杂度和数据量
> 2. 脚本作为事务划分的边界（transactional boundary）来编写，而不是作为一段完整功能来编写
> 3. 充足的测试

### 3.4 典型使用场景

Limited Counters（例如实现Rate Limiter：控制限定时间内请求的数量）

> Rate Limiter会用到CAS（Compare And Set操作），Redis不支持，需要借助Lua Script来实现

## 4. 例子：使用Lua Script操作票务库存

### 4.1 使用Python管理Lua Script

代码：[https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc06-inventory-with-lua/intro.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc06-inventory-with-lua/intro.py)

功能：

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_lua_demo_script_mgn.jpg" width="500" /></div>

代码

> ~~~python
> # LUA脚本
> stats_script = """
> 	-- Convert arguments to numbers
> 	local k1 = redis.call('get', KEYS[1])
> 	local k2 = redis.call('get', KEYS[2])
> 
> 	if ARGV[1] == "sum" then
> 	  return k1 + k2
> 	elseif ARGV[1] == "max" then
> 	  return math.max(k1, k2)
> 	else
> 	  return nil
> 	end
> """
> 
> def main():
> 	from redisu.utils.clean import clean_keys
> 	# 连接Redis
> 	global redis
> 	redis = Redis(host=os.environ.get("REDIS_HOST", "localhost"),
> 				  port=os.environ.get("REDIS_PORT", 6379),
> 				  password=os.environ.get("REDIS_PASSWORD", None),
> 				  db=0, decode_responses=True)
> 
> 	# 创建测试数据
> 	clean_keys(redis, "hits")
> 	redis.set("hits:homepage", 2000)
> 	redis.set("hits:loginpage", 75)
>     
> 	# 创建脚本对象，是一个Callable Object
> 	stats = redis.register_script(stats_script)
> 
> 	# 调用脚本
> 	# 它会触发SCRIPT LOAD命令编译并且在服务器端保存脚本，
> 	# 并且记录SHA1签名在对象中，以用于下次调用
> 	total = stats(["hits:homepage", "hits:loginpage"], ["sum"])
> 	assert(total == 2075)
> 
> 	# 继续调用该脚本
> 	max = stats(["hits:homepage", "hits:loginpage"], ["max"])
> 	assert(max == 2000)
> 
> if __name__ == "__main__":
> 	main()
> ~~~

### 4.2 功能和数据模型

#### (1) 功能

> 使用Lua Script来改写之前的Ticket Inventory Tracking System，包括如下部分
>
> 1. 确定数据模型（modeling）：比赛、锁定赛票、购买过程
> 2. 使用Python管理Lua Script
> 3. 使用一个简单的Lua状态机实现购买流程、赛票锁定

#### (2) 数据模型

##### (a) Purchase

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_luademo_datamodel_purchase.jpg" width="380" /></div>
>
> 使用Redis Hash存储

##### (b) Event

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_luademo_model_event.jpg" width="380" /></div>
>
> 使用Redis Hash存储

##### (c) Ticket holds

每个Hold的详细信息存储

> 每个Hold信息的key的格式为：`hold:${customer_id}:${event_id}`，类型为Hash
>
> 存入其中的member分别是：`qty`（hold数量）；`state`（例如”HELD“，用于调试）
>
> 另外hold信息将被设置TTL（Time To Live）时间

Hold信息的Key存储

> 为每个event，创建一个SET，内存这个event所有Hold信息的Key
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_lua_demo_model_hold.jpg" width="380" /></div>

### 4.3 代码

> [https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc06-inventory-with-lua/inventory-lua.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc06-inventory-with-lua/inventory-lua.py)

#### (1) 购买流程

##### (a) 用例描述

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_luademo_purchase_flow_intro.jpg" width="500" /></div>

##### (b) 状态转换

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_luademo_purchase_flow_state_trans.jpg" width="500" /></div>

##### (c) Lua脚本

> 要点：简短、实现一个类似Redis命令的功能、用来简化应用代码、提供原子保证
>
> ~~~lua
> # Compare-and-set for the state of a ticket purchase. This effectively
> # implements a state machine using compare-and-set. This function
> # supports the following state changes.
> # RESERVE -> [AUTHORIZE, TIMEOUT]
> # AUTHORIZE -> [COMPLETE, TIMEOUT]
> #
> # KEYS[1] is a key of type Hash pointing to a purchase.
> # ARGV[1] is the newly-requested state.
> # ARGV[2] is the current timestamp.
> # Returns 1 if successful. Otherwise, return 0.
> update_purchase_state_script = """
> 	local current_state = redis.call('HGET', KEYS[1], 'state')
> 	local requested_state = ARGV[1]
> 
> 	if ((requested_state == 'AUTHORIZE' and current_state == 'RESERVE') or
> 		(requested_state == 'FAIL' and current_state == 'RESERVE') or
> 		(requested_state == 'FAIL' and current_state == 'AUTHORIZE') or
> 		(requested_state == 'COMPLETE' and current_state == 'AUTHORIZE')) then
> 		redis.call('HMSET', KEYS[1], 'state', requested_state, 'ts', ARGV[2])
> 		return 1
> 	else
> 		return 0
> 	end
> """
> ~~~

##### (d) 测试

> ```python
> def test_modify_purchase(self):
> 	quantity = 5
> 	# 创建测试数据
> 	event = self.redis.hgetall(self.event_key)
> 	customer = self.redis.hgetall(self.customer_key)
> 	purchase_key = self.create_purchase(customer, event, quantity)
> 
> 	# 创建脚本对象
> 	update_purchase_state = self.redis.register_script(update_purchase_state_script)
> 
> 	# 运行脚本
> 	# At first, the purchase is in a RESERVE state
> 	assert(self.redis.hget(purchase_key, 'state') == "RESERVE")
> 
> 	# Try to move the purchase to an invalid state
> 	assert(update_purchase_state([purchase_key], ["COMPLETE", int(time.time())]) == 0)
> 	assert(self.redis.hget(purchase_key, 'state') == "RESERVE")
> 
> 	# Try to move the purchase to a valid state
> 	assert(update_purchase_state([purchase_key], ["AUTHORIZE", int(time.time())]) == 1)
> 	assert(self.redis.hget(purchase_key, 'state') == "AUTHORIZE")
> 
> 	# Try to move the purchase to another valid state
> 	assert(update_purchase_state([purchase_key], ["COMPLETE", int(time.time())]) == 1)
> 	assert(self.redis.hget(purchase_key, 'state') == "COMPLETE")
> ```

#### (2) 锁定赛票

##### (a) Lua脚本

> ~~~lua
> # Request a hold on a number of tickets.
> #
> # KEYS[1] is a key of type Hash pointing to an event
> # ARGV[1] is the customer ID
> # ARGV[2] is the requested number of general admission tickets
> # ARGV[3] is the number of seconds to hold the tickets
> request_ticket_hold_v2_script = """
> 	--  获取event capacity、requested tickets、hold timeout，都转为number
> 	local event_capacity = tonumber(redis.call('HGET', KEYS[1], 'available:General'))
> 	local requested_tickets = tonumber(ARGV[2])
> 	local hold_timeout = tonumber(ARGV[3])
> 	
> 	-- 生成ticket_holds_key、格式为holds:event:${event_sku}
> 	-- 用来向SET中存入customer_hold_key
> 	local ticket_holds_key = 'holds:' .. KEYS[1]
> 
> 	-- 生成customer_hold_key、格式为holds:${customer_id}:event:${event_sku}
> 	-- 用来向Hash存储hold的具体信息（qty和state）
> 	local customer_hold_key = 'hold:' .. ARGV[1] .. ':' .. KEYS[1]
> 
> 	-- 考虑到之前的锁定会过期释放，需要更新tickedt hold数，并清理哪些过期的hold
> 	-- 这部分要放在Lua脚本中，保证他们在一个原子操作中完成
> 	local tickets_held = 0 
> 	-- * 遍历event下所有的customer_hold_key
> 	local hold_keys = redis.call('SMEMBERS', ticket_holds_key)
> 	for _,hold_key in ipairs(hold_keys) do
> 		local count = redis.call('HGET', hold_key, 'qty')
> 		if (count == nil) then
> 			-- 过期的customer_hold_key会被清理，返回nil
> 			-- 将其在ticket_holds_key中对应的member也删除
> 			redis.call('SREM', ticket_holds_key, hold_key)
> 		else
> 			-- 没过期的customer_hold_key，其qty依然有效
> 			tickets_held = tickets_held + count
> 		end
> 	end
> 
> 	-- 检查容量，存入hold信息或返回失败
> 	if (tickets_held + requested_tickets) <= event_capacity then
> 		redis.call("HMSET", customer_hold_key, 'qty', requested_tickets, 'state', 'HELD')
> 		redis.call("EXPIRE", customer_hold_key, hold_timeout)
> 		redis.call("SADD", ticket_holds_key, customer_hold_key)
> 		return 1
> 	else
> 		return requested_tickets
> 	end
> """
> ~~~

#### (3) 完整购票流程

> ~~~lua
> # KEYS[1] is a key of type Hash pointing to an event
> # ARGV[1] is the customer ID
> # ARGV[2] is the requested number of general admission tickets
> # ARGV[3] is the amount of time to extend the customer hold by
> prepare_purchase_script = """
> 	local customer_hold_key = 'hold:' .. ARGV[1] .. ':' .. KEYS[1]
> 	local requested_tickets = tonumber(ARGV[2])
> 
> 	local hold_qty = redis.call('HGET', customer_hold_key, 'qty')
> 	if (hold_qty == nil) then
> 		return 0
> 	elseif (requested_tickets == tonumber(hold_qty)) then
> 		redis.call('HSET', customer_hold_key, 'state', 'PREPARE')
> 		redis.call('EXPIRE', customer_hold_key, tonumber(ARGV[3]))
> 		return 1
> 	else
> 	  return 0
> 	end
> """
> 
> # KEYS[1] is a key of type Hash pointing to an event
> # KEYS[2] is a key of type Hash pointing to the purchase
> # ARGV[1] is the customer ID
> # ARGV[2] is the requested number of general admission tickets
> # ARGV[3] is the timestamp of the complete purchase
> complete_purchase_script = """
> 	local customer_hold_key = 'hold:' .. ARGV[1] .. ':' .. KEYS[1]
> 	local requested_tickets = tonumber(ARGV[2])
> 	local purchase_state = redis.call('HGET', KEYS[2], 'state')
> 
> 	local hold_qty = redis.call('HGET', customer_hold_key, 'qty')
> 	if (hold_qty == nil) then
> 		return 0
> 	elseif requested_tickets == tonumber(hold_qty) and
> 		   purchase_state == 'AUTHORIZE' then
> 		-- Decrement the number of available tickets
> 		redis.call('HINCRBY', KEYS[1], "available:General", -requested_tickets)
> 		-- Delete the customer hold key
> 		redis.call('DEL', customer_hold_key)
> 		-- Set the purchase to 'COMPLETE'
> 		redis.call('HMSET', KEYS[2], 'state', 'COMPLETE', 'ts', ARGV[3])
> 		return 1
> 	else
> 	  return 0
> 	end
> """
> ~~~

没有reservation超时的情况

> ```python
> def test_successful_purchase_flow(self):
> 	# 加载测试数据（event、customer）
> 	event = self.redis.hgetall(self.event_key)
> 	customer = self.redis.hgetall(self.customer_key)
> 
> 	# 初始化Lua脚本
> 	# * 上面介绍的两个Lua脚本
> 	update_purchase_state = self.redis.register_script(update_purchase_state_script)
> 	request_tickets = self.redis.register_script(request_ticket_hold_v2_script)    
> 	# * 表示用户即将开始锁定座位（存入的数据与request_tickets相似，但state是prepared）
> 	prepare_purchase = self.redis.register_script(prepare_purchase_script)
> 	# * 
> 	complete_purchase = self.redis.register_script(complete_purchase_script)
> 
> 	# 1. A user requests a number of seats. Internally,
> 	# we attempt to set aside the requested number of seats.
> 	# If successful, we create a purchase, whose initial state is RESERVED.
> 	if (request_tickets([self.event_key], [customer['id'], 5, 10]) == 1):
> 		purchase_key = self.create_purchase(customer, event, 5)
> 	else:
> 		print("Failed")
> 		assert(False)
> 
> 	# 2. The user enters payment info and authorizes us to charge
> 	# their credit card. First, we make sure we can complete the purchase
> 	# (i.e., that the user has done this within the requested time period.)
> 	if (prepare_purchase([self.event_key], [customer['id'], 5, 30]) == 1):
> 		update_purchase_state([purchase_key], ["AUTHORIZE", int(time.time())])
> 	else:
> 		# Inform the user that they did not complete the purchase in time.
> 		# Mark the purchase as failed.
> 		update_purchase_state([purchase_key], ["FAIL", int(time.time())])
> 
> 	# Now, if we can authorize the credit card, then we complete the purchase.
> 	if self.creditcard_auth(customer['id'], 500):
> 		success = complete_purchase([self.event_key, purchase_key], [customer['id'], 5, int(time.time())])
> 	else:
> 		update_purchase_state([purchase_key], ["FAIL", int(time.time())])
> 		success = 0
> 		# The purchase failed. Note that we don't have to explicity
> 		# return inventory to the event because it will be reclaimed automatically
> 		# when the ticket lease expires.
> 
> 	assert(success == 1)
> 	purchase = self.redis.hgetall(purchase_key)
> 	assert(purchase['state'] == "COMPLETE")
> ```

发生reservation超时的情况

> ```python
> def test_purchase_flow_with_timeout(self):
> 	# Get the event and customer and generate a purchase.
> 	event = self.redis.hgetall(self.event_key)
> 	customer = self.redis.hgetall(self.customer_key)
> 
> 	# Initialize request and update scripts
> 	request_tickets = self.redis.register_script(request_ticket_hold_v2_script)
> 	update_purchase_state = self.redis.register_script(update_purchase_state_script)
> 	complete_purchase = self.redis.register_script(complete_purchase_script)
> 
> 	# Request seats with a short timeout so that we can test
> 	# what happens when a user delays.
> 	if (request_tickets([self.event_key], [customer['id'], 5, 1]) == 1):
> 		purchase_key = self.create_purchase(customer, event, 5)
> 
> 	# Simulate the user taking longer than the timeout
> 	time.sleep(2)
> 
> 	# This should fail because we timed out.
> 	if (complete_purchase([self.event_key, purchase_key], [customer['id'], 5]) == 1):
> 		update_purchase_state([purchase_key], ["AUTHORIZE", int(time.time())])
> 	else:
> 		# Inform the user that they did not complete the purchase in time.
> 		# Mark the purchase as failed.
> 		update_purchase_state([purchase_key], ["FAIL", int(time.time())])
> 
> 	purchase = self.redis.hgetall(purchase_key)
> 	assert(purchase['state'] == "FAIL")
> ```

### 4.4 要点

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_luademo_reminds.jpg" width="350" /></div>

