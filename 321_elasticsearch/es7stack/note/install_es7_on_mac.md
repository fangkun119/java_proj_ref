[TOC]

# 在Mac OS上安装Elasticsearch

## 1 下载

> [https://www.elastic.co/cn/downloads/elasticsearch](https://www.elastic.co/cn/downloads/elasticsearch)
>
> 点击页面中的[MACOS](https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.13.0-darwin-x86_64.tar.gz)连接，下载Elasticsearch的安装包

## 2 安装

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/
> $ mkdir -p ~/Run; cd ~/Run; ls
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/
> $ cp ~/Downloads/elasticsearch-7.13.0-darwin-x86_64.tar.gz .; ls
> elasticsearch-7.13.0-darwin-x86_64.tar.gz
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/
> $ tar xvfz elasticsearch-7.13.0-darwin-x86_64.tar.gz; ls
> ...
> x elasticsearch-7.13.0/bin/elasticsearch-service-tokens
> x elasticsearch-7.13.0/bin/x-pack-watcher-env
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/
> $ ls
> elasticsearch-7.13.0 elasticsearch-7.13.0-darwin-x86_64.tar.gz
> ~~~

## 3 修改配置

文档：[https://sundog-education.com/elasticsearch/](https://sundog-education.com/elasticsearch/)

修改内容

> `node.name`：
>
> `network.host`：`0.0.0.0`
>
> `discovery.seed.hosts`：`[“127.0.0.1”]`
>
> `cluster.initial_master_nodes`： `[“node-1”]`

修改操作步骤

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/config/
> $ pwd; cp elasticsearch.yml backup_elasticsearch.yml; ls # 进入存放配置文件目录，备份配置文件
> /Users/fangkun/Run/elasticsearch-7.13.0/config 
> backup_elasticsearch.yml elasticsearch.yml       jvm.options.d           role_mapping.yml        users
> elasticsearch.keystore  jvm.options             log4j2.properties       roles.yml               users_roles
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/config/
> $ vi elasticsearch.yml # 修改配置文件
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/config/
> $ diff elasticsearch.yml backup_elasticsearch.yml
> 23c23
> < node.name: node-1
> ---
> > #node.name: node-1
> 56c56
> < network.host: 0.0.0.0
> ---
> > #network.host: 192.168.0.1
> 70c70
> < discovery.seed_hosts: ["127.0.0.1"]
> ---
> > #discovery.seed_hosts: ["host1", "host2"]
> 74c74
> < cluster.initial_master_nodes: ["node-1"]
> ---
> > #cluster.initial_master_nodes: ["node-1", "node-2"]
> ~~~

## 4 启动及测试

### (1) 使用命令行启动

> 启动
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/
> $ pwd
> /Users/fangkun/Run/elasticsearch-7.13.0
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Run/elasticsearch-7.13.0/
> $ ./bin/elasticsearch
> ...
> [2021-05-31T14:52:24,664][INFO ][o.e.n.Node               ] [node-1] started
> [2021-05-31T14:52:24,950][INFO ][o.e.l.LicenseService     ] [node-1] license [0728a831-3016-4de8-b310-dbf79621c8cf] mode [basic] - valid
> ~~~
>
> 在另一个窗口测试
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/note/
> $ curl -XGET http://localhost:9200/
> {
>   "name" : "node-1",
>   "cluster_name" : "elasticsearch",
>   "cluster_uuid" : "kKL80KMgSSu1TrKLlErNUA",
>   "version" : {
>     "number" : "7.13.0",
>     "build_flavor" : "default",
>     "build_type" : "tar",
>     "build_hash" : "5ca8591c6fcdb1260ce95b08a8e023559635c6f3",
>     "build_date" : "2021-05-19T22:22:26.081971330Z",
>     "build_snapshot" : false,
>     "lucene_version" : "8.8.2",
>     "minimum_wire_compatibility_version" : "6.8.0",
>     "minimum_index_compatibility_version" : "6.0.0-beta1"
>   },
>   "tagline" : "You Know, for Search"
> }
> ~~~
>
> 按Ctrl+C让Elasticsearch退出
>
> ~~~bash
> atch service, reason [shutdown initiated]
> [2021-05-31T14:12:24,301][INFO ][o.e.x.w.WatcherLifeCycleService] [fangkundeMacBook-Pro.local] watcher has stopped and shutdown
> [2021-05-31T14:12:24,695][INFO ][o.e.n.Node               ] [fangkundeMacBook-Pro.local] stopped
> [2021-05-31T14:12:24,695][INFO ][o.e.n.Node               ] [fangkundeMacBook-Pro.local] closing ...
> [2021-05-31T14:12:24,713][INFO ][o.e.n.Node               ] [fangkundeMacBook-Pro.local] closed
> ~~~

### (2) 使用GUI启动

> 和命令行启动没啥区别
>
> 步骤参考：[https://juejin.cn/post/6844903893596373006](https://juejin.cn/post/6844903893596373006)

## 5 其他参考

> 官方提供的用于上手的视频：[https://www.elastic.co/cn/webinars/getting-started-elasticsearch](https://www.elastic.co/cn/webinars/getting-started-elasticsearch)
>
> - 如何下载／运行 Elasticsearch，及其先决条件
> - 通过CRUD REST API 添加，更新，检索和删除数据
> - 基本的文本分析，包括标记和过滤
> - 基本搜索查询
> - 聚合：Elasticsearch 的面向和分析的主功能

