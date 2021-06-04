<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [CH03 使用Elasticsearch搜索](#ch03-%E4%BD%BF%E7%94%A8elasticsearch%E6%90%9C%E7%B4%A2)
  - [01 本章内容](#01-%E6%9C%AC%E7%AB%A0%E5%86%85%E5%AE%B9)
  - [02 使用“Query Lite Interface”进行搜索](#02-%E4%BD%BF%E7%94%A8query-lite-interface%E8%BF%9B%E8%A1%8C%E6%90%9C%E7%B4%A2)
  - [03 使用JSON进行搜索](#03-%E4%BD%BF%E7%94%A8json%E8%BF%9B%E8%A1%8C%E6%90%9C%E7%B4%A2)
    - [(1) 使用JSON作为Request Body进行搜索](#1-%E4%BD%BF%E7%94%A8json%E4%BD%9C%E4%B8%BArequest-body%E8%BF%9B%E8%A1%8C%E6%90%9C%E7%B4%A2)
    - [(2) `Query`和`Filters`](#2-query%E5%92%8Cfilters)
    - [(3) Filters介绍](#3-filters%E4%BB%8B%E7%BB%8D)
    - [(4) Queries介绍](#4-queries%E4%BB%8B%E7%BB%8D)
    - [(5) Query和Filter的区别](#5-query%E5%92%8Cfilter%E7%9A%84%E5%8C%BA%E5%88%AB)
    - [(5) 组合Query和Filter](#5-%E7%BB%84%E5%90%88query%E5%92%8Cfilter)
  - [04 短语搜索（Phrase Search）](#04-%E7%9F%AD%E8%AF%AD%E6%90%9C%E7%B4%A2phrase-search)
    - [(1) `match_phrase`](#1-match_phrase)
    - [(2) 对比`match`和`match_phrase`](#2-%E5%AF%B9%E6%AF%94match%E5%92%8Cmatch_phrase)
  - [05 各类搜索功能举例](#05-%E5%90%84%E7%B1%BB%E6%90%9C%E7%B4%A2%E5%8A%9F%E8%83%BD%E4%B8%BE%E4%BE%8B)
    - [(1) Query Lite的错误和正确用法](#1-query-lite%E7%9A%84%E9%94%99%E8%AF%AF%E5%92%8C%E6%AD%A3%E7%A1%AE%E7%94%A8%E6%B3%95)
    - [(2) 使用JSON作为查询请求](#2-%E4%BD%BF%E7%94%A8json%E4%BD%9C%E4%B8%BA%E6%9F%A5%E8%AF%A2%E8%AF%B7%E6%B1%82)
  - [06 分页（pagination）](#06-%E5%88%86%E9%A1%B5pagination)
  - [07 排序](#07-%E6%8E%92%E5%BA%8F)
  - [08 过滤器（Filters）的使用例子](#08-%E8%BF%87%E6%BB%A4%E5%99%A8filters%E7%9A%84%E4%BD%BF%E7%94%A8%E4%BE%8B%E5%AD%90)
  - [09 使用模糊查询（Fuzzy Queries）让查询能够兼容拼写错误](#09-%E4%BD%BF%E7%94%A8%E6%A8%A1%E7%B3%8A%E6%9F%A5%E8%AF%A2fuzzy-queries%E8%AE%A9%E6%9F%A5%E8%AF%A2%E8%83%BD%E5%A4%9F%E5%85%BC%E5%AE%B9%E6%8B%BC%E5%86%99%E9%94%99%E8%AF%AF)
  - [10 字符串字段部分匹配（Partial Matching）](#10-%E5%AD%97%E7%AC%A6%E4%B8%B2%E5%AD%97%E6%AE%B5%E9%83%A8%E5%88%86%E5%8C%B9%E9%85%8Dpartial-matching)
    - [(1) 字符串前缀查询](#1-%E5%AD%97%E7%AC%A6%E4%B8%B2%E5%89%8D%E7%BC%80%E6%9F%A5%E8%AF%A2)
    - [(2) 字符串通配符匹配](#2-%E5%AD%97%E7%AC%A6%E4%B8%B2%E9%80%9A%E9%85%8D%E7%AC%A6%E5%8C%B9%E9%85%8D)
    - [(3) 正则表达式匹配](#3-%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E5%8C%B9%E9%85%8D)
    - [(4) 例子](#4-%E4%BE%8B%E5%AD%90)
  - [11 "Search as you type"](#11-search-as-you-type)
  - [12 N-Grams](#12-n-grams)
    - [(1) N-Grams索引](#1-n-grams%E7%B4%A2%E5%BC%95)
    - [(2) Edge N-Gram索引](#2-edge-n-gram%E7%B4%A2%E5%BC%95)
    - [(3) Autocomplete Analyzer](#3-autocomplete-analyzer)
    - [(4) Completion Suggesters](#4-completion-suggesters)
    - [(5) 例子](#5-%E4%BE%8B%E5%AD%90)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# CH03 使用Elasticsearch搜索

## 01 本章内容

> * Elasticsearch的组成
> * 各种使用方法
> * 分页、过滤、处理模糊查询和部分匹配
> * 一些常见搜索功能的实现

## 02 使用“Query Lite Interface”进行搜索

功能：把上一章中使用JSON的查询请求，简化到url编码中，作为查询的快捷方式

用途：使用命令行工具或浏览器（需要能自动进行URL Encoding）使用简单的查询进行调试

例子：

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # "127.0.0.1:9200/movies/_search"：与上一章相同、查询名为movies的索引
> $ # "q"：JSON中“query”的缩写
> $ # "title:star": "title"能够match查询语句“star”
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?q=title:star'
> {"took":2,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":2,"relation":"eq"},"max_score":0.919734,"hits":[{"_index":"movies","_type":"_doc","_id":"135569","_score":0.919734,"_source":{ "id": "135569", "title" : "Star Trek Beyond", "year":2016 , "genre":["Action", "Adventure", "Sci-Fi"] }},{"_index":"movies","_type":"_doc","_id":"122886","_score":0.666854,"_source":{ "id": "122886", "title" : "Star Wars: Episode VII - The Force Awakens", "year":2015 , "genre":["Action", "Adventure", "Fantasy", "Sci-Fi", "IMAX"] }}]}}
> }__________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # "year:>2010": "year"字段的值大于2010
> $ # "+"：表示需要满足接下来的条件
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?q=+year:>2010+title:trek'
> {"took":125,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":3,"relation":"eq"},"max_score":2.456388,"hits":[{"_index":"movies","_type":"_doc","_id":"135569","_score":2.456388,"_source":{ "id": "135569", "title" : "Star Trek Beyond", "year":2016 , "genre":["Action", "Adventure", "Sci-Fi"] }},{"_index":"movies","_type":"_doc","_id":"122886","_score":1.0,"_source":{ "id": "122886", "title" : "Star Wars: Episode VII - The Force Awakens", "year":2015 , "genre":["Action", "Adventure", "Fantasy", "Sci-Fi", "IMAX"] }},{"_index":"movies","_type":"_doc","_id":"109487","_score":1.0,"_source":{ "id": "109487", "title" : "Interstellar", "year":2014 , "genre":["Sci-Fi", "IMAX"] }}]}}
> ~~~

因为如下缺点，不建议在生产环境使用

> (1) URL编码问题：URL中的特殊字符在发往服务器之前会进行URL编码、影响可读性；另外，如果查询条件中中有空格、也需要进行URL编码。下面是例子
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_querylite_url_encoding.jpg" width="500" /></div>
>
> (2) 可读性差、不便于debug
>
> (3) 暴露给End User会有安全隐患
>
> (4) 需要仔细拼写，容易出错

## 03 使用JSON进行搜索

### (1) 使用JSON作为Request Body进行搜索

> 因为使用url参数搜索的局限性，实际应用中仍然以使用JSON作为Request Body来进行搜索，之前已经介绍一部分，本小节进一步深入介绍
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_json_request_demo.jpg" width="500" /></div>
>
> 在最外层的`“query”:{ ... }`标签内部，可以放入两种搜索条件
>
> (1) Query：包括`match`、`match_all`、`multi_match`、`bool`等
>
> (2) Filters：包括`term`、`terms`、`range`、`exists`、`missing`、`bool`等

### (2) `Query`和`Filters`

> `filters`：回答`yes/no`的问题，计算更加快速，并且结果可以缓存
>
> `queries`（例如上面的match）：返回与查询条件（例如`match`）相关的结果，例如之前的各个例子

例子：

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_query_dsl_filter.jpg" width="500" /></div>
>
> `bool`：相当于and、将多个查询条件组合起来
>
> `"must":{"term" : {"title", "trek"}}`：title字段中必须包含内容是“trek”的term
>
> `"filter":{"range":{"year": {"gte":2010}}}`：year字段的值大于等于2010

查询语法较丰富、并且会随着Elasticsearch的版本而发生变化，需要查看最新的文档

### (3) Filters介绍

下面列出一些filter，查询官方文档可以得到完整的filter列表

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_query_dst_filter_examples.jpg" width="600" /></div>
>
> 其中的bool过滤器，用来将多个过滤器按照指定的boolean logic进行组合、包括：
>
> * `must`：按照logical AND关系进行组合
> * `must not`：相当于logical NOT
> * `should`：相当于logical OR

### (4) Queries介绍

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_query_dsl_queries_intro.jpg" width="900" /></div>
>
> 其中：
>
> * `match_all`是默认query，不指定query时其实使用的就是`match_all`，返回所有document作为结果

### (5) Query和Filter的区别

Query关注文档与查询字句的匹配**程度**，Filter是关注文档与查询字句**是否**匹配

> Query的关注点：(1) 是否包含；(2) 相关度得分；(3) 排序
>
> Filter的关注点：是否包含、不涉及评分、完全精确匹配或范围查找

性能差别：Filter速度更快并且可以（通过bitset）进行缓存

应用场景：

> Query用于全文检索以及相关性查询
>
> Filter用于其他场景

详细内容可参考[https://blog.csdn.net/laoyang360/article/details/80468757](https://blog.csdn.net/laoyang360/article/details/80468757)

### (5) 组合Query和Filter

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_query_dsl_combine_query_and_filter.jpg" width="600" /></div>
>
> * Query被包裹在`"query":{}`中、Filter被包裹在`"filter":{}`中
>
> * 可以在某些Query中放置Filter，也可以在某些Filter中放置Query
>
> 以上面的查询条件为例，是一个bool query，在这个query内部放置了一个must filter和一个range filter。

下面是具体的例子

> 例子1：没有使用filter的match query
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET 127.0.0.1:9200/movies/_search?pretty -d '
> {
>   "query": {
>     "match": {
>       "title":"star"
>     }
>   }
> }'
> ~~~
>
> 例子2：内嵌了1个range filter的bool query
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET 127.0.0.1:9200/movies/_search?pretty -d '
> {
>   "query": {
>     "bool": {
>       "must":[{"term":{"title":"trek"}}, {"match":{"title":"star"}}],
>       "filter":{"range":{"year":{"gte":2010}}}
>     }
>   }
> }'
> ~~~

## 04 短语搜索（Phrase Search）

### (1) `match_phrase`

使用`match_phrase`可以进行短语搜索，例如下面的查询，要求title中出现“star wars”短语

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_phrase_search_example.jpg" width="350" /></div>

可以使用`slop`来设置中单词之间的距离，例如下面的查询，容许在"star"和“beyond”之间出现最多1个其他单词

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_match_phrase_slop.jpg" width="400" /></div>

也可以进行模糊查询，例如希望词组中两个词只是距离比较近，但不做过于严格的限制，只是距离越近relevance score越高而已。例如下面的查询` {"query":"star force", "slop":10}`可以匹配`Star Wars: Episode VII - The Force Awakens`

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET 127.0.0.1:9200/movies/_search?pretty -d '
> {
>   "query":{
>     "match_phrase":{
>       "title": {"query":"star force", "slop":10}
>     }
>   }
> }'
> {
>   ...
>   "title" : "Star Wars: Episode VII - The Force Awakens",
>   ...
> }
> ~~~

### (2) 对比`match`和`match_phrase`

`"match": {"title":"star wars"}`：会把title中包含star，或者包含wars的所有Document都搜出来

`"match_phrase": {"title": {"query":"star wars"}}`：会把"star wars"当做一个短语来搜索，如果指定“slop”的值还可以进行模糊查询

例子如下

> 使用match查询
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET 127.0.0.1:9200/movies/_search?pretty -d '
> {
>   "query":{
>     "match":{
>       "title": "star wars"
>     }
>   }
> }' | grep title
>           "title" : "Star Wars: Episode VII - The Force Awakens",
>           "title" : "Star Trek Beyond",
> ~~~
>
> 使用match_phrase查询
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET 127.0.0.1:9200/movies/_search?pretty -d '
> {
>   "query":{
>     "match_phrase": {"title": {"query":"star wars"}}
>   }
> }' | grep title
>        "title" : "Star Wars: Episode VII - The Force Awakens",
> ~~~

## 05 各类搜索功能举例

### (1) Query Lite的错误和正确用法

> 用Query Lite Interface查1980年之后上映、title能够匹配“star wars”的电影

错误用法：没有对query条件进行url encoding，查出来的结果时错的

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET '127.0.0.1:9200/movies/_search?q=+year:>1980+title:star%20wars&pretty' | grep -E 'year|title'
>           "title" : "Star Wars: Episode VII - The Force Awakens",
>           "year" : 2015,
>           "title" : "Star Trek Beyond",
>           "year" : 2016,
>           "title" : "Interstellar",
>           "year" : 2014,
>           "title" : "Dark Knight, The",
>           "year" : 2008,
> ~~~

正确用法，对query进行url encoding

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET '127.0.0.1:9200/movies/_search?q=%2Byear%3A%3E1980+%2Btitle%3Astar%20wars&pretty' | grep -E 'year|title'
>           "title" : "Star Wars: Episode VII - The Force Awakens",
>           "year" : 2015,
>           "title" : "Star Trek Beyond",
>           "year" : 2016,
> ~~~

### (2) 使用JSON作为查询请求

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H "Content-Type:application/json" -XGET '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "bool" : {
>       "must": {"match_phrase": {"title":"Star Wars"}},
>       "filter": {"range": {"year": {"gte":1980}}}
>   }
> }
> }' | grep -E 'year|title'
>          "title" : "Star Wars: Episode VII - The Force Awakens",
>           "year" : 2015,
> ~~~

## 06 分页（pagination）

在查询条件中，使用`from`（从0开始计数）和`size`参数，可以进行分页查询

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_pagination.jpg" width="300" /></div>

查询语句如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_pagination_example.jpg" width="500" /></div>

但是注意过深的翻页（例如`from=10373&size=10`）会影响性能

> from或者size，任何一个值过大，都会影响性能
>
> 分页并不能节省性能，仍然需要通过查询条件限制扫描范围来节省性能

## 07 排序

如果排序字段没有被全文索引，可以直接使用`sort`来指定，例如：

> ~~~bash
> $ curl -H "Content-Type:application/json" -XGET '127.0.0.1:9200/movies/_search?sort=year&pretty' | grep -E "year|title"
>           "title" : "Plan 9 from Outer Space",
>           "year" : 1959,
>           "title" : "Dark Knight, The",
>           "year" : 2008,
>           "title" : "Interstellar",
>           "year" : 2014,
>           "title" : "Star Wars: Episode VII - The Force Awakens",
>           "year" : 2015,
>           "title" : "Star Trek Beyond",
>           "year" : 2016,
> ~~~

但是如果用于排序的字段，被进行了全文索引（类型是text），排序就会比较复杂，因为倒排表中存放的是文本被解析后得到的token，而不是完整的字段。如果对类型为text的字段进行排序，Elasticsearch会报错

如果既相对某个字段做`全文索引`，又想把它`用于排序`，需要为字段生成两份拷贝，例如下面定义的`title`字段

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fulltext_and_exactmatch_field.jpg" width="350" /></div>
>
> 字段title的type是text：代表这它会被全文索引
>
> 同时它有一个内部字段名为raw、类型是keyword：代表title.raw不会被全文索引，可用于排序

对于采用上面schema定义的数据，虽然title被全文索引而不能用于排序，但是title.raw可以用于排序

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_sorting_with_shadow_keyword.jpg" width="600" /></div>

但是**注意**：schema一旦被定义就不能修改，需要在最初定义schema时想清楚这些（是否要建立全文索引、是否要设置用于排序的keyword，是否都需要）

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 对全文索引（类型为text）的字段排序，会抛异常
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?sort=title&pretty' | grep exception
>         "type" : "illegal_argument_exception",
>     "type" : "search_phase_execution_exception",
>           "type" : "illegal_argument_exception",
>       "type" : "illegal_argument_exception",
>         "type" : "illegal_argument_exception",
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 删除索引
> $ curl -H 'Content-Type:application/json' -XDELETE '127.0.0.1:9200/movies'
> {"acknowledged":true}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 新建索引，指定一个类型为keyword的shadow字段用于sorting
> $ curl -H 'Content-Type:application/json' -XPUT 127.0.0.1:9200/movies/ -d '
> {
>   "mappings": {
>     "properties" : {
>       "title" : {
>         "type" : "text",
>         "fields" : {
>           "raw" : {
>             "type" : "keyword"
>           }
>         }
>       }
>     }
>   }
> }'
> {"acknowledged":true,"shards_acknowledged":true,"index":"movies"}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 重新导入数据
> $ curl -H 'Content-Type:application/json' -XPUT 127.0.0.1:9200/_bulk --data-binary @movies.json
> {"took":105,"errors":false,"items":[{"create":{"_index":"movies","_type":"_doc","_id":"135569","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":0,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"122886","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":1,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"109487","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":2,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"58559","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":3,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"1924","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":4,"_primary_term":1,"status":201}}]
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 使用新建的shadow字段title.raw可以进行排序
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?sort=title.raw&pretty' | grep title
>           "title" : "Dark Knight, The",
>           "title" : "Interstellar",
>           "title" : "Plan 9 from Outer Space",
>           "title" : "Star Trek Beyond",
>           "title" : "Star Wars: Episode VII - The Force Awakens",
> ~~~

## 08 过滤器（Filters）的使用例子

一些更加复杂的例子

例子1：

> ~~~bash
> $ curl -H 'Content-Type:application/json' -XGET 127.0.0.1:9200/movies/_search?pretty -d'
> {
>   "query": {
>      "bool": {
>        "must":{"match":{"genre":"Sci-Fi"}},
>        "must_not":{"match":{"title":"trek"}},
>        "filter":{"range":{"year":{"gte":2010, "lt":2015}}}
>      }
>   }
> }' | grep -E 'genre|title|year'
>           "title" : "Interstellar",
>           "year" : 2014,
>           "genre" : [
> ~~~

例子2：

> ~~~bash
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?sort=title.raw&pretty' -d '
> {
>   "query":{
>     "bool": {
>       "must": {"match": {"genre":"Sci-Fi"}},
>       "filter": {"range": {"year": {"lt": 1960}}}
>     }
>   }
> }' | grep -E 'title|genre|year' -A3
>           "title" : "Plan 9 from Outer Space",
>           "year" : 1959,
>           "genre" : [
>             "Horror",
>             "Sci-Fi"
>           ]
> ~~~

## 09 使用模糊查询（Fuzzy Queries）让查询能够兼容拼写错误

> Fuzzy Queries可以用于处理拼写错误，它基于`Levenshtein编辑距离`（即从单词A转变成单词别、所需要的（1）替换字符（2）插入字符（3）删除字符的次数）

例子如下：其中`fuzziness`表示能容忍的最大编辑距离

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_fuzzy_query_intro_demo.jpg" width="500" /></div>

如果没有设置fuzziness参数，将使用默认值`fuzziness:AUTO`，它代表者

> 长度为1-2的字符串：fuzziness=0
>
> 长度为3-5的字符串：fuzziness=1
>
> 长度大于5的字符串：fuzziness=2

## 10 字符串字段部分匹配（Partial Matching）

> 需要字段类型是'text'

### (1) 字符串前缀查询

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_prefix_matching_on_string.jpg" width="400" /></div>

### (2) 字符串通配符匹配

> 例如下面的`1*`
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_wildcard_query.jpg" width="400" /></div>

### (3) 正则表达式匹配

> 也支持正则表达式查询、具体可查询文档

### (4) 例子

> 首先删除并重建索引，将year字段的类型改为字符串，并重新导入数据
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' -XDELETE '127.0.0.1:9200/movies'
> {"acknowledged":true}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' -XPUT '127.0.0.1:9200/movies' -d '
>   {
>       "mappings": {
>         "properties": {
>           "year": {"type":"text"}
>         }
>       }
>   }'
> {"acknowledged":true,"shards_acknowledged":true,"index":"movies"}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $  curl -H 'Content-Type:application/json' -XPUT 127.0.0.1:9200/_bulk --data-binary @movies.json
> {"took":128,"errors":false,"items":[{"create":{"_index":"movies","_type":"_doc","_id":"135569","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":0,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"122886","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":1,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"109487","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":2,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"58559","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":3,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"1924","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":4,"_primary_term":1,"status":201}}]}
> ~~~
>
> 执行部分匹配查询
>
> ~~~bash
> __________________________________________________________________
> $ # 前缀查询
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "prefix": {
>       "year":"201"
>     }
>   }
> }' | grep -E 'title|year'
>           "title" : "Star Trek Beyond",
>           "year" : 2016,
>           "title" : "Star Wars: Episode VII - The Force Awakens",
>           "year" : 2015,
>           "title" : "Interstellar",
>           "year" : 2014,          
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 通配符查询
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "wildcard": {
>       "year":"1*"
>     }
>   }
> }' | grep -E 'title|year'
>           "title" : "Plan 9 from Outer Space",
>           "year" : 1959,
> ~~~

## 11 "Search as you type"

功能：类似google的搜索提示

可以使用`match_phrase_prefix`来完成，还可以通过制定slop来对”prefix phrase“进行模糊匹配，下面是一个例子（只是slop值设置的过高了）

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_search_as_you_type.jpg" width="400" /></div>

例子

> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "match_phrase_prefix": {
>       "title": {
>         "query":"star", "slop":10
>       }
>     }
>   }
> }' | grep title
>           "title" : "Star Trek Beyond",
>           "title" : "Star Wars: Episode VII - The Force Awakens",
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "match_phrase_prefix": {
>       "title": {
>         "query":"star tr", "slop":10
>       }
>     }
>   }
> }' | grep title
>           "title" : "Star Trek Beyond",
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # t匹配了结果1的Trek中的T，结果2的The中的t
> $ # 同时slop=10起了作用
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "match_phrase_prefix": {
>       "title": {
>         "query":"star t", "slop":10
>       }
>     }
>   }
> }' | grep title
>           "title" : "Star Trek Beyond",
>           "title" : "Star Wars: Episode VII - The Force Awakens",
> ~~~

## 12 N-Grams

### (1) N-Grams索引

N-Grams索引可以用于加快上一小节中的“搜索提示”功能的查询速度

以单词”star“为例，从`1-grams`到`4-grams`生成的索引如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_ngram_idx_by_exmp_star.jpg" width="600" /></div>

对于搜索提示（”search as you are typing"）的场景、可以通过这类索引来完成，例如

> 当键入“s”时，可以匹配unigram索引中的s，进一步找到对应的单词
>
> 键入到“st”时，可以匹配bigram索引；……；键入到“star”时可以额匹配到4-gram

### (2) Edge N-Gram索引

在N-gram索引中，有一种叫做`Edge N-gram`更加适合上面的”搜索提示“应用场景

> `Edge N-Gram`只从filed的起始位置开始建立索引、在上面例子中分别对应“s”、“st”、“sta”、“star”

### (3) Autocomplete Analyzer

可以使用`n-gram索引`创建`autocomplete_filter`并进一步创建`autocomplete analyzer`，例子如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_autocomplete_analyzer.jpg" width="600" /></div>
>
> 使用了从1-gram到20-gram的`edge_ngram`索引，最多支持长度为20的字符串前缀

在创建mapping时，将其上面的analyzer作用在需要的text field上，例如

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_apply_autocomplete_analyzer_on_field.jpg" width="500" /></div>

但是设置了基于n-gram的analyzer之后，对这个filed的默认查询行为也会发生改变：默认会把query term也拆成同样数量的n-gram，然后匹配各个ngram索引。

如果不想要这种查询行为，需要设置`"analyzer":"standard"`重新改回使用标准analyzer

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_reset_to_standard_analyzer.jpg" width="600" /></div>

### (4) Completion Suggesters

> 除了上面的autocomplete analyzer，ES还提供completion suggesters功能，可以通过提前上传一个列表、来实现”自动完成"/"搜索建议"这样的功能

### (5) 例子

> 删除index，新建并设置autocomplete analyzer，重新导入数据到index
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 删除索引
> $ curl -H 'Content-Type:application/json' -XDELETE '127.0.0.1:9200/movies'
> {"acknowledged":true}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 创建n-gram analyzer
> $ curl -H 'Content-Type:application/json' -XPUT '127.0.0.1:9200/movies?pretty' -d '
> {
>   "settings": {
>     "analysis": {
>       "filter": {
>         "autocomplete_filter": { "type":"edge_ngram", "min_gram":1, "max_gram":20 }
>       },
>       "analyzer": {
>         "autocomplete": { "type":"custom", "tokenizer":"standard", "filter": ["lowercase", "autocomplete_filter"]}
>       }
>     }
>   }
> }'
> {
>   "acknowledged" : true,
>   "shards_acknowledged" : true,
>   "index" : "movies"
> }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查看n-gram analyzer的功能
> $ curl -H 'Content-Type:application/json' -XGET 127.0.0.1:9200/movies/_analyze?pretty -d '
> {
>   "analyzer":"autocomplete",
>   "text":"Sta"
> }'
> {
>   "tokens" : [
>     {"token" : "s",   "start_offset" : 0, "end_offset" : 3, "type" : "<ALPHANUM>", "position" : 0},
>     {"token" : "st",  "start_offset" : 0, "end_offset" : 3, "type" : "<ALPHANUM>", "position" : 0},
>     {"token" : "sta", "start_offset" : 0, "end_offset" : 3, "type" : "<ALPHANUM>", "position" : 0}
>   ]
> }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 将analyzer作用在某个text field上
> $ curl -H "Content-Type:application/json" -XPUT '127.0.0.1:9200/movies/_mapping?pretty' -d'
>   {
>     "properties": {
>       "title": {
>         "type":"text",
>         "analyzer":"autocomplete"
>       }
>     }
>   }'
> {
>   "acknowledged" : true
> }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 导入数据
> $ curl -H 'Content-Type:application/json' -XPUT 127.0.0.1:9200/_bulk --data-binary @movies.json
> {"took":130,"errors":false,"items":[{"create":{"_index":"movies","_type":"_doc","_id":"135569","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":0,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"122886","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":1,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"109487","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":2,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"58559","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":3,"_primary_term":1,"status":201}},{"create":{"_index":"movies","_type":"_doc","_id":"1924","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":4,"_primary_term":1,"status":201}}]}
> ~~~
>
> 对设置了autocomplete analyzer的字段title进行查询，发现虽然“Plan 9 from Outer Space”不包含“sta”但仍然那鞥被搜索到，是因为”sta“中的1-edge-gram “s”可以匹配到“Space"中的1-edge-gram ”S“
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "match" : {
>       "title": "sta"
>     }
>   }
> }' | grep title
>           "title" : "Star Trek Beyond",
>           "title" : "Star Wars: Episode VII - The Force Awakens",
>           "title" : "Plan 9 from Outer Space",
> ~~~
>
> 因此如果不希望使用n-gram进行搜索，避免上述问题，可以指定
>
> * Query Side所使用analyzer为Standard Analyzer
> * 于此同时Index Side仍然会使用之前的Autocomplete Analyzer
>
> 此时搜索“sta”返回的结果正确了
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "match" : {
>       "title": {
>         "query": "sta",
>         "analyzer": "standard"
>       }
>     }
>   }
> }' | grep title
>           "title" : "Star Trek Beyond",
>           "title" : "Star Wars: Episode VII - The Force Awakens",
> ~~~
>
> 但是如果继续模拟用户的查询，搜索”star tr“，这是所有结果出现了我们不想要的”Star Wars：..."，这是因为standard analyzer对于每个token都独立进行处理所导致的。虽然“Tr”不能匹配”Star War:..."中的任何单词，但仍然将其作为结果返回。好在借助Index Side使用的Autocomplete Analyzer，因此“Star Trek……”的score仍然比“Star Wars”高。
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' '127.0.0.1:9200/movies/_search?pretty' -d '
> {
>   "query": {
>     "match" : {
>       "title": {
>         "query": "star tr",
>         "analyzer": "standard"
>       }
>     }
>   }
> }' | grep title
>           "title" : "Star Trek Beyond",
>           "title" : "Star Wars: Episode VII - The Force Awakens",
> ~~~
>
> 要想彻底解决上述问题，还需要使用“Completion Suggesters”来进行查询



