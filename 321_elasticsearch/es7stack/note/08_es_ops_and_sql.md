# CH08 Elasticsearch运维及Elasticsearch SQL

## 01 本章内容

> 实际部署中Elasticsearch通常是多节点部署，涉及到选取部署架构、硬件选择、使用XPack进行集群监控、备份、软件升级重启等

## 02 Primary Shards数量选择

每台机器上部署几个Elastic Shards

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_shard_num_1_intro.jpg" width="380" /></div>
>
> 取决于总的内存，另外每个Shard都有额外开销，所以不是越多越好

Primary Shards和Replica Shards

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_ops_primary_replica_shards.jpg" width="500" /></div>
>
> * Primary Shards：处理写操作
>
> * Replica Shards：增加容错能力和读吞吐量
>
> 两种Shards的数量和比例、取决于读操作的量以及写操作的量，注意Primary Shards扩容需要删除和重建索引，而Replica Shards可以随时增加。

确定Shards的具体数量

> 用线上真实请求和相同的请求配置，在测试环境进行读写极限压测，测出具体的承载基线以及内存占用，然后根据业务增长预估预留buffer

## 03 创建带有Shard的索引

### (1) 创建索引时指定Shard配置

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_sharded_idx_creation.jpg" width="350" /></div>
>
> 例如上面的命令，总共有10个Primary Shards，为每个Primary Shard配1个Replica Shard

创建索引时，设置shards非常重要，特别是线上环境的Primard Shard扩容代价很大，要在创建索引时想好预留多少shards

### (2) 查看某个索引当前Shards的配置

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_ops_shards_setting_query.jpg" width="800" /></div>

## 04 使用`Index aliast rotation`的方法来管理索引容量

用途

> 为索引增加Primary Shards比较痛苦，好在它不是唯一为索引扩容的方法。另一种方法是让请求可以跨多个索引，这样就可以直接增添新索引，而不用为旧索引扩容。

方法

> 1. 创建新的索引来承载新数据
> 2. 搜索多个索引
> 3. 使用索引别名，来让这些索引便于搜索

应用场景举例

> Time-Based数据：为每个Time Frame创建一个索引
>
> 日志数据：通常关心当前日期的数据、但也会访问历史数据，一种方法是创建两个索引别名：
>
> * `logs_current`：指向最近日期的索引
> * `last_3_months`：指向最近3个月的一组索引
>
> 随着日期的变化滚动它所覆盖的索引范围

代码举例

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_index_alias_rotation.jpg" width="800" /></div>
>
> 例如每月一个索引，当日期从2017-05切换到2017-06时，可以使用上面的命令来对两个alias覆盖的索引范围进行滚动，并且可选删除不再需要的旧索引

## 05 索引生命周期管理（ES7）

### (1) 生命周期

索引生命周期是Elasticsearch 7提供的新功能，包括4个阶段：

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_idx_life_cycle.jpg" width="800" /></div>
>
> Hot：索引被更新和查询
>
> Warm：被查询但是不被更新（只读）
>
> Cold：不被更新、少量被查询，可以用省钱的方案
>
> Delete：不再需要，可以被彻底删除

可以为每个阶段设置不同的Policy，以设置状态切换的触发条件

### (2) 例子

步骤1：下面的命令会定义一个Index Lifecycle Policy

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_idx_lifecycle_mgn_example.jpg" width="300" /></div>
>
> 定义了两个阶段：Hot和Delete
>
> 当索引体积超过50G或者30天时roll over到`Delete Stage`，并继续保存90天

步骤2：创建Index Template、用于将Policy作用在具体的Indices上

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_idx_life_cycle_apply_to_pattern.jpg" width="400" /></div>
>
> 这个Template定义了：
>
> (1) 覆盖索引的命名通配符
>
> (2) Primary Shards（扩容困难）和Replicas Shards（可扩容）的数量
>
> (3) Index Life Cycle Policy：在上一步中定义
>
> (4) Rollover别名：查询时可以使用，另外对这个alias进行rollover操作的方法可以参考上面的04小节

## 06 硬件选择

### (1) 内存

> 内存是Elasticsearch的主要瓶颈，至少64G会比较合适（32G用于Elasticsearch、32G用于OS/Lucence Disk Cache），不推荐使用8GB

### (2) SSD

> 如果可以、使用`SSD（with deadline or noop i/o schedule）`会更好

### (3) RAID0

> 集群已经做了节点级的冗余备份，不必再使用RAID

### (4) 其他

> CPU并不重要
>
> 需要比较快的网络
>
> 不建议使用NAS（Network Attached Storage）
>
> 通常使用中大型的硬件套餐（太大会有浪费并且带来Overhead，太小导致节点太多并且装不下单个节点也不好）

## 07 Heap Sizing

