<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [BITMAP及消息发布订阅](#bitmap%E5%8F%8A%E6%B6%88%E6%81%AF%E5%8F%91%E5%B8%83%E8%AE%A2%E9%98%85)
  - [1. BITMAP](#1-bitmap)
    - [1.1 说明](#11-%E8%AF%B4%E6%98%8E)
      - [(1) 介绍](#1-%E4%BB%8B%E7%BB%8D)
      - [(2) 文档](#2-%E6%96%87%E6%A1%A3)
    - [1.2 BITFIELDS](#12-bitfields)
    - [1.3 BIT ARRAYS](#13-bit-arrays)
      - [(1) 介绍](#1-%E4%BB%8B%E7%BB%8D-1)
      - [(2) 命令](#2-%E5%91%BD%E4%BB%A4)
      - [(3) 例子](#3-%E4%BE%8B%E5%AD%90)
  - [2. 例子：座位预订](#2-%E4%BE%8B%E5%AD%90%E5%BA%A7%E4%BD%8D%E9%A2%84%E8%AE%A2)
    - [2.1 Use Case](#21-use-case)
    - [2.2 创建和获取Seat Map](#22-%E5%88%9B%E5%BB%BA%E5%92%8C%E8%8E%B7%E5%8F%96seat-map)
      - [(1) 创建：BITFIELD SET](#1-%E5%88%9B%E5%BB%BAbitfield-set)
      - [(2) 获取：BITFIELD GET](#2-%E8%8E%B7%E5%8F%96bitfield-get)
    - [2.3 查询可用座位](#23-%E6%9F%A5%E8%AF%A2%E5%8F%AF%E7%94%A8%E5%BA%A7%E4%BD%8D)
    - [2.4 座位锁定](#24-%E5%BA%A7%E4%BD%8D%E9%94%81%E5%AE%9A)
      - [(1) 方法：Bit异或 + 使用Latch Key来Improve Concurrency](#1-%E6%96%B9%E6%B3%95bit%E5%BC%82%E6%88%96--%E4%BD%BF%E7%94%A8latch-key%E6%9D%A5improve-concurrency)
      - [(2) 代码](#2-%E4%BB%A3%E7%A0%81)
  - [3. 消息发布订阅](#3-%E6%B6%88%E6%81%AF%E5%8F%91%E5%B8%83%E8%AE%A2%E9%98%85)
    - [3.1 介绍](#31-%E4%BB%8B%E7%BB%8D)
      - [(1) Redis发布订阅机制的特点](#1-redis%E5%8F%91%E5%B8%83%E8%AE%A2%E9%98%85%E6%9C%BA%E5%88%B6%E7%9A%84%E7%89%B9%E7%82%B9)
      - [(2) 文档](#2-%E6%96%87%E6%A1%A3-1)
      - [(3) 命令](#3-%E5%91%BD%E4%BB%A4)
        - [(a) 简单发布订阅（Simple Syndication）：`PUBLISH`/`SUBSCRIBE`/`UNSUBSCRIBE`](#a-%E7%AE%80%E5%8D%95%E5%8F%91%E5%B8%83%E8%AE%A2%E9%98%85simple-syndicationpublishsubscribeunsubscribe)
        - [(b) 模式发布订阅（Patterned Syndication）：`PSUBSCRIBE`/`PUNSUBSCRIBE`](#b-%E6%A8%A1%E5%BC%8F%E5%8F%91%E5%B8%83%E8%AE%A2%E9%98%85patterned-syndicationpsubscribepunsubscribe)
        - [(c) 管理（Admin）：`PUNSUB`](#c-%E7%AE%A1%E7%90%86adminpunsub)
    - [3.2 根据name匹配channel（Simple Publish  / Subscribe）](#32-%E6%A0%B9%E6%8D%AEname%E5%8C%B9%E9%85%8Dchannelsimple-publish---subscribe)
      - [(1) single channel](#1-single-channel)
      - [(2) multiple channels](#2-multiple-channels)
    - [3.3 根据name pattern匹配channel（Patterned Publish  / Subscribe）](#33-%E6%A0%B9%E6%8D%AEname-pattern%E5%8C%B9%E9%85%8Dchannelpatterned-publish---subscribe)
      - [(1) 文档](#1-%E6%96%87%E6%A1%A3)
      - [(2) 方法](#2-%E6%96%B9%E6%B3%95)
      - [(3) 命令](#3-%E5%91%BD%E4%BB%A4-1)
        - [(a) `PUBSUB subcommand [argument [argument ...]]`](#a-pubsub-subcommand-argument-argument-)
        - [(b) `PSUBSCRIBE pattern [pattern ...]`](#b-psubscribe-pattern-pattern-)
        - [(c) `PUNSUBSCRIBE pattern [pattern ...]`](#c-punsubscribe-pattern-pattern-)
  - [4. 例子：事件通知 & Fan Out](#4-%E4%BE%8B%E5%AD%90%E4%BA%8B%E4%BB%B6%E9%80%9A%E7%9F%A5--fan-out)
    - [4.1 介绍](#41-%E4%BB%8B%E7%BB%8D)
    - [4.2 例子1：实时销售分析](#42-%E4%BE%8B%E5%AD%901%E5%AE%9E%E6%97%B6%E9%94%80%E5%94%AE%E5%88%86%E6%9E%90)
    - [4.3 Filtered Events](#43-filtered-events)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# BIT MAP及消息发布订阅

## 1. BIT MAP

### 1.1 说明

#### (1) 介绍

> Redis提供BITMAP相关的操作，但是并没有专用的数据类型
>
> BITMAP操作直接作用在编码为`raw`的`String`类型上
>
> 例子如下：
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_01/
> $ redis-cli
> 127.0.0.1:6379> bitfield mykey INCRBY u8 0 42
> 1) (integer) 42
> 127.0.0.1:6379> del mykey
> (integer) 1
> 127.0.0.1:6379> bitfield mykey SET u8 0 42
> 1) (integer) 0
> 127.0.0.1:6379> bitfield mykey GET u8 0
> 1) (integer) 42
> 127.0.0.1:6379> bitfield mykey INCRBY u8 0 1
> 1) (integer) 43
> 127.0.0.1:6379> type mykey
> string
> 127.0.0.1:6379> object encoding mykey
> "raw"
> ~~~

#### (2) 文档

> [ Wikipedia article on Bit Fields](https://en.wikipedia.org/wiki/Bit_field)
>
> Documentation at redis.io
>
> - [ BITCOUNT](https://redis.io/commands/bitcount)
> - [ BITOP](https://redis.io/commands/bitop)
> - [ BITPOS](https://redis.io/commands/bitpos)
> - [ BITFIELD](https://redis.io/commands/bitfield)

### 1.2 BITFIELDS

命令格式

> ~~~bash
> bitfield key 
> 	[GET type offset] 
> 	[SET type offset value] 
> 	[INCRBY type offset increment]
> 	[OVERFLOW WRAP|SAT|FAIL]
> ~~~

说明

> `type`：例如"u4"表示4 bits无符号整数、"i11"表示11 bits有符号整数、……
>
> * 指定是无符号数（用“u”表示）、还是有符号数（用“i”表示）
> * 指定每个field的bit数，对无符号数最多64bits，对有符号数最多指定63bits
>
> `offset`：是每个field的bit偏移量，下图为例3个field的偏移量分别是0、8、16 （type是“u8”）
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_bitfield_offset.jpg" width="300" /></div>
>
> 这样的设计，对于定长字段数组应用（例如用一组计数器实现histogram）会非常有帮助
>
> `overflow`：当设置的value超过type导致溢出时的处理方法

例子：直接传入要操作的field的offset

> ~~~bash
> 127.0.0.1:6379> bitfield bf1 set u8 0 2
> 1) (integer) 0
> 127.0.0.1:6379> bitfield bf1 get u8 0
> 1) (integer) 2
> 127.0.0.1:6379> get bf1
> "\x02"
> ~~~

例子：用"#"符号传入要操作的field的index（从0开始计数、order最大的field序号为0）

> ~~~bash
> 127.0.0.1:6379> bitfield bf2 set u8 #1 5
> 1) (integer) 0
> 127.0.0.1:6379> bitfield bf2 get u8 #1
> 1) (integer) 5
> 127.0.0.1:6379> bitfield bf2 get u8 8
> 1) (integer) 5
> 127.0.0.1:6379> bitfield bf2 get u8 #1 get u8 8
> 1) (integer) 5
> 2) (integer) 5
> 127.0.0.1:6379> get bf2
> "\x00\x05"
> ~~~

使用bitfields表示histogram值

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_bitfield_as_histogram.jpg" width="400" /></div>

### 1.3 BIT ARRAYS

#### (1) 介绍

> 容许通过offset来操作每个单独的bit
>
> 其中的offset：
>
> * 从0开始计数、order最高的bit为0
> * 最大值取决于String的最大体积（目前版本512MB）
>
> 例如可以用于检查权限等（使用逻辑与操作）

#### (2) 命令

> `GETBIT key offset`：获取某个BIT，使用type为"u1"的BITFIELD命令可以做到同样的事情
>
> `SETBIT key offset value`：设置某个BIT，使用type为“u1”的BITFIELD命令可以做到同样的事情
>
> `BITCOUNT key [first_byte_offset last_byte_offset]`：查询range内设为1的bit数
>
> 注意
>
> * offet的单位是byte（不是bit）
> * 从0开始计数，与python数组索引一样支持负值
> * last_byte_offset也包含在内（是闭区间）
>
> `BITOP operation destkey key [key ...]`：执行bit set操作，包括AND、OR、XOR、NOT
>
> `BITPOS key bit [start] [end]`：返回第一个set或unset的bit的offset

#### (3) 例子

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_bitarrays_op_example_1.jpg" width="350" /></div>
>
> ~~~bash
> 127.0.0.1:6379> bitfield ba1 set u1 6 1
> 1) (integer) 0
> 127.0.0.1:6379> get ba1
> "\x02"
> 127.0.0.1:6379> bitcount ba1
> (integer) 1
> 127.0.0.1:6379> bitfield ba2 set u1 7 1
> 1) (integer) 0
> 127.0.0.1:6379> bitop or ba3 ba1 ba2
> (integer) 1
> 127.0.0.1:6379> bitcount ba3
> (integer) 2
> ~~~
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_bitarrays_op_example_2_2.jpg" width="380" /></div>
>
> ~~~bash
> 127.0.0.1:6379> bitfield ba4 set u1 7 1 set u1 15 1 set u1 23 1
> 1) (integer) 0
> 2) (integer) 0
> 3) (integer) 0
> 127.0.0.1:6379> bitcount ba4
> (integer) 3
> 127.0.0.1:6379> bitcount ba4 1 2
> (integer) 2
> 127.0.0.1:6379> bitcount ba4 0 -2
> (integer) 2
> ~~~

## 2. 例子：座位预订

> 代码：[https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc03-seat-reservation/seat_reservation.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc03-seat-reservation/seat_reservation.py)

### 2.1 Use Case

> 1. 为每场比赛维护一个Seat Map
> 2. Customer可以根据查询条件（例如5个连续的座位）搜索到符合的Seat
> 3. 预订seat，每个seat只能被预订一次（需要有concurrency机制保护）

### 2.2 创建和获取Seat Map

#### (1) 创建：BITFIELD SET

每场`<比赛、票区>`的座位都由多个block组成

> * 每个block默认有32个座位
> * block命名规则为“A、B、……、AA、AB、……”

代码

> ~~~python
> def create_event(event_sku, blocks=2, seats_per_block=32, tier="General"):
> 	"""Create the seats blocks for the given event. 32 bits are available for
> seats. This could be extended to accommodate more bits, by storing multiple
> u32 fields."""
> 	block_name = "A"
> 	for _ in range(blocks):
> 		filled_seat_map
> 			= int(math.pow(2, min(seats_per_block, __max__seats_per_block__))) - 1
> 		vals = ["SET", "u32", 0, filled_seat_map]
> 		key = keynamehelper.create_key_name("seatmap", event_sku, tier, block_name)
> 		redis.execute_command("BITFIELD", key, *vals)
> 		block_name = textincr.incr_str(block_name)
> ~~~

以上面blocks=2，seats_per_block=32，tier=“General”为例，代码将使用如下命令，创建BITFIELDS

> `BITFIELD <key> SET u32 0 4294967295`

代码创建出的seat map如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_seat_map_1.jpg" width="500" /></div>
>
> 其中1表示还有位置，0表示该位置已经被占用了

#### (2) 获取：BITFIELD GET

步骤1：使用`BITFIELD <key> GET ...`命令获取`u32`类型的field

> ~~~python
> def get_event_seat_block(event_sku, tier, block_name):
> 	"""For the given Event, Tier and Block, return the seat map"""
> 	vals = ["GET", "u32", 0]
> 	key = keynamehelper.create_key_name("seatmap", event_sku, tier, block_name)
> 	return  redis.execute_command("BITFIELD", key, *vals)[0]
> ~~~
>
> 例如上面的代码，使用`BITFIELD <event_tier_block_key> GET u32 0`命令返回指定BLOCK的BITFIELDS。但是为了检查具体的座位，调用这个方法之后，需要进一步解析其中的bit为

步骤2：解析返回的field，检查具体的bit位

> ~~~python
> def print_event_seat_map(event_sku, tier="*"):
> 	"""Format the seat map for display purposes."""
> 	# 使用了通配符，例如seatmap:123-ABC-723:General:*或者seatmap:123-ABC-723:*:*
> 	key_pattern = keynamehelper.create_key_name("seatmap", event_sku, tier, "*")
> 	# 根据通配符扫描得到符合条件的所有key
> 	for block_key in redis.scan_iter(key_pattern):
> 		# 从key中解析出tier_name和block_name
> 		(_, tier_name, block_name) = block_key.rsplit(":", 2)
> 		# 调用步骤1的函数，得到field
> 		seat_map = get_event_seat_block(event_sku, tier_name, block_name)
> 		print(("{:40s} ").format(block), end=' ')
> 		# 遍历field中的比特位，检查每个bit对应的座位是否被占用
> 		for i in range(seat_map.bit_length()):
> 			if (i % 10) == 0:
> 				print("|", end=' ')
> 			print((seat_map >> i) & 1, end=' ')
> 		print("|") 
> ~~~

测试

> ~~~python
> def test_create_seat_map():
> 	"""Part One - Create the event map"""
> 	print("\n==Test - Create & Print seat map")
> 	print("== Create two blocks of 10 seats")
> 	event = "123-ABC-723"
> 	seats = 10
> 	create_event(event, seats_per_block=seats)
> 	print_event_seat_map(event)
> ~~~

### 2.3 查询可用座位

> 出于简单的目的，只考虑查询连续座位的场景

用掩码检查block中是否有指定数量的座位

> ~~~python
> # seat_map		：是根据event_sku、tier、block_name获得的u32类型的bit field
> # seats_required：需要的连续座位数量
> def get_available(seat_map, seats_required):
> 	"""Return the available contiguous seats that match the criteria"""
> 	seats = []
> 	end_seat = seat_map.bit_length()+1
> 	if seats_required <= end_seat:
> 		# 用座位数量掩码和位置0生成的初始掩码
> 		required_block = int(math.pow(2, seats_required))-1
> 		# 遍历block中所有的位置
> 		for i in range(1, end_seat+1):
> 			# 用掩码检查座位数量是否满足
> 			if (seat_map & required_block) == required_block:
> 				seats.append({'first_seat': i, 'last_seat': i + seats_required -1})
> 			# 更新掩码
> 			required_block = required_block << 1
> 	# 返回找到的位置
> 	return seats
> ~~~

查询redis获取block，检查并返回符合数量要求的空位

> ~~~python
> def find_seat_selection(event_sku, tier, seats_required):
> 	"""Find seats ranges that meet the criteria"""
> 	seats = []
> 	# 通配符，例如：seatmap:123-ABC-723:General:*或者seatmap:123-ABC-723:*:*
> 	key_pattern = keynamehelper.create_key_name("seatmap", event_sku, tier, "*")
> 	# 符合通配符的所有block的key
> 	for block_key in redis.scan_iter(key_pattern):
> 		# 使用bitcount命令检查空闲位置（值为1的bit）的数量是否够用
> 		if redis.bitcount(block_key) >= seats_required:
> 			# 提取tier_name和block_name
> 			(_, tier_name, block_name) = block.rsplit(":", 2)
> 			# 调用上一小节的函数，从redis中获取某个block的bit map field
> 			seat_map = get_event_seat_block(event_sku, tier_name, block_name)
> 			# 调用上面的函数，检查bit map field是否能找到连续seats_required个空位
> 			block_availability = get_available(seat_map, seats_required)
> 			if len(block_availability) > 0:
> 				seats.append({'event': event_sku, 'tier' : tier_name, 'block': block_name, 'available': block_availability})
> 		else:
> 			print("Row '{}' does not have enough seats".format(block))
> 	return seats
> ~~~

测试

> ~~~python
> def print_seat_availabiliy(seats):
> 	"""Print out the seat availbaility"""
> 	for block in seats:
> 		print("Event: {}".format(block['event']))
> 		current_block = block['available']
> 		for i in range(len(current_block)):
> 			print("-Row: {}, Start {}, End {}".format(
> 				block['block'], current_block[i]['first_seat'], current_block[i]['last_seat'],))
> 
> def set_seat_map(event_sku, tier, block_name, seat_map):
> 	""" Set the seatmap to the given value"""
> 	vals = ["SET", "u32", 0, seat_map]
> 	key = keynamehelper.create_key_name("seatmap", event_sku, tier, block_name)
> 	redis.execute_command("BITFIELD", key, *vals)
> 
> def test_find_seats():
> 	""" Test function to find various combinations of seats."""
> 	print("\n==Test - Find Seats")
> 	event = "123-ABC-723"
> 	seats = 10
> 	create_event(event, seats_per_block=seats)
> 
> 	print("== Find 6 contiguous available seats")
> 	available_seats = find_seat_selection(event, "General", 6)
> 	print_seat_availabiliy(available_seats)
> 
> 	# Check that we skip rows
> 	print("""== Remove a 4 seat from Block A, so only Block B has the right
>  availability for 6 seats""")
> 	# Unset bits 2-5
> 	set_seat_map(event, "General", "A", int(math.pow(2, seats) - 31))
> 	print_event_seat_map(event)
> 	available_seats = find_seat_selection(event, "General", 6)
> 	print_seat_availabiliy(available_seats)
> ~~~

### 2.4 座位锁定

#### (1) 方法：Bit异或 + 使用Latch Key来Improve Concurrency

> `BITOP operation destkey key [key ...]`：执行bit set操作，包括AND、OR、XOR、NOT
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisds_seat_reverse_approach.jpg" width="380" /></div>

但是为了阻止其他请求预订相同的位置，需要添加一个座位级别的Latch（门锁）

#### (2) 代码

> ~~~python
> # Part Two - reserve seats
> class Error(Exception):
> 	"""Base class for exceptions in this module."""
> 	pass
> 
> class SeatTaken(Error):
> 	"""Expception if a seat is taken during the reservation process."""
> 	def __init__(self, expression, message):
> 		super(SeatTaken, self).__init__()
> 		self.expression = expression
> 		self.message = message
> 
> def reservation(event_sku, tier, block_name, first_seat, last_seat):
> 	""" Reserve the required seats. Create an expiring key (i.e. a latch) to reserve each seat. If that is successful, then an XOR can be executed to update the seat map, without needed a Watch."""
> 	reserved = False
> 	# 创建Redis管道
> 	p = redis.pipeline()
> 	try:
> 		# 设置短时Latch(门锁)、阻止并发请求预订相同的座位
> 		for i in range(first_seat, last_seat+1):
> 			# key format: seatres:<sku>:<tier>:<block_name>:<seat_idx>
> 			# NX=true：当该预订信息已经存在时（说明已经被预订）会返回false
> 			# PX=5000：设置TTL（Time To Live），使这个门锁可以自动过期被清理
> 			seat_key = keynamehelper.create_key_name("seatres", event_sku, tier, block_name, str(i))
> 			if redis.set(seat_key, "True", px=5000, nx=True) != True:
> 				raise SeatTaken(i, seat_key)
> 
> 		# 存入预订信息
> 		# key format: <sku>:<tier>:<block_name>:<order_id>
> 		order_id = generate.order_id()
> 		res_key = keynamehelper.create_key_name("seatres", event_sku, tier, block_name, order_id)
> 		# mask code of seats to reserve 
> 		required_block = int(math.pow(2, last_seat-first_seat +1)) -1 << (first_seat-1)
> 		vals = ["SET", "u32", 0, required_block]		
> 		# bitfield <sku>:<tier>:<block_name>:<order_id> SET u30 0 <seat_mask>
> 		p.execute_command("BITFIELD", res_key, *vals)
> 		# expire after 5 sedonds
> 		p.expire(res_key, 5)
> 
> 		# 更新Seat Map写入座位锁定信息
> 		block_key = keynamehelper.create_key_name("seatmap", event_sku, tier, block_name)
> 		p.bitop("XOR", block_key, block_key, res_key)
> 		p.execute()
> 		reserved = True
> 	except SeatTaken as error:
> 		print("Seat Taken/{}".format(error.message))
> 	finally:
> 		p.reset()
> 	return reserved
> ~~~

## 3. 消息发布订阅

### 3.1 介绍

#### (1) Redis发布订阅机制的特点

Redis可以用来实现简单的消息总线，把Redis用作Broker，让多个客户端进行消息订阅和发布

> 发送的数据类型是Redis String

包含两种模式

> 根据channel name订阅和发布
>
> 根据channel name的pattern订阅和发布

它只是一个简单的消息发布订阅机制

> * 单节点的Redis可以保证消息顺序（Cluster不能保证）
> * 不保证订阅之前的消息可以被收到

影响性能的因素

> * `Payload Size`
>
> * `Number of connected subscribers`
>
>     对于一条message，每个订阅者都会收到一份拷贝，也就是说100个订阅者会导致每条消息在网络上被传输100份，在设计时要考虑这个因素
>
> * `Number of patterns`
>
>     channel需要和每一个pattern进行比较、来确定消息是否可以发给某个Patterned Subscriber。因此Patterned Subscriber越多，处理开销和延时越大。

#### (2) 文档

> - [ Overview of Publish / Subscribe](https://redis.io/topics/pubsub)
> - [ Documentation for Publish / Subscribe commands](https://redis.io/commands#pubsub)

#### (3) 命令

##### (a) 简单发布订阅（Simple Syndication）：`PUBLISH`/`SUBSCRIBE`/`UNSUBSCRIBE`

> `PUBLISH channel message`
>
> `SUBSCRIBE channel [channel ...]`：订阅channel，不支持通配符，订阅之后会阻塞（但可以响应其他命令、例如UNSUCRIBE）
>
> `UNSCRIBE [channel [channel ...]]`

##### (b) 模式发布订阅（Patterned Syndication）：`PSUBSCRIBE`/`PUNSUBSCRIBE`

> `PSUBSCRIBE pattern [pattern ...]`
>
> `PUNSUBCRIBE [pattern [pattern ...]]`

##### (c) 管理（Admin）：`PUNSUB`

> `PUBSUB subcommand [argument [argument ...]]`

### 3.2 根据name匹配channel（Simple Publish  / Subscribe）

#### (1) single channel

> client 1 订阅 ch-1
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_01/
> $ redis-cli
> 127.0.0.1:6379> subscribe ch-1
> Reading messages... (press Ctrl-C to quit)
> 1) "subscribe" # 表示消息订阅成功
> 2) "ch-1"      # 订阅了哪个channel
> 3) (integer) 1 # client当前订阅的channel数
> ~~~
>
> client 2 发布消息到 ch-1
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_02/
> $ redis-cli
> 127.0.0.1:6379> publish ch-1 hello
> (integer) 1
> ~~~
>
> client 1 收到消息
>
> ~~~bash
> 1) "message" # 表示当前接收的payload是message payload，这个字段还可以是subscribe和unsubscribe
> 2) "ch-1"    # 来自哪个channel
> 3) "hello"   # 消息内容
> ~~~

#### (2) multiple channels

> client 1订阅ch-1，ch-2
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_01/
> $ redis-cli
> 127.0.0.1:6379> subscribe ch-1 ch-2
> Reading messages... (press Ctrl-C to quit)
> 1) "subscribe"
> 2) "ch-1"
> 3) (integer) 1
> 1) "subscribe"
> 2) "ch-2"
> 3) (integer) 2 # client当前订阅的channel数
> ~~~
>
> client 2发布消息到ch-1，ch-2
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_02/
> $ redis-cli
> 127.0.0.1:6379> publish ch-1 adams
> (integer) 1
> 127.0.0.1:6379> publish ch-2 3.1415
> (integer) 1
> ~~~
>
> client 1收到消息
>
> ~~~bash
> 1) "message"
> 2) "ch-1"     # 来自channel 1
> 3) "adams"
> 1) "message"
> 2) "ch-2"     # 来自channel 2
> 3) "3.1415"
> ~~~

### 3.3 根据name pattern匹配channel（Patterned Publish  / Subscribe）

#### (1) 文档

> documentation at redis.io
>
> * [ PSUBSCRIBE](https://redis.io/commands/psubscribe)
> * [ PUNSUBSCRIBE](https://redis.io/commands/punsubscribe)

#### (2) 方法

> 1. 使用`PUBSUB`命令来了解目前有哪些channel可以用来订阅
> 2. 使用`PSUBSCRIBE`来订阅channel同时指定接收channel中哪些消息
> 3. 需要解除订阅时、可以使用`PUNSUBSCRIBE`命令

#### (3) 命令

##### (a) `PUBSUB subcommand [argument [argument ...]]`

> 根据subcommand取值不同，该命令的功能也不同

`PUBSUB CHANNELS [pattern]`

> 返回可订阅的channel列表
>
> ~~~bash
> 127.0.0.1:6379> pubsub channels *
> 1) "ch-2"
> 2) "ch-1"
> ~~~

`PUBSUB NUMSUB [channel-1 ... channel-n]`

> 返回各个channels的subscribers数量（不包括patterned subscribers）
>
> ~~~bash
> 127.0.0.1:6379> pubsub numsub ch-1 ch-2
> 1) "ch-1"
> 2) (integer) 1
> 3) "ch-2"
> 4) (integer) 1
> ~~~

`PUBSUB NUMPAT`

> 返回所有pattern的数量，例如
>
> Client 2：创建patterned scriber
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_02/
> $ redis-cli
> 127.0.0.1:6379> psubscribe ch-? c?-1
> Reading messages... (press Ctrl-C to quit)
> 1) "psubscribe"
> 2) "ch-?"
> 3) (integer) 1
> 1) "psubscribe"
> 2) "c?-1"
> 3) (integer) 2
> ~~~
>
> Client 1：查看pattern数量
>
> ~~~bash
> 127.0.0.1:6379> PUBSUB NUMPAT
> (integer) 2
> ~~~

##### (b) `PSUBSCRIBE pattern [pattern ...]`

创建Patterned Subscribers，命令使用`glob like patterns`来匹配channel名称，其中

> *  `?`用来匹配单个字符
> * `*`用来匹配多个字符
>
> * `[abc]`表示a、b、c三个字符中的一个

性能开销

> channel需要和每一个pattern进行比较、来确定消息是否可以发给某个Patterned Subscriber。因此Patterned Subscriber越多，处理开销和延时越大。
>
> 因此创建Patterned Subscriber需要审慎

例子

> Client 2：使用psubscribe订阅、pattern为`ch-?`
>
> ~~~bash
> # 使用pattern订阅
> 127.0.0.1:6379> psubscribe ch-?
> Reading messages... (press Ctrl-C to quit)
> 1) "psubscribe" # 表示是psubscribe订阅的response
> 2) "ch-?"       # pattern
> 3) (integer) 1  # 返回1表示成功
> ~~~
>
> Client 1：向ch-9发送消息
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/client_01/
> $ redis-cli
> 127.0.0.1:6379> pubsub channels * # 可以看到当前没有active channel
> (empty array)
> 127.0.0.1:6379> publish ch-9 douglas # 向ch-9 channel发送
> (integer) 1 # 表示发往了1个subscriber
> 127.0.0.1:6379> publish ch-x hello # 向ch-x channel发送
> (integer) 1 # 表示发往了1个subscriber
> ~~~
>
> Client 2：能够接收到上述两条消息
>
> ~~~bash
> 1) "pmessage"  # 表示是patterned subscriber接收到了message
> 2) "ch-?"      # 匹配到的pattern
> 3) "ch-9"      # 消息发布者使用的channel
> 4) "douglas"   # 消息内容
> 1) "pmessage"
> 2) "ch-?"
> 3) "ch-x"
> 4) "hello
> ~~~

