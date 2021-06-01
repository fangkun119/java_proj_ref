<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [CH01 Elasticsearch 7安装及原理介绍](#ch01-elasticsearch-7%E5%AE%89%E8%A3%85%E5%8F%8A%E5%8E%9F%E7%90%86%E4%BB%8B%E7%BB%8D)
  - [1 安装Elasticsearch及修改配置](#1-%E5%AE%89%E8%A3%85elasticsearch%E5%8F%8A%E4%BF%AE%E6%94%B9%E9%85%8D%E7%BD%AE)
  - [2 数据加载](#2-%E6%95%B0%E6%8D%AE%E5%8A%A0%E8%BD%BD)
    - [(1) 下载Schema和数据文件](#1-%E4%B8%8B%E8%BD%BDschema%E5%92%8C%E6%95%B0%E6%8D%AE%E6%96%87%E4%BB%B6)
    - [(2) 加载Schema和数据文件到Elasticsearch](#2-%E5%8A%A0%E8%BD%BDschema%E5%92%8C%E6%95%B0%E6%8D%AE%E6%96%87%E4%BB%B6%E5%88%B0elasticsearch)
  - [3 HTTP及RESTful API介绍](#3-http%E5%8F%8Arestful-api%E4%BB%8B%E7%BB%8D)
  - [4 基本概念：Document，Index，Cluster](#4-%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5documentindexcluster)
  - [5 Elasticsearch技术栈](#5-elasticsearch%E6%8A%80%E6%9C%AF%E6%A0%88)
    - [(1) Elasticsearch](#1-elasticsearch)
    - [(2) Kibana](#2-kibana)
    - [(3) Logstash / Beats](#3-logstash--beats)
    - [(4) X-Pack（付费插件）](#4-x-pack%E4%BB%98%E8%B4%B9%E6%8F%92%E4%BB%B6)
  - [6 词频以及`TF/IDF`](#6-%E8%AF%8D%E9%A2%91%E4%BB%A5%E5%8F%8Atfidf)
  - [7 使用Elasticsearch](#7-%E4%BD%BF%E7%94%A8elasticsearch)
  - [8 Elasticsearch 7新特性](#8-elasticsearch-7%E6%96%B0%E7%89%B9%E6%80%A7)
  - [9 Elasticsearch如何扩展（Scales）](#9-elasticsearch%E5%A6%82%E4%BD%95%E6%89%A9%E5%B1%95scales)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# CH01 Elasticsearch 7安装及原理介绍

> 内容分两部分：（1）在Ubuntu或Mac OS上安装Elastic （2）理解Elasticsearch的运行原理

## 1 安装Elasticsearch及修改配置

> 在Mac上安装：[install_es7_on_mac.md](install_es7_on_mac.md)
>
> 在Ubuntu上安装：[install_es7_on_ubuntu.md](install_es7_on_ubuntu.md)

## 2 数据加载

###  (1) 下载Schema和数据文件

下载schema文件

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/data/
> $ wget http://media.sundog-soft.com/es7/shakes-mapping.json
> ...
> shakes-mapping.json      100%[================================>]     215  --.-KB/s  用时 0s
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/data/
> $ cat shakes-mapping.json
> {
> 	"mappings" : {
> 		"properties" : {
> 			"speaker" : {"type": "keyword" },
> 			"play_name" : {"type": "keyword" },
> 			"line_id" : { "type" : "integer" },
> 			"speech_number" : { "type" : "integer" }
> 		}
> 	}
> }
> ~~~

下载数据文件

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/data/
> $ wget http://media.sundog-soft.com/es7/shakespeare_7.0.json
> ...
> shakespeare_7.0.json     100%[================================>]  24.15M   260KB/s  用时 2m 41s
> 2021-05-31 16:32:33 (154 KB/s) - 已保存 “shakespeare_7.0.json” [25327465/25327465])
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/data/
> $ head -n4 shakespeare_7.0.json
> {"index":{"_index":"shakespeare","_id":0}}
> {"type":"act","line_id":1,"play_name":"Henry IV", "speech_number":"","line_number":"","speaker":"","text_entry":"ACT I"}
> {"index":{"_index":"shakespeare","_id":1}}
> {"type":"scene","line_id":2,"play_name":"Henry IV","speech_number":"","line_number":"","speaker":"","text_entry":"SCENE I. London. The palace."}
> ...
> ~~~

### (2) 加载Schema和数据文件到Elasticsearch

加载schema

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/data/
> $ curl -H "Content-Type: application/json" -XPUT 127.0.0.1:9200/shakespeare --data-binary @shakes-mapping.json
> {"acknowledged":true,"shards_acknowledged":true,"index":"shakespeare"}
> ~~~

加载数据

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/data/
> $ curl -H "Content-Type: application/json" -XPOST '127.0.0.1:9200/shakespeare/_bulk' --data-binary @shakespeare_7.0.json
> ... "shakespeare","_type":"_doc","_id":"111394","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":111394,"_primary_term":3,"status":201}},{"index":{"_index":"shakespeare","_type":"_doc","_id":"111395","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":111395,"_primary_term":3,"status":201}}]}
> # 需要花费一段时间，注意观察日志
> # 电脑磁盘不足时，执行会失败，ES进入只读模式，拒绝写入数据
> ~~~

测试：希望数据的text_entry字段可以匹配"to be or not to be"

> ~~~java
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/data/
> $ curl -H "Content-Type: application/json" -XGET '127.0.0.1:9200/shakespeare/_search?pretty' -d '
> {"query" : { "match_phrase" : { "text_entry" : "to be or not to be"}}}'
> {
>   "took" : 2407,
>   "timed_out" : false,
>   "_shards" : { "total" : 1, "successful" : 1, "skipped" : 0, "failed" : 0 },
>   "hits" : {
>     "total" : { "value" : 1, "relation" : "eq" },
>     "max_score" : 13.889601,
>     "hits" : [
>       { "_index" : "shakespeare", "_type" : "_doc", "_id" : "34229", "_score" : 13.889601,
>         "_source" : {
>           "type" : "line", "line_id" : 34230,
>           "play_name" : "Hamlet", "speech_number" : 19,
>           "line_number" : "3.1.64", "speaker" : "HAMLET",
>           "text_entry" : "To be, or not to be: that is the question:"
>         }
>       }
>     ]
>   }
> }
> ~~~

清理下载的文件

> 上面两个文件已经压缩，并提交到如下位置，供下载
>
> [https://github.com/kenfang119/pics/blob/main/files/shakespeare_data.tar.gz](https://github.com/kenfang119/pics/blob/main/files/shakespeare_data.tar.gz)

## 3 HTTP及RESTful API介绍

HTTP以及RESTful API

> 略

CURL

> 格式： `curl -H "Content-Type; application/json" <URL> -d '<BODY>'`
>
> 例子1：进行查询，查询命令是执行一个match_phrase指令，要求数据的text_entry字段能够匹配”to be or not to be“
>
> ~~~bash
> curl -H 'Content-Type : application/json' -XGET '127.0.0.1:9200/shakespeare/_search?pretty' -d '
> {
> 	"query" : {
> 		'match_phrase' : {
> 			"text_entry" : "to be or not to be"
> 		}
> 	}
> }'
> ~~~
>
> 例子2：插入数据，索引名称是movies，数据类型是movie，唯一的ID值为109487
>
> ~~~bash
> curl -H 'Content-Type : application/json' -XPUT '127.0.0.1:9200/movies/movie/109487' -d '
> {
> 	"genre" : ["IMAX", "Sci-Fi"],
> 	"title" : "Interstellar",
> 	"year" : 2014
> }'
> ~~~

## 4 基本概念：Document，Index，Cluster

Document

> * 查询的基本单元（类似RMDB中的行），可以结构化地表达文档中的信息，支持JSON
> * 每个Document都有一个unique id，也可以给它设置一个unique id

Index

> * 用于组织Document（类似RMDB中的表）
> * 每个Index都设有一个Schema，用来定义Document内存储的数据类型和结构

Cluster

> * Elasticsearch集群（类似RMDB数据库）

## 5 Elasticsearch技术栈

### (1) Elasticsearch

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_1.jpg" width="1024" /></div>
>
> Elasticsearch可以理解为可以水平扩展的Lucene
>
> 每个Elasticsearch Shard是一个文档的倒排索引，但功能更加丰富包括
>
> * 文档全文索引
> * 处理结构化的数据
> * 快速聚合等
>
> 因此Elasticsearch也经常被用于日志聚合、并且通常比Hadoop/Spark/Flink等更快
>
> Elasticsearch也不断添加新的组件、例如Graph、Visualization、Machine Learning等，因此在一定程度上也可以和Hadoop、Spark、Flink等竞争
>
> Elasticsearch最擅长的是以毫秒级别的响应速度返回查询结果、并以JSON的形式来传递数据

### (2) Kibana

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_kibana.jpg" width="600" /></div>
>
> 可视化工具集Web UI，当不想自己开发Web界面时、可以使用Kibana来替代
>
> 功能包括：聚合、图表、日志分析（类似GA）  
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_kibana_ui.jpg" width="600" /></div>

### (3) Logstash / Beats

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_logstash_beats.jpg" width="600" /></div>
>
> 用于以Streaming的形式实时发布数据到Elasticsearch
>
> * `File Beats`：可以用于监控文件内写入的数据、解析并发送给Elasticsearch
> * `Logstash`：可以用于中继、收集多台机器的数据，并push给Elasticsearch

### (4) X-Pack（付费插件）

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_xpack.jpg" width="600" /></div>
>
> 提关于`安全`、`报警`、`监控`、`报表`等功能
>
> 支持`machine learning`、`graph exploration`等

## 6 词频以及`TF/IDF`

Elasticsearch的`text→document`索引叫做反向索引

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_invertedidx.jpg" width="800" /></div>

相关性计算：基于TF-IDF给不同的词赋予的不同权重

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_tfidf.jpg" width="600" /></div>

## 7 使用Elasticsearch

3种方式

> * RESTFUL API
> * Client API：各种编程语言的ES Client Lib
> * Analytic Tools：例如Kibana

## 8 Elasticsearch 7新特性

文档类型（Document Types）被弃用（Deprecated）

> * 一些老式的要求传入type的API，可以通过使用通用类型`_doc`来兼容
> * 新式的API以及配置文件不需要再传入type

支持SQL

默认配置值更改，例如`default shards`的值从5改为1

底层引擎Lucene已经改为Lucene 8

某些ES6版本中只有在付费的X-Pack中的插件，已经被包含在ES7的标准版本中不再需要付费

内置JDK、运行ES7不再需要安装Java（然而对于Kibana等依然需要）

跨集群副本（Cross-Cluster Replication）

索引生命周期管理（ILM：Index Lifecycle Management： Hot → Warm → Cold ）：便于根据需要逐渐改变数据的维护成本

High Level REST client in JAVA （HLRC）

## 9 Elasticsearch如何扩展（Scales）

索引（index）的数据被存储（split）在多个Shards中

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_shards.jpg" width="500" /></div>
>
> * 每个shards是一台部署在某个node上的Lucene
> * 通过添加节点、可以扩展Elasticsearch的处理能力

主备Shards（Primary and Replica Shards）

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_primary_replica_shards.jpg" width="600" /></div>
>
> 上图总共2个Primary Shards，4个Replica Shards，其中为每个Primary Shard配备了2个Replica Shards，6个Shards部署在3个节点上，这样任何一个节点宕机，服务都不会中断
>
> * 写请求发往Primary Shards，宕机时会重新选举将某个Replica Shards升级为Primary Shards
> * 读请求发往任意一个Shards

Replica Shards的数量可以根据流量变化来设置，然而Primary Shards的数量需要预先设定

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_esstack_primary_replica_shards_num.jpg" width="600" /></div>
>
> * 在`创建index的时候`使用上面的PUT请求指定Primary Shards的数量、以及为每个Primary Shard配备的Replica Shards的数量
> * 之后可以随时修改Replica Shards的数量以应对流量的变化
> * 然而如果想要修改Primary Shards的数量，则需要对数据重新进行索引（re-index）