默认的Heap Size是1GB，不够用

配置建议：

> * 用于Elasticsearch不要超过总内存的一半（余下的一半留给OS和Lucene）
> * 如果不使用”aggreating“或”analyzed string fields，可以使用更少的内存
> * Heap Size小可以加快垃圾回收，并且留下更多内存用于缓存

配置方法

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_heap_size_cfg.jpg" width="500" /></div>
>
> 超过32GB时，无法使用4字节指针存储对象的内存地址，JVM会改用8字节存储，导致内存膨胀浪费增加

## 08 使用X-Pack进行监控

### (1) X-Pack

> 是Elasticsearch 7之前的扩展（Elastic Stack Extention：例如shield / watcher / marvel等）的统称，功能覆盖安全、监控、报警、报表、graph、机器学习等。
>
> Elasticsearch 7已经包含了这些扩展，不需要单独安全，目前大部分功能都是付费的，只有例如Monitor、Alerting等少量功能免费

### (2) X-Pack Monitoring

> 点击左上角的菜单按钮：`Management` → `Stack Monitoring`，可以进入监控页面设置和查看监控

## 09 Elasticsearch SQL

### (1) 查询index的schema

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/note/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/_xpack/sql' -d '
>   {"query" : "DESCRIBE movies" }'
> {"columns":[{"name":"column","type":"keyword"},{"name":"type","type":"keyword"},{"name":"mapping","type":"keyword"}],"rows":[["genre","VARCHAR","text"],["genre.keyword","VARCHAR","keyword"],["id","VARCHAR","text"],["id.keyword","VARCHAR","keyword"],["title","VARCHAR","text"],["title.keyword","VARCHAR","keyword"],["year","BIGINT","long"]]}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/note/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/_xpack/sql?pretty' -d '
> {"query" : "DESCRIBE movies" }'
> {
>   "columns" : [
>     {"name" : "column", "type" : "keyword"},
>     {"name" : "type", "type" : "keyword"},
>     {"name" : "mapping", "type" : "keyword"}
>   ],
>   "rows" : [
>     ["genre"         , "VARCHAR", "text"   ],
>     ["genre.keyword" , "VARCHAR", "keyword"],
>     ["id"            , "VARCHAR", "text"   ],
>     ["id.keyword"    , "VARCHAR", "keyword"],
>     ["title"         , "VARCHAR", "text"   ],
>     ["title.keyword" , "VARCHAR", "keyword"],
>     ["year"          , "BIGINT" , "long"   ]
>   ]
> }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/note/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/_xpack/sql?format=txt' -d '
> {"query" : "DESCRIBE movies" }'
>     column     |     type      |    mapping
> ---------------+---------------+---------------
> genre          |VARCHAR        |text
> genre.keyword  |VARCHAR        |keyword
> id             |VARCHAR        |text
> id.keyword     |VARCHAR        |keyword
> title          |VARCHAR        |text
> title.keyword  |VARCHAR        |keyword
> year           |BIGINT         |long
> ~~~

### (2) 使用SQL查询索引

例子1

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/note/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/_xpack/sql?format=txt' -d '
>   {"query" : "SELECT title FROM movies LIMIT 10"}'
>            title
> ---------------------------
> Toy Story
> Jumanji
> Grumpier Old Men
> Waiting to Exhale
> Father of the Bride Part II
> Heat
> Sabrina
> Tom and Huck
> Sudden Death
> GoldenEye
> ~~~

例子2

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/note/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/_xpack/sql?format=txt' -d '
> {"query" : "SELECT title, year FROM movies WHERE year < 1920 ORDER BY year"}'
>                      title                      |     year
> ------------------------------------------------+---------------
> Trip to the Moon, A                             |1902
> The Great Train Robbery                         |1903
> The Electric Hotel                              |1908
> Birth of a Nation, The                          |1915
> Intolerance: Love s Struggle Throughout the Ages|1916
> 20,000 Leagues Under the Sea                    |1916
> Snow White                                      |1916
> Rink, The                                       |1916
> Immigrant, The                                  |1917
> Daddy Long Legs                                 |1919
> ~~~

### (3) 用SQL计算出对应的Query JSON

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/note/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/_xpack/sql/translate?pretty' -d '
> {
>   "size" : 1000,
>   "query" : {
>     "range" : {
>       "year" : {
>         "from" : null,
>         "to" : 1920,
>         "include_lower" : false,
>         "include_upper" : false,
>         "boost" : 1.0
>       }
>     }
>   },
>   "_source" : false,
>   "fields" : [
>     {
>       "field" : "title"
>     },
>     {
>       "field" : "year"
>     }
>   ],
>   "sort" : [
>     {
>       "year" : {
>         "order" : "asc",
>         "missing" : "_last",
>         "unmapped_type" : "long"
>       }
>     }
>   ]
> }
> 
> ~~~

