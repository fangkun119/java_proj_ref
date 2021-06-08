<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [CH02 数据索引](#ch02-%E6%95%B0%E6%8D%AE%E7%B4%A2%E5%BC%95)
  - [1 本章内容](#1-%E6%9C%AC%E7%AB%A0%E5%86%85%E5%AE%B9)
  - [2 访问Elasticsearch](#2-%E8%AE%BF%E9%97%AEelasticsearch)
  - [3 MovieLens数据集介绍](#3-movielens%E6%95%B0%E6%8D%AE%E9%9B%86%E4%BB%8B%E7%BB%8D)
  - [4 Analyzerz](#4-analyzerz)
    - [(1) `Mapping`](#1-mapping)
    - [(2) 添加mapping设置](#2-%E6%B7%BB%E5%8A%A0mapping%E8%AE%BE%E7%BD%AE)
    - [(3) 可供选择的Analyzers](#3-%E5%8F%AF%E4%BE%9B%E9%80%89%E6%8B%A9%E7%9A%84analyzers)
  - [5 使用JSON / REST插入单条数据](#5-%E4%BD%BF%E7%94%A8json--rest%E6%8F%92%E5%85%A5%E5%8D%95%E6%9D%A1%E6%95%B0%E6%8D%AE)
  - [6 使用Bulk API批量插入数据](#6-%E4%BD%BF%E7%94%A8bulk-api%E6%89%B9%E9%87%8F%E6%8F%92%E5%85%A5%E6%95%B0%E6%8D%AE)
  - [7 更新数据](#7-%E6%9B%B4%E6%96%B0%E6%95%B0%E6%8D%AE)
    - [(1) 操作原理：`versioned update`](#1-%E6%93%8D%E4%BD%9C%E5%8E%9F%E7%90%86versioned-update)
    - [(2) 例子](#2-%E4%BE%8B%E5%AD%90)
  - [8 删除数据](#8-%E5%88%A0%E9%99%A4%E6%95%B0%E6%8D%AE)
  - [9 更多例子](#9-%E6%9B%B4%E5%A4%9A%E4%BE%8B%E5%AD%90)
    - [(1) Insert](#1-insert)
    - [(2) 查询](#2-%E6%9F%A5%E8%AF%A2)
    - [(3) Update](#3-update)
    - [(3) Delete](#3-delete)
  - [10 使用乐观锁处理并发](#10-%E4%BD%BF%E7%94%A8%E4%B9%90%E8%A7%82%E9%94%81%E5%A4%84%E7%90%86%E5%B9%B6%E5%8F%91)
    - [(1) Optimistic Concurrenty Control](#1-optimistic-concurrenty-control)
  - [11 使用Analyzers和Tokenizers](#11-%E4%BD%BF%E7%94%A8analyzers%E5%92%8Ctokenizers)
  - [12 Data Modeling以及父子表结构（Parent / Child Relationship）](#12-data-modeling%E4%BB%A5%E5%8F%8A%E7%88%B6%E5%AD%90%E8%A1%A8%E7%BB%93%E6%9E%84parent--child-relationship)
    - [(1) Normalize和Denormalize](#1-normalize%E5%92%8Cdenormalize)
    - [(2) `Parent / Child Relationship`：索引创建](#2-parent--child-relationship%E7%B4%A2%E5%BC%95%E5%88%9B%E5%BB%BA)
    - [(3) `Parent / Child Relationship`：索引查询](#3-parent--child-relationship%E7%B4%A2%E5%BC%95%E6%9F%A5%E8%AF%A2)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# CH02 数据索引

## 1 本章内容

以电影数据集为例，介绍如下内容

> Document插入、更新、删除操作
>
> 集群负载高时会遇到的问题
>
> Elasticsearch对文本的分词操作
>
> 处理数据的结构关系时所用到的Data Modeling技巧

## 2 访问Elasticsearch

> [原始视频](https://livevideo.manning.com/module/96_2_2/elasticsearch-7-and-elastic-stack/mapping-and-indexing-data/connecting-to-your-cluster)介绍了如何通过端口映射、以及ssh访问安装在VirtualBox上的Elasticsearch
>
> 因为使用了安装在MacOS本地的Elasticsearch、此节跳过

在MacOS上启动Elasticsearch

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/
> $ pwd
> /Users/fangkun/Run/elasticsearch-7.13.0
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/
> $ nohup ./bin/elasticsearch &
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/
> $ tail -n 50 nohup.out | grep started
> [2021-06-01T13:52:24,733][INFO ][o.e.n.Node               ] [node-1] started
> [2021-06-01T13:52:25,567][INFO ][o.e.c.r.a.AllocationService] [node-1] Cluster health status changed from [RED] to [YELLOW] (reason: [shards started [[shakespeare][0]]]).
> ~~~

## 3 [MovieLens](https://movielens.org/)数据集介绍

来自[MovieLens]()网站的电影点评数据集，下载地址是[http://grouplens.org/datasets/movielens](http://grouplens.org/datasets/movielens)

下载"recommended for education and development"的小数据集

> *Small*: 100,000 ratings and 3,600 tag applications applied to 9,000 movies by 600 users. Last updated 9/2018.
>
> - [README.html](https://files.grouplens.org/datasets/movielens/ml-latest-small-README.html)
> - [ml-latest-small.zip](https://files.grouplens.org/datasets/movielens/ml-latest-small.zip) (size: 1 MB)

下载解压

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ ls ml-latest-small/
> README.txt  links.csv   movies.csv  ratings.csv tags.csv
> ~~~

文件内容

> ~~~bash
> $ head -n3 ml-latest-small/*.csv
> ==> ml-latest-small/links.csv <==
> movieId,imdbId,tmdbId
> 1,0114709,862
> 2,0113497,8844
> 
> ==> ml-latest-small/movies.csv <==
> movieId,title,genres
> 1,Toy Story (1995),Adventure|Animation|Children|Comedy|Fantasy
> 2,Jumanji (1995),Adventure|Children|Fantasy
> 
> ==> ml-latest-small/ratings.csv <==
> userId,movieId,rating,timestamp
> 1,1,4.0,964982703
> 1,3,4.0,964981247
> 
> ==> ml-latest-small/tags.csv <==
> userId,movieId,tag,timestamp
> 2,60756,funny,1445714994
> 2,60756,Highly quotable,1445714996
> ~~~

数据集的详细信息可以在[README.html](https://files.grouplens.org/datasets/movielens/ml-latest-small-README.html)中看到

## 4 Analyzerz

### (1) `Mapping`

默认mapping推导以及手动设置mapping hint

> 用于为定义Schema，通常Elasticsearch可以根据数据推导出默认的mapping，但有时也需要手动设置给与提示，例如下面的例子，希望”year“的数据类型是”date“而不是string或number
>
> ~~~bash
> _________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -XPUT -H "Content-Type: application/json" 127.0.0.1:9200/movies -d '
> {
>   "mappings":{
>     "properties":{
>       "year":{"type":"date"}
>     }
>   }
> }'
> {"acknowledged":true,"shards_acknowledged":true,"index":"movies"}
> ~~~

### (2) 添加mapping设置

可以为Mapping添加如下设置

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_mapping_setup.jpg" width="800" /></div>

具体介绍如下

`Field Types`：把一个字段设置为指定类型

> 包括string、byte、short、integer、long、float、double、boolean、date

`Field Index`：指定一个字段如何用于生成反向索引

> 包括analyzed（全文索引）、not analyzed（不参与全文索引、只用作普通索引）、no（不用作索引）

`Field Analyzer`：  设置自定义操作，例如 

> character filters：字符过滤，比如过滤文本中的html标签等
>
> tokenizers：设置不同的tokenizer等、设定不同的语言
>
> token filters：转换为小写、词干提取、同义词、停用词等

### (3) 可供选择的Analyzers

`standard`：根据单词边界进行分词、删除标点符号、转换成小写；适合语言类型不确定的场景

`simple`：任何不是字母的符号、都作为单词分隔边界、并转换为小写

`whitespace`：根据whitespace来分词，可以保留标点符号

`language`：基于特定语言的分词（例如 english）、使用适合该语言的停用词表、并进行词干提取（stemming）

## 5 使用JSON / REST插入单条数据

发送PUT请求给下面带有`_doc`的url来插入一条数据，请求路径参考下面例子，其中`movies`是index name，`10948`是Document ID，`_doc`是index中document的数据类型（ES7已经取消、用doc表示默认类型）

> ~~~bash
> $ curl -H "Content-Type: application/json" -XPUT '127.0.0.1:9200/movies/_doc/109487' -d '
> {
>   "genre" : ["IMAX", "Sci-Fi"],
>   "title" : "interstellar",
>   "year"  : 2014
> }'
> {"_index":"movies","_type":"_doc","_id":"109487","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":0,"_primary_term":1}
> ~~~

查看index的schema可以模仿如下的RESTFUL请求

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XGET '127.0.0.1:9200/movies/_mapping'
> {"movies":{"mappings":{"properties":{"genre":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"title":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"year":{"type":"date"}}}}}_
> ~~~
>
> 在这个例子中是由上面的insert推导自动生成，但是在上一小节已经将"year"字段的类型改成了"date"

发送POST请求插入数据

> ~~~bash
> _________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPOST 127.0.0.1:9200/movies/_doc/109487 -d '
> {
>   "genre":["IMAX", "Sci-fi"],
>   "title":"Interstellar",
>   "year":2014
> }'
> {"_index":"movies","_type":"_doc","_id":"109487","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":1,"_primary_term":1}
> ~~~

查询插入的数据

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -XGET 127.0.0.1:9200/movies/_search?pretty
> {
>   "took" : 405,
>   "timed_out" : false,
>   "_shards" : {
>     "total" : 1,
>     "successful" : 1,
>     "skipped" : 0,
>     "failed" : 0
>   },
>   "hits" : {
>     "total" : {
>       "value" : 1,
>       "relation" : "eq"
>     },
>     "max_score" : 1.0,
>     "hits" : [
>       {
>         "_index" : "movies",
>         "_type" : "_doc",
>         "_id" : "109487",
>         "_score" : 1.0,
>         "_source" : {
>           "genre" : [
>             "IMAX",
>             "Sci-fi"
>           ],
>           "title" : "Interstellar",
>           "year" : 2014
>         }
>       }
>     ]
>   }
> }
> ~~~

## 6 使用Bulk API批量插入数据

在命令行中批量插入数据的例子如下

> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPUT 127.0.0.1:9200/_bulk -d '
> { "create" : { "_index" : "movies", "_id" : "135569" } }
> { "id" : "135569", "title" : "Star Trek Beyond", "year":2016, "genre":["Action", "Adventure", "Sci-Fi"] }
> { "create" : { "_index" : "movies", "_id" : "122886" } }
> { "id" : "122886", "title" : "Star Wars: Episode VII - The Force Awakens", "year":2015, "genre":["Action", "Adventure", "Fantasy", "Sci-Fi", "IMAX"] }
> '
> ~~~
>
> 每条数据都需要两行，是为了方便将数据分发到不同的shared上（第1行指定`_id`以用于确定发往哪个shared，第2行指定payload）

从文件中批量导入数据的例子如下（需要先清空已经加载的数据，重复key会导致该条数据插入失败，另外文件中的数字2011无法转换成日期）

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ wget http://media.sundog-soft.com/es/movies.json
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ cat movies.json # 查看数据
> { "create" : { "_index" : "movies", "_type" : "movie", "_id" : "135569" } }
> { "id": "135569", "title" : "Star Trek Beyond", "year":2016 , "genre":["Action", "Adventure", "Sci-Fi"] }
> { "create" : { "_index" : "movies", "_type" : "movie", "_id" : "122886" } }
> { "id": "122886", "title" : "Star Wars: Episode VII - The Force Awakens", "year":2015 , "genre":["Action", "Adventure", "Fantasy", "Sci-Fi", "IMAX"] }
> { "create" : { "_index" : "movies", "_type" : "movie", "_id" : "109487" } }
> { "id": "109487", "title" : "Interstellar", "year":2014 , "genre":["Sci-Fi", "IMAX"] }
> { "create" : { "_index" : "movies", "_type" : "movie", "_id" : "58559" } }
> { "id": "58559", "title" : "Dark Knight, The", "year":2008 , "genre":["Action", "Crime", "Drama", "IMAX"] }
> { "create" : { "_index" : "movies", "_type" : "movie", "_id" : "1924" } }
> { "id": "1924", "title" : "Plan 9 from Outer Space", "year":1959 , "genre":["Horror", "Sci-Fi"] }
> ___________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPUT 127.0.0.1:9200/_bulk?pretty --data-binary @movies.json
> {
>   "took" : 423,
>   "errors" : false,
>   "items" : [
>  	...
>     {
>       "create" : {
>         "_index" : "movies",
>         "_type" : "movie",
>         "_id" : "1924",
>         "_version" : 1,
>         "result" : "created",
>         "_shards" : {
>           "total" : 2,
>           "successful" : 1,
>           "failed" : 0
>         },
>         "_seq_no" : 4,
>         "_primary_term" : 1,
>         "status" : 201
>       }
>     }
>   ]
> }
> ~~~

## 7 更新数据

### (1) 操作原理：`versioned update`

> Elasticsearch的Document都是Immutable的，无法修改，但是它有一个`_version`字段
>
> 因此，当update一个Document时，其实是
>
> * 创建了一个有新的`_version`的Document
> * 同时将先前版本的Document置为deleted
>
> 当Elasticsearch后续清理数据时，会将先前的Document清理

### (2) 例子

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPOST 127.0.0.1:9200/movies/_doc/109487/_update -d '
>   {
>     "doc" : {
>       "title": "Interstellar"
>     }
>   }'
> {"_index":"movies","_type":"_doc","_id":"109487","_version":1,"result":"noop","_shards":{"total":0,"successful":0,"failed":0},"_seq_no":2,"_primary_term":1}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPOST 127.0.0.1:9200/movies/_doc/109487/_update -d '
> {
>   "doc" : {
>     "title": "Interstellar Updated"
>   }
> }'
> {"_index":"movies","_type":"_doc","_id":"109487","_version":2,"result":"updated","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":5,"_primary_term":1}
> ~~~
>
> 检查：可以看到查到的数据”version“是2，然后再update一次把数据改回来
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XGET 127.0.0.1:9200/movies/_doc/109487?pretty
> {
>   "_index" : "movies",
>   "_type" : "_doc",
>   "_id" : "109487",
>   "_version" : 2, 
>   "_seq_no" : 5,
>   "_primary_term" : 1,
>   "found" : true,
>   "_source" : {
>     "id" : "109487", "title" : "Interstellar Updated", "year" : 2014, "genre" : [ "Sci-Fi", "IMAX" ]
>   }
> }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPOST 127.0.0.1:9200/movies/_doc/109487/_update -d '
> {
>   "doc" : {
>     "title": "Interstellar"
>   }
> }'
> {"_index":"movies","_type":"_doc","_id":"109487","_version":3,"result":"updated","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":6,"_primary_term":1}
> ~~~

## 8 删除数据

例子如下

先找到要删除的Document的ID

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XGET 127.0.0.1:9200/movies/_search?q=Dark
> {"took":319,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":1,"relation":"eq"},"max_score":1.705694,"hits":[{"_index":"movies","_type":"movie","_id":"58559","_score":1.705694,"_source":{ "id": "58559", "title" : "Dark Knight, The", "year":2008 , "genre":["Action", "Crime", "Drama", "IMAX"] }}]}}
> ~~~

删除

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XDELETE 127.0.0.1:9200/movies/_doc/58559?pretty
> {
>   "_index" : "movies",
>   "_type" : "_doc",
>   "_id" : "58559",
>   "_version" : 2,
>   "result" : "deleted",
>   "_shards" : {
>     "total" : 2,
>     "successful" : 1,
>     "failed" : 0
>   },
>   "_seq_no" : 8,
>   "_primary_term" : 1
> }
> ~~~

检查

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XGET 127.0.0.1:9200/movies/_search?q=Dark
> {"took":2,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":0,"relation":"eq"},"max_score":null,"hits":[]}
> ~~~

## 9 更多例子

### (1) Insert

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/
> $ curl  -H "Content-Type: application/json" -XPUT 127.0.0.1:9200/movies/_doc/200000?pretty -d '
> {
>   "title": "Franke Adventures in Elasticsearch",
>   "genres" : ["Documentary"],
>   "year": 2019
> }'
> {
>   "_index" : "movies",
>   "_type" : "_doc",
>   "_id" : "200000",
>   "_version" : 1,
>   "result" : "created",
>   "_shards" : {
>     "total" : 2,
>     "successful" : 1,
>     "failed" : 0
>   },
>   "_seq_no" : 9,
>   "_primary_term" : 1
> }
> ~~~

### (2) 查询

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/
> $ curl -H "Content-Type:application/json" -XGET 127.0.0.1:9200/movies/_doc/200000?pretty
> {
>   "_index" : "movies",
>   "_type" : "_doc",
>   "_id" : "200000",
>   "_version" : 1,
>   "_seq_no" : 9,
>   "_primary_term" : 1,
>   "found" : true,
>   "_source" : {
>     "title" : "Franke Adventures in Elasticsearch",
>     "genres" : [
>       "Documentary"
>     ],
>     "year" : 2019
>   }
> }
> ~~~

### (3) Update

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/
> $ curl -H "Content-Type:application/json" -XPOST 127.0.0.1:9200/movies/_doc/200000/_update -d '
>   {
>     "doc" : {
>       "genres" : ["Documentary", "Comedy"]
>     }
>   }'
> {"_index":"movies","_type":"_doc","_id":"200000","_version":2,"result":"updated","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":10,"_primary_term":1}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/
> $ curl -H "Content-Type:application/json" -XGET 127.0.0.1:9200/movies/_doc/200000?pretty
> {
>   "_index" : "movies",
>   "_type" : "_doc",
>   "_id" : "200000",
>   "_version" : 2,
>   "_seq_no" : 10,
>   "_primary_term" : 1,
>   "found" : true,
>   "_source" : {
>     "title" : "Franke Adventures in Elasticsearch",
>     "genres" : [
>       "Documentary",
>       "Comedy"
>     ],
>     "year" : 2019
>   }
> }
> ~~~

### (3) Delete

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/
> $ curl -H "Content-Type:application/json" -XDELETE 127.0.0.1:9200/movies/_doc/200000?pretty
> {
>   "_index" : "movies",
>   "_type" : "_doc",
>   "_id" : "200000",
>   "_version" : 3,
>   "result" : "deleted",
>   "_shards" : {
>     "total" : 2,
>     "successful" : 1,
>     "failed" : 0
>   },
>   "_seq_no" : 11,
>   "_primary_term" : 1
> }
> ~~~

## 10 使用乐观锁处理并发

### (1) Optimistic Concurrenty Control

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_optimistic_concurrency_ctrl.jpg" width="600" /></div>

基于乐观锁，使用`<_seq_no, _primary_term>`二元组，可以标记一个数据的唯一时间序列

> * `_seq_no`（`sequence number`）由Primary Shared维护
> * 两个update作用在同一个`<_seq_no, _primary_term>`二元组上时、只有一个可以成功
> * 失败的update可以设置自动重试

例子

> 查询一个Document，可以看到`_primary_term`是1，_`seq_no`是6，`_version`是3
>
> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XGET 127.0.0.1:9200/movies/_doc/109487?pretty
> {
>   "_index" : "movies",
>   "_type" : "_doc",
>   "_id" : "109487",
>   "_version" : 3,
>   "_seq_no" : 6,
>   "_primary_term" : 1,
>   "found" : true,
>   "_source" : {
>     "id" : "109487",
>     "title" : "Interstellar",
>     "year" : 2014,
>     "genre" : [
>       "Sci-Fi",
>       "IMAX"
>     ]
>   }
> }
> ~~~
>
> 更新文档，但是希望将跟新只限制在`<seq_no=6, primary_term=1>`上，以便得到乐观锁的保护
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPUT '127.0.0.1:9200/movies/_doc/109487?if_seq_no=6&if_primary_term=1' -d '
> {
>   "genres" : ["IMAX", "Sci-Fi"],
>   "title" : "Interstellar foo",
>   "year" : 2014
> }'
> {"_index":"movies","_type":"_doc","_id":"109487","_version":4,"result":"updated","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":13,"_primary_term":1}
> ~~~
>
> 如果再次更新、同样作用在`seq_no=6, primary_term=1`上，会收到“ version conflict”异常，更新被乐观锁阻止。
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPUT '127.0.0.1:9200/movies/_doc/109487?if_seq_no=6&if_primary_term=1' -d '
> {
>   "genres" : ["IMAX", "Sci-Fi"],
>   "title" : "Interstellar foo",
>   "year" : 2014
> }'
> {"error":{"root_cause":[{"type":"version_conflict_engine_exception","reason":"[109487]: version conflict, required seqNo [6], primary term [1]. current document has seqNo [13] and primary term [1]","index_uuid":"pdIkO-6PRmWIii_1gmtGjA","shard":"0","index":"movies"}],"type":"version_conflict_engine_exception","reason":"[109487]: version conflict, required seqNo [6], primary term [1]. current document has seqNo [13] and primary term [1]","index_uuid":"pdIkO-6PRmWIii_1gmtGjA","shard":"0","index":"movies"},"status":409}
> ~~~
>
> 在上述情况下：
>
> 需要使用最新的`_seq_no`和`_primary_term`才能再次进行更新。
>
> 也可以使用retry_on_conflict来更新
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XPOST '127.0.0.1:9200/movies/_doc/109487/_update?retry_on_conflict=5' -d '
> {
>     "doc" : {
>       "title" : "Interstellar"
>     }
> }'
> {"_index":"movies","_type":"_doc","_id":"109487","_version":5,"result":"updated","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":14,"_primary_term":1}
> ~~~

## 11 使用Analyzers和Tokenizers

> Analyzers用来控制文本字段在搜索时如何被匹配，是严格匹配还是根据相关性进行匹配。

两种查询方法

> * 将字段类型设为“keyword”：会严格匹配并且区分大消息
> * 使用Analyzer：会开启相关性匹配，具体逻辑因Analyzer的不同而异；涉及词干提取、停用词、同义词匹配、是否区分大小写等 

使用“Star Trek”对"title"字段进行`相关性比配`，返回结果中的score指定了相似度

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XGET 127.0.0.1:9200/movies/_search?pretty -d '
>   {
>     "query": {
>       "match": {
>         "title": "Star Trek"
>       }
>     }
>   }'
> {
>   "took" : 113,
>   "timed_out" : false,
>   "_shards" : {
>     "total" : 1,
>     "successful" : 1,
>     "skipped" : 0,
>     "failed" : 0
>   },
>   "hits" : {
>     "total" : {
>       "value" : 2,
>       "relation" : "eq"
>     },
>     "max_score" : 2.729586,
>     "hits" : [
>       {
>         "_index" : "movies",
>         "_type" : "movie",
>         "_id" : "135569",
>         "_score" : 2.729586,
>         "_source" : {
>           "id" : "135569",
>           "title" : "Star Trek Beyond",
>           "year" : 2016,
>           "genre" : [
>             "Action",
>             "Adventure",
>             "Sci-Fi"
>           ]
>         }
>       },
>       {
>         "_index" : "movies",
>         "_type" : "movie",
>         "_id" : "122886",
>         "_score" : 0.73069775,
>         "_source" : {
>           "id" : "122886",
>           "title" : "Star Wars: Episode VII - The Force Awakens",
>           "year" : 2015,
>           "genre" : [
>             "Action",
>             "Adventure",
>             "Fantasy",
>             "Sci-Fi",
>             "IMAX"
>           ]
>         }
>       }
>     ]
>   }
> }
> ~~~

如果需要对某个字段进行精确匹配（例如下面的genre）、需要在Mapping中将该字段类型设置为"keyword"；

而对于默认的“text”类型、可以为它设置具体的analyzer以更好地进行相关性搜索

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type: application/json" -XDELETE 127.0.0.1:9200/movies
> {"acknowledged":true}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 定义index schema，其中将genre类型指定为keyword
> $ curl -H "Content-Type: application/json" -XPUT 127.0.0.1:9200/movies -d '
>   {
>     "mappings":{
>       "properties":{
>         "id":{"type":"integer"},
>         "year":{"type":"date"},
>         "genre":{"type":"keyword"},
>         "title":{"type":"text", "analyzer":"english"}
>       }
>     }
>   }'
> {"acknowledged":true,"shards_acknowledged":true,"index":"movies"}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> # 修改要导入的数据，将"_type":"movie"全部改成"_type":"_doc"，因为ES7不支持document type
> vim movies.json # 也可以使用sed命令修改
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查看修改后的结果
> $ diff movies.json backup_movies.json 
> 1c1
> < { "create" : { "_index" : "movies", "_type" : "_doc", "_id" : "135569" } }
> ---
> > { "create" : { "_index" : "movies", "_type" : "movie", "_id" : "135569" } }
> ...
> 9c9
> < { "create" : { "_index" : "movies", "_type" : "_doc", "_id" : "1924" } }
> ---
> > { "create" : { "_index" : "movies", "_type" : "movie", "_id" : "1924" } }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 导入数据
> $ curl -H "Content-Type: application/x-ndjson" -XPUT 127.0.0.1:9200/_bulk?pretty --data-binary @movies.json
> {
>   "took" : 43,
>   "errors" : false,
>   "items" : [
>     {
>       "create" : {
>         "_index" : "movies", "_type" : "_doc", "_id" : "135569", "_version" : 1, "result" : "created",
>         "_shards" : { "total" : 2, "successful" : 1, "failed" : 0 },
>         "_seq_no" : 0, "_primary_term" : 1, "status" : 201
>       }
>     },
>     ...
>   ]
> }
> ~~~

字段类型设为keyword之后，就可以进行精确搜索

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 相似度匹配已经被禁用
> $ curl -H "Content-Type: application/json" -XGET 127.0.0.1:9200/movies/_search?pretty -d '
>   {
>     "query": {
>       "match": {
>         "genre":"sci"
>       }
>     }
>   }
>   '
> {
>   "took" : 7,
>   "timed_out" : false,
>   "_shards" : {
>     "total" : 1,
>     "successful" : 1,
>     "skipped" : 0,
>     "failed" : 0
>   },
>   "hits" : {
>     "total" : {
>       "value" : 0,
>       "relation" : "eq"
>     },
>     "max_score" : null,
>     "hits" : [ ]
>   }
> }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 只容许精确匹配
> $ curl -H "Content-Type: application/json" -XGET 127.0.0.1:9200/movies/_search?pretty -d '
> {
>   "query": {
>     "match": {
>       "genre":"Sci-Fi"
>     }
>   }
> }
> '
> {
>   "took" : 6,
>   "timed_out" : false,
>   "_shards" : {
>     "total" : 1,
>     "successful" : 1,
>     "skipped" : 0,
>     "failed" : 0
>   },
>   "hits" : {
>     "total" : {
>       "value" : 4,
>       "relation" : "eq"
>     },
>     "max_score" : 0.40025333,
>     "hits" : [
>       {
>         "_index" : "movies",
>         "_type" : "_doc",
>         "_id" : "135569",
>         "_score" : 0.40025333,
>         "_source" : {
>           "id" : "135569",
>           "title" : "Star Trek Beyond",
>           "year" : 2016,
>           "genre" : [
>             "Action",
>             "Adventure",
>             "Sci-Fi"
>           ]
>         }
>       },
>       ...
>     ]
>   }
> }
> ~~~

## 12 Data Modeling以及父子表结构（Parent / Child Relationship）

### (1) Normalize和Denormalize

> 传统的RMDB：倾向于对数据进行Normalize、根据存储范式将数据存放在多张表中
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_rmdb_normalize.jpg" width="600" /></div>
>
> Casandra、Mongo DB等：倾向于Denormalize，对数据进行统一存储
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_denormalize.jpg" width="300" /></div>

Elasticsearch：既支持Denormalize也支持Normalize

在选择时，需要考虑如下因素

> * 跨表查询引起的查询延时（Denormalize可以避免）
> * 数据分散在不同节点上，带来的查询联表问题（Denormalize可以避免）
> * 数据存储是否更加紧凑（Normalize的优势，但现在存储设备比较便宜）
> * 更新和数据维护简单、避免多处修改（Normalize的优势）

### (2) `Parent / Child Relationship`：索引创建

以星球大战电影系列为例、电影系列与具体的多部影片，可以建模为如下的父子关系

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_parent_child_relationship_example.jpg" width="500" /></div>

相关的index创建过程如下

> 为表示`电影系列`的名为`series`设置mappings属性，属性起名为"film_to_franchise"
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XPUT 127.0.0.1:9200/series -d '
> {
>   "mappings": {
>     "properties": {
>       "film_to_franchise": {
>         "type": "join",
>         "relations": {"franchise":"film"}
>       }
>     }
>   }
> }'
> {"acknowledged":true,"shards_acknowledged":true,"index":"series"}
> ~~~

下载数据，可以看到数据中第1条的name是`franchise`，随后的3条都是`film`并且都通过parent字段指向了第一条的id

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 下载数据
> $ wget http://media.sundog-soft.com/es7/series.json
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查看数据
> $ head -n8 series.json
> { "create" : { "_index" : "series", "_id" : "1", "routing" : 1} }
> { "id": "1", "film_to_franchise": {"name": "franchise"}, "title" : "Star Wars" }
> { "create" : { "_index" : "series", "_id" : "260", "routing" : 1} }
> { "id": "260", "film_to_franchise": {"name": "film", "parent": "1"}, "title" : "Star Wars: Episode IV - A New Hope", "year":"1977" , "genre":["Action", "Adventure", "Sci-Fi"] }
> { "create" : { "_index" : "series", "_id" : "1196", "routing" : 1} }
> { "id": "1196", "film_to_franchise": {"name": "film", "parent": "1"}, "title" : "Star Wars: Episode V - The Empire Strikes Back", "year":"1980" , "genre":["Action", "Adventure", "Sci-Fi"] }
> { "create" : { "_index" : "series", "_id" : "1210", "routing" : 1} }
> { "id": "1210", "film_to_franchise": {"name": "film", "parent": "1"}, "title" : "Star Wars: Episode VI - Return of the Jedi", "year":"1983" , "genre":["Action", "Adventure", "Sci-Fi"] }
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 导入数据
> $ curl -H "Content-Type:application/json" -XPUT 127.0.0.1:9200/_bulk?pretty --data-binary @series.json
> {
>   "took" : 189,
>   "errors" : false,
>   "items" : [
>     {
>       "create" : {
>         "_index" : "series",
>         "_type" : "_doc",
>         "_id" : "1",
>         "_version" : 1,
>         "result" : "created",
>         "_shards" : {
>           "total" : 2,
>           "successful" : 1,
>           "failed" : 0
>         },
>         "_seq_no" : 0,
>         "_primary_term" : 1,
>         "status" : 201
>       }
>     },
> 	...
>   ]
> }
> ~~~

### (3) `Parent / Child Relationship`：索引查询

根据Parent查询Children：查询属于"Star Wars"系列的所有电影

> 即parent是title为Star Wars的franchise的所有film
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET 127.0.0.1:9200/series/_search?pretty -d '
> {
>   "query":{
>     "has_parent":{
>       "parent_type":"franchise",
>       "query":{
>         "match":{
>           "title":"Star Wars"
>         }
>       }
>     }
>   }
> }'
> {
>   "took" : 1052,
>   "timed_out" : false,
>   "_shards" : {
>     "total" : 1,
>     "successful" : 1,
>     "skipped" : 0,
>     "failed" : 0
>   },
>   "hits" : {
>     "total" : {
>       "value" : 7,
>       "relation" : "eq"
>     },
>     "max_score" : 1.0,
>     "hits" : [
>       {
>         "_index" : "series",
>         "_type" : "_doc",
>         "_id" : "260",
>         "_score" : 1.0,
>         "_routing" : "1",
>         "_source" : {
>           "id" : "260",
>           "film_to_franchise" : {
>             "name" : "film",
>             "parent" : "1"
>           },
>           "title" : "Star Wars: Episode IV - A New Hope",
>           "year" : "1977",
>           "genre" : [
>             "Action",
>             "Adventure",
>             "Sci-Fi"
>           ]
>         }
>       },
>       ...
>     ]
>   }
> }
> ~~~

根据Child查询Parent：例如查询《原力觉醒》所属于的电影系列

> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' -XGET 127.0.0.1:9200/series/_search?pretty -d '
>   {
>     "query" : {
>       "has_child" : {
>         "type" : "film",
>         "query" : {
>           "match" : {
>             "title" : "The Force Awakens"
>           }
>         }
>       }
>     }
>   }'
> {
>   "took" : 4,
>   "timed_out" : false,
>   "_shards" : {
>     "total" : 1,
>     "successful" : 1,
>     "skipped" : 0,
>     "failed" : 0
>   },
>   "hits" : {
>     "total" : {
>       "value" : 1,
>       "relation" : "eq"
>     },
>     "max_score" : 1.0,
>     "hits" : [
>       {
>         "_index" : "series",
>         "_type" : "_doc",
>         "_id" : "1",
>         "_score" : 1.0,
>         "_routing" : "1",
>         "_source" : {
>           "id" : "1",
>           "film_to_franchise" : {
>             "name" : "franchise"
>           },
>           "title" : "Star Wars"
>         }
>       }
>     ]
>   }
> }
> ~~~



