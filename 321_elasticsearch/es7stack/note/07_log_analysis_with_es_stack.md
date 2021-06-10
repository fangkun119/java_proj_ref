# CH07 使用Elasticsearch技术栈分析日志数据

## 01 章节介绍

> 本章使用Beat来解耦Web Server Log和Logstash，将日志数据发布到Logstash集群上

## 02 Filebeat在ES Stack中的作用

### (1) Filebeat

> Filebeat: [https://www.elastic.co/cn/beats/filebeat](https://www.elastic.co/cn/beats/filebeat)
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_in_es_stack.jpg" width="600" /></div>
>
> 提供轻量级接口、转发日志，并起到缓冲作用
>
> 可以根据logstash的状态来调节发送速度
>
> 比Logstash更加轻量级、适合与Web Server一同部署
>
> 支持多种日志格式，例如Apache、Ngix、MySQL、……

### (2) 为何要在ELK中添加一个Filebeat

> ELK：Elasticsearch、Logstash、Kibana
>
> 虽然架构越简洁出问题的环节就越少，但是添加Filebeat确实能带来好处，主要是提升reliablility和scalibility，具体包括
>
> * Filebeat更加轻量级，不容易抢占Web Servers的资源
> * 提供Back Pressure功能，起到流量缓冲的作用，并调节数据发送速度
> * 提升扩展性：Web Server、Elasticsearch、Logstash三个集群解构，可以各自独立扩容

### (3) Back Pressure

> Logstash有可能capacity不足、无法处理接收到的日志，也有可能有突增的流量堆积。Filebeat与Logstash的通信协议中实现了Back Pressure的功能、Filebeat可以根据Logstash返回的信息来判断Logstash是否负载过高、进而调节发送速度
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_back_pressure.jpg" width="400" /></div>
>
> 下面是使用了Filebeat之后的ELK部署结构
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_elk_deploy.jpg" width="800" /></div>
>
> Logstash具有基于磁盘的persistence queue，宕机重启后可以继续发送而不会丢失这些数据，并确保每条日志至少被提交一次
>
> Logstash通常至少部署2个节点、这样1个节点关机或重启时，另一个节点可以继续工作。对于每条消息、Logstash会持续尝试、直到确认有一次提交成功。
>
> Elasticsearch集群需要包含3种不同角色的Nodes
>
> * `Master Nodes(3)`：至少3个、以便有节点宕机时能选举出新的Master
>
> * `Hot Data Nodes`：只接收写请求，以便有Master Node宕机时能立即替补
> * `Warm Data Nodes`：只接收写请求，以便有Hot Data Node宕机时能立即替补

## 03 X-Pack security

> 因为存在`Filebeat→Logstash`、`Logstash→Elasticsearch`两条数据链路，因此需要考虑链路的信息安全，通过X-Pack来实现。

X-Pack是付费软件：虽然开源，但让然需要购买Licence才能使用

X-Pack包含如下功能：

> * 为集群提供基于密码的访问控制
> * 权限控制
> * IP过滤
> * 使用SSL或TLS对数据通信进行加密，对消息发送进行权限认证
> * 对访问进行记录

如果是在云环境下运行（例如AWS），云环境可能提供自己的解决方案而不需要X-Pack

权限结构包括

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_xpack_auth_structure.jpg" width="800" /></div>
>
> * 用户（user）：属于某些group
> * 用户组（group）：每个用户组都授予不同的权限
> * 权限（Privilege）：可以在index、documents、fields不同粒度进行授权，可以与LDAP或Active Directory等集成

定义权限

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_xpack_define_role_json.jpg" width="280" /></div>

## 04 Filebeat安装

### (1) 在Ubuntu上安装和配置Filebeat

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_install_on_ubuntu.jpg" width="500" /></div>

### (2) 在MacOS上安装Filebeat

参考文档

> [https://www.elastic.co/guide/en/beats/filebeat/7.13/filebeat-installation-configuration.html](https://www.elastic.co/guide/en/beats/filebeat/7.13/filebeat-installation-configuration.html)
>
> [https://www.elastic.co/guide/en/beats/filebeat/7.13/directory-layout.html](https://www.elastic.co/guide/en/beats/filebeat/7.13/directory-layout.html)

有多种方法，为了与前面几个组件保持一致，选择使用brew

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ brew tap elastic/tap 
> $ # 如果卡在Updating Homebrew步骤、按一次Ctrl+C等几秒钟即可
> $ # 如果报fatal: unable to access 'https://github.com/elastic/homebrew-tap/': LibreSSL SSL_connect: SSL_ERROR_SYSCALL in connection to github.com:443错误
> $ # 解决方法是
> $ # (1) 让termnal翻墙
> $ # (2) 把https协议改成git协议，根据日志提示的路径手动git clone
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ brew install elastic/tap/filebeat-full
> Updating Homebrew...
> ^C==> Installing filebeat-full from elastic/tap
> ==> Downloading https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-7.13.1-darwin-x86_6
> ######################################################################## 100.0%
> ==> Caveats
> To have launchd start elastic/tap/filebeat-full now and restart at login:
>   brew services start elastic/tap/filebeat-full
> Or, if you don't want/need a background service you can just run:
>   filebeat
> ==> Summary
> 🍺  /usr/local/Cellar/filebeat-full/7.13.1: 800 files, 134.3MB, built in 16 seconds
> ~~~

启动Filebeat，同样两种方法

> * 设置为服务，在MacOS启动时自动运行：`brew services start elastic/tap/filebeat-full`
> * 只运行一次：`filebeat`

### (3) 在MacOS上配置Filebeat

> 本章直接使用Filebeat发送日志数据到Elasticsearch，不使用Logstash进行转发，所以在Filebeat中配的时Elasticsearch的地址和端口

#### 下载数据文件

> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ wget http://media.sundog-soft.com/es/access_log
> --2021-06-08 22:31:14--  http://media.sundog-soft.com/es/access_log
> 正在解析主机 media.sundog-soft.com (media.sundog-soft.com)... 52.217.194.153
> 正在连接 media.sundog-soft.com (media.sundog-soft.com)|52.217.194.153|:80... 已连接。
> 已发出 HTTP 请求，正在等待回应... 200 OK
> 长度：23200421 (22M) [application/octet-stream]
> 正在保存至: “access_log”
> 
> access_log                27%[========>                        ]   6.18M  7.76KB/s  剩余 19m 3s
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ wget http://media.sundog-soft.com/es/error_log
> --2021-06-08 23:09:49--  http://media.sundog-soft.com/es/error_log
> 正在解析主机 media.sundog-soft.com (media.sundog-soft.com)... 52.217.45.204
> 正在连接 media.sundog-soft.com (media.sundog-soft.com)|52.217.45.204|:80... 已连接。
> 已发出 HTTP 请求，正在等待回应... 200 OK
> 长度：36569 (36K) [application/octet-stream]
> 正在保存至: “error_log”
> 
> error_log                100%[================================>]  35.71K  18.4KB/s  用时 1.9s
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 下载完成后，将文件拷贝到一个比较短的目录路径下，以方便配置
> $ mv access_log /Users/fangkun/tmp/access_log
> $ mv error_log  /Users/fangkun/tmp/error_log
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 数据文件存放路径
> $ ls /Users/fangkun/tmp/*_log
> /Users/fangkun/tmp/access_log /Users/fangkun/tmp/error_log
> ~~~

#### 配置文件路径

> 具体的路径参考[https://www.elastic.co/guide/en/beats/filebeat/7.13/directory-layout.html](https://www.elastic.co/guide/en/beats/filebeat/7.13/directory-layout.html)
>
> 下面是使用brew安装、不做任何修改时的默认路径
>
> | Location                                           | Type       |
> | -------------------------------------------------- | ---------- |
> | `/usr/local/var/homebrew/linked/filebeat-full`     | **home**   |
> | `/usr/local/var/homebrew/linked/filebeat-full/bin` | **bin**    |
> | `/usr/local/etc/filebeat`                          | **config** |
> | `/usr/local/var/lib/filebeat`                      | **data**   |
> | `/usr/local/var/log/filebeat`                      | **logs**   |

#### 修改配置

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 进入存放配置的路径（brew版本）
> $ cd /usr/local/etc/filebeat
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/filebeat/
> $ # 找到module sample configuration file
> $ ls modules.d/
> activemq.yml.disabled         haproxy.yml.disabled          osquery.yml.disabled
> apache.yml.disabled           ibmmq.yml.disabled            panw.yml.disabled
> ...
> google_workspace.yml.disabled o365.yml.disabled             zscaler.yml.disabled
> googlecloud.yml.disabled      okta.yml.disabled
> gsuite.yml.disabled           oracle.yml.disabled
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/filebeat/
> $ # 用于apache log的sample configuration
> $ ls modules.d/apache.yml.disabled
> modules.d/apache.yml.disabled
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/filebeat/
> $ # 进入存放module sample config的目录
> $ cd modules.d/
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/filebeat/modules.d/
> $ # 激活apache module config
> $ cp apache.yml.disabled apache.yml
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/filebeat/modules.d/
> $ # 修改配置文件
> $ vi apache.yml
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/filebeat/modules.d/
> $ # 查看修改内容
> $ diff apache.yml apache.yml.disabled
> 11c11
> <     var.paths: ["/Users/fangkun/tmp/access*"]
> ---
> >     #var.paths:
> 19c19
> <     var.paths: ["/Users/fangkun/tmp/error*"]
> ---
> >     #var.paths:
> ~~~

### (4) 使用Filebeat发送数据给Elasticsearch

启动filebeat发送数据

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ filebeat -e
> 2021-06-08T23:28:09.602+0800	INFO	instance/beat.go:665	Home path: [/usr/local/Cellar/filebeat-full/7.13.1/libexec] Config path: [/usr/local/etc/filebeat] Data path: [/usr/local/var/lib/filebeat] Logs path: [/usr/local/var/log/filebeat]
> 2021-06-08T23:28:09.603+0800	INFO	instance/beat.go:673	Beat ID: 42f416ad-a532-406b-a254-aabe5a1ce681
> 2021-06-08T23:28:09.617+0800	INFO	[beat]	instance/beat.go:1014	Beat info	{"system_info": {"beat": {"path": {"config": "/usr/local/etc/filebeat", "data": "/usr/local/var/lib/filebeat", "home": "/usr/local/Cellar/filebeat-full/7.13.1/libexec", "logs": "/usr/local/var/log/filebeat"}, "type": "filebeat", "uuid": "42f416ad-a532-406b-a254-aabe5a1ce681"}}}
> 2021-06-08T23:28:09.624+0800	INFO	[beat]	instance/beat.go:1023	Build info	{"system_info": {"build": {"commit": "2d80f6e99f41b65a270d61706fa98d13cfbda18d", "libbeat": "7.13.1", "time": "2021-05-28T17:43:52.000Z", "version": "7.13.1"}}}
> 2021-06-08T23:28:09.624+0800	INFO	[beat]	instance/beat.go:1026	Go runtime info	{"system_info": {"go": {"os":"darwin","arch":"amd64","max_procs":4,"version":"go1.15.12"}}}
> 2021-06-08T23:28:09.625+0800	INFO	[beat]	instance/beat.go:1030	Host info	{"system_info": {"host": {"architecture":"x86_64","boot_time":"2021-05-28T12:53:42.471007+08:00","name":"fangkundeMacBook-Pro.local","ip":["127.0.0.1/8","::1/128","fe80::1/64","fe80::899:e0a6:801b:3e36/64","192.168.31.248/24","fe80::3016:41ff:fee1:f2a5/64","fe80::3016:41ff:fee1:f2a5/64","fe80::997d:1141:4a06:83ef/64","fe80::29fd:ff15:4250:77ef/64","fe80::cad5:4807:7ac1:baea/64","fe80::ec9f:6796:6757:ef3d/64"],"kernel_version":"20.3.0","mac":["78:4f:43:92:8d:90","82:c8:2d:e5:8c:01","82:c8:2d:e5:8c:00","82:c8:2d:e5:8c:00","0a:4f:43:92:8d:90","32:16:41:e1:f2:a5","32:16:41:e1:f2:a5"],"os":{"type":"macos","family":"darwin","platform":"darwin","name":"Mac OS X","version":"10.16","major":10,"minor":16,"patch":0,"build":"20D74"},"timezone":"CST","timezone_offset_sec":28800,"id":"5DEB7DD5-2819-5626-A7E0-F936EB425831"}}}
> 2021-06-08T23:28:09.626+0800	INFO	[beat]	instance/beat.go:1059	Process info	{"system_info": {"process": {"cwd": "/Users/fangkun/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp", "exe": "/usr/local/Cellar/filebeat-full/7.13.1/libexec/bin/filebeat", "name": "filebeat", "pid": 90320, "ppid": 43608, "start_time": "2021-06-08T23:28:07.347+0800"}}}
> 2021-06-08T23:28:09.626+0800	INFO	instance/beat.go:309	Setup Beat: filebeat; Version: 7.13.1
> 2021-06-08T23:28:09.627+0800	INFO	[index-management]	idxmgmt/std.go:184	Set output.elasticsearch.index to 'filebeat-7.13.1' as ILM is enabled.
> 2021-06-08T23:28:09.630+0800	INFO	eslegclient/connection.go:99	elasticsearch url: http://localhost:9200
> 2021-06-08T23:28:09.631+0800	INFO	[publisher]	pipeline/module.go:113	Beat name: fangkundeMacBook-Pro.local
> 2021-06-08T23:28:09.639+0800	INFO	instance/beat.go:473	filebeat start running.
> 2021-06-08T23:28:09.641+0800	INFO	memlog/store.go:119	Loading data file of '/usr/local/var/lib/filebeat/registry/filebeat' succeeded. Active transaction id=0
> 2021-06-08T23:28:09.641+0800	INFO	memlog/store.go:124	Finished loading transaction log file for '/usr/local/var/lib/filebeat/registry/filebeat'. Active transaction id=0
> 2021-06-08T23:28:09.642+0800	INFO	[registrar]	registrar/registrar.go:109	States Loaded from registrar: 0
> 2021-06-08T23:28:09.643+0800	INFO	[crawler]	beater/crawler.go:71	Loading Inputs: 2
> 2021-06-08T23:28:09.649+0800	INFO	log/input.go:157	Configured paths: [/Users/fangkun/tmp/access*]
> 2021-06-08T23:28:09.650+0800	INFO	log/input.go:157	Configured paths: [/Users/fangkun/tmp/error*]
> 2021-06-08T23:28:09.650+0800	INFO	[crawler]	beater/crawler.go:108	Loading and starting Inputs completed. Enabled inputs: 0
> 2021-06-08T23:28:09.639+0800	INFO	[monitoring]	log/log.go:117	Starting metrics logging every 30s
> 2021-06-08T23:28:09.650+0800	INFO	cfgfile/reload.go:164	Config reloader started
> 2021-06-08T23:28:09.661+0800	INFO	log/input.go:157	Configured paths: [/Users/fangkun/tmp/access*]
> 2021-06-08T23:28:09.662+0800	INFO	log/input.go:157	Configured paths: [/Users/fangkun/tmp/error*]
> 2021-06-08T23:28:09.662+0800	INFO	eslegclient/connection.go:99	elasticsearch url: http://localhost:9200
> 2021-06-08T23:28:09.741+0800	INFO	[esclientleg]	eslegclient/connection.go:314	Attempting to connect to Elasticsearch version 7.13.0
> 2021-06-08T23:28:11.358+0800	INFO	[modules]	fileset/pipelines.go:133	Elasticsearch pipeline loaded.	{"pipeline": "filebeat-7.13.1-apache-access-pipeline"}
> 2021-06-08T23:28:11.545+0800	INFO	[modules]	fileset/pipelines.go:133	Elasticsearch pipeline loaded.	{"pipeline": "filebeat-7.13.1-apache-error-pipeline"}
> 2021-06-08T23:28:11.546+0800	INFO	cfgfile/reload.go:224	Loading of config files completed.
> 2021-06-08T23:28:11.582+0800	INFO	log/harvester.go:302	Harvester started for file: /Users/fangkun/tmp/error_log
> 2021-06-08T23:28:11.582+0800	INFO	log/harvester.go:302	Harvester started for file: /Users/fangkun/tmp/access_log
> 2021-06-08T23:28:12.610+0800	INFO	[add_cloud_metadata]	add_cloud_metadata/add_cloud_metadata.go:101	add_cloud_metadata: hosting provider type not detected.
> 2021-06-08T23:28:12.677+0800	INFO	[publisher_pipeline_output]	pipeline/output.go:143	Connecting to backoff(elasticsearch(http://localhost:9200))
> 2021-06-08T23:28:12.677+0800	INFO	[publisher]	pipeline/retry.go:219	retryer: send unwait signal to consumer
> 2021-06-08T23:28:12.677+0800	INFO	[publisher]	pipeline/retry.go:223	  done
> 2021-06-08T23:28:12.690+0800	INFO	[esclientleg]	eslegclient/connection.go:314	Attempting to connect to Elasticsearch version 7.13.0
> 2021-06-08T23:28:12.725+0800	INFO	[esclientleg]	eslegclient/connection.go:314	Attempting to connect to Elasticsearch version 7.13.0
> 2021-06-08T23:28:12.756+0800	INFO	[index-management]	idxmgmt/std.go:261	Auto ILM enable success.
> 2021-06-08T23:28:12.834+0800	INFO	[index-management.ilm]	ilm/std.go:170	ILM policy filebeat successfully created.
> 2021-06-08T23:28:12.834+0800	INFO	[index-management]	idxmgmt/std.go:401	Set setup.template.name to '{filebeat-7.13.1 {now/d}-000001}' as ILM is enabled.
> 2021-06-08T23:28:12.835+0800	INFO	[index-management]	idxmgmt/std.go:406	Set setup.template.pattern to 'filebeat-7.13.1-*' as ILM is enabled.
> 2021-06-08T23:28:12.835+0800	INFO	[index-management]	idxmgmt/std.go:440	Set settings.index.lifecycle.rollover_alias in template to {filebeat-7.13.1 {now/d}-000001} as ILM is enabled.
> 2021-06-08T23:28:12.835+0800	INFO	[index-management]	idxmgmt/std.go:444	Set settings.index.lifecycle.name in template to {filebeat {"policy":{"phases":{"hot":{"actions":{"rollover":{"max_age":"30d","max_size":"50gb"}}}}}}} as ILM is enabled.
> 2021-06-08T23:28:12.851+0800	INFO	template/load.go:228	Existing template will be overwritten, as overwrite is enabled.
> 2021-06-08T23:28:14.462+0800	INFO	template/load.go:131	Try loading template filebeat-7.13.1 to Elasticsearch
> 2021-06-08T23:28:15.206+0800	INFO	template/load.go:123	template with name 'filebeat-7.13.1' loaded.
> 2021-06-08T23:28:15.206+0800	INFO	[index-management]	idxmgmt/std.go:297	Loaded index template.
> 2021-06-08T23:28:16.131+0800	INFO	[index-management.ilm]	ilm/std.go:135	Index Alias filebeat-7.13.1 successfully created.
> 2021-06-08T23:28:16.155+0800	INFO	[publisher_pipeline_output]	pipeline/output.go:151	Connection to backoff(elasticsearch(http://localhost:9200)) established
> 2021-06-08T23:28:39.697+0800	INFO	[monitoring]	log/log.go:144	Non-zero metrics in the last 30s	{"monitoring": {"metrics": {"beat":{"cpu":{"system":{"ticks":1587,"time":{"ms":1588}},"total":{"ticks":5474,"time":{"ms":5475},"value":5474},"user":{"ticks":3887,"time":{"ms":3887}}},"info":{"ephemeral_id":"6111f408-0ebf-447d-b8e9-60d1482f7b84","uptime":{"ms":30855}},"memstats":{"gc_next":71213472,"memory_alloc":49925720,"memory_sys":218864656,"memory_total":539004912,"rss":100868096},"runtime":{"goroutines":44}},"filebeat":{"events":{"active":4117,"added":20753,"done":16636},"harvester":{"open_files":2,"running":2,"started":2}},"libbeat":{"config":{"module":{"running":1,"starts":1},"reloads":1,"scans":1},"output":{"events":{"acked":16634,"active":50,"batches":334,"total":16684},"read":{"bytes":335438},"type":"elasticsearch","write":{"bytes":24899446}},"pipeline":{"clients":2,"events":{"active":4117,"filtered":2,"published":20750,"retry":50,"total":20753},"queue":{"acked":16634,"max_events":4096}}},"registrar":{"states":{"current":2,"update":16636},"writes":{"success":335,"total":335}},"system":{"cpu":{"cores":4},"load":{"1":3.8804,"15":2.5386,"5":2.6006,"norm":{"1":0.9701,"15":0.6346,"5":0.6501}}}}}}
> ...
> 
> ~~~

查看filebeat在Elasticsearch中创建的索引

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ curl -H 'Content-Type:application/json' -XGET '127.0.0.1:9200/_cat/indices?v'
> health status index                             uuid                   pri rep docs.count docs.deleted store.size pri.store.size
> green  open   .kibana_task_manager_7.13.1_001   Jclm29swQ9ebi3atIMj1Nw   1   0         10         9481        1mb            1mb
> yellow open   movielens-sql                     L92zFaUuQeaeOQBinrtXSw   1   1       1677            0    296.1kb        296.1kb
> yellow open   logstash-2021.06.05-000001        I3buDoupRx2hWDH2aoPM6Q   1   1     102972            0     27.7mb         27.7mb
> green  open   .apm-agent-configuration          PfgRHzPqSWqoVsdx9p3Acw   1   0          0            0       208b           208b
> green  open   .kibana-event-log-7.13.1-000001   eDGd_N2YTIulhA2dzIFwcw   1   0          1            0      5.6kb          5.6kb
> yellow open   tags                              X2-SkCcXQU21rXDeZm6_CA   1   1       3683            0      634kb          634kb
> yellow open   movies                            nmXW0XGfSdCut1lIA4ZwXw   1   1       9742            0      1.2mb          1.2mb
> yellow open   filebeat-7.13.1-2021.06.08-000001 7MvbpdvLQJuqsG2l5VqOBg   1   1     102972            0     31.7mb         31.7mb
> green  open   .apm-custom-link                  sv7JgviWR8KDO3w_R5AjoQ   1   0          0            0       208b           208b
> yellow open   series                            ODKNPVnoRJazMKCHbQjBrw   1   1          8            0      9.5kb          9.5kb
> yellow open   ratings                           MxEiddBcTqWTuvnuPcz1jg   1   1     100836            0      9.6mb          9.6mb
> yellow open   shakespeare                       fJmCN9gySUaRG_HZvPNsMQ   1   1     111396            0     18.1mb         18.1mb
> green  open   .kibana_7.13.1_001                LZXTkf68SaOdV1qqV9QG5Q   1   0         73           17      2.1mb          2.1mb
> green  open   .async-search                     5r0iFheHSfiauww2w8L0Sw   1   0          0            0      3.3kb          3.3kb
> ~~~
>
> 其中的`filebeat-7.13.1-2021.06.08-000001`就是filebeat创建的

## 05 使用Kibana Dashboard分析数据

> filebeat数据导入Kibana之后，可以对Kibana进行配置以可视化这些数据，过程如下

### (1) 配置Dashboard

> 执行`filebeat setup --dashboards`命令，让filebeat访问Kibana配置Dashboard
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/
> $ # 如果是ubuntu需要cd到/usr/share/filebeat/bin目录下、使用sudo执行
> $ filebeat setup --dashboards 
> Loading dashboards (Kibana must be running and reachable)
> Loaded dashboards
> ~~~

### (2) 重启Kibana

> 重启kibiana以使这些配置生效：因为之前是使用`nohup kibana &`命令启动的kibana、要通过`kill ${process_id}`的方式来关闭kibana然后再重启
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 查进程ID
> $ ps ax | grep kibana | grep -v grep
> 82803   ??  S      5:17.15 /usr/local/Cellar/kibana-full/7.13.1/libexec/node/bin/node --preserve-symlinks-main --preserve-symlinks /usr/local/Cellar/kibana-full/7.13.1/libexec/src/cli/dist
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 发送终端信号给kibana，kibina收到后会主动退出
> $ kill 82803
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 检查
> $ ps ax | grep kibana | grep -v grep
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 重启
> $ nohup kibana &
> [1] 94257
> ~~~
>
> MacOS上如果是使用brew service启动，需要用brew service的方式重启
>
> Ubuntu上使用如下命令重启
>
> ~~~bash
> sudo /bin/systemctl stop kibana.service
> sudo /bin/systemctl start kibana.service
> ~~~

### (3) 查看Index Pattern

> 访问`http://127.0.0.1:5601/app/home/`可以找到Kibana
>
> 点击页面左上角的菜单按钮：`Home` → `Management`可以进入管理页面: `http://127.0.0.1:5601/app/management`
>
> 点击管理页面左侧的：`Kibana`→`Index Pattern`可以之前已经配置好的名为`filebeat-*`的`Index Pattern`
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_index_pattern.jpg" width="800" /></div>
>
> 点击后，可以看到索引的schema，包含了大量的字段
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_idxptn.jpg" width="800" /></div>

### (4) 查看数据

> 点击左上角的菜单按钮：`Analytics` → `Discovery` 然后切换到`filebeat-*`
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_discover.jpg)
>
> 将timestamp范围从默认的最近15分钟，改为指定时间段，可以查看日志
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_discover_2.jpg" width="800" /></div>
>
> 可以对具体的field进行drill down和可视化
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_drill_down.jpg" width="800" /></div>
>
> 对可视化图的配置进行一定调整，得到如下的图
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_beat_drill_down2.jpg" width="800" /></div>
>
> 回到菜单中的`Home`→`Analytics`→`Discover`，设置过滤器如下，可以看到response 500的日志
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_sample_logs.jpg" width="800" /></div>

### (5) 创建Dashboard

> 左上角菜单按钮：`Home` → `Analytics` →`Dashboard`，Dashboard类型选择`[Filebeat Apache] Access and error logs ECS`，可以查看使用filebeat安装好的Dashboard
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_filebeat_dashboard.jpg" width="800" /></div>
>
> 可以在Dashboard上进行交互查询，例如可以查看在某个时间段，产生404 Response的请求，都来自哪个城市
>
> 视频链接：[https://livevideo.manning.com/module/96_7_6/elasticsearch-7-and-elastic-stack/analyzing-log-data-with-elastic-stack/%5bexercise%5d-log-analysis-with-kibana?](https://livevideo.manning.com/module/96_7_6/elasticsearch-7-and-elastic-stack/analyzing-log-data-with-elastic-stack/%5bexercise%5d-log-analysis-with-kibana?)

