<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [1. Capped Collection（固定集合）](#1-capped-collection%E5%9B%BA%E5%AE%9A%E9%9B%86%E5%90%88)
  - [1.1 Cardinality（基数）和Capped Collection](#11-cardinality%E5%9F%BA%E6%95%B0%E5%92%8Ccapped-collection)
    - [(1) 文档](#1-%E6%96%87%E6%A1%A3)
    - [(2) Cardinality](#2-cardinality)
    - [(3) Capped Collection使用场景](#3-capped-collection%E4%BD%BF%E7%94%A8%E5%9C%BA%E6%99%AF)
    - [(4) 实现Capped Collection特性](#4-%E5%AE%9E%E7%8E%B0capped-collection%E7%89%B9%E6%80%A7)
      - [a) LIST：`LTRIM`](#a-listltrim)
      - [b) SORTED SET：`ZREMRANGEBYRANK`](#b-sorted-setzremrangebyrank)
  - [1.2 关于Set和Sorted Set的集合操作](#12-%E5%85%B3%E4%BA%8Eset%E5%92%8Csorted-set%E7%9A%84%E9%9B%86%E5%90%88%E6%93%8D%E4%BD%9C)
    - [(1) 计算交集：`ZINTERSTORE`](#1-%E8%AE%A1%E7%AE%97%E4%BA%A4%E9%9B%86zinterstore)
      - [a) 计算多个SORTED SET的交集](#a-%E8%AE%A1%E7%AE%97%E5%A4%9A%E4%B8%AAsorted-set%E7%9A%84%E4%BA%A4%E9%9B%86)
      - [b) 计算SORTED SET和SET的交集](#b-%E8%AE%A1%E7%AE%97sorted-set%E5%92%8Cset%E7%9A%84%E4%BA%A4%E9%9B%86)
    - [(2) 计算并集](#2-%E8%AE%A1%E7%AE%97%E5%B9%B6%E9%9B%86)
      - [a) `SUNION`](#a-sunion)
      - [b) `SUNIONSTORE`和`ZUNIONSTORE`](#b-sunionstore%E5%92%8Czunionstore)
      - [c) 并集操作同样可以在SET和SORTED SET之间进行](#c-%E5%B9%B6%E9%9B%86%E6%93%8D%E4%BD%9C%E5%90%8C%E6%A0%B7%E5%8F%AF%E4%BB%A5%E5%9C%A8set%E5%92%8Csorted-set%E4%B9%8B%E9%97%B4%E8%BF%9B%E8%A1%8C)
- [2. 根据属性进行组合查询](#2-%E6%A0%B9%E6%8D%AE%E5%B1%9E%E6%80%A7%E8%BF%9B%E8%A1%8C%E7%BB%84%E5%90%88%E6%9F%A5%E8%AF%A2)
  - [2.1 背景](#21-%E8%83%8C%E6%99%AF)
    - [(1) 代码，环境初始化，相关文档](#1-%E4%BB%A3%E7%A0%81%E7%8E%AF%E5%A2%83%E5%88%9D%E5%A7%8B%E5%8C%96%E7%9B%B8%E5%85%B3%E6%96%87%E6%A1%A3)
    - [(2) Use Case](#2-use-case)
    - [(3) Faceted Search](#3-faceted-search)
    - [(4) 数据格式](#4-%E6%95%B0%E6%8D%AE%E6%A0%BC%E5%BC%8F)
  - [2.2 方法1：Object Inspection（受限于Key数量）](#22-%E6%96%B9%E6%B3%951object-inspection%E5%8F%97%E9%99%90%E4%BA%8Ekey%E6%95%B0%E9%87%8F)
  - [2.3 方法2：Set Intersection（受限于数据分布、以及属性数量）](#23-%E6%96%B9%E6%B3%952set-intersection%E5%8F%97%E9%99%90%E4%BA%8E%E6%95%B0%E6%8D%AE%E5%88%86%E5%B8%83%E4%BB%A5%E5%8F%8A%E5%B1%9E%E6%80%A7%E6%95%B0%E9%87%8F)
  - [2.4 方法3：Hashed Keys（仅受限于查询结果的数据量、但有索引维护开销）](#24-%E6%96%B9%E6%B3%953hashed-keys%E4%BB%85%E5%8F%97%E9%99%90%E4%BA%8E%E6%9F%A5%E8%AF%A2%E7%BB%93%E6%9E%9C%E7%9A%84%E6%95%B0%E6%8D%AE%E9%87%8F%E4%BD%86%E6%9C%89%E7%B4%A2%E5%BC%95%E7%BB%B4%E6%8A%A4%E5%BC%80%E9%94%80)
- [3. Redis命令的时间复杂度](#3-redis%E5%91%BD%E4%BB%A4%E7%9A%84%E6%97%B6%E9%97%B4%E5%A4%8D%E6%9D%82%E5%BA%A6)
      - [(1) `O(1)`复杂度的命令：`APPEND`,` EXISTS`,` GET`,` SET`,` HGET`,` LPUSH`,` RPOP`, ……](#1-o1%E5%A4%8D%E6%9D%82%E5%BA%A6%E7%9A%84%E5%91%BD%E4%BB%A4append-exists-get-set-hget-lpush-rpop-)
      - [(2) `DEL <key> [<key> ...]`在不同情况下复杂度不同的命令](#2-del-key-key-%E5%9C%A8%E4%B8%8D%E5%90%8C%E6%83%85%E5%86%B5%E4%B8%8B%E5%A4%8D%E6%9D%82%E5%BA%A6%E4%B8%8D%E5%90%8C%E7%9A%84%E5%91%BD%E4%BB%A4)
      - [(3) `UNLINK`可以异步删除，不会阻塞其他命令的执行](#3-unlink%E5%8F%AF%E4%BB%A5%E5%BC%82%E6%AD%A5%E5%88%A0%E9%99%A4%E4%B8%8D%E4%BC%9A%E9%98%BB%E5%A1%9E%E5%85%B6%E4%BB%96%E5%91%BD%E4%BB%A4%E7%9A%84%E6%89%A7%E8%A1%8C)
      - [(4) `SINTER`的复杂度是O(N*S)](#4-sinter%E7%9A%84%E5%A4%8D%E6%9D%82%E5%BA%A6%E6%98%AFons)
      - [(5) `LRANGE`的复杂度是O(S+N)](#5-lrange%E7%9A%84%E5%A4%8D%E6%9D%82%E5%BA%A6%E6%98%AFosn)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 1. Capped Collection（固定集合）

### 1.1 Cardinality（基数）和Capped Collection

#### (1) 文档

> [ Introduction to Capped Lists at redis.io](https://redis.io/topics/data-types-intro#capped-lists)

#### (2) Cardinality

查看一个Collection的的Cardinality

> `LLEN`：查看List的Cardinality
>
> `SCARD`：查看Set的Cardinality
>
> `ZCARD`：查看Sorted Set的Cardinality

#### (3) Capped Collection使用场景

> 关注集合中固定数量的元素，例如leader board中的top 3、信息流中最近的5条Post

#### (4) 实现Capped Collection特性

> 保持集合中的元素数量固定，可以防止元素数量膨胀，性能下降
>
> 下面的命令使得LIST和SORTED SET实现了Capped Collection特性，但要根据元素数量的大小来关注命令的执行开销

##### a) LIST：`LTRIM`

`LTRIM <key> <first_idx> <last_idx>`

> first_idx, last_idx可以为负数，表意方法与python数组下标相同
>
> * idx >= 0时，从左向右的计数，第一个元素下标是0
> * idx < 0时，从右向左计数，第一个元素下标是-1
>
> ~~~bash
> 127.0.0.1:6379> rpush list-one a b c d e f
> (integer) 6
> 127.0.0.1:6379> ltrim list-one 0 4
> OK
> 127.0.0.1:6379> lrange list-one 0 -1
> 1) "a"
> 2) "b"
> 3) "c"
> 4) "d"
> 5) "e"
> 127.0.0.1:6379> ltrim list-one 0 -2
> OK
> 127.0.0.1:6379> lrange list-one 0 -1
> 1) "a"
> 2) "b"
> 3) "c"
> 4) "d"
> 127.0.0.1:6379> llen list-one
> (integer) 4
> 127.0.0.1:6379> lpush list-one z
> (integer) 5
> 127.0.0.1:6379> ltrim list-one 0 3
> OK
> 127.0.0.1:6379> lrange list-one 0 -1
> 1) "z"
> 2) "a"
> 3) "b"
> 4) "c"
> ~~~

##### b) SORTED SET：`ZREMRANGEBYRANK`

`ZREMRANGEBYRANK <key> <first_rank_idx> <last_rank_idx>`

> first_rank_idx、last_rank_idx的表意方法与python数组相同，可参照上面的LTRIM命令
>
> 与LTRIM根据传入的index保留元素不同，ZREMRANGEBYRANK根据传入的index删除元素
>
> ~~~bash
> 127.0.0.1:6379> zadd set-one 1 a 2 b 3 c 4 d 5 e 6 f
> (integer) 6
> 127.0.0.1:6379> zremrangebyrank set-one 5 -1
> (integer) 1
> 127.0.0.1:6379> zrange set-one 0 -1
> 1) "a"
> 2) "b"
> 3) "c"
> 4) "d"
> 5) "e"
> 127.0.0.1:6379> zremrangebyrank set-one 4 -1
> (integer) 1
> 127.0.0.1:6379> zrange set-one 0 -1
> 1) "a"
> 2) "b"
> 3) "c"
> 4) "d"
> 127.0.0.1:6379> # 常见pattern，加入元素后，调整set大小
> 127.0.0.1:6379> zadd set-one 26 z 
> (integer) 1
> 127.0.0.1:6379> zremrangebyrank set-one 0 0 
> (integer) 1
> 127.0.0.1:6379> zrange set-one 0 -1
> 1) "b"
> 2) "c"
> 3) "d"
> 4) "z"
> ~~~

