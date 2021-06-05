[TOC]

# CH04 向索引中导入数据

## 01 本章内容

> 数据并不总是可以通过REST API导入的JSON文件、例如他们可能是Log Stash搜集的Web日志、是RMBD或者NoSQL中的数据，或者来自Kafka、Hadoop、Spark等。
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_import_approach.jpg" width="700" /></div>
>
> 本章讨论这些方法

## 02 使用脚本导入数据

> 从某个外部系统获取数据，将数据转换成指定格式的JSON文件，通过Elasticsearch的HTTP/REST bulk inserts API将数据导入Elasticsearch集群。例如下面的Python脚本
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_import_data_with_python2.jpg" width="600" /></div>

完整过程

> 数据来自[http://files.grouplens.org/datasets/movielens/ml-latest-small.zip](http://files.grouplens.org/datasets/movielens/ml-latest-small.zip)
>
> Python脚本可以从[http://media.sundog-soft.com/es7/MoviesToJson.py](http://media.sundog-soft.com/es7/MoviesToJson.py)下载
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # Python脚本和数据文件
> $ ls ml-latest-small/movies.csv *.py
> MoviesToJson.py            ml-latest-small/movies.csv
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 调用Python脚本，解析数据，生成用于导入数据到Elastion的JSON Bulk Payload
> $ python3 MoviesToJson.py > moremovies.json
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查看Payload中的前4条数据
> $ head -n4 moremovies.json
> { "create" : { "_index": "movies", "_id" : "1" } }
> { "id": "1", "title": "Toy Story", "year":1995, "genre":["Adventure","Animation","Children","Comedy","Fantasy"] }
> { "create" : { "_index": "movies", "_id" : "2" } }
> { "id": "2", "title": "Jumanji", "year":1995, "genre":["Adventure","Children","Fantasy"] }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 删除索引
> $ curl -H 'Content-Type:application/json' -XDELETE '127.0.0.1:9200/movies'
> {"acknowledged":true}
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 导入数据重新创建索引
> $ # 数据量比较大，将response重定向到文件log.txt中，以便检查导入情况
> $ curl -H 'Content-Type:application/json' -XPUT '127.0.0.1:9200/_bulk' --data-binary @moremovies.json 2>&1 >log.txt
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查询索引
> $ curl -H 'Content-Type:application/json' -XGET "127.0.0.1:9200/movies/_search?q=mary%20poppins&pretty" | grep -i --color -E 'mary|poppins|score'
>  "max_score" : 18.292715,
>      "_score" : 18.292715,
>        "title" : "Mary Poppins",
>      "_score" : 8.223341,
>        "title" : "Mary Reilly",
>      "_score" : 7.0822973,
>        "title" : "Mary Shelley's Frankenstein",
>      "_score" : 7.0822973,
>        "title" : "Mary of Scotland",
>      "_score" : 7.0822973,
>        "title" : "Mary and Max",
>      "_score" : 6.219324,
>        "title" : "There's Something About Mary",
>      "_score" : 2.9663239,
>        "title" : "Adventures of Mary-Kate and Ashley, The: The Case of the Christmas Caper",
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ mkdir -p ../demo/ch04/01_load_by_script ../demo/ch04/01_load_by_scrip/ml-latest-small
> $ cp MoviesToJson.py ../demo/ch04/01_load_by_script/
> $ cp moremovies.json ../demo/ch04/01_load_by_script/
> ~~~

相关代码： [`../demo/ch04/01_load_by_scrip/`](../demo/ch04/01_load_by_scrip/)

数据文件`ml-latest-small/movies.csv`来自[grouplens.org/datasets/movielens](http://grouplens.org/datasets/movielens)的"recommended for education and development"小数据集[ml-latest-small.zip](https://files.grouplens.org/datasets/movielens/ml-latest-small.zip) 

## 03 使用Client Lib导入数据

Client Lib举例

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_import_by_client_1_example_libs.jpg" width="600" /></div>

例如之前的一组curl命令，可以用下面的python API简单替换

> ~~~python
> es = elasticsearch.Elasticsearch()
> es.indices.delete(index="ratings", ignore=404)
> deuqe(helpers.parallel_bulk(es, readRatings(), index="ratings"), maxlen=0)
> es.indices.refresh()
> ~~~

代码如下

(1) `IndexRatings.py`

> ~~~python
> import csv
> from collections import deque
> import elasticsearch
> from elasticsearch import helpers
> 
> def readMovies():
> 	csvfile = open('ml-latest-small/movies.csv', 'r')
> 	reader = csv.DictReader( csvfile )
> 	titleLookup = {}
> 	for movie in reader:
> 		titleLookup[movie['movieId']] = movie['title']
> 	return titleLookup
> 
> def readRatings():
> 	csvfile = open('ml-latest-small/ratings.csv', 'r')
> 	titleLookup = readMovies()
> 	reader = csv.DictReader( csvfile )
> 	for line in reader:
> 		rating = {}
> 		rating['user_id'] = int(line['userId'])
> 		rating['movie_id'] = int(line['movieId'])
> 		rating['title'] = titleLookup[line['movieId']]
> 		rating['rating'] = float(line['rating'])
> 		rating['timestamp'] = int(line['timestamp'])
> 		yield rating
> 
> es = elasticsearch.Elasticsearch()
> 
> es.indices.delete(index="ratings",ignore=404)
> deque(helpers.parallel_bulk(es,readRatings(),index="ratings"), maxlen=0)
> es.indices.refresh()
> ~~~

(2) IndexTags.py

> ~~~python
> import csv
> from collections import deque
> import elasticsearch
> from elasticsearch import helpers
> 
> def readMovies():
>     csvfile = open('ml-latest-small/movies.csv', 'r')
>     reader = csv.DictReader( csvfile )
>     titleLookup = {}
>     for movie in reader:
>             titleLookup[movie['movieId']] = movie['title']
>     return titleLookup
> 
> def readTags():
>     csvfile = open('ml-latest-small/tags.csv', 'r')
>     titleLookup = readMovies()
>     reader = csv.DictReader( csvfile )
>     for line in reader:
>         tag = {}
>         tag['user_id'] = int(line['userId'])
>         tag['movie_id'] = int(line['movieId'])
>         tag['title'] = titleLookup[line['movieId']]
>         tag['tag'] = line['tag']
>         tag['timestamp'] = int(line['timestamp'])
>         yield tag
> 
> es = elasticsearch.Elasticsearch()
> 
> es.indices.delete(index="tags",ignore=404)
> deque(helpers.parallel_bulk(es,readTags(),index="tags"), maxlen=0)
> es.indices.refresh()
> ~~~

完整过程

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 安装elasticsearch package
> $ pip3 install elasticsearch
> ...
> Installing collected packages: urllib3, certifi, elasticsearch
> Successfully installed certifi-2021.5.30 elasticsearch-7.13.1 urllib3-1.26.5
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 下载脚本，代码内容见上面的讲解部分
> $ wget http://media.sundog-soft.com/es7/IndexRatings.py
> ...
> IndexRatings.py          100%[================================>]    1023  --.-KB/s  用时 0s
> 2021-06-05 12:18:12 (81.3 MB/s) - 已保存 “IndexRatings.py” [1023/1023])
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ wget http://media.sundog-soft.com/es7/IndexTags.py
> ...
> IndexTags.py             100%[================================>]     974  --.-KB/s  用时 0s
> 2021-06-05 13:43:41 (66.3 MB/s) - 已保存 “IndexTags.py” [974/974])
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 用到的文件
> $ ls ml-latest-small/ratings.csv ml-latest-small/movies.csv IndexRatings.py IndexTags.py
> IndexRatings.py             ml-latest-small/movies.csv
> IndexTags.py                ml-latest-small/ratings.csv
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 执行脚本创建索引
> $ python3 IndexRatings.py
> $ /usr/local/lib/python3.9/site-packages/elasticsearch/connection/base.py:208: ElasticsearchWarning: Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.13/security-minimal-setup.html to enable security.warnings.warn(message, category=ElasticsearchWarning)
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ /usr/local/Cellar/python\@3.9/3.9.1/bin/python3 IndexTags.py
> /usr/local/lib/python3.9/site-packages/elasticsearch/connection/base.py:208: ElasticsearchWarning: Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.13/security-minimal-setup.html to enable security.
>   warnings.warn(message, category=ElasticsearchWarning)
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查询新建好的索引
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/ratings/_search?pretty' | grep title
>           "title" : "Little Miss Sunshine (2006)",
>           "title" : "Fly Away Home (1996)",
>           "title" : "Cinderella (1950)",
>           "title" : "Tombstone (1993)",
>           "title" : "Robin Hood: Prince of Thieves (1991)",
>           "title" : "Brady Bunch Movie, The (1995)",
>           "title" : "Leaving Las Vegas (1995)",
>           "title" : "Silence of the Lambs, The (1991)",
>           "title" : "Snow White and the Seven Dwarfs (1937)",
>           "title" : "Schindler's List (1993)",
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Context-Type:application/json' -XGET '127.0.0.1:9200/tags/_search?pretty' | grep -i -E 'title|tag' | head -n 6
>         "_index" : "tags",
>           "title" : "Step Brothers (2008)",
>           "tag" : "funny",
>         "_index" : "tags",
>           "title" : "Step Brothers (2008)",
>           "tag" : "Highly quotable",
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 保存脚本
> $ mkdir ../demo/ch04/02_load_with_python
> $ cp IndexRatings.py ../demo/ch04/02_load_with_python/
> $ cp IndexTags.py ../demo/ch04/02_load_with_python/
> ~~~

数据文件`ml-latest-small/ratings.csv`来自[grouplens.org/datasets/movielens](http://grouplens.org/datasets/movielens)的"recommended for education and development"小数据集[ml-latest-small.zip](https://files.grouplens.org/datasets/movielens/ml-latest-small.zip) 

相关代码：[../demo/ch04/02_load_with_python/](../demo/ch04/02_load_with_python/)

## 04 Logstash介绍

在系统中的应用

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_logstash_in_systems.jpg" width="380" /></div>

功能特性

> * 解析、转换、过滤数据
> * 从非结构数据中推测数据格式：例如可以将Web logs转化为结构化的数据、并为每个字段设置名称
> * 对隐私数据进行匿名化、甚至删除
> * 支持地理位置查询：例如可以根据web access log的IP地址计算出访问来源
> * 可以通过增加节点来扩充性能
> * 保证每条信息至少被传递一次
> * 对突增流量进行缓冲（削峰填谷）

更多LogStash插件

> [http://www.elastic.co/guide/en/logstash/current/filter-plugins.html](http://www.elastic.co/guide/en/logstash/current/filter-plugins.html)

LogStash支持的数据源

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_logstash_input_source.jpg" width="600" /></div>

LogStash支持的输出目的地

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_logstash_destinations.jpg" width="600" /></div>

典型使用方式

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_logstash_typical_usage.jpg" width="500" /></div>

## 05 Logstash安装和运行：以导入Apache日志为例

### (1) 在Ubuntu上安装和运行logstash

安装

> ~~~bash
> sudo apt install openjdk-8-jre-headless
> sudo apt-get update
> sudo apt-get install logstash
> ~~~

配置文件

> 路径是`/etc/logstash/conf.d/logstash.conf`，样本如下
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_logstash_conf.jpg" width="500" /></div>
>
> 可以根据需要设置不同的input、output、filter插件，在上面的例子中
>
> * 输入：使用file插件，读取文件作为输入源，从文件起始位置开始读取
> * 过滤：
>     * `grok`过滤器：如何从日志中提取数据，对于apache log，可以使用logstash提供的COMBINEDAPACHELOG过滤器来提取
>     * `date`过滤器：时间格式，用于从日志中提取时间
> * 输出：同时向两个destionation输出
>     * 运行在本地的9200端口的Elasticsearch
>     * 使用`rubydebug format` 来格式化的终端打印

运行

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_run_logstash_on_ubuntu.jpg" width="500" /></div>

### (2) 在Mac上安装和运行logstash

参考文档

> [https://www.elastic.co/guide/en/logstash/current/installing-logstash.html](https://www.elastic.co/guide/en/logstash/current/installing-logstash.html)
>
> [https://princeli.com/mac%E5%AE%89%E8%A3%85elasticsearch%E3%80%81logstash%E3%80%81kibana/](https://princeli.com/mac%E5%AE%89%E8%A3%85elasticsearch%E3%80%81logstash%E3%80%81kibana/)

安装过程

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ brew tap elastic/tap 
> $ # 如果卡在Updating Homebrew步骤、按一次Ctrl+C等几秒钟即可
> $ # 如果报fatal: unable to access 'https://github.com/elastic/homebrew-tap/': LibreSSL SSL_connect: SSL_ERROR_SYSCALL in connection to github.com:443错误
> $ # 让termnal翻墙或者把https协议改成git协议手动git clone，都可以解决
> ...
> __________________________________________________________________
> $ brew install elastic/tap/logstash-full
> Updating Homebrew...
> ^C==> Installing logstash-full from elastic/tap
> ...
> ==> Summary
> 🍺  /usr/local/Cellar/logstash-full/7.13.1: 13,627 files, 583.5MB, built in 1 minute 1 second
> ~~~

安装位置：`/usr/local/Cellar/logstash-{suffix}/{version}/`

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/Homebrew/Library/Taps/elastic/
> $ ls /usr/local/Cellar/logstash-full/7.13.1/
> INSTALL_RECEIPT.json              bin
> LICENSE.txt                       homebrew.mxcl.logstash-full.plist
> NOTICE.TXT                        libexec
> ~~~

运行

> ~~~bash
> logstash -v ${config_path}
> ~~~

### (3) Demo过程（在MacOS上）

> 下载用作输入的Apach日志
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 下载一个Apache Log样本文件
> $ wget media.sundog-soft.com/es/access_log
> ...
> access_log               100%[================================>]  22.12M  40.5KB/s  用时 4m 56s
> 2021-06-05 16:55:26 (76.7 KB/s) - 已保存 “access_log” [23200421/23200421])
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查看文件内容
> $ head -n2 access_log
> 216.244.66.246 - - [30/Apr/2017:04:28:11 +0000] "GET /docs/triton/pages.html HTTP/1.1" 200 5639 "-" "Mozilla/5.0 (compatible; DotBot/1.1; http://www.opensiteexplorer.org/dotbot, help@moz.com)"
> 199.21.99.207 - - [30/Apr/2017:04:29:44 +0000] "GET /docs/triton/class_triton_1_1_wind_fetch-members.html HTTP/1.1" 200 1845 "-" "Mozilla/5.0 (compatible; YandexBot/3.0; +http://yandex.com/bots)"
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 将文件拷贝到一个方便实验配置的目录下
> $ mkdir -p /Users/fangkun/tmp/; mv access_log /Users/fangkun/tmp/; ls -l /Users/fangkun/tmp/access_log
> -rw-r--r--  1 fangkun  staff  23200421  5  6  2017 /Users/fangkun/tmp/access_log
> ~~~
>
> 修改配置文件
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/logstash/
> $ cd /usr/local/etc/logstash/; ls
> jvm.options          logstash-sample.conf pipelines.yml
> log4j2.properties    logstash.yml         startup.options
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/logstash/
> $ # 编写配置文件
> $ vi logstash-file-demo.conf
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/logstash/
> $ # 查看并检查配置文件
> $ cat logstash-file-demo
> input {
> 	file {
> 		path => "/Users/fangkun/tmp/access_log"
> 		start_position => "beginning"
> 	}
> }
> filter {
> 	grok {
> 		match => {"message" => "%{COMBINEDAPACHELOG}"}
> 	}
> 	date {
> 		match => ["timestamp", "dd/MMM/yyyy:HH:mm:ss Z"]
> 	}
> }
> output {
> 	elasticsearch {
> 		hosts => ["localhost:9200"]
> 	}
> 	stdout {
> 		codec => rubydebug
> 	}
> }
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/logstash/
> $ # 配置文件存放路径
> $ ls $(pwd)/logstash-file-demo.conf
> /usr/local/etc/logstash/logstash-file-demo.conf
> ~~~
>
> 运行LogStash，文件处理完之后按Ctrl+C使其退出
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ logstash -f /usr/local/etc/logstash/logstash-demo.conf
> Using bundled JDK: /usr/local/Cellar/logstash-full/7.13.1/libexec/jdk.app/Contents/Home
> OpenJDK 64-Bit Server VM warning: Option UseConcMarkSweepGC was deprecated in version 9.0 and will likely be removed in a future release.
> ...
> {
>            "verb" => "GET",
>         "message" => "54.210.20.202 - - [05/May/2017:18:01:44 +0000] \"GET /creativo_portfolio-sitemap.xml HTTP/1.1\" 200 658 \"http://sundog-soft.com/creativo_portfolio-sitemap.xml\" \"W3 Total Cache\"",
>        "@version" => "1",
>            "auth" => "-",
>           "agent" => "\"W3 Total Cache\"",
>       "timestamp" => "05/May/2017:18:01:44 +0000",
>     "httpversion" => "1.1",
>        "referrer" => "\"http://sundog-soft.com/creativo_portfolio-sitemap.xml\"",
>            "host" => "fangkundeMacBook-Pro.local",
>           "bytes" => "658",
>        "clientip" => "54.210.20.202",
>        "response" => "200",
>            "path" => "/Users/fangkun/tmp/access_log",
>         "request" => "/creativo_portfolio-sitemap.xml",
>      "@timestamp" => 2017-05-05T18:01:44.000Z,
>           "ident" => "-"
> }
> ^C[2021-06-05T20:25:17,031][WARN ][logstash.runner          ] SIGINT received. Shutting down.
> [2021-06-05T20:25:17,168][INFO ][filewatch.observingtail  ] QUIT - closing all files and shutting down.
> [2021-06-05T20:25:18,268][INFO ][logstash.javapipeline    ][main] Pipeline terminated {"pipeline.id"=>"main"}
> [2021-06-05T20:25:19,319][INFO ][logstash.runner          ] Logstash shut down.
> ~~~
>
> 查看LogStash导入到Elasticsearch中的数据，由于配置文件中为指定ES的索引，因此LogStash会自己生成，具体的取值需要查询ES
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查找所有的索引名称
> $ # _cat代表category
> $ # -v表示verbose
> $ curl -H "Content-Type:application/json" -XGET '127.0.0.1:9200/_cat/indices?v'
> health status index                      uuid                   pri rep docs.count docs.deleted store.size pri.store.size
> yellow open   movies                     nmXW0XGfSdCut1lIA4ZwXw   1   1       9742            0      1.2mb          1.2mb
> yellow open   logstash-2021.06.05-000001 I3buDoupRx2hWDH2aoPM6Q   1   1     102972            0     27.7mb         27.7mb
> yellow open   ratings                    NhDE2pTESJeJnzMwtTo0zA   1   1     100836            0      9.7mb          9.7mb
> yellow open   series                     ODKNPVnoRJazMKCHbQjBrw   1   1          8            0      9.5kb          9.5kb
> yellow open   shakespeare                fJmCN9gySUaRG_HZvPNsMQ   1   1     111396            0     18.1mb         18.1mb
> yellow open   tags                       X2-SkCcXQU21rXDeZm6_CA   1   1       3683            0      634kb          634kb
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查看logstash写入到Elasticsearch中的数据
> $ curl -H "Content-Type:application/json" -XGET '127.0.0.1:9200/logstash-2021.06.05-000001/_search?pretty' | head -n50
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
>       "value" : 10000,
>       "relation" : "gte"
>     },
>     "max_score" : 1.0,
>     "hits" : [
>       {
>         "_index" : "logstash-2021.06.05-000001",
>         "_type" : "_doc",
>         "_id" : "8UId3HkBSQhEl4CE5FHx",
>         "_score" : 1.0,
>         "_source" : {
>           "verb" : "POST",
>           "message" : "185.85.191.201 - - [01/May/2017:21:28:41 +0000] \"POST /wp-login.php HTTP/1.1\" 200 6164 \"http://sundog-soft.com/wp-login.php\" \"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36\"",
>           "@version" : "1",
>           "auth" : "-",
>           "agent" : "\"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36\"",
>           "timestamp" : "01/May/2017:21:28:41 +0000",
>           "httpversion" : "1.1",
>           "referrer" : "\"http://sundog-soft.com/wp-login.php\"",
>           "host" : "fangkundeMacBook-Pro.local",
>           "bytes" : "6164",
>           "clientip" : "185.85.191.201",
>           "response" : "200",
>           "path" : "/Users/fangkun/tmp/access_log",
>           "request" : "/wp-login.php",
>           "@timestamp" : "2017-05-01T21:28:41.000Z",
>           "ident" : "-"
>         }
>         ...
>         
> ~~~

## 06 使用Logstash导入MySQL中的数据

### (1) 准备JDBC Driver

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_logstash_jdbc_driver_prep.jpg" width="800" /></div>

### (2) 用于JDBC数据源的Input Config

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es_logstash_input_cfg_for_jdbc_source.jpg" width="800" /></div>
>
> 可以指定提取数据的SQL
>
> 其余部分（filter、output）与上一小节Demo中的配置相同

### (3) Demo过程（MacOS上）

安装并启动MySQL

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_mysql_server_on_mac.jpg" width="500" /></div>
>

下载并解压数据

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ wget http://files.grouplens.org/datasets/movielens/ml-100k.zip
> $ ls -lh ml-100k.zip
> ...
> -rw-r--r--  1 fangkun  staff   4.7M 12  4  2019 ml-100k.zip
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ unzip ml-100k.zip
> ...
> inflating: ml-100k/ub.base
> inflating: ml-100k/ub.test
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 要加载到MySQL中的数据
> $ ls $(pwd)/ml-100k/u.item
> /Users/fangkun/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/ml-100k/u.item
> ~~~
>

建库建表，导入数据到MySQL

> SQL
>
> ~~~sql
> CREATE DATABASE movielens;
> CREATE TABLE movielens.movies (
> 	movieID INT PRIMARY KEY NOT NULL,
>     title TEXT,
>     releaseDate DATE
>     );
> LOAD DATA LOCAL INFILE 'ml-100k/u.item' INTO TABLE movielens.movies FIELDS TERMINATED BY '|' 
> 	(movieID, title, @var3)
>     set releaseDate = STR_TO_DATE(@var3, '%d-%M-%Y');    
> ~~~
>
> 命令
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # Mac上的MySQL WorkBench不支持LOAD DATA LOCAL INFILE，需要安装命令行客户端
> $ brew install mysql-client
> ...
> ==> Summary
> 🍺  /usr/local/Cellar/mysql-client/8.0.22: 135 files, 150.7MB
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 启动mysql客户端时开启load local infile功能
> $ mysql --local-infile=1 -u root -p
> Enter password:
> ...
> Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
> mysql> CREATE DATABASE movielens;
> Query OK, 1 row affected (0.00 sec)
> 
> mysql> CREATE TABLE movielens.movies (
>     -> movieID INT PRIMARY KEY NOT NULL,
>     ->     title TEXT,
>     ->     releaseDate DATE
>     ->     );
> Query OK, 0 rows affected (0.01 sec)
> 
> mysql> LOAD DATA LOCAL INFILE 'ml-100k/u.item' INTO TABLE movielens.movies FIELDS TERMINATED BY '|'
>     -> (movieID, title, @var3)
>     ->     set releaseDate = STR_TO_DATE(@var3, '%d-%M-%Y');
> Query OK, 1677 rows affected, 1684 warnings (0.03 sec)
> Records: 1677  Deleted: 0  Skipped: 0  Warnings: 1684
> ~~~
>
> 检查
>
> ~~~bash
> mysql> use movielens;
> Reading table information for completion of table and column names
> You can turn off this feature to get a quicker startup with -A
> 
> Database changed
> mysql> SELECT * FROM movies WHERE title LIKE 'Star%';
> +---------+------------------------------------------------+-------------+
> | movieID | title                                          | releaseDate |
> +---------+------------------------------------------------+-------------+
> |      50 | Star Wars (1977)                               | 1977-01-01  |
> |      62 | Stargate (1994)                                | 1994-01-01  |
> |     222 | Star Trek: First Contact (1996)                | 1996-11-22  |
> |     227 | Star Trek VI: The Undiscovered Country (1991)  | 1991-01-01  |
> |     228 | Star Trek: The Wrath of Khan (1982)            | 1982-01-01  |
> |     229 | Star Trek III: The Search for Spock (1984)     | 1984-01-01  |
> |     230 | Star Trek IV: The Voyage Home (1986)           | 1986-01-01  |
> |     271 | Starship Troopers (1997)                       | 1997-01-01  |
> |     380 | Star Trek: Generations (1994)                  | 1994-01-01  |
> |     449 | Star Trek: The Motion Picture (1979)           | 1979-01-01  |
> |     450 | Star Trek V: The Final Frontier (1989)         | 1989-01-01  |
> |    1068 | Star Maker, The (Uomo delle stelle, L') (1995) | 1996-03-01  |
> |    1265 | Star Maps (1997)                               | 1997-01-01  |
> |    1293 | Star Kid (1997)                                | 1998-01-16  |
> |    1464 | Stars Fell on Henrietta, The (1995)            | 1995-01-01  |
> +---------+------------------------------------------------+-------------+
> 15 rows in set (0.01 sec)
> 
> mysql> quit
> Bye
> ~~~
>
> 

## 08 Logstash与`S#`

> d

## 09 Elasticsearch与Kafka

> d

## 10 Elasticsearch与Apache Spark

> d

## 11 例子：从Spark导入数据到Elasticsearch

> d