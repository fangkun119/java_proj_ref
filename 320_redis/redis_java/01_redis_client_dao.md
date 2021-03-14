<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [1. 介绍](#1-%E4%BB%8B%E7%BB%8D)
  - [1.1 课程Demo项目](#11-%E8%AF%BE%E7%A8%8Bdemo%E9%A1%B9%E7%9B%AE)
    - [(1) 项目功能](#1-%E9%A1%B9%E7%9B%AE%E5%8A%9F%E8%83%BD)
    - [(2) 本地开发环境Redis安装](#2-%E6%9C%AC%E5%9C%B0%E5%BC%80%E5%8F%91%E7%8E%AF%E5%A2%83redis%E5%AE%89%E8%A3%85)
    - [(3) 运行程序冰箱Redis中加载数据](#3-%E8%BF%90%E8%A1%8C%E7%A8%8B%E5%BA%8F%E5%86%B0%E7%AE%B1redis%E4%B8%AD%E5%8A%A0%E8%BD%BD%E6%95%B0%E6%8D%AE)
    - [(4) 其他](#4-%E5%85%B6%E4%BB%96)
      - [(a) docker web IDE](#a-docker-web-ide)
      - [(b) RedisTimeSeries的环境设置（第4章使用）](#b-redistimeseries%E7%9A%84%E7%8E%AF%E5%A2%83%E8%AE%BE%E7%BD%AE%E7%AC%AC4%E7%AB%A0%E4%BD%BF%E7%94%A8)
- [2. Jedis Hello World](#2-jedis-hello-world)
  - [(1) 句柄释放](#1-%E5%8F%A5%E6%9F%84%E9%87%8A%E6%94%BE)
  - [(2) Thread Safe](#2-thread-safe)
- [3. Redis Clients介绍](#3-redis-clients%E4%BB%8B%E7%BB%8D)
- [4. Jedis介绍](#4-jedis%E4%BB%8B%E7%BB%8D)
  - [4.1 `Jedis` classg](#41-jedis-class)
  - [4.2 `JedisPool`以及`JedisPoolConfig` class](#42-jedispool%E4%BB%A5%E5%8F%8Ajedispoolconfig-class)
  - [4.3 `JedisSentinelPool`以及`JedisCluster`](#43-jedissentinelpool%E4%BB%A5%E5%8F%8Ajediscluster)
  - [4.4 选择哪个类来连接Redis](#44-%E9%80%89%E6%8B%A9%E5%93%AA%E4%B8%AA%E7%B1%BB%E6%9D%A5%E8%BF%9E%E6%8E%A5redis)
  - [4.5 Jedis文档](#45-jedis%E6%96%87%E6%A1%A3)
- [5. Jedis基本操作](#5-jedis%E5%9F%BA%E6%9C%AC%E6%93%8D%E4%BD%9C)
  - [5.1 类型映射](#51-%E7%B1%BB%E5%9E%8B%E6%98%A0%E5%B0%84)
  - [5.2 例子](#52-%E4%BE%8B%E5%AD%90)
- [6. 定义DAOs和Domain Objects](#6-%E5%AE%9A%E4%B9%89daos%E5%92%8Cdomain-objects)
  - [6.1 DAO](#61-dao)
  - [6.2 Domain Object](#62-domain-object)
- [7. 实现DAO](#7-%E5%AE%9E%E7%8E%B0dao)
  - [7.1 SiteDao](#71-sitedao)
  - [7.2 SiteDaoRedisImpl](#72-sitedaoredisimpl)
  - [7.3 SiteDaoRedisImplTest.java](#73-sitedaoredisimpltestjava)
- [8 注意事项](#8-%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9)
  - [(1) 线程安全](#1-%E7%BA%BF%E7%A8%8B%E5%AE%89%E5%85%A8)
  - [(2) Redis Server类型](#2-redis-server%E7%B1%BB%E5%9E%8B)
  - [(3) 最大连接数设置](#3-%E6%9C%80%E5%A4%A7%E8%BF%9E%E6%8E%A5%E6%95%B0%E8%AE%BE%E7%BD%AE)
  - [(4) 是否将Jedis实例归还给JedisPool](#4-%E6%98%AF%E5%90%A6%E5%B0%86jedis%E5%AE%9E%E4%BE%8B%E5%BD%92%E8%BF%98%E7%BB%99jedispool)
  - [(5) 是否释放Jedis Pool](#5-%E6%98%AF%E5%90%A6%E9%87%8A%E6%94%BEjedis-pool)
  - [(6) 避免使用O(N)复杂度的Redis命令](#6-%E9%81%BF%E5%85%8D%E4%BD%BF%E7%94%A8on%E5%A4%8D%E6%9D%82%E5%BA%A6%E7%9A%84redis%E5%91%BD%E4%BB%A4)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

资料：

> Redis Enterprise: [https://redislabs.com/redis-enterprise](https://redislabs.com/redis-enterprise)
>
> Redis Cloud (for AWS, GCP, Azure)：[https://redislabs.com/redis-enterprise-cloud](https://redislabs.com/redis-enterprise-cloud)

## 1. 介绍

### 1.1 课程Demo项目

#### (1) 项目功能

后端：

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_demo_backend.jpg" width="400" /></div>
>
> * 每个安装了太阳能发电板的屋顶被称为一个site
>
> * 每个site有一个电力计（power meter）报告每个site每分钟的发电量和耗电量
>
> 使用Dropwizard REST框架编写

前端

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_demo_frontend.jpg" width="1024" /></div>
>
> * site在地图上的显示
>
> * 搜索距离某个地址最近的太阳能发电板
>
> * 查看每个site近期每小时的发电量和用电量（watt-hours）
>
> * 查看存储量最大、最小的site
>
> 使用VUE编写

#### (2) 本地开发环境Redis安装

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ ls
> redis-6.2.1.tar.gz
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ tar xvfz redis-6.2.1.tar.gz
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ cd redis-6.2.1
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/redis-6.2.1/
> $ ls
> 00-RELEASENOTES   COPYING          README.md        runtest          sentinel.conf
> BUGS            INSTALL          TLS.md           runtest-cluster   src
> CONDUCT          MANIFESTO        deps            runtest-moduleapi tests
> CONTRIBUTING      Makefile         redis.conf        runtest-sentinel  utils
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/redis-6.2.1/
> $ make install
> xcrun: error: invalid active developer path (/Library/Developer/CommandLineTools), missing xcrun at: /Library/Developer/CommandLineTools/usr/bin/xcrun
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/redis-6.2.1/
> $ xcode-select --install # 在弹出的图形界面中完成xcode-select的安装
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/redis-6.2.1/
> $ make install
> ...
>     LINK redis-benchmark
>     INSTALL redis-check-rdb
>     INSTALL redis-check-aof
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/redis-6.2.1/
> $ redis-server  # 启动redis服务端（ctrl + c关闭服务端）
> 10108:C 13 Mar 2021 20:09:34.920 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
> 10108:C 13 Mar 2021 20:09:34.920 # Redis version=6.2.1, bits=64, commit=00000000, modified=0, pid=10108, just started
> 10108:C 13 Mar 2021 20:09:34.920 # Warning: no config file specified, using the default config. In order to specify a config file use redis-server /path/to/redis.conf
> 10108:M 13 Mar 2021 20:09:34.922 * Increased maximum number of open files to 10032 (it was originally set to 256).
> 10108:M 13 Mar 2021 20:09:34.922 * monotonic clock: POSIX clock_gettime
>              _._
>           _.-``__ ''-._
>       _.-``   `.  `_.  ''-._          Redis 6.2.1 (00000000/0) 64 bit
>   .-`` .-```.  ```\/    _.,_ ''-._
>  (   '     ,      .-`  | `,   )    Running in standalone mode
>  |`-._`-...-` __...-.``-._|'` _.-'|    Port: 6379
>  |   `-._   `._    /    _.-'   |    PID: 10108
>   `-._    `-._  `-./  _.-'    _.-'
>  |`-._`-._    `-.__.-'    _.-'_.-'|
>  |   `-._`-._        _.-'_.-'   |         http://redis.io
>   `-._    `-._`-.__.-'_.-'    _.-'
>  |`-._`-._    `-.__.-'    _.-'_.-'|
>  |   `-._`-._        _.-'_.-'   |
>   `-._    `-._`-.__.-'_.-'    _.-'
>       `-._    `-.__.-'    _.-'
>          `-._        _.-'
>             `-.__.-'
> 
> 10108:M 13 Mar 2021 20:09:34.923 # Server initialized
> 10108:M 13 Mar 2021 20:09:34.924 * Ready to accept connections
> ~~~
>
> 在另一个窗口可以运行redis客户端
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ redis-cli
> 127.0.0.1:6379>
> 127.0.0.1:6379> help @list
> 
>   BLMOVE source destination LEFT|RIGHT LEFT|RIGHT timeout
>   summary: Pop an element from a list, push it to another list and return it; or block until one is available
>   since: 6.2.0
>   ...
> ~~~

#### (3) 运行程序冰箱Redis中加载数据

在本地安装Redis并，fork并clone代码镜像到本地进行开发和调试

> 环境：Java 1.8，maven，Redis（5.0.3以上）
>
> 代码：[https://github.com/fangkun119/ru102j/](https://github.com/fangkun119/ru102j/) （fork自[https://github.com/redislabs-training/ru102j](https://github.com/redislabs-training/ru102j)）
>
> 配置
>
> * [https://github.com/fangkun119/ru102j/blob/master/config.yml](https://github.com/fangkun119/ru102j/blob/master/config.yml)
> * [src/test/java/com/redislabs/university/RU102J/HostPort.java](src/test/java/com/redislabs/university/RU102J/HostPort.java)
>
> 向Redis中加载数据以及运行程序：[https://github.com/fangkun119/ru102j/blob/master/README.md](https://github.com/fangkun119/ru102j/blob/master/README.md)
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/ru102j/ 
> mvn clean package # 在本地redis server启动的状态下
> ...
> [INFO] ------------------------------------------------------------------------
> [INFO] BUILD SUCCESS
> [INFO] ------------------------------------------------------------------------
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/ru102j/ 
> $ java -jar target/redisolar-1.0.jar load --flush true # 向redis存入数据
> Using Redis at localhost:6379
> Flushing database...
> Loading solar sites...
> Generating sample historical data.......................................................................................................................................................................................Data load complete!
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/ru102j/ 
> $ java -jar target/redisolar-1.0.jar server config.yml # 启动redis
> WARNING: An illegal reflective access operation has occurred
> WARNING: Illegal reflective access by com.fasterxml.jackson.module.afterburner.util.MyClassLoader (file:/Users/fangkun/Dev/git/ru102j/target/redisolar-1.0.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int)
> WARNING: Please consider reporting this to the maintainers of com.fasterxml.jackson.module.afterburner.util.MyClassLoader
> WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
> WARNING: All illegal access operations will be denied in a future release
> INFO  [2021-03-14 05:32:18,328] io.dropwizard.server.DefaultServerFactory: Registering jersey handler with root path prefix: /
> INFO  [2021-03-14 05:32:18,331] io.dropwizard.server.DefaultServerFactory: Registering admin handler with root path prefix: /
> INFO  [2021-03-14 05:32:18,331] io.dropwizard.assets.AssetsBundle: Registering AssetBundle with name: assets for path /*
> INFO  [2021-03-14 05:32:18,440] io.dropwizard.server.ServerFactory: Starting RediSolar
> ================================================================================
>     ____          ___ _____       __
>    / __ \___  ____/ (_) ___/____  / /___ ______
>   / /_/ / _ \/ __  / /\__ \/ __ \/ / __ `/ ___/
>  / _, _/  __/ /_/ / /___/ / /_/ / / /_/ / /
> /_/ |_|\___/\__,_/_//____/\____/_/\__,_/_/
> ================================================================================
> 
> 
> INFO  [2021-03-14 05:32:18,523] org.eclipse.jetty.setuid.SetUIDListener: Opened application@642a16aa{HTTP/1.1,[http/1.1]}{0.0.0.0:8081}
> INFO  [2021-03-14 05:32:18,524] org.eclipse.jetty.setuid.SetUIDListener: Opened admin@642ee49c{HTTP/1.1,[http/1.1]}{0.0.0.0:8084}
> INFO  [2021-03-14 05:32:18,526] org.eclipse.jetty.server.Server: jetty-9.4.z-SNAPSHOT; built: 2018-06-05T18:24:03.829Z; git: d5fc0523cfa96bfebfbda19606cad384d772f04c; jvm 15.0.1+9-18
> INFO  [2021-03-14 05:32:19,095] io.dropwizard.jersey.DropwizardResourceConfig: The following paths were found for the configured resources:
> 
>     GET     /api/capacity/ (com.redislabs.university.RU102J.resources.CapacityResource)
>     GET     /api/meterReadings (com.redislabs.university.RU102J.resources.MeterReadingResource)
>     POST    /api/meterReadings (com.redislabs.university.RU102J.resources.MeterReadingResource)
>     GET     /api/meterReadings/{id} (com.redislabs.university.RU102J.resources.MeterReadingResource)
>     GET     /api/metrics/{siteId} (com.redislabs.university.RU102J.resources.MetricsResource)
>     GET     /api/sites (com.redislabs.university.RU102J.resources.SiteResource)
>     GET     /api/sites/{id} (com.redislabs.university.RU102J.resources.SiteResource)
> 
> WARN  [2021-03-14 05:32:19,098] org.glassfish.jersey.internal.Errors: The following warnings have been detected: WARNING: The (sub)resource method getCapacity in com.redislabs.university.RU102J.resources.CapacityResource contains empty path annotation.
> 
> INFO  [2021-03-14 05:32:19,100] org.eclipse.jetty.server.handler.ContextHandler: Started i.d.j.MutableServletContextHandler@54489296{/,null,AVAILABLE}
> INFO  [2021-03-14 05:32:19,104] io.dropwizard.setup.AdminEnvironment: tasks = 
> 
>     POST    /tasks/log-level (io.dropwizard.servlets.tasks.LogConfigurationTask)
>     POST    /tasks/gc (io.dropwizard.servlets.tasks.GarbageCollectionTask)
> 
> INFO  [2021-03-14 05:32:19,112] org.eclipse.jetty.server.handler.ContextHandler: Started i.d.j.MutableServletContextHandler@68c34db2{/,null,AVAILABLE}
> INFO  [2021-03-14 05:32:19,133] org.eclipse.jetty.server.AbstractConnector: Started application@642a16aa{HTTP/1.1,[http/1.1]}{0.0.0.0:8081}
> INFO  [2021-03-14 05:32:19,134] org.eclipse.jetty.server.AbstractConnector: Started admin@642ee49c{HTTP/1.1,[http/1.1]}{0.0.0.0:8084}
> INFO  [2021-03-14 05:32:19,134] org.eclipse.jetty.server.Server: Started @2147ms
> ~~~
>
> 访问：http://localhost:8081/

#### (4) 其他

##### (a) docker web IDE

> 1. `$ docker run --rm --name redis-lab-102j -p:8888:8888 -p:8081:8081 redisuniversity/ru102j-lab`
>
> 2. 访问[http://localhost:8888/entry.html](http://localhost:8888/entry.html)（内含网页版IDE、Redis Console、代码、maven）

##### (b) RedisTimeSeries的环境设置（第4章使用）

> For the [RedisTimeSeries](https://oss.redislabs.com/redistimeseries/) unit in Week 4, you'll want to get RedisTimeSeries working. See the [setup instructions on the RedisTimeSeries documentation page](https://oss.redislabs.com/redistimeseries/#setup) for details.

## 2. Jedis Hello World

> 代码：[/src/test/java/com/redislabs/university/RU102J/examples/HelloTest.java](https://github.com/fangkun119/ru102j/blob/master/src/test/java/com/redislabs/university/RU102J/examples/HelloTest.java)
>

### (1) 句柄释放

> ```java
>@Test
> public void sayHello() {
>	// 创建Jedis对象（线程不安全，只能用于单线程场景）
> 	Jedis jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());
> 	if (HostPort.getRedisPassword().length() > 0) {
> 		jedis.auth(HostPort.getRedisPassword());
>    	}
>    
>    	// 127.0.0.1:6379> SET hello world
>    	// OK
>    	// 127.0.0.1:6379> GET hello
>    	// "world"
> 	String result = jedis.set("hello", "world"); // 获取返回值
>    	assertThat(result, is("OK"));
>    	String value = jedis.get("hello");
>    	assertThat(value, is("world"));
>    
>    	// 关闭连接、防止连接泄漏
>    	jedis.close();
>    }
>    ```
> 

### (2) Thread Safe

> ~~~java
> @Test
> public void sayHelloThreadSafe() {
> 	// 多线程环境下需要从JedisPool中取出Jedis对象
> 	JedisPool jedisPool;
> 	String password = HostPort.getRedisPassword();
> 	if (password.length() > 0) {
> 		jedisPool = new JedisPool(new JedisPoolConfig(), HostPort.getRedisHost(), HostPort.getRedisPort(), 2000, password);
> 	} else {
> 		jedisPool = new JedisPool(new JedisPoolConfig(), HostPort.getRedisHost(), HostPort.getRedisPort());
> 	}
> 
> 	// 从连接池中获取Jedis对象
> 	// try-with-resource block保证Jedis对象在离开try块时被归还给Jedis Poll
> 	try (Jedis jedis = jedisPool.getResource()) {
> 		String result = jedis.set("hello", "world");
> 		assertThat(result, is("OK"));
> 		String value = jedis.get("hello");
> 		assertThat(value, is("world"));
> 	}
> 
> 	// 释放Jedis Pool，同时会释放所有Redis链接
> 	jedisPool.close();
> }
> ~~~

## 3. Redis Clients介绍

管理connections

> 管理与Redis Server的TCP了；链接
>
> 连接池管理

实现RESP（REdis Serialization Protocal）协议

> 在redis.io上可以找到该协议的具体信息

实现Redis API

> 实现Redis命令、例如：GET、SET、INCR、HGET、HSET、ZSET、LRPUSH、……
>
> 将Redis数据、转换成对应编程语言的数据类型
>
> [redis.io](http://redis.io)上列出各种编程语言的实现，对于Java，推荐Jedis、Lettuce、或者Reddison

## 4. Jedis介绍

### 4.1 `Jedis` class

特点

> 观察Jedis类的构造函数，它适合于连接单个Redis Server或Redis Enterprise
>
> 同时它线程不安全，适合给单线程使用

例子

> ~~~java
> public class Jedis extends BinaryJedis implements JedisCommands, MultiKeyCommands, AdvancedJedisCommands, ScriptingCommands, BasicCommands, ClusterCommands, SentinelCommands, ModuleCommands {
> 	protected JedisPoolAbstract dataSource = null;
> 	public Jedis() {}
> 	public Jedis(String host) { super(host);}
> 	public Jedis(HostAndPort hp) {super(hp);}
> 	public Jedis(String host, int port) { super(host, port);}
> 	...
> }
> ~~~

### 4.2 `JedisPool`以及`JedisPoolConfig` class

特点

> 以线程安全的方式提供Jedis对象，将Jedis对象放在try-with-resource语句中可以保证它被释放
>
> 适用于连接单个Redis Server或Redis Enterprise

创建

> 创建`JedisPool`对象，需要提供host、port、JedisPoolConfig
>
> ~~~java
> JedisPool jedisPool = new JedisPool(
> 	new JedisPoolConfig(),
> 	HostPort.getRedisHost(), HostPort.getRedisPort(), 2000, password);
> ~~~

配置

> 在`JedisPoolConfig`代码中可以看到它使用的默认设置
>
> ~~~java
> public class JedisPoolConfig extends GenericObjectPoolConfig {
> 	public JedisPoolConfig() {
> 		this.setTestWhileIdle(true);
> 		this.setMinEvictableIdleTimeMillis(60000L);
> 		this.setTimeBetweenEvictionRunsMillis(30000L);
> 		this.setNumTestsPerEvictionRun(-1);
> 	}
> }
> ~~~
>
> 以及从基类`GenericObjectPoolConfig`、`BaseObjectPoolConfig`继承的默认配置
>
> ~~~java
> public class GenericObjectPoolConfig<T> extends BaseObjectPoolConfig<T> {
> 	public static final int DEFAULT_MAX_TOTAL = 8;
> 	public static final int DEFAULT_MAX_IDLE = 8;
> 	public static final int DEFAULT_MIN_IDLE = 0;
> 	private int maxTotal = 8; // 最大连接数默认为8
> 	private int maxIdle = 8;  // 最大闲置连接数默认值也是8
> 	private int minIdle = 0;
>     ...
> }
> 
> public abstract class BaseObjectPoolConfig<T> extends BaseObject implements Cloneable {
> 	public static final boolean DEFAULT_LIFO = true;
> 	public static final boolean DEFAULT_FAIRNESS = false;
> 	public static final long DEFAULT_MAX_WAIT_MILLIS = -1L;
> 	public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1800000L;
>     ...
> }
> ~~~
>
> 下面是如何修改maxTotal配置
>
> ~~~java
> final JedisPoolConfig poolConfig = new JedisConfig();
> poolConfig.setMaxTotal(64);
> poolConfig.setMaxIdel(64);
> JedisPool pool = new JedisPool(poolConfig, "redis.enterprise", 6379);
> ~~~

### 4.3 `JedisSentinelPool`以及`JedisCluster`

> 使用例子：[/src/main/java/com/redislabs/university/RU102J/examples/ConnectionExamples.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/examples/ConnectionExamples.java)

### 4.4 选择哪个类来连接Redis

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_conn_classes.jpg" width="500" /></div>
>
> RedisCluster封装了节点重定向机制
>
> RedisEnterprise则在服务端提供了proxy来将请求重定向到背后的Redis Cluster中的节点

### 4.5 Jedis文档

> Java Doc: [http://redis.github.io/jedis/](http://redis.github.io/jedis/)
>
> Google group

## 5. Jedis基本操作

### 5.1 类型映射

Redis Type与Java Type的类型映射关系

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_jedis_type_mapping.jpg" width="400" /></div>

### 5.2 例子

> [/src/test/java/com/redislabs/university/RU102J/examples/JedisBasicsTest.java](https://github.com/fangkun119/ru102j/blob/master/src/test/java/com/redislabs/university/RU102J/examples/JedisBasicsTest.java)
>
> 内含与List、Set、Hash相关的操作和数据类型转换，以及基本类型Integer, Float的转换
>
> ~~~bash
> mvn test -Dtest=JedisBasicsTest#testRedisList
> mvn test -Dtest=JedisBasicsTest#testRedisSet
> mvn test -Dtest=JedisBasicsTest#testRedisHash
> ~~~

再次强调对于数据量大（high-cardinality）的key，不要使用O(N)复杂度的命令，例如：

> * 不要使用LREM、SCARD、SMEMBERS等，
> * 应当使用SSCAN等命令

## 6. 定义DAOs和Domain Objects

### 6.1 DAO

> Data Access Object
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/320_redis/redisjava_dao.jpg" width="700" /></div>

### 6.2 Domain Object

> [/src/main/java/com/redislabs/university/RU102J/api/Site.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/api/Site.java)
>
> 类似普通的POJO
>
> (1) 使用jackson注解使其可以进行JSON序列化/反序列化，用于通信协议
>
> (2) 提供`toMap()`以及使用`Map<String,String>`为参数的构造函数，用来与redis的Hash类型进行转换
>
> (3) 标准的`equals()`、`hashCode()`、`toString()`方法
>
> ~~~java
> public class Site implements Comparable<Site> {
> 	// 存储在Redis的Hash类型中的Domain Fields
> 	private Long id;
> 	private Double capacity;
> 	...
> 
> 	// 用来与Redis的Hash类型进行转换
>     public Site(Map<String, String> fields) { ... }
> 	public Map<String, String> toMap() { ... }
> 
> 	// JSON序列化/反序列化，用于网络通信
> 	@JsonProperty
> 	public Long getId() { return id; }
> 	@JsonProperty
> 	public void setId(Long id) { this.id = id;}
> 	@JsonProperty
> 	public Double getCapacity() { return capacity;}
> 	@JsonProperty
> 	public void setCapacity(Double capacity) {this.capacity = capacity;}
> 	...
> 
> 	// 标准的`equals()`、`hashCode()`、`toString()`方法
> 	@Override
> 	public boolean equals(Object o) {
> 		if (this == o) return true;
> 		if (o == null || getClass() != o.getClass()) return false;
> 		Site that = (Site) o;
> 		return Objects.equals(id, that.id) &&
> 				Objects.equals(capacity, that.capacity) &&
> 				Objects.equals(panels, that.panels) &&
> 				Objects.equals(address, that.address) &&
> 				Objects.equals(city, that.city) &&
> 				Objects.equals(state, that.state) &&
> 				Objects.equals(postalCode, that.postalCode) &&
> 				Objects.equals(coordinate, that.coordinate);
>     }
> 	@Override
> 	public String toString() { ... }
> 	@Override
> 	public int hashCode() {
> 		return Objects.hash(id, capacity, panels, address, city, state, postalCode, coordinate);
> 	}
> 
> 	@Override
> 	public int compareTo(Site o) { return id.compareTo(o.id); }  
> }
> ~~~

## 7. 实现DAO

### 7.1 SiteDao

> [/src/main/java/com/redislabs/university/RU102J/dao/SiteDao.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/SiteDao.java)
>
> ~~~java
> public interface SiteDao {
> 	void insert(Site site);
> 	Site findById(long id);
> 	Set<Site> findAll();
> }
> ~~~

### 7.2 SiteDaoRedisImpl

> [/src/main/java/com/redislabs/university/RU102J/dao/SiteDaoRedisImpl.java](https://github.com/fangkun119/ru102j/blob/master/src/main/java/com/redislabs/university/RU102J/dao/SiteDaoRedisImpl.java)
>
> 1. 因为这个Demo中，连接的是单机版的Redis（或者使用了proxy来隐藏集群节点的Redis Enterprise），因此使用了`JedisPool`来实现
> 2. 如果连接的是Redis Sentinel则应当使用`JedisSentinelPool`，连接Redis Cluster应当使用`JedisCluster`，使用方法链接见前面的小节
> 3. RedisSchema用来封装生成redis key的逻辑
>
> ~~~java
> public class SiteDaoRedisImpl implements SiteDao {
> 	private final JedisPool jedisPool;
> 	public SiteDaoRedisImpl(JedisPool jedisPool) {
> 		this.jedisPool = jedisPool;
> 	}
> 
> 	@Override
> 	public void insert(Site site) {
> 		try (Jedis jedis = jedisPool.getResource()) {
> 			// 业务逻辑
> 			// When we insert a site, we set all of its values into a single hash.
> 			// We then store the site's id in a set for easy access.
> 			String hashKey   = RedisSchema.getSiteHashKey(site.getId()); // 返回"sites:info:${siteId}"
> 			String siteIdKey = RedisSchema.getSiteIDsKey(); // 返回"sites:ids"
> 			// 设置某个site的数据
> 			jedis.hmset(hashKey, site.toMap());
> 			// 存储所有site id的一个redis set
> 			jedis.sadd(siteIdKey, hashKey);
> 		}
> 	}
> 
> 	@Override
> 	public Site findById(long id) {
> 		try(Jedis jedis = jedisPool.getResource()) {
> 			String key = RedisSchema.getSiteHashKey(id);
> 			Map<String, String> fields = jedis.hgetAll(key);
> 			if (fields == null || fields.isEmpty()) {
> 				return null;
> 			} else {
> 				return new Site(fields);
> 			}
> 		}
> 	}
> 
> 	...
> }
> ~~~

### 7.3 SiteDaoRedisImplTest.java

> [/src/test/java/com/redislabs/university/RU102J/dao/SiteDaoRedisImplTest.java](https://github.com/fangkun119/ru102j/blob/master/src/test/java/com/redislabs/university/RU102J/dao/SiteDaoRedisImplTest.java)

## 8 注意事项

### (1) 线程安全

### (2) Redis Server类型

### (3) 最大连接数设置

### (4) 是否将Jedis实例归还给JedisPool

### (5) 是否释放Jedis Pool

### (6) 避免使用O(N)复杂度的Redis命令