### 1.2 关于Set和Sorted Set的集合操作

#### (1) 计算交集：`ZINTERSTORE`

> 对于Sorted Set来说，因为两个集合中元素的score不同，需要决定交集中元素的score还如何计算

##### a) 计算多个SORTED SET的交集

`ZINTERSTORE <destination> <numkeys> <key> [<key> ...] WEIGHTS <weight> [<weight> ...] AGGREGATE SUM|MIN|MAX`

> destine: 存储结果的key
>
> numkeys：后面key的数量（每个key是一个sorted set）
>
> weights：每个input set的score因子，set的元素的score与这个因子相乘，用在命令的`aggregate`部分
>
> aggregate：指定如何生成ouput set中的score
>
> ~~~bash
> 127.0.0.1:6379> zadd sales:judo 1500 june 2000 bill 200 mary
> (integer) 3
> 127.0.0.1:6379> zadd sales:wrestling 1800 bill 1000 bob 800 mary
> (integer) 3
> 127.0.0.1:6379> zinterstore promo:taekwondo 2 sales:wrestling sales:judo aggregate sum
> (integer) 2
> 127.0.0.1:6379> zrange promo:taekwondo 0 -1 withscores
> 1) "mary"
> 2) "1000"
> 3) "bill"
> 4) "3800"
> ~~~

