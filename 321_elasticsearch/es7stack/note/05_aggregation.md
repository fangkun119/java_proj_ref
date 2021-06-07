<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [CH05 Aggregation](#ch05-aggregation)
  - [01 本章内容](#01-%E6%9C%AC%E7%AB%A0%E5%86%85%E5%AE%B9)
  - [02 功能介绍](#02-%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D)
  - [03 Metrics：简单聚合](#03-metrics%E7%AE%80%E5%8D%95%E8%81%9A%E5%90%88)
  - [04 Bucket：以Histograms为例](#04-bucket%E4%BB%A5histograms%E4%B8%BA%E4%BE%8B)
    - [(1) 介绍](#1-%E4%BB%8B%E7%BB%8D)
    - [(2) 代码](#2-%E4%BB%A3%E7%A0%81)
    - [(3) 演示](#3-%E6%BC%94%E7%A4%BA)
  - [04 时间序列](#04-%E6%97%B6%E9%97%B4%E5%BA%8F%E5%88%97)
  - [06 嵌套聚合](#06-%E5%B5%8C%E5%A5%97%E8%81%9A%E5%90%88)
    - [(1) 方法](#1-%E6%96%B9%E6%B3%95)
    - [(2) 演示](#2-%E6%BC%94%E7%A4%BA)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# CH05 Aggregation

## 01 本章内容

> 聚合数据以用于生成报表和可视化图表等

## 02 功能介绍

功能

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_aggregation_intro.jpg" width="600" /></div>

特点

> 速度比Hadoop、Spark快，支持实时查询
>
> 支持嵌套Aggregation，进行复杂查询

## 03 Metrics：简单聚合

几个演示如下

(1) 根据rating字段的值，统计这个term（即名为rating的term）的数量

> 其中的url参数`size=0`表示不要返回search result，因此只会返回aggratation result
>
> * "ratings”是aggregation的名称
> * "term”是aggregation执行的具体计算，表示统计term数量
> * "field:rating"表示使用的term是rating字段
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/ratings/_search?size=0&pretty' -d '
> {
>   "aggs": {
>     "ratings": {
>       "terms": {
>           "field":"rating"
>       }
>     }
>   }
> }'
> {
>   "took" : 563,
>   "timed_out" : false,
>   "_shards" : { "total" : 1, "successful" : 1, "skipped" : 0, "failed" : 0},
>   "hits" : {
>     "total" : { "value" : 10000, "relation" : "gte"}, "max_score" : null, "hits" : [ ]
>   },
>   "aggregations" : {
>     "ratings" : {
>       "doc_count_error_upper_bound" : 0,
>       "sum_other_doc_count" : 0,
>       "buckets" : [
>         { "key" : 4.0, "doc_count" : 26818 },
>         { "key" : 3.0, "doc_count" : 20047 },
>         { "key" : 5.0, "doc_count" : 13211 },
>         { "key" : 3.5, "doc_count" : 13136 },
>         { "key" : 4.5, "doc_count" : 8551  },
>         { "key" : 2.0, "doc_count" : 7551  },
>         { "key" : 2.5, "doc_count" : 5550  },
>         { "key" : 1.0, "doc_count" : 2811  },
>         { "key" : 1.5, "doc_count" : 1791  },
>         { "key" : 0.5, "doc_count" : 1370  }
>       ]
>     }
>   }
> }
> ~~~

(2) 也可以先query，再在查询结果上进行aggreation

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/ratings/_search?size=0&pretty' -d '
>   {
>     "query":{
>       "match":{
>         "rating":5.0
>       }
>     },
>     "aggs":{
>       "ratings":{
>         "terms":{
>           "field":"rating"
>         }
>       }
>     }
>   }'
> {
>   "took" : 64,
>   "timed_out" : false,
>   "_shards" : { "total" : 1, "successful" : 1, "skipped" : 0, "failed" : 0},
>   "hits" : {
>     "total" : { "value" : 10000, "relation" : "gte"}, "max_score" : null, "hits" : [ ]
>   },
>   "aggregations" : {
>     "ratings" : {
>       "doc_count_error_upper_bound" : 0,
>       "sum_other_doc_count" : 0,
>       "buckets" : [
>         {"key" : 5.0, "doc_count" : 13211}
>       ]
>     }
>   }
> }
> ~~~

(3) 另一个例子：query是查询某部电影，aggragation是计算rating字段的平均值，“avg_rating”是aggregation的名称，执行的具体聚合方法是“avg”

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/ratings/_search?size=0&pretty' -d '
>   {
>     "query":{
>       "match_phrase": {
>         "title":"Star Wars Episode IV"
>       }
>     },
>     "aggs":{
>       "avg_rating":{
>         "avg":{
>           "field":"rating"
>         }
>       }
>     }
>   }'
> {
>   "took" : 123,
>   "timed_out" : false,
>   "_shards" : { "total" : 1, "successful" : 1, "skipped" : 0, "failed" : 0},
>   "hits" : {
>     "total" : { "value" : 251, "relation" : "eq"}, "max_score" : null, "hits" : [ ]
>   },  
>   "aggregations" : {
>     "avg_rating" : {
>       "value" : 4.231075697211155
>     }
>   }
> }
> ~~~

## 04 Bucket：以Histograms为例

### (1) 介绍

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_aggregation_histogram_intro.jpg" width="500" /></div>

### (2) 代码

例子1

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_aggregation_histogram.jpg" width="500" /></div>
>
> 执行名为"whole_ratings"的aggregation，具体的操作是以1.0为间隔，计算rating字段的histogram

例子2

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es_aggregation_histogram2.jpg" width="400" /></div>
>
> 执行名为“release”的aggregation，具体的操作是以10位间隔，在字段“year”上计算histogram

### (3) 演示

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/ratings/_search?size=0&pretty' -d '
> {
>   "aggs":{
>     "whole_ratings":{
>       "histogram":{
>         "field":"rating",
>         "interval":"1.0"
>       }
>     }
>   }
> }'
> {
>   "took" : 186,
>   "timed_out" : false,
>   "_shards" : {"total" : 1, "successful" : 1, "skipped" : 0, "failed" : 0 },
>   "hits" : {"total" : {"value" : 10000, "relation" : "gte"}, "max_score" : null, "hits" : [ ]},
>   "aggregations" : {
>     "whole_ratings" : {
>       "buckets" : [
>         {"key" : 0.0, "doc_count" : 1370 },
>         {"key" : 1.0, "doc_count" : 4602 },
>         {"key" : 2.0, "doc_count" : 13101},
>         {"key" : 3.0, "doc_count" : 33183},
>         {"key" : 4.0, "doc_count" : 35369},
>         {"key" : 5.0, "doc_count" : 13211}
>       ]
>     }
>   }
> }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/movies/_search?size=0&pretty' -d '
> {
>   "aggs":{
>     "release":{
>       "histogram":{
>         "field":"year",
>         "interval":"10"
>      }
>    }
>  }
> }'
> {
>   "took" : 34,
>   "timed_out" : false,
>   "_shards" : {"total" : 1, "successful" : 1, "skipped" : 0, "failed" : 0 },  
>   "hits" : {"total" : {"value" : 9742, "relation" : "eq"}, "max_score" : null, "hits" : [ ]},
>   "aggregations" : {
>     "release" : {
>       "buckets" : [
>         {"key" : 1900.0, "doc_count" : 3  },
>         {"key" : 1910.0, "doc_count" : 7  },
>         {"key" : 1920.0, "doc_count" : 37 },
>         {"key" : 1930.0, "doc_count" : 136},
>         {"key" : 1940.0, "doc_count" : 197},
>         ...
>         {"key" : 2010.0, "doc_count" : 1950}
>       ]
>     }
>   }
> }
> ~~~

## 04 时间序列

> Elasticsearch可以对时间序列进行分桶和聚合、例如得到“year”，“month"粒度的统计数据

例子1：进行timestamp聚合，生成一个基于字段timestamp以hour为粒度的date_histogram

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_aggregation_timeseries_1.jpg" width="500" /></div>

例子2：与Query结合使用

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_aggregation_timeseries_2.jpg" width="500" /></div>

例子3

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/kafka-logs/_search?size=0&pretty' -d '
> {
>   "aggs": {
>     "timestamp": {
>       "date_histogram": {
>         "field":"@timestamp",
>         "interval":"hour"
>       }
>     }
>   }
> }'
> ...
> ~~~

例子4

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_aggregation_time_series.jpg" width="500" /></div>

## 06 嵌套聚合

### (1) 方法

> 将数据在多个维度上聚合，例如下面的二维聚合：计算每一部电影、各个评分的数量
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_aggregation_nested_1.jpg" width="500" /></div>
>
> 其中`title.raw`上之前章节创建的类型为`keyword`的字段（创建过程参考[02_mapping_and_indexing_data.md](02_mapping_and_indexing_data.md)的第11章）用于精确匹配
>
> 而`title`的类型是用于全文索引的`text`、无法用于aggregation

### (2) 演示

 类型为text的title字段无法用于聚合

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/ratings/_search?size=0&pretty' -d '
> {
>   "query":{
>     "match_phrase":{
>       "title":"Star Wars"
>     }
>   },
>   "aggs":{
>     "titles":{
>       "terms":{"field":"title"},
>       "aggs":{
>         "avg_rating":{
>           "avg":{
>             "field":"rating"
>           }
>         }
>       }
>     }
>   }
> }'
> {
>   "error" : {
>     "root_cause" : [
>       {
>         "type" : "illegal_argument_exception",
>         "reason" : "Text fields are not optimised for operations that require per-document field data like aggregations and sorting, so these operations are disabled by default. Please use a keyword field instead. Alternatively, set fielddata=true on [title] in order to load field data by uninverting the inverted index. Note that this can use significant memory."
>       }
>     ],
>     "type" : "search_phase_execution_exception",
>     "reason" : "all shards failed",
>     "phase" : "query",
>     "grouped" : true,
>     "failed_shards" : [
>       {
>         "shard" : 0,
>         "index" : "ratings",
>         "node" : "_VUsBiWrSP6T51YjGPMj2A",
>         "reason" : {
>           "type" : "illegal_argument_exception",
>           "reason" : "Text fields are not optimised for operations that require per-document field data like aggregations and sorting, so these operations are disabled by default. Please use a keyword field instead. Alternatively, set fielddata=true on [title] in order to load field data by uninverting the inverted index. Note that this can use significant memory."
>         }
>       }
>     ],
>     "caused_by" : {
>       "type" : "illegal_argument_exception",
>       "reason" : "Text fields are not optimised for operations that require per-document field data like aggregations and sorting, so these operations are disabled by default. Please use a keyword field instead. Alternatively, set fielddata=true on [title] in order to load field data by uninverting the inverted index. Note that this can use significant memory.",
>       "caused_by" : {
>         "type" : "illegal_argument_exception",
>         "reason" : "Text fields are not optimised for operations that require per-document field data like aggregations and sorting, so these operations are disabled by default. Please use a keyword field instead. Alternatively, set fielddata=true on [title] in order to load field data by uninverting the inverted index. Note that this can use significant memory."
>       }
>     }
>   },
>   "status" : 400
> }
> ~~~

先尝试错误提示中给出的第二种方法，将fielddata属性设置为true，这种方法的优点是不需要re-indexing，可以在线运行而不是删除并重建索引

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/ratings/_mapping?pretty' -d '
> {
>   "properties":{
>     "title":{
>       "type":"text",
>       "fielddata":true
>     }
>   }
> }'
> {
>   "acknowledged" : true
> }
> ~~~

`fielddata`属性设置为true之后，再次尝试在"title"字段上聚合，虽然命令可以执行。

然而发现**聚合并没有发生在title字段上，而是发生在title字段的分词token上**。

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/ratings/_search?size=0&pretty' -d '
> {
>   "query":{
>     "match_phrase":{
>       "title":"Star Wars"
>     }
>   },
>   "aggs":{
>     "titles":{
>       "terms":{"field":"title"},
>       "aggs":{
>         "avg_rating":{
>           "avg":{
>             "field":"rating"
>           }
>         }
>       }
>     }
>   }
> }'
> {
>   "took" : 978,
>   "timed_out" : false,
>   "_shards" : {"total" : 1, "successful" : 1, "skipped" : 0, "failed" : 0},
>   "hits" : {"total" : {"value" : 1062, "relation" : "eq"}, "max_score" : null, "hits" : [ ]},
>   "aggregations" : {
>     "titles" : {
>       "doc_count_error_upper_bound" : 0,
>       "sum_other_doc_count" : 3424,
>       "buckets" : [
>         {"key" : "star"   , "doc_count" : 1062, "avg_rating" : {"value" : 3.8587570621468927}},
>         {"key" : "wars"   , "doc_count" : 1062, "avg_rating" : {"value" : 3.8587570621468927}},
>         {"key" : "episode", "doc_count" : 1009, "avg_rating" : {"value" : 3.8785926660059467}},
>         {"key" : "the"    , "doc_count" : 779 , "avg_rating" : {"value" : 3.7362002567394095}},
>         ...
>         {"key" : "new"    , "doc_count" : 251 , "avg_rating" : {"value" : 4.231075697211155}}
>       ]
>     }
>   }
> }
> ~~~

仍然需要回到方法1：删除并重建索引，建索引时为`title`增加一个类型为keyword的`title.raw` 字段（sub field）

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 删除索引
> $ curl -XDELETE 127.0.0.1:9200/ratings
> {"acknowledged":true}
> _________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 重建索引
> $ curl -H 'Content-Type:application/json' -XPUT 127.0.0.1:9200/ratings/ -d '
> {
>   "mappings":{
>     "properties":{
>       "title":{
>         "type":"text",
>         "fielddata":true,
>         "fields":{
>            "raw":{
>              "type":"keyword"
>            }
>         }
>       }
>     }
>   }
> }'
> {"acknowledged":true,"shards_acknowledged":true,"index":"ratings"}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 修改导数据脚本，注释掉删除index的那行，因为index已经被上面的curl给删除了
> $ vi IndexRatings.py
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查看修改结果
> $ diff IndexRatings.py backup.IndexRatings.py
> 36c36
> < # es.indices.delete(index="ratings",ignore=404)
> ---
> > es.indices.delete(index="ratings",ignore=404)
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 重新导入数据
> $ python3 IndexRatings.py
> /usr/local/lib/python3.9/site-packages/elasticsearch/connection/base.py:208: ElasticsearchWarning: Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.13/security-minimal-setup.html to enable security.
>   warnings.warn(message, category=ElasticsearchWarning)
> ~~~

` IndexRatings.py`的代码参考[`../demo/ch05/IndexRatings.py`](../demo/ch05/IndexRatings.py)

使用类型为keyword的`title.raw`字段进行查询，这次将可以得到期望的结果

> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/ratings/_search?size=0&pretty' -d '
> {
>   "query":{
>     "match_phrase":{
>       "title":"Star Wars"
>     }
>   },
>   "aggs":{
>     "titles":{
>       "terms":{"field":"title.raw"},
>       "aggs":{
>         "avg_rating":{
>           "avg":{
>             "field":"rating"
>           }
>         }
>       }
>     }
>   }
> }'
> {
>   "took" : 9,
>   "timed_out" : false,
>   "_shards" : {
>     "total" : 1,
>     "successful" : 1,
>     "skipped" : 0,
>     "failed" : 0
>   },
>   "hits" : {
>     "total" : {
>       "value" : 1062,
>       "relation" : "eq"
>     },
>     "max_score" : 7.2487316,
>     "hits" : [
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "BUfB5XkBSQhEl4CEEkNt",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 298,
>           "movie_id" : 61160,
>           "title" : "Star Wars: The Clone Wars (2008)",
>           "rating" : 2.5,
>           "timestamp" : 1453032806
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "vUfB5XkBSQhEl4CEECQk",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 249,
>           "movie_id" : 179819,
>           "title" : "Star Wars: The Last Jedi (2017)",
>           "rating" : 4.5,
>           "timestamp" : 1514315792
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "VkbB5XkBSQhEl4CECLYY",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 62,
>           "movie_id" : 179819,
>           "title" : "Star Wars: The Last Jedi (2017)",
>           "rating" : 3.5,
>           "timestamp" : 1523048605
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "XUbB5XkBSQhEl4CECLYY",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 62,
>           "movie_id" : 187595,
>           "title" : "Solo: A Star Wars Story (2018)",
>           "rating" : 4.0,
>           "timestamp" : 1528934550
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "9UbB5XkBSQhEl4CEBaHh",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 21,
>           "movie_id" : 61160,
>           "title" : "Star Wars: The Clone Wars (2008)",
>           "rating" : 1.0,
>           "timestamp" : 1468113607
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "4kbB5XkBSQhEl4CEDOpw",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 153,
>           "movie_id" : 179819,
>           "title" : "Star Wars: The Last Jedi (2017)",
>           "rating" : 0.5,
>           "timestamp" : 1525553024
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "eUfB5XkBSQhEl4CEDgil",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 210,
>           "movie_id" : 179819,
>           "title" : "Star Wars: The Last Jedi (2017)",
>           "rating" : 4.5,
>           "timestamp" : 1527266081
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "ikfB5XkBSQhEl4CEDxAP",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 220,
>           "movie_id" : 61160,
>           "title" : "Star Wars: The Clone Wars (2008)",
>           "rating" : 3.0,
>           "timestamp" : 1244606048
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "ZEfB5XkBSQhEl4CEFnQw",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 380,
>           "movie_id" : 61160,
>           "title" : "Star Wars: The Clone Wars (2008)",
>           "rating" : 2.5,
>           "timestamp" : 1494696399
>         }
>       },
>       {
>         "_index" : "ratings",
>         "_type" : "_doc",
>         "_id" : "gkfB5XkBSQhEl4CEFnVc",
>         "_score" : 7.2487316,
>         "_source" : {
>           "user_id" : 380,
>           "movie_id" : 179819,
>           "title" : "Star Wars: The Last Jedi (2017)",
>           "rating" : 0.5,
>           "timestamp" : 1536872721
>         }
>       }
>     ]
>   },
>   "aggregations" : {
>     "titles" : {
>       "doc_count_error_upper_bound" : 0,
>       "sum_other_doc_count" : 7,
>       "buckets" : [
>         {
>           "key" : "Star Wars: Episode IV - A New Hope (1977)",
>           "doc_count" : 251,
>           "avg_rating" : {
>             "value" : 4.231075697211155
>           }
>         },
>         {
>           "key" : "Star Wars: Episode V - The Empire Strikes Back (1980)",
>           "doc_count" : 211,
>           "avg_rating" : {
>             "value" : 4.2156398104265405
>           }
>         },
>         {
>           "key" : "Star Wars: Episode VI - Return of the Jedi (1983)",
>           "doc_count" : 196,
>           "avg_rating" : {
>             "value" : 4.137755102040816
>           }
>         },
>         {
>           "key" : "Star Wars: Episode I - The Phantom Menace (1999)",
>           "doc_count" : 140,
>           "avg_rating" : {
>             "value" : 3.107142857142857
>           }
>         },
>         {
>           "key" : "Star Wars: Episode II - Attack of the Clones (2002)",
>           "doc_count" : 92,
>           "avg_rating" : {
>             "value" : 3.157608695652174
>           }
>         },
>         {
>           "key" : "Star Wars: Episode III - Revenge of the Sith (2005)",
>           "doc_count" : 78,
>           "avg_rating" : {
>             "value" : 3.4294871794871793
>           }
>         },
>         {
>           "key" : "Star Wars: Episode VII - The Force Awakens (2015)",
>           "doc_count" : 41,
>           "avg_rating" : {
>             "value" : 3.8536585365853657
>           }
>         },
>         {
>           "key" : "Rogue One: A Star Wars Story (2016)",
>           "doc_count" : 27,
>           "avg_rating" : {
>             "value" : 3.925925925925926
>           }
>         },
>         {
>           "key" : "Star Wars: The Last Jedi (2017)",
>           "doc_count" : 12,
>           "avg_rating" : {
>             "value" : 3.125
>           }
>         },
>         {
>           "key" : "Star Wars: The Clone Wars (2008)",
>           "doc_count" : 7,
>           "avg_rating" : {
>             "value" : 2.357142857142857
>           }
>         }
>       ]
>     }
>   }
> }
> ~~~





