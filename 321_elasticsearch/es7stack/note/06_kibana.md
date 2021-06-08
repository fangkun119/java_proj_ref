<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [CH06 Kibana](#ch06-kibana)
  - [01 章节介绍](#01-%E7%AB%A0%E8%8A%82%E4%BB%8B%E7%BB%8D)
  - [02 Kibana安装和配置](#02-kibana%E5%AE%89%E8%A3%85%E5%92%8C%E9%85%8D%E7%BD%AE)
    - [(1) 在ubuntu上安装Kibana](#1-%E5%9C%A8ubuntu%E4%B8%8A%E5%AE%89%E8%A3%85kibana)
    - [(2) 在Mac OS上安装Kibana](#2-%E5%9C%A8mac-os%E4%B8%8A%E5%AE%89%E8%A3%85kibana)
  - [03 Kibana使用](#03-kibana%E4%BD%BF%E7%94%A8)
    - [(1) Create Index Pattern](#1-create-index-pattern)
    - [(2) Experimenting with the data](#2-experimenting-with-the-data)
      - [搜索任意字符串、Drill Down、可视化](#%E6%90%9C%E7%B4%A2%E4%BB%BB%E6%84%8F%E5%AD%97%E7%AC%A6%E4%B8%B2drill-down%E5%8F%AF%E8%A7%86%E5%8C%96)
      - [数据可视化：标签云为例](#%E6%95%B0%E6%8D%AE%E5%8F%AF%E8%A7%86%E5%8C%96%E6%A0%87%E7%AD%BE%E4%BA%91%E4%B8%BA%E4%BE%8B)
    - [(3) Kibana Dev Tools](#3-kibana-dev-tools)
    - [(4) 例子](#4-%E4%BE%8B%E5%AD%90)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# CH06 Kibana

## 01 章节介绍

> Kibana是构建在Elasticsearch基础之上的Web UI，可以直接与Elasticsearch交互来进行数据分析
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_intro.jpg" width="600" /></div>

## 02 Kibana安装和配置

### (1) 在ubuntu上安装Kibana

> ~~~bash
> sudo apt-get install Kibana
> sudo vi /etc/Kibana/Kibana.yml # 把server.host改为0.0.0.0
> sudo /bin/systemctl deamon-reload
> sudo /bin/systemctl enable Kibana.service
> sudo /bin/systemctl start Kibana.service # 运行在5601端口上
> ~~~

### (2) 在Mac OS上安装Kibana

参考文档：[https://www.elastic.co/guide/en/kibana/current/brew.html](https://www.elastic.co/guide/en/kibana/current/brew.html)

安装过程

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
> $ brew install elastic/tap/kibana-full
> Updating Homebrew...
> ^C==> Installing kibana-full from elastic/tap
> ==> Downloading https://artifacts.elastic.co/downloads/kibana/kibana-7.13.1-darwin-x86_64.tar.gz?t
> ######################################################################## 100.0%
> ==> Caveats
> Config: /usr/local/etc/kibana/
> If you wish to preserve your plugins upon upgrade, make a copy of
> /usr/local/opt/kibana-full/plugins before upgrading, and copy it into the
> new keg location after upgrading.
> 
> To have launchd start elastic/tap/kibana-full now and restart at login:
>   brew services start elastic/tap/kibana-full
> Or, if you don't want/need a background service you can just run:
>   kibana
> ==> Summary
> 🍺  /usr/local/Cellar/kibana-full/7.13.1: 50,608 files, 755.6MB, built in 2 minutes 59 seconds
> ~~~

安装后config、logs、data文件的存储路径

> | Type        | Default Location and Description                     | Setting     |
> | ----------- | ---------------------------------------------------- | ----------- |
> | **home**    | `/usr/local/var/homebrew/linked/kibana-full`         |             |
> | **bin**     | `/usr/local/var/homebrew/linked/kibana-full/bin`     |             |
> | **conf**    | `/usr/local/etc/kibana`                              |             |
> | **data**    | `/usr/local/var/lib/kibana`                          | `path.data` |
> | **logs**    | `/usr/local/var/log/kibana`                          | `path.logs` |
> | **plugins** | `/usr/local/var/homebrew/linked/kibana-full/plugins` |             |
>
> 说明
>
> | Type        | Description                                                  |
> | ----------- | ------------------------------------------------------------ |
> | **home**    | Kibana home directory or `$KIBANA_HOME`                      |
> | **bin**     | Binary scripts including `kibana` to start a node and `kibana-plugin` to install plugins |
> | **conf**    | Configuration files including `kibana.yml`                   |
> | **data**    | The location of the data files of each index / shard allocated on the node. Can hold multiple locations. |
> | **logs**    | Log files location.                                          |
> | **plugins** | Plugin files location. Each plugin will be contained in a subdirectory. |

修改配置文件

> `/usr/local/etc/kibana/Kibana.yml`
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/kibana/
> $ # 修改前备份
> $ cd /usr/local/etc/kibana/; cp kibana.yml kibana.yml.backup; ls kibana.ym*;
> kibana.yml        kibana.yml.backup
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/kibana/
> $ # 修改配置文件
> $ vi kibana.yml
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/kibana/
> $ # 查看修改内容
> $ diff kibana.yml kibana.yml.backup
> 8c8
> < server.host: "0.0.0.0"
> ---
> > #server.host: "localhost"
> ~~~

启动Kibana

> 部署为自动启动的服务：`brew services start elastic/tap/kibana-full`
>
> 单次运行：直接执行kibana命令	
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ # 用nohup &让kibina在后台运行
> $ nohup kibana &
> [1] 82797
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/321_elasticsearch/es7stack/tmp/
> $ tail -n5 nohup.out
> {"type":"response","@timestamp":"2021-06-08T11:43:32+08:00","tags":[],"pid":82803,"method":"get","statusCode":200,"req":{"url":"/plugins/kibanaOverview/assets/solutions_observability_2x.jpg","method":"get","headers":{"host":"127.0.0.1:5601","connection":"keep-alive","sec-ch-ua":"\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"","sec-ch-ua-mobile":"?0","user-agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","accept":"image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8","sec-fetch-site":"same-origin","sec-fetch-mode":"no-cors","sec-fetch-dest":"image","referer":"http://127.0.0.1:5601/app/kibana_overview","accept-encoding":"gzip, deflate, br","accept-language":"zh-CN,zh;q=0.9,en;q=0.8"},"remoteAddress":"127.0.0.1","userAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","referer":"http://127.0.0.1:5601/app/kibana_overview"},"res":{"statusCode":200,"responseTime":15,"contentLength":976},"message":"GET /plugins/kibanaOverview/assets/solutions_observability_2x.jpg 200 15ms - 976.0B"}
> {"type":"response","@timestamp":"2021-06-08T11:43:32+08:00","tags":[],"pid":82803,"method":"get","statusCode":200,"req":{"url":"/plugins/kibanaOverview/assets/solutions_security_solution_2x.jpg","method":"get","headers":{"host":"127.0.0.1:5601","connection":"keep-alive","sec-ch-ua":"\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"","sec-ch-ua-mobile":"?0","user-agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","accept":"image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8","sec-fetch-site":"same-origin","sec-fetch-mode":"no-cors","sec-fetch-dest":"image","referer":"http://127.0.0.1:5601/app/kibana_overview","accept-encoding":"gzip, deflate, br","accept-language":"zh-CN,zh;q=0.9,en;q=0.8"},"remoteAddress":"127.0.0.1","userAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","referer":"http://127.0.0.1:5601/app/kibana_overview"},"res":{"statusCode":200,"responseTime":7,"contentLength":1441},"message":"GET /plugins/kibanaOverview/assets/solutions_security_solution_2x.jpg 200 7ms - 1.4KB"}
> {"type":"response","@timestamp":"2021-06-08T11:43:35+08:00","tags":[],"pid":82803,"method":"post","statusCode":200,"req":{"url":"/internal/global_search/find","method":"post","headers":{"host":"127.0.0.1:5601","connection":"keep-alive","content-length":"77","sec-ch-ua":"\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"","sec-ch-ua-mobile":"?0","user-agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","kbn-version":"7.13.1","content-type":"application/json","accept":"*/*","origin":"http://127.0.0.1:5601","sec-fetch-site":"same-origin","sec-fetch-mode":"cors","sec-fetch-dest":"empty","referer":"http://127.0.0.1:5601/app/kibana_overview","accept-encoding":"gzip, deflate, br","accept-language":"zh-CN,zh;q=0.9,en;q=0.8"},"remoteAddress":"127.0.0.1","userAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","referer":"http://127.0.0.1:5601/app/kibana_overview"},"res":{"statusCode":200,"responseTime":9,"contentLength":14},"message":"POST /internal/global_search/find 200 9ms - 14.0B"}
> {"type":"response","@timestamp":"2021-06-08T11:43:37+08:00","tags":[],"pid":82803,"method":"post","statusCode":200,"req":{"url":"/internal/global_search/find","method":"post","headers":{"host":"127.0.0.1:5601","connection":"keep-alive","content-length":"77","sec-ch-ua":"\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"","sec-ch-ua-mobile":"?0","user-agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","kbn-version":"7.13.1","content-type":"application/json","accept":"*/*","origin":"http://127.0.0.1:5601","sec-fetch-site":"same-origin","sec-fetch-mode":"cors","sec-fetch-dest":"empty","referer":"http://127.0.0.1:5601/app/kibana_overview","accept-encoding":"gzip, deflate, br","accept-language":"zh-CN,zh;q=0.9,en;q=0.8"},"remoteAddress":"127.0.0.1","userAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","referer":"http://127.0.0.1:5601/app/kibana_overview"},"res":{"statusCode":200,"responseTime":8,"contentLength":14},"message":"POST /internal/global_search/find 200 8ms - 14.0B"}
> {"type":"response","@timestamp":"2021-06-08T11:44:12+08:00","tags":[],"pid":82803,"method":"post","statusCode":200,"req":{"url":"/api/telemetry/v2/clusters/_stats","method":"post","headers":{"host":"127.0.0.1:5601","connection":"keep-alive","content-length":"21","sec-ch-ua":"\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"","sec-ch-ua-mobile":"?0","user-agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","kbn-version":"7.13.1","content-type":"application/json","accept":"*/*","origin":"http://127.0.0.1:5601","sec-fetch-site":"same-origin","sec-fetch-mode":"cors","sec-fetch-dest":"empty","referer":"http://127.0.0.1:5601/app/kibana_overview","accept-encoding":"gzip, deflate, br","accept-language":"zh-CN,zh;q=0.9,en;q=0.8"},"remoteAddress":"127.0.0.1","userAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36","referer":"http://127.0.0.1:5601/app/kibana_overview"},"res":{"statusCode":200,"responseTime":523,"contentLength":54918},"message":"POST /api/telemetry/v2/clusters/_stats 200 523ms - 53.6KB"}
> ~~~

访问`http://127.0.0.1:5601/app/kibana_overview#/`可以打开Kibana页面

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_overview_page.jpg" width="600" /></div>

## 03 Kibana使用

> 直接使用Web UI即可，下面是例子，用Kibana可视化莎士比亚数据集

### (1) Create Index Pattern

> 左上角菜单按钮 → Home → Analytics → Dashboard → 左边栏`Kibnan` → `Index Patterns` （对应的url是：`http://127.0.0.1:5601/app/management/kibana/indexPatterns`：点击`Create Index Pattern`
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_create_idx_pattern_1_define.jpg" width="800" /></div>
>
> 在`Index pattern name`属于想要使用的Elasticsearch Index名称，Kinbana会访问Elasticsearch查找名称与之匹配的Index，找到之后点击`Next Step`
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_idx_pattern_2_create.jpg" width="800" /></div>
>
> 没有需要修改的配置，点击”Create Index Pattern“，创建之后，可以看到Index中的各个field
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_idx_pattern_3_complete.jpg" width="800" /></div>

### (2) Experimenting with the data

#### 搜索任意字符串、Drill Down、可视化

> 首先进入Discovery Mode，点击页面左上角菜单按钮：`Home` → `Analytics` → `Discover`
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_experiment_1_discover.jpg" width="800" /></div>
>
> 可以看到所有的fields以及Sample Data
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_search_with_kw.jpg" width="800" /></div>
>
> 可以进行搜索，例如任意一个field包含字符串”die“，还可以看哪个”play_name"包含“die”最多，类似的也可以看哪个”spkeaker“包含”die“最多。点击”Visualize“按钮，可以看到可视化图
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_visualize.jpg" width="800" /></div>
>
> 回到前一个页面，可以双击将某个speaker将其添加到filters中，并进一步查看该`speaker`下各个`play_name`的record数量
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_filters_2.jpg" width="800" /></div>
>
> 可视化页面如下
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_filters.jpg" width="800" /></div>
>
> 还可以点击顶部的"speaker:CLOUCESTER"来去除刚才选择的filter

#### 数据可视化：标签云为例

> 点击左上角的菜单按钮：`Analytics`→`Visualize Library`→`Create new Visualization`
>
> 选择`Aggregation Based`→`Explore More`→在filter中输入“cloud”→`Tag Cloud`
>
> 选择索引“shakespeare*”，进入标签云配置页面，填入下图右侧的配置，即可看到标签云
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_tag_cloud.jpg" width="800" /></div>

### (3) Kibana Dev Tools

> 可以执行RAW JSON Request
>
> 点击左上角的菜单按钮：`Home`→`Management` → `Dev Tools`
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_dev_tools.jpg" width="800" /></div>

### (4) 例子

创建Bar Chart找出句子数最多的莎士比亚著作

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/321_elasticsearch/es7_kibana_virtualize_example.jpg" width="800" /></div>