##### b) 计算SORTED SET和SET的交集

ZINTERSTORE可以同时作用在SORTED SET和SET上，但是SET中的元素没有score，需要在weights参数中为SET指定一个默认score

> 下面例子中
>
> * sales:wrestling的score权重：1.2
>
> * sales:judo的score权重：0.8
>
> * waitlist:taekwondo是SET、无法使用score权重，为它设置了默认score：1000
>
> ~~~bash
> 127.0.0.1:6379> sadd waitlist:taekwondo emma bill mary
> (integer) 3
> 127.0.0.1:6379> zinterstore promo:taekwondo 3 sales:wrestling sales:judo waitlist:taekwondo weights 1.2 0.8 1000 aggregate max
> (integer) 2
> 127.0.0.1:6379> zrange promo:taekwondo 0 -1 withscores
> 1) "mary"
> 2) "1000"
> 3) "bill"
> 4) "2160"
> ~~~

#### (2) 计算并集

##### a) `SUNION`

`SUNION <key> <key> [<key> ...]`

> ~~~bash
> 127.0.0.1:6379> sadd venues:Athletics "Imperial Place Garden" "Olympic Stadium"
> (integer) 2
> 127.0.0.1:6379> sadd venues:Judo "Tokyo Stadium"
> (integer) 1
> 127.0.0.1:6379> sadd venues:Footbasll "Nippon Budokan" "Sapporo Dome"
> (integer) 2
> 127.0.0.1:6379> sunion venues:Athletics venues:Judo venues:Footbasll
> 1) "Nippon Budokan"
> 2) "Tokyo Stadium"
> 3) "Imperial Place Garden"
> 4) "Olympic Stadium"
> 5) "Sapporo Dome"
> ~~~

