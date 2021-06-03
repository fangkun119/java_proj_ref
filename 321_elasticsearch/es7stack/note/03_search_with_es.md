[TOC]

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

## 10 部分匹配（Partial Matching）

> d

## 11 "Search as you type"

> d

## 12 N-Grams

> d