### (4) 使用SQL Client进行查询

> 对于ubuntun版本来说，目录在：`/urs/share/elasticsearch/`
>
> 对于Mac OS 版本来说，Elasticsearch的目录可以在安装时选择，演示如下
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/
> $ pwd
> /Users/fangkun/Run/elasticsearch-7.13.0
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/
> $ ./bin/elasticsearch-sql-cli
> sql> describe movies;
>     column     |     type      |    mapping
> ---------------+---------------+---------------
> genre          |VARCHAR        |text
> genre.keyword  |VARCHAR        |keyword
> id             |VARCHAR        |text
> id.keyword     |VARCHAR        |keyword
> title          |VARCHAR        |text
> title.keyword  |VARCHAR        |keyword
> year           |BIGINT         |long
> 
> sql> SELECT title, year FROM movies WHERE year < 1920 ORDER BY year;
>                      title                      |     year
> ------------------------------------------------+---------------
> Trip to the Moon, A                             |1902
> The Great Train Robbery                         |1903
> The Electric Hotel                              |1908
> Birth of a Nation, The                          |1915
> Intolerance: Loves Struggle Throughout the Ages |1916
> 20000 Leagues Under the Sea                     |1916
> Snow White                                      |1916
> Rink, The                                       |1916
> Immigrant, The                                  |1917
> Daddy Long Legs                                 |1919
> ~~~

## 10 Failover

演示操作步骤

> * 在同一台Ubuntu VM上安装3个Elasticsearch作为3个节点，并启动两个节点
> * 观察Elasticsearch在新节点加入时扩充数据
> * 关闭主节点，观察数据如何转移到其他节点

演示视频