##### b) `SUNIONSTORE`和`ZUNIONSTORE`

`ZUNIONSTORE <destination> <numkeys> <key> [<key> ...] WIGHTS <weight> [<weight> ...] ARRGREGATE SUM|MIN|MAX`

> 参数格式与ZUNIONINTERSECT相同
>
> ~~~bash
> 127.0.0.1:6379> zadd events:capacity 60102 "Closing Ceremony" 10000 "Table Tenis" 6000 "Skateboarding"
> (integer) 3
> 127.0.0.1:6379> zadd events:sales -200 "Table Tenis" -5900 "Skateboarding"
> (integer) 2
> 127.0.0.1:6379> zunionstore events:availability 2 events:capacity events:sales aggregate sum
> (integer) 3
> 127.0.0.1:6379> zrange events:availability 0 -1 withscores
> 1) "Skateboarding"
> 2) "100"
> 3) "Table Tenis"
> 4) "9800"
> 5) "Closing Ceremony"
> 6) "60102"
> ~~~

##### c) 并集操作同样可以在SET和SORTED SET之间进行

## 2. 根据属性进行组合查询

### 2.1 背景

#### (1) 代码，环境初始化，相关文档

> 代码：[https://github.com/fangkun119/ru101](https://github.com/fangkun119/ru101)
>
> 环境设置
>
> ```
> $ git clone https://github.com/fangkun119/ru101
> $ cd ru101
> $ export PYTHONPATH=`pwd`
> $ python3 -m venv venv
> $ . venv/bin/activate
> $ pip install -r requirements.txt
> ```
>
> Faceted Search和反向索引的WIKI文档
>
> - [ Faceted Search](https://en.wikipedia.org/wiki/Faceted_search)
> - [ Inverted Index](https://en.wikipedia.org/wiki/Inverted_index)
>
> 关于通配符
>
> * [ Wikipedia article on Glob style wildcards](https://en.wikipedia.org/wiki/Glob_(programming))

#### (2) Use Case

Use Case背景：使用Redis在一个比赛赛事系统中实现票务、赛事搜索等功能，其中会需要进行Attributed Search（根据属性搜索）

> 以赛事搜索为例、用户可能使用任何一个属性来所有赛事
>
> 1. disabled access available : yes / no
> 2. medal event : yes / no   
> 3. event venue : string

#### (3) Faceted Search

传统在RMDB中的方法：使用二级索引或者全文搜索引擎

> 会有维护开销、难以支持多种属性组合、数据一致性维护等问题

Faceted Search：借助反向索引（inverted index）使用多个filter和criteria来逐层分类导航，完成基于属性的组合查询

> 在这个例子中，Domain Object具有三个属性：(1) disabled access (2) venue (3) medal event
>
> Redis不支持复合索引也不支持二级索引，它使用反向索引加filter来实现

接下来介绍Faceted Search如何用在这个case上

#### (4) 数据格式

本小节先以String的形式存储赛事Object

> 例如
>
> ~~~json
> {'sku': "123-ABC-723", 'name': "Men's 100m Final", 'disabled_access': "True", 'medal_event': "True", 'venue': "Olympic Stadium", 'category': "Track & Field", 'capacity': 60102, 'available:General': 20000, 'price:General': 25.00 }
> ~~~

接下来介绍查询功能如何实现

### 2.2 方法1：Object Inspection（受限于Key数量）

扫描所有domain object并与query比较。这个方法效率非常低、受限于key的数量，不推荐用在大的数据量上

> ~~~bash
> 127.0.0.1:6379> scan 0 match event:*
> 1) "18"
> 2) 1) "event:123-ABC-723"
>    2) "event:737-DEF-911"
>    3) "event:320-GHI-921"
> 127.0.0.1:6379> get event:123-ABC-723
> "{\"sku\": \"123-ABC-723\", \"name\": \"Men\"s 100m Final\", \"disabled_access\": \"True\", \"medal_event\": \"True\", \"venue\": \"Olympic Stadium\", \"category\": \"Track & Field\", \"capacity\": 60102, \"available:General\": 20000, \"price:General\": 25.00 }"
> ~~~