##### (c) `PUNSUBSCRIBE pattern [pattern ...]`

> 取消订阅

## 4. 例子：事件通知 & Fan Out

### 4.1 介绍

功能

> 例子1：使用Simple Subscribe-Publish模型的Fan-out特性，让票务系统为下游的销售分析系统提供数据
>
> 例子2：使用Patterned Subscribe-Publish模型，来挑选开幕仪式的lottery winner并实时显示结果

备注

> Fan-out可理解为使用订阅-发布模型进行系统解构，可以很方便地添加和删除下游接受者

### 4.2 例子1：实时销售分析

比赛票务系统向销售分析发送消息，销售系统使用这些消息计算两类数据

> * 每场比赛的销售状况
> * 每小时的销售状况

代码

> [https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc04-notifications/notify.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc04-notifications/notify.py)

消息发布订阅

> ~~~python
> # Part One - simple publish & subscribe
> # 消息发布
> def post_purchases(order_id, s_order):
> 	"""Publish purchases to the queue."""
> 	# 创建订单信息（json）存入redis hash
> 	so_key = keynamehelper.create_key_name("sales_order", order_id)
> 	redis.hmset(so_key, s_order)
> 	# 向名为sales_order_notify的channel发送消息，消息内容是order_id
> 	notify_key = keynamehelper.create_key_name("sales_order_notify")
> 	redis.publish(notify_key, order_id)
> 	# 向名为sales_order_notify:${event_name}的channel发送消息，消息内容是order_id
> 	# 例如sales_order_notify:judo
> 	notify_key = keynamehelper.create_key_name("sales_order_notify", s_order['event'])
> 	redis.publish(notify_key, order_id)
> 
> # 消息订阅1：订阅sales_order_notify:${event_name} channel，用于统计每场比赛的所有销售额和票数
> def listener_events_analytics(channel):
> 	"""Listener to summarize total sales by ticket numbers and order value."""
> 	# 创建Listener：其中ignore_subscribe_messages表示
> 	# * 忽略response中第一个字段为subscribe或unsubscribe的（可参考上一小节中的例子）
> 	# * 即只接该字段为message的消息
> 	l = redis.pubsub(ignore_subscribe_messages=True)
> 	# 订阅消息
> 	c_key = keynamehelper.create_key_name(channel)
> 	l.subscribe(c_key)
> 	# 在Redis Pipeline中执行操作
> 	p = redis.pipeline()
> 	# 监听channel、收到消息时会解除阻塞
> 	for message in l.listen():
> 		# 提取消息payload，本例中其内容为order_id
> 		order_id = message['data']
> 		# 从Redis获取订单数据
> 		so_key = keynamehelper.create_key_name("sales_order", order_id)
> 		(cost, qty, event_sku) = redis.hmget(so_key, 'cost', 'qty', 'event')
> 		# 向Redis存入销售数据、订单数据
> 		so_set_key = keynamehelper.create_key_name("sales", event_sku)
> 		p.sadd(so_set_key, order_id)
> 		# 更新Redis中比赛销售汇总数据
> 		sum_key = keynamehelper.create_key_name("sales_summary")
> 		p.hincrbyfloat(sum_key, eynamehelper.create_field_name(event_sku, "total_sales"), cost)
> 		p.hincrby(sum_key, keynamehelper.create_field_name(event_sku, "total_tickets_sold"), qty)
> 		# 执行pipeline中的所有命令
> 		p.execute()
> 
> # 消息订阅2：订阅sales_order_notify channel，用于统计每小时的销售数据
> def listener_sales_analytics(channel):
> 	"""Listener to summarize the sales statistics. Histograms, using
>  BITFIELDs are maintained to show sales by hour."""
> 	# 创建Listener，并且只接受类型为message的消息
> 	l = redis.pubsub(ignore_subscribe_messages=True)
> 	# 订阅channel
> 	c_key = keynamehelper.create_key_name(channel)
> 	l.subscribe(c_key)
> 	# 监听channel
> 	for message in l.listen():
> 		# 从消息中提取payload，内容是订单id
> 		order_id = message['data']
> 		# 从redis获取订单数据
> 		so_key = keynamehelper.create_key_name("sales_order", order_id)
> 		(ts, qty, event_sku) = redis.hmget(so_key, 'ts', 'qty', 'event')
> 		# 更新所属日期的汇总数据
> 		hour_of_day = int(time.strftime("%H", time.gmtime(int(ts))))
> 		vals = ["INCRBY", "u16", max(hour_of_day * 16, 0), int(qty)]
> 		tod_event_hist_key = keynamehelper.create_key_name("sales_histogram", "time_of_day", event_sku)
> 		redis.execute_command("BITFIELD", tod_event_hist_key, *vals)
> 
> def print_statistics(stop_event):
> 	"""Thread that prints current event statsistics."""
> 	# 打印汇总信息
>     ...
> ~~~