> [https://livevideo.manning.com/module/96_8_10/elasticsearch-7-and-elastic-stack/elasticsearch-operations-and-sql-support/failover-in-action-part-1](https://livevideo.manning.com/module/96_8_10/elasticsearch-7-and-elastic-stack/elasticsearch-operations-and-sql-support/failover-in-action-part-1)

步骤（Ubuntu上）如下

(1) 在之前单节点Elasticsearch的配置和数据基础上进行修改，首先修改配置文件elasticsearch.yml

> 每台VM最多容许启动三个Elasticsearch节点
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_max_local_node_num.jpg" width="800" /></div>
>
> 集群名称和当前节点的名称
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_cluster_and_node_name.jpg" width="800" /></div>
>
> 查看初始master node配置
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_initial_master_node_cfg.jpg" width="800" /></div>
>
> 添加其他node到cluster中
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_add_node_to_cluster.jpg" width="800" /></div>

(2) 将安装Elasticsearch的目录拷贝三份、并修改各自的`node.name`和`http.port`配置

> | 目录                | node.name | http.port |
> | ------------------- | --------- | --------- |
> | elasticsearch       | node-1    | 9200      |
> | elasticsearch-node2 | node-2    | 9201      |
> | elasticsearch-node3 | node-3    | 9202      |

(3) 拷贝3份在`elasticsearch-*.service`配置文件并修改配置

目录：

> /usr/lib/systemd/system/

拷贝：

> elasticsearch.service  → elasticsearch-node1.service, elasticsearch-node2.service, elasticsearch-node3.service

修改文件内容，以node2为例elasticsearch-node2.service文件修改如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_path_conf.jpg" width="800" /></div>
>
> 主要是涉及到`/etc/elasticsearch*`的目录

(3) 重新载入服务配置

> ~~~bash
> cd /usr/lib/systemd/system
> sudo /bin/systemctl deamon-reload
> ~~~

(4) 启动2个Elasticsearch

> ~~~bash
> # 关闭之前演示时使用的单节点服务
> sudo /bin/systemctl stop elasticsearch
> # 启动刚配好的多集群服务，但只启动2个节点
> sudo /bin/systemctl start elasticsearch
> sudo /bin/systemctl start elasticsearch-node2
> ~~~

(5) 检查2个节点启动状况和shards加载情况

> ~~~bash
> # 需要等待一段时间启动完成，命令才能执行成功，可以使用top命令来查看进程状况
> curl -XGET localhost:9200/_cluster/health?pretty
> ~~~
>
> 命令执行成功后可以看到集群状态
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_cluster_start.jpg" width="800" /></div>
>
> 可以看到，集群中有2个Node正在运行
>
> 状态是Green Status，表示Elasticsearch认为每个shards都有冗余节点备份，但其实并不是真正的Green，有两个原因：
>
> * 部署在同一台VM上，如果VM宕机所有node都会宕机
> * 节点数量小于3，master宕机时无法选出新的master

(6) 启动第3个节点，重复上面的检查，查看shards rebalance状况

> ~~~bash
> # 启动节点
> sudo /bin/systemctl start elasticsearch-node2
> # 检查集群状况，需要过一段时间新节点启动完成并加入cluster之后，才能看到Response发生变化
> curl -XGET localhost:9200/_cluster/health?pretty #端口9200，9201都可以
> ~~~
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_check_status_3.jpg" width="800" /></div>
>
> 可以看到数据已经同步到3个node上，状态是green

(9) 关闭第1个节点，重复上面的检查，查看shards的reblance状况

> ~~~bash
> # 关闭node1
> cd /usr/lib/systemd/system
> sudo /bin/systemctl stop elasticsearch.service 
> curl -XGET localhost:9200/_cluster/health?pretty
> ~~~
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_stop_one_node.jpg" width="800" /></div>
>
> 可以看到只有2个node了， 状态yellow因为还在等待rebalance，可以看到有14个shard处于unassigned的状态。但在这段时间执行查询，仍然可以成功，是因为开起来shared index之后Elasticsearch在用剩余两个节点上的redundant备份进行查询。

(7) 等待一段时间待rebalance完成后再次查看状态

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fodemo_final.jpg" width="800" /></div>
>
> 状态将会变为Green，所有shard都是active，unassigned shards为0。
>
> 但是节点数是2，即如果有节点宕机，将无法再次选举master node的状态。

(8) 启动被关闭的node1

> 带rebalance完成后，将回再次回到3个节点的Green状态

## 11 Snapshots

特点

> 可以被分到NAS、AWS S3、HDFS、Azure等
>
> 足够智能，可以只存储上次Snapshot之后的变更

配置repo路径并创建repo

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_snapshots_cfg_and_cmd.jpg" width="450" /></div>
>
> 配置文件（`path.repo`）：例子中是本地磁盘，也可以配成其他机器、DataCenter等
>
> 创建snapshot命令
>
> * `type`是repo类型，其中fs表示本地磁盘系统，也可以配成`s3`等
> * `location`是备份路径，需要与repo相匹配

备份和恢复的相关操作，需要提前演练，因为用到时往往是紧急情况

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_snapshot_ops.jpg" width="500" /></div>

演示视频：[https://livevideo.manning.com/module/96_8_12/elasticsearch-7-and-elastic-stack/elasticsearch-operations-and-sql-support/snapshots](https://livevideo.manning.com/module/96_8_12/elasticsearch-7-and-elastic-stack/elasticsearch-operations-and-sql-support/snapshots)

大致步骤

> (1) 修改`elasticsearch.yml`文件，添加`path.repo`配置
>
> ~~~yml
> path.repo: ["/home/student/backups"]
> ~~~
>
> (2) 创建用于存储备份文件的目录，并设置写权限
>
> (3) 重启Elasticsearch，如果是集群就都重启
>
> (4) 创建snapshot repo
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_snapshot_setup_cfg.jpg" width="800" /></div>
>
> (5) 创建snapshot
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_snapshot_create.jpg" width="800" /></div>
>
> (6) 查看snapshot创建状况
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_snapshot_view.jpg" width="800" /></div>
>
> (7) 查看新建的snapshot的各项状况
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_snapshot_check_status.jpg" width="800" /></div>
>
> (8) 关闭indices的写操作，以用于restore
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_snapshot_close_write.jpg" width="800" /></div>
>
> (9) 用snapshot恢复索引
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_snapshot_restore.jpg" width="800" /></div>
>
> (10) 执行数据查询，确认索引可以使用

## 12 Rolling Restarts

问题及方法

> 集群升级和维护时，需要依次重启节点，但是会触发Elasticsearch的Reallocation。为了想要操作高价高效率，希望把reallocation给disable掉。

过程

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_rolling_restart_proc.jpg" width="800" /></div>

相关命令

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_rolling_restart_cmd.jpg" width="800" /></div>

演示

> (1) 检查集群状态
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_rsdemo_1_pre_check.jpg" width="600" /></div>
>
> (2) 关闭shard rebalance
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_rsdemo_2_close_sr.jpg" width="800" /></div>
>
> (3) 关闭要升级的Elasticsearch节点、查看健康状态（随后可进行升级操作）
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_rsdemo_3_update_node.jpg" width="800" /></div>
>
> 可以看到此时集群处于yellow状态，因为一个节点关闭，只能靠其他节点的redundency来提供这部分数据的服务
>
> (4) 升级操作完成后，启动这个节点并检查状态
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_rsdemo_start_node.jpg" width="800" /></div>
>
> 可以看到集群仍然处于yellow状态，是因为reballance还没有恢复，新数据到不了这个节点
>
> (5) 恢复Rebalance功能
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_rsdemo_enable_rebalance.jpg" width="800" /></div>
>
> 过一小段时间等待rebalance完成后，再次检查集群状态，状态会恢复回green