Python代码：[https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc01-faceted-search/search.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc01-faceted-search/search.py)（test_object_inspection）

> ~~~python
> def match_by_inspection(*keys):
>   	"""Match Method 1 - Object inspection
>   	Find all matching keys, retreive value and then exeamine for all macthing
>   	attributes."""
>   	matches = []
>   	key = keynamehelper.create_key_name("event", "*")
>   	for key in redis.scan_iter(key):
>    		match = False
>    		event = json.loads(redis.get(key))
>    		for keyval in keys:
>    			key, val = keyval
>    			if key in event and event[key] == val:
>    				match = True
>    			else:
>    				match = False
>    				break
>    			if match:
>    				matches.append(event['sku'])
>   	return matches
> 
> def test_object_inspection():
>   	"""Test function for Method 1: Object Inspection"""
>   	print("\n== Method 1: Object Inspection")
>   	# Create events
>   	create_events(__events__)
> 
>   	# Find the match
>   	print("=== disabled_access=True")
>   	matches = match_by_inspection(('disabled_access', True))
>   	for match in matches:
>    		print_event_name(match)
> 
>   	print("=== disabled_access=True, medal_event=False")
>   	matches = match_by_inspection(('disabled_access', True), ('medal_event', False))
>    	for match in matches:
>   		print_event_name(match)
>    
> 	print("=== disabled_access=False, medal_event=False, venue='Nippon Budokan'")
>   	matches = match_by_inspection(('disabled_access', False), ('medal_event', False), ('venue', "Nippon Budokan"))
>   	for match in matches:
>    		print_event_name(match)
>    ~~~

运行

> ~~~bash
> == Method 1: Object Inspection
> === disabled_access=True
> Women's 4x100m Heats
> Men's 100m Final
> === disabled_access=True, medal_event=False
> Women's 4x100m Heats
> === disabled_access=False, medal_event=False, venue='Nippon Budokan'
> Womens Judo Qualifying
> ~~~

### 2.3 方法2：Set Intersection（受限于数据分布、以及属性数量）

建立`属性取值 → key`的反向索引，用反向索引查找候选key，再进行集合运算

时间复杂度：

> (1) 建立反向索引：O(N)、但是可以当做一次性开销
>
> (2) 查询反向索引：取决于属性值所能找到候选key的数量，受数据分布和查询条件的影响

用redis命令演示

> ~~~bash
> 127.0.0.1:6379> sadd fs:disabled_access:True 123-ABC-723 737-DEF-911
> (integer) 2
> 127.0.0.1:6379> sadd fs:disabled_access:False 320-GHI-921
> (integer) 1
> 127.0.0.1:6379> sadd fs:medal_event:True 123-ABC-723
> (integer) 1
> 127.0.0.1:6379> sadd fs:medal_event:False 737-DEF-911 320-GHI-921
> (integer) 2
> 127.0.0.1:6379> sadd fs:venue:"Olympic Stadium" 123-ABC-723 737-DEF-911
> (integer) 2
> 127.0.0.1:6379> sadd fs:venue:"Nippon Budokan" 320-GHI-921
> (integer) 1
> 127.0.0.1:6379> sinter fs:disabled_access:True fs:medal_event:False
> 1) "737-DEF-911"
> 127.0.0.1:6379> get event:737-DEF-911
> "{\"sku\": \"737-DEF-911\",\"name\": \"Women\"s 4x100m Heats\",\"disabled_access\": \"True\",\"medal_event\": \"False\",\"venue\": \"Olympic Stadium\",\"category\": \"Track & Field\",\"capacity\": 60102,\"available:General\": 10000,\"price:General\": 19.50}"
> ~~~