测试用例

> ~~~python
> # 给测试用例使用的工具函数
> # (1) 生成订单，并向redis发布
> def purchase(event_sku):
> 	"""Simple purchase function, that pushes the sales order for publishing"""
> 	...
> 
> # (2) 向redis存入用来测试比赛
> def create_event(event_sku):
> 	"""Create the event key from the provided details."""
> 	...
> 
> # 测试用例
> def test_pub_sub():
> 	"""Test function for pub/sub messages for fan out"""
> 	...
> ~~~

### 4.3 例子2：开幕闭幕式抽奖

背景

> 在开幕式和闭幕式中，每5个order中会有一个中奖，中奖信息会实时推送
>
> 中奖标准和礼品可以灵活配置，因此需要把每一个order都实时发布到一个单独的channel上
>
> 本例使用patterned subscription来实现

参考

> [ Wikipedia article on Glob style wildcards](https://en.wikipedia.org/wiki/Glob_(programming))

代码

> [https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc04-notifications/notify.py](https://github.com/fangkun119/ru101/blob/main/redisu/ru101/uc04-notifications/notify.py)

消息发布订阅

> ~~~python
> # Part Two - pattern subscriptions
> 
> # 订阅“Opening Ceremony”和“Closing Ceremony” Channel，从中接收订单数据
> # 每收到5个订单，会选出一个中奖者
> def listener_ceremony_alerter(channel):
>     # 创建Listener，只接收message类型的消息，不接收subscribe和unsubscribe类型的
> 	l = redis.pubsub(ignore_subscribe_messages=True)
> 	# 使用Pattern “*Cerenmony”订阅两个庆典相关channel的消息
> 	c_key = keynamehelper.create_key_name(channel, "*Ceremony")
> 	l.psubscribe(c_key)
> 	# 监听channel，收到消息时解除阻塞
> 	for message in l.listen():
> 		# 提取消息payload（内容为order id），提取消息的channel name（当做event）
> 		order_id = message['data']
> 		_, event = message['channel'].rsplit(":", 1)
> 		# “Opening Ceremony”和”Closing Ceremony“分开计数
> 		sum_key = keynamehelper.create_key_name("sales_summary")
> 		field_key = keynamehelper.create_field_name(event, "total_orders")
> 		total_orders = redis.hincrby(sum_key, field_key, 1)
> 		# 不论是哪种ceremony，这个ceremony中每5个订单赠送一份奖品
> 		if total_orders % 5 == 0:
> 			print("===> Winner!!!!! Ceremony Lottery - Order Id: {}"\
> 				.format(order_id))
> ~~~

测试

> ~~~python
> def test_patterned_subs():
> 	"""Test function for patterned subscriptions"""
>     ...
> ~~~