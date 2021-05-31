[TOC]

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

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/upload/es7_esstack_invertedidx.png" width="800" /></div>

相关性计算：基于TF-IDF给不同的词赋予的不同权重

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/upload/es7_esstack_tfidf.png" width="600" /></div>

## 7 使用Elasticsearch

3种方式

> * RESTFUL API
> * Client API：各种编程语言的ES Client Lib
> * Analytic Tools：例如Kibana

## 8 Elasticsearch 7新特性

> 

## 9 Elasticsearch如何扩展（Scales）

> 