Python代码：[https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc01-faceted-search/search.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc01-faceted-search/search.py)

> 创建`属性取值 → key`的反向索引
>
> ~~~python
> def create_events_with_lookups(e_array):
>   	"""Match method 2 - Faceted Search
>    	For each attribute & value combination, add the event into a Set"""
>   	for i in range(len(e_array)):
>    		key = keynamehelper.create_key_name("event", e_array[i]['sku'])
>    		redis.set(key, json.dumps(e_array[i]))
>    		for k in range(len(__lookup_attrs__)):
>    			if __lookup_attrs__[k] in e_array[i]:
>    				attr_name = str(e_array[i][__lookup_attrs__[k]])
>    				fs_key = keynamehelper.create_key_name("fs", __lookup_attrs__[k], attr_name)
>    				redis.sadd(fs_key, e_array[i]['sku'])
>    ~~~
>    
> 根据查询条件查询反向索引，再做集合运算
>
> ~~~python
>def match_by_faceting(*keys):
> 	"""Use SINTER to find the matching elements"""
> 	facets = []
>   	for keyval in keys:
>   		key, val = keyval
>   		fs_key = keynamehelper.create_key_name("fs", key, str(val))
>    		facets.append(fs_key)
>    	return redis.sinter(facets)
>    
>   def test_faceted_search():
> 	"""Test function for Method 2: Faceted Search"""
> 	print("\n== Method 2: Faceted Search")
>   	# Create events
>   	create_events_with_lookups(__events__)
>   	# Find the match
>   	print("=== disabled_access=True")
>   	matches = match_by_faceting(('disabled_access', True))
>   	for match in matches:
>   		print_event_name(match)
>   	print("=== disabled_access=True, medal_event=False")
>    	matches = match_by_faceting(('disabled_access', True), ('medal_event', False))
> 	for match in matches:
>   		print_event_name(match)
>   
>   	print("=== disabled_access=False, medal_event=False, venue='Nippon Budokan'")
>    	matches = match_by_faceting(('disabled_access', False), ('medal_event', False), ('venue', "Nippon Budokan"))
> 	for match in matches:
>   		print_event_name(match)
>   ~~~

运行

> ~~~bash
> == Method 2: Faceted Search
> === disabled_access=True
> Men's 100m Final
> Women's 4x100m Heats
> === disabled_access=True, medal_event=False
> Women's 4x100m Heats
> === disabled_access=False, medal_event=False, venue='Nippon Budokan'
> Womens Judo Qualifying
> ~~~

### 2.4 方法3：Hashed Keys（仅受限于查询结果的数据量、但有索引维护开销）

> 方法2为每个`<attribute, value>`创建一个set，存入`attribute=value`的所有key。收到查询请求时、才去各个set查询并进行intersection运算。

方法3预先将所有查询要用到的查询条件（`<attribute, value>`组合）所对应的key都存储到set中。这个方法的时间复杂度仅受限于查询后返回结果的数据量，但是当object属性更新或者要支持新的查询结果时，都需要同步地更新这些索引

> ~~~bash
> 127.0.0.1:6379> sadd hfs:c32d9001b44dc00cc97829c0d62f12f9 123-ABC-723 737-DEF-911
> (integer) 2
> 127.0.0.1:6379> sadd hfs:1827539d6ca175cd045bc401c8985fbd 123-ABC-723
> (integer) 1
> ~~~
>
> 例如：上面的代码中
>
> * 第一个set key存储`disable_access=True`的key
>
> * 第二个set key存储`disable_access=True&&medal_event=False`的key
>
> key name就是查询条件的MD5或者SHA1值等

