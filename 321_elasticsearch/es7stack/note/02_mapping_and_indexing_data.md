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

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/upload/es7_mapping_setup.png" width="800" /></div>

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

## 9 练习

> 略

## 10 处理并发（Concurrency）

> 

## 11 使用Analyzers和Tokenizers

> 

## 12 Data Modeling以及父子关系

> 