代码：[https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc01-faceted-search/search.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc01-faceted-search/search.py)

> ~~~python
> # Match method 3 - Hashed Faceted Search
> def create_events_hashed_lookups(e_array):
>   	"""Create hashed lookup for each event"""
>   	for i in range(len(e_array)):
>    		key = keynamehelper.create_key_name("event", e_array[i]['sku'])
>    		redis.set(key, json.dumps(e_array[i]))
>    		hfs = []
>    		for key in range(len(__lookup_attrs__)):
>    			if __lookup_attrs__[key] in e_array[i]:
>    				hfs.append((__lookup_attrs__[key], e_array[i][__lookup_attrs__[key]]))
>    			hashed_val = hashlib.sha256(str(hfs).encode('utf-8')).hexdigest()
>    			hfs_key = keynamehelper.create_key_name("hfs", hashed_val)
>    			redis.sadd(hfs_key, e_array[i]['sku'])
> 
> def match_by_hashed_faceting(*keys):
>   	"""Match method 3 - Hashed Faceted Search"""
>   	matches = []
>   	hfs = []
>   	for i in range(len(__lookup_attrs__)):
>    		key = [x for x in keys if x[0] == __lookup_attrs__[i]]
>    		if key:
>    			hfs.append(key[0])
>   	hashed_val = hashlib.sha256(str(hfs).encode('utf-8')).hexdigest()
>   	hashed_key = keynamehelper.create_key_name("hfs", hashed_val)
>   	for found_key in redis.sscan_iter(hashed_key):
>    		matches.append(found_key)
>   	return matches
> 
> def test_hashed_faceting():
>   	"""Test function for Method 3: Hashed Faceting"""
>   	print("\n== Method 3: Hashed Faceting")
>   	# Create events
>   	create_events_hashed_lookups(__events__)
>   	# Find the match
>   	print("=== disabled_access=True")
>   	matches = match_by_hashed_faceting(('disabled_access', True))
>   	for match in matches:
>    		print_event_name(match)
> 
>   	print("=== disabled_access=True, medal_event=False")
>   	matches = match_by_hashed_faceting(('disabled_access', True), ('medal_event', False))
>    	for match in matches:
>   		print_event_name(match)
>    
> 	print("=== disabled_access=False, medal_event=False, venue='Nippon Budokan'")
>   	matches = match_by_hashed_faceting(('disabled_access', False), ('medal_event', False), ('venue', "Nippon Budokan"))
>   	for match in matches:
>    		print_event_name(match)
>    ~~~

运行

> ~~~bash
> == Method 3: Hashed Faceting
> === disabled_access=True
> Men's 100m Final
> Women's 4x100m Heats
> === disabled_access=True, medal_event=False
> Women's 4x100m Heats
> === disabled_access=False, medal_event=False, venue='Nippon Budokan'
> Womens Judo Qualifying
> ~~~

## 3. Redis命令的时间复杂度

为了保证命令执行的原子性，Redis会串行化命令的执行，慢执行命令会阻塞其他命令

##### (1) `O(1)`复杂度的命令：`APPEND`,` EXISTS`,` GET`,` SET`,` HGET`,` LPUSH`,` RPOP`, ……

> 注意：O(1)不代表速度一定会很快，例如HGET从Hash中取出一个512MB的value，其他命令也类似

##### (2) `DEL <key> [<key> ...]`在不同情况下复杂度不同的命令

> * 删除每个String类型的key：O(1)
>
> * 删除每个List、Set、Sorted Set、Hash类型key：O(M)

##### (3) `UNLINK`可以异步删除，不会阻塞其他命令的执行

##### (4) `SINTER`的复杂度是O(N*S)

> * N是较小Set的Cardinality
> * S是集合的数量

##### (5) `LRANGE`的复杂度是O(S+N)

> * S是start_idx + 1的值
> * N是要返回的元素的数量（List使用双向链表实现）

