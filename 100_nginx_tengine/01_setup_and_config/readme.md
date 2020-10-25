

# Tengine和Nginx

## 介绍

> * Tengine是Nginx的增强版本，淘宝以Nginx为内核，在其基础上增加如健康检查、Lua脚本等内置C语言插件模块，侧重于提升稳定性、可用性、性能、负载
> * OpenResty是另一个Nginx，侧重于基于LUA的功能增强

### Nginx

Nginx ("engine x") 是一个高性能的 **`HTTP`** 和 **`反向代理`** 服务器，也是一个 IMAP/POP3/SMTP 代理服务器

> 解析url（需要解析socket包到应用层内部）得到主机名、资源
> 
> * 作为HTTP服务器：如果是静态资源、会定位到服务器磁盘上的一个文件
> * 作为反向代理服务器：接受外部的连接请求，然后将请求转发给内部网络上的服务器，并将从服务器上得到的结果返回给外部的请求连接的客户端
> 
> 官方测试nginx能够支撑5万并发链接，并且cpu、内存等资源消耗却非常低，运行非常

### Nginx和apache的优缺点

Apache Httpd (Linux, Unix, Windows) / IIS (Windows)：都是Web服务器、也能作为反向代理服务器

#### nginx相对于apache的优点：

> * 轻量级，同样起web 服务，比apache 占用更少的内存及资源 
> * 非阻塞的，而apache 则是阻塞型的，在高并发下nginx 能保持低资源低消耗高性能 
> * 高度模块化的设计，编写模块相对简单
> * 社区活跃，各种高性能模块出品迅速

####  apache相对于nginx的优点：

> * rewrite功能：比nginx的rewrite强大
> * rewite是一种SEO技术，例如通过把`http://.../p=161`改写成`http://.../product/161.html`，来得到搜索引擎爬虫更多的关注（权重）
> * 模块很多，基本想到的都可以找到
> * 少bug，nginx的bug相对较多 

Nginx配置简洁, Apache复杂 

最核心的区别在于：apache是同步多进程模型，一个连接对应一个进程；nginx是异步的，多个连接（万级别）可以对应一个进程

## Nginx解决的问题

> * 高并发
> * 负载均衡
> * 高可用
> * 虚拟主机
> * 伪静态
> * 动静分离

## 安装

### 准备工作

#### 操作系统

> * 最好使用linux操作系统

#### 文档

> * Tengine：[http://tengine.taobao.org/](http://tengine.taobao.org/) 
> * Nginx：[http://nginx.org](http://nginx.org)
> 
> 上传Nginx压缩包到服务器，一般安装在/usr/local目录下

### 编译安装

> * 下载：
> *  Tengine-2.3.2.tar.gz： [http://tengine.taobao.org/download.html](http://tengine.taobao.org/download.html) 
> * [http://mirror.centos.org/centos/](http://mirror.centos.org/centos/) 或 []()
> 
> * 虚拟机：centos7.x 64位，准备3台
> 
>* centos镜像下载：[http://mirrors.163.com/centos/7/](http://mirrors.163.com/centos/7/)
>* centos虚拟机安装：用运行在Mac OS Catalina 10.15.7上的Virtual Box 6.1上安装和运行Centos 7虚拟机
>* 需要在Virtual Box中关闭虚拟机声卡功能来避免虚拟机启动时Virtual Box崩溃
>* 将非root账号添加到sudoer列表：[参考url](https://blog.csdn.net/myself00/article/details/9112817) 
>* 让VM可以连接外网：[参考url](https://blog.csdn.net/qq_23286071/article/details/80871352?utm_medium=distribute.pc_relevant.none-task-bloag-BlogCommendFromBaidu-4.channel_param&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-4.channel_param) 
>* 在VM中查看该VM的内网IP：执行命令 `ip add` （不再是ifconfig）
>* 安装Virtual Box增强工具，用来设置共享目录从主机传递文件：参考url [1](https://www.jianshu.com/p/1ccff5a7d750) [2](https://blog.csdn.net/kswkly/article/details/83690565) [3](https://blog.csdn.net/Scythe666/article/details/88624279)
>* 安装依赖的lib：``yum install gcc openssl-devel pcre-devel zlib-devel ``
> * 安装tengine 

~~~shell
cp /home/share/tengine-2.3.1.tar.gz /usr/local/
cd /usr/local/
tar xvfz tengine-2.3.1.tar.gz
cd tengine-2.3.1
vi README.markdown
./configure --prefix=/usr/local/tengine
make && make install
~~~

> * 检查tengine是否可以启动

~~~shell
[root@localhost tengine]# 
cd /usr/local/tengine/
[root@localhost tengine]# 
sbin/nginx
[root@localhost tengine]# ls -l logs/
总用量 4
-rw-r--r--. 1 root root 0 10月 19 20:20 access.log
-rw-r--r--. 1 root root 0 10月 19 20:20 error.log
-rw-r--r--. 1 root root 5 10月 19 20:20 nginx.pid
[root@localhost tengine]# tail logs/nginx.pid
7368
[root@localhost tengine]# curl 127.0.0.1 | head -n5
<!DOCTYPE html>
<html>
<head>
<title>Welcome to tengine!</title>
<style>
~~~

> * 开启80端口以及其他一些可能用到的端口：[参考url](https://blog.csdn.net/weixin_38750084/article/details/90387056)

~~~shell
[root@localhost tengine]# systemctl status firewalld  #如果是关闭就执行systemctl start firewalld
● firewalld.service - firewalld - dynamic firewall daemon
   Loaded: loaded (/usr/lib/systemd/system/firewalld.service; enabled; vendor preset: enabled)
   Active: active (running) since 一 2020-10-19 20:00:05 CST; 28min ago
     Docs: man:firewalld(1)
 Main PID: 754 (firewalld)
    Tasks: 2
   CGroup: /system.slice/firewalld.service
           └─754 /usr/bin/python2 -Es /usr/sbin/firewalld --nofork --nopid

10月 19 20:00:03 localhost.localdomain systemd[1]: Starting firewalld - dynamic fi....
10月 19 20:00:05 localhost.localdomain systemd[1]: Started firewalld - dynamic fir....
10月 19 20:00:05 localhost.localdomain firewalld[754]: WARNING: AllowZoneDrifting ....
Hint: Some lines were ellipsized, use -l to show in full.
[root@localhost tengine]# firewall-cmd --list-ports

[root@localhost tengine]# firewall-cmd --zone=public --add-port=80/tcp --permanent
success
[root@localhost tengine]# firewall-cmd --reload
success
[root@localhost tengine]# firewall-cmd --list-ports
80/tcp
[root@localhost tengine]# firewall-cmd --zone=public --add-port=8080/tcp --permanent
success
[root@localhost tengine]# firewall-cmd --zone=public --add-port=8088/tcp --permanent
success
[root@localhost tengine]# firewall-cmd --zone=public --add-port=3306/tcp --permanent
success
[root@localhost tengine]# firewall-cmd --reload
success
[root@localhost tengine]# firewall-cmd --list-ports
80/tcp 8080/tcp 8088/tcp 3306/tcp
~~~

> * 查看虚拟机IP地址，为192.168.1.169

~~~shell
[root@localhost tengine]# ip add | grep inet
    inet 127.0.0.1/8 scope host lo
    inet6 ::1/128 scope host
    inet 192.168.1.169/24 brd 192.168.1.255 scope global noprefixroute dynamic enp0s8
    inet6 fe80::fc2e:c8d8:879e:f698/64 scope link noprefixroute
    inet 192.168.122.1/24 brd 192.168.122.255 scope global virbr0
~~~

> * 在笔记本上（不是虚拟机）打开浏览器访问 http://192.168.1.169/ 检查tengine是否可以被其他机器访问

> * 关闭tengine

~~~shell
[root@localhost tengine]# ps -ef | grep ngix
root      7665  1692  0 20:34 pts/0    00:00:00 grep --color=auto ngix
[root@localhost tengine]# ps -ef | grep nginx
root      7368     1  0 20:20 ?        00:00:00 nginx: master process ./sbin/nginx
nobody    7369  7368  0 20:20 ?        00:00:00 nginx: worker process
root      7667  1692  0 20:34 pts/0    00:00:00 grep --color=auto nginx
[root@localhost tengine]# kill 7368 7369
[root@localhost tengine]# ps -ef | grep ngix
root      7669  1692  0 20:34 pts/0    00:00:00 grep --color=auto nginx
~~~

## 启动服务

### 准备启动脚本

> 拷贝[`../files/nginx`](../files/nginx)到`/etc/init.d/nginx`文件，设置可执行权限

~~~
[root@localhost tengine]# cd /etc/init.d/
[root@localhost init.d]# ls 
functions  netconsole  network  README
[root@localhost init.d]# vi nginx   # 拷贝并检查后保存，如有必要，修改nginx和配置的路径等
[root@localhost init.d]# chmod +x nginx
~~~

> * 这个脚本是有人在 [https://www.nginx.com/resources/wiki/start/topics/examples/redhatnginxinit/](https://www.nginx.com/resources/wiki/start/topics/examples/redhatnginxinit/) 的基础上修改得到，主要是对`make_dirs()`函数做了修改，在CentOS 7上可以正常使用
> * 更多启动脚本：[https://www.nginx.com/resources/wiki/start/topics/examples/initscripts/](https://www.nginx.com/resources/wiki/start/topics/examples/initscripts/)

> 注意: 
> 
> * 启动脚本需要知道nginx的bin文件以及配置文件的路径、用来启动nginx
> * 系统服务通过一个nginx启动时写入的pid文件，来知道nginx的进程id，以便能够发信号给nginx进程执行stop, restart, reload等操作

> 为了让它们保持一致，需要做如下修改 
> 
> * nginx配置（`/usr/local/tengine/conf/nginx.conf`）: pid文件与启动脚本约定路径一致

~~~
pid        /var/run/nginx.pid;
~~~

> * 启动脚本（`/etc/init.d/nginx`）: bin文件及配置文件指向nginx的安装位置

~~~
nginx="/usr/local/tengine/sbin/nginx"
NGINX_CONF_FILE="/usr/local/tengine/conf/nginx.conf"
~~~


### 服务启动和停止命令

> * service nginx start  # 启动服务
> * service nginx stop   # 停止
> * service nginx status # 状态
> * service nginx reload # 动态重载配置文件
> ...
> * service nginx help   # 命令列表

~~~shell
[root@localhost init.d]# service nginx start
Starting nginx (via systemctl):  Warning: nginx.service changed on disk. Run 'systemctl daemon-reload' to reload units.
~~~

### 测试

~~~shell
[root@localhost conf]# service nginx start
Starting nginx (via systemctl):                            [  OK  ]
[root@localhost conf]# cat /var/run/nginx.pid
5771
[root@localhost conf]# service nginx reload
Reloading nginx configuration (via systemctl):             [  OK  ]
[root@localhost conf]# service nginx status
● nginx.service - SYSV: Nginx is an HTTP(S) server, HTTP(S) reverse proxy and IMAP/POP3 proxy server
   Loaded: loaded (/etc/rc.d/init.d/nginx; bad; vendor preset: disabled)
   Active: active (running) since Thu 2020-10-22 15:56:07 CST; 3min 56s ago
     Docs: man:systemd-sysv-generator(8)
  Process: 5827 ExecReload=/etc/rc.d/init.d/nginx reload (code=exited, status=0/SUCCESS)
  Process: 5742 ExecStart=/etc/rc.d/init.d/nginx start (code=exited, status=0/SUCCESS)
 Main PID: 5771 (nginx)
    Tasks: 2
   CGroup: /system.slice/nginx.service
           ├─5771 nginx: master process /usr/local/tengine/sbin/nginx -c /usr/local/...
           └─5836 nginx: worker process

Oct 22 15:56:07 localhost.localdomain systemd[1]: Starting SYSV: Nginx is an HTTP(S....
Oct 22 15:56:07 localhost.localdomain nginx[5742]: Starting nginx: [  OK  ]
Oct 22 15:56:07 localhost.localdomain systemd[1]: Can't open PID file /var/run/ngin...y
Oct 22 15:56:07 localhost.localdomain systemd[1]: Started SYSV: Nginx is an HTTP(S)....
Oct 22 15:59:50 localhost.localdomain systemd[1]: Reloading SYSV: Nginx is an HTTP(....
Oct 22 15:59:50 localhost.localdomain nginx[5827]: nginx: the configuration file /u...k
Oct 22 15:59:50 localhost.localdomain nginx[5827]: nginx: configuration file /usr/l...l
Oct 22 15:59:50 localhost.localdomain nginx[5827]: Reloading nginx: [  OK  ]
Oct 22 15:59:50 localhost.localdomain systemd[1]: Reloaded SYSV: Nginx is an HTTP(S....
Hint: Some lines were ellipsized, use -l to show in full.
[root@localhost conf]# service nginx stop
Stopping nginx (via systemctl):                            [  OK  ]
~~~

## Nginx配置解析

以上面部署的tengin为例，配置文件在`"/usr/local/tengine/conf/nginx.conf`

~~~
# ngix的用户和用户组，不配置则使用启动ngix时当前用户和用户组
# user www www;
# user nobody;

# 可以为ngix开启多少个worker进程，一般配成与CPU内核数相同 (因为ngix是异步非阻塞的)
# worker_process越大，CPU消耗越高
worker_processes  1;

# 日志配置
# error_log  logs/error.log;
# error_log  logs/error.log  notice;
# error_log  logs/error.log  info;
# error_log  "pipe:rollback logs/error_log interval=1d baknum=7 maxsize=2G";

# pid        logs/nginx.pid;

# 基于事件的异步IO
events {
    # 一个worker进程可以处理的连接数，数量越大，并发量峰值时消耗的内存越高
    # 上面的worker_processes乘以worker_connections就是一个ngix可以处理的最大并发量，并发量达到上限时，ngix将不会响应新的连接请求 
    # 最大并发量同时也受Linux内核对能打开的文件句柄数限制的影响，下面有查看和修改方法
    worker_connections  10240;
}

# HTTP配置
http {
    # include 引入存放在其他文件中配置，可以引入多个文件
    # mime.types 文件类型与文件名后缀的映射关系，例如
    #video/mp4                                      mp4;
    # 浏览器根据mime type决定该如何处理
    # HTTP请求头中，Accept字段指定了客户端能接收什么样类型的响应
    # HTTP响应头中，Content-Type字段指定了服务器返回的响应类型
    include       mime.types;
    
    # 没有配置的文件名后缀，默认的mime type（浏览器会把octet-stream当做下载文件)
    default_type  application/octet-stream;

    # 日志相关
    # log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';
    # access_log  logs/access.log  main;
    # access_log  "pipe:rollback logs/access_log interval=1d baknum=7 maxsize=2G"  main;

    # 发送文件时文件内容是否经过应用程序
    # sendfile off: 文件 -> 读到APP -> APP把文件内容交给OS内核 -> 内核发送文件内容到网络
    # sendfile on: APP发送指令给内核 -> 内核读文件将其发送到网络 (异步网络IO) 
    # 一般设为开启，当网络下游接收能力不够时、需要关闭sendfile
    sendfile        on;
    # 优化TCP吞吐量，数据包累积到一定大小后才发送，仅在sendfile开启时才能生效
    # tcp_nopush    on;

    # 设置keep-alive客户端连接在服务器端保持开启的超时值（默认75s）
    # keepalive_timeout  0;
    keepalive_timeout  65;

    # 开启压缩，用服务器和客户端的计算开销，来减小网络传输量降低延迟
    # 开启后有很多细化的配置可选，下面有详细介绍
    # gzip  on;

    # 虚拟server，可以配多个虚拟server，
    # 只用一台物理服务器，可以让客户端感受到有多个server，提升硬件使用效率
    server {
        # 该server的监听端口号
        listen       80;
        # 该server绑定的域名或IP（一个网卡可以绑不同的IP，不同域名可以指向相同IP）
        server_name  localhost;

        # charset koi8-r;
        # access_log  logs/host.access.log  main;
        # access_log  "pipe:rollback logs/host.access_log interval=1d baknum=7 maxsize=2G"  main;

        # 虚拟目录：
        # 只有url中文件路径只有位于"/"顶层的文件使用{}内的规则：例如http://${host}/index.htm
        location / {
            # root:根目录
            # 所有文件的根目录是${nginx_install_path}/html/，也可以配绝对路径 
            root  html;
            
            # index：默认页（首页)
            # 类似访问http://${hostname}/时，返回的首页html  
            index  index.html index.htm;
        }
        
        # 对于以/bulma开头的路径，反向代理
        location /bulma {
            proxy_pass https://bulma.zcopy.site/;
        }
        # 配置http用户密码验证，IP访问控制等，这里省略，下面有具体说明  

        # 配置访问状态监控
        location /conn_status {
            stub_status on;
        }
        
        # 配置404页面
        # error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }


    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}

    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;

    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;

    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;

    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}
}
~~~

参考文档：

> * [http://nginx.org/en/docs/](http://nginx.org/en/docs/)
> * [http://tengine.taobao.org/](http://tengine.taobao.org/)，既有nginx文档、又有tengine文档

### 配置文件配置项说明

**全局错误日志**

> 全局错误日志定义类型，[ debug | info | notice | warn | error | crit ]，对应文件 `/var/log/nginx/error.log` 中的数据

**进程文件**

> `pid /var/run/nginx.pid;`

**打开的最多文件描述符**

> * 一个nginx进程打开的最多文件描述符数目，理论值应该是最多打开文件数（系统的值ulimit -n）与nginx进程数相除，但是nginx分配请求并不均匀，所以建议与ulimit -n的值保持一致。
> * `worker_rlimit_nofile 65535;`

**单个进程最大连接数**

> * 并发总数是event下`worker_processes`和`worker_connections`的乘积，即`max_clients = worker_processes * worker_connections`
> * 在设置了反向代理的情况下，`max_clients = worker_processes * worker_connections / 4`。除以4是一个经验值，根据以上条件，正常情况下的Nginx Server可以应付的最大连接数为：`4 * 8000 = 32000worker_connections`值的设置跟物理内存大小有关</br>
> 因为并发受IO约束，max_clients的值须小于系统可以打开的最大文件数

~~~
# 工作模式与连接数上限
events
{
	# 参考事件模型，use [ kqueue | rtsig | epoll | /dev/poll | select | poll ]; epoll模型是Linux 2.6以上版本内核中的高性能网络I/O模型，如果跑在FreeBSD上面，就用kqueue模型。
	use epoll;
	单个进程最大连接数（最大连接数=连接数*进程数）
	worker_connections 65535;
}
~~~

**可以打开的文件句柄数是多少**

~~~shell
$ cat /proc/sys/fs/file-max
97320
~~~

> * 并发连接总数小于系统可以打开的文件句柄总数，这样就在操作系统可以承受的范围之内
> * 所以worker_connections 的值需根据 worker_processes 进程数目和系统可以打开的最大文件总数进行适当地进行设置,使得并发总数小于操作系统可以打开的最大文件数目。其实质也就是根据主机的物理CPU和内存进行配置
> * 当然，理论上的并发总数可能会和实际有所偏差，因为主机还有其他的工作进程需要消耗系统资源

**查看系统限制**

~~~shell
ulimit -a
~~~

**打开文件句柄数量限制**

> 是Linux操作系统对一个进程打开的文件句柄数量的限制(也包含打开的SOCKET数量，可影响MySQL的并发连接数目)
> 
> * 系统总限制： `/proc/sys/fs/file-max`
> * 当前使用句柄数：`/proc/sys/fs/file-nr`
> * 修改句柄数：

~~~shell
ulimit -SHn 65535
~~~

**`http`**

> * `include mime.types`: 文件扩展名与文件类型映射表
> * default_type application/octet-stream; #默认文件类型
> * charset utf-8; #默认编码
> * client_header_buffer_size 32k; #上传文件大小限制

**`sendfile`**

> * `sendfile on;`: 开启高效文件传输模式
> 
> 	* sendfile指令指定nginx是否调用sendfile函数来输出文件，对于普通应用设为 on，如果用来进行下载等应用磁盘IO重负载应用，可设置为off，以平衡磁盘与网络I/O处理速度，降低系统的负载。注意：如果图片显示不正常把这个改成off。
> 	* sendfile()还能够用来在两个文件夹之间移动数据
> 
> * `tcp_nopush: 在linux/Unix系统中优化tcp数据传输，仅在sendfile开启时有效

**目录列表访问**

> `autoindex on`: 开启目录列表访问，合适下载服务器，默认关闭。

**长连接**

> `keepalive_timeout 120`: 长连接超时时间，单位是秒

**`gzip`**

> * `gzip on;`: 开启gzip压缩输出
> * `gzip_min_length 1k;`: 设置允许压缩的页面最小字节数，页面字节数从header头得content-length中进行获取。默认值是0，不管页面多大都压缩。建议设置成大于2k的字节数，小于2k可能会越压越大。  
> * `gzip_buffers 4 16k;` 设置系统获取几个单位的缓存用于存储gzip的压缩结果数据流。 例如 4 4k 代表以4k为单位，按照原始数据大小以4k为单位的4倍申请内存。 4 8k 代表以8k为单位，按照原始数据大小以8k为单位的4倍申请内存。如果没有设置，默认值是申请跟原始数据相同大小的内存空间去存储gzip压缩结果。
> * `gzip_http_version 1.0; `压缩版本（默认1.1，前端如果是squid2.5请使用1.0）
> * `gzip_comp_level 2;` 压缩级别，1-10，数字越大压缩的越好，也越占用CPU时间  
> * `gzip_types text/plain application/x-javascript text/css application/xml;` : 压缩类型，默认就已经包含text/html，所以下面就不用再写了，写上去也不会有问题，但是会有一个warn。默认值: gzip_types text/html (默认不对js/css文件进行压缩) 
> * 压缩类型，匹配MIME类型进行压缩:  参考配置文件conf/mime.types
> * `gzip_disable "MSIE [1-6]\.";   `E6及以下禁止压缩ß
> * `gzip_vary on;  `给CDN和代理服务器使用，针对相同url，可以根据头信息返回压缩和非压缩副本  

**`server`**

> * `listen 80;`: 监听端口
> * `server_name www.myhost.com myhost.com;`: 域名可以有多个，用空格隔开
> * `charset koi8-r;` 编码集

**日志相关**

~~~
access_log  logs/host.access.log  main;
access_log  "pipe:rollback logs/host.access_log interval=1d baknum=7 maxsize=2G"  main;
~~~

`index index.html index.htm index.jsp;` 默认页
`root /data/www/ha97;` 主目录

**虚拟主机**

> * 虚拟主机是一种特殊的软硬件技术，它可以将网络上的每一台计算机分成多个虚拟主机，每个虚拟主机可以独立对外提供www服务，这样就可以实现一台主机对外提供多个web服务，每个虚拟主机之间是独立的，互不影响的
> * 通过nginx可以实现虚拟主机的配置，nginx支持三种类型的虚拟主机配置
> 	* 基于ip的虚拟主机， （一块主机绑定多个ip地址）
> 	* 基于域名的虚拟主机（servername）
> 	* 基于端口的虚拟主机（listen如果不写ip端口模式）

~~~
http{
server{
#表示一个虚拟主机
}
}
~~~

##### location

映射/虚拟目录

```
location = / {
    [ configuration A ]
}

location / {
    [ configuration B ]
}

location /documents/ {
    [ configuration C ]
}

location ^~ /images/ {
    [ configuration D ]
}

location ~* \.(gif|jpg|jpeg)$ {
    [ configuration E ]
}
```

`location [ = | ~ | ~* | ^~ ] uri { ... }`

> * `location URI {}` 对当前路径及子路径下的所有对象都生效；
> * `location = URI {}` 注意URL最好为具体路径。  精确匹配指定的路径，不包括子路径，因此，只对当前资源生效；
> `location ~ URI {}   location ~* URI {} `  模式匹配URI，此处的URI可使用正则表达式，~区分字符大小写，~*不区分字符大小写；
> * `location ^~ URI {}` 禁用正则表达式
> * **优先级**：= > ^~ > ~|~* >  /|/dir/

location配置规则

> * `location`的匹配优先级
>	* “普通location”的匹配规则是“最大前缀”，与location编辑顺序无关
>	* “正则location”的匹配规则是“顺序匹配，使用第一个匹配到的
>	* “普通location”与“正则location”之间的匹配顺序是先匹配普通location，再“考虑”匹配正则location 
>		* “考虑”是“可能”的意思，即有时候继续用“正则location”来匹配、有时候不用）。两种情况下，不需要继续匹配“正则location”
>			* “普通location”前面指定了“ ^~ ”，指明本条普通一旦匹配上，则不需要继续正则匹配
>			* “普通location”恰好严格匹配、而不是最大前缀匹配，则不再继续匹配正则

<b>IP访问控制</b>

~~~
location  {
   deny  IP /IP段
   deny  192.168.1.109;
   allow 192.168.1.0/24;192.168.0.0/16;192.0.0.0/8
}
~~~

**用户认证访问**

> 模块ngx_http_auth_basic_module 允许使用“HTTP基本认证”协议验证用户名和密码来限制对资源的访问。例如对mp4文件的访问增加用户验证。

~~~
        location ~(.*)\.avi$ {
                 auth_basic  "closed site";
                 # 指向${tengine_install_path}/conf/users文件
                 auth_basic_user_file users; 
        }
~~~

> 配置文件中的`conf/users`指定了密码文件存放位置，这个文件要用需要使用**httpd-tools**来生成

~~~
yum install httpd
htpasswd -c -d /usr/local/users ${some_user_name}
~~~

**反向代理**

> * 通常的代理服务器，只用于代理内部网络对Internet的连接请求，客户机必须指定代理服务器,并将本来要直接发送到Web服务器上的http请求发送到代理服务器中由代理服务器向Internet上的web服务器发起请求，最终达到客户机上网的目的。
> * 反向代理（Reverse Proxy）方式是指以代理服务器来接受internet上的连接请求，然后将请求转发给内部网络上的服务器，并将从服务器上得到的结果返回给internet上请求连接的客户端，此时代理服务器对外就表现为一个反向代理服务器

例如上面配置文件中的：

~~~
proxy_pass http://192.168.43.152/
~~~

配置反向代理会遇到`301重定向问题`，需代理到不会返回301的下游

**upstream**

> 反向代理配合upstream使用

~~~
  upstream httpds {
    server 192.168.43.152:80;
    server 192.168.43.153:80;
  }
~~~

**weight(权重)**

> 指定轮询几率，weight和访问比率成正比，用于后端服务器性能不均的情况（`=`前后不能加空格）。

~~~
upstream some_name {
    server 127.0.0.1:8050       weight=10 down;
    server 127.0.0.1:8060       weight=1;
    server 127.0.0.1:8060      weight=1 backup;
}
~~~

> * down：表示当前的server暂时不参与负载 
> * weight：默认为1.weight越大，负载的权重就越大。 
> * backup： 其它所有的非backup机器down或者忙的时候，请求backup机器。

**max_conns**

> 可以根据服务的好坏来设置最大连接数，防止挂掉，比如1000，我们可以设置800

~~~
upstream some_name {
    server 127.0.0.1:8050    weight=5  max_conns=800;
    server 127.0.0.1:8060    weight=1;
}
~~~

**`max_fails`、`fail_timeout`**

> `max_fails`:失败多少次 认为主机已挂掉则，踢出，公司资源少的话一般设置2~3次，多的话设置1次

> `max_fails=3`, `fail_timeout=30s`代表在30秒内请求某一应用失败3次，认为该应用宕机，后等待30秒，这期间内不会再把新请求发送到宕机应用，而是直接发到正常的那一台，时间到后再有请求进来继续尝试连接宕机应用且仅尝试1次，如果还是失败，则继续等待30秒...以此循环，直到恢复

~~~
upstream some_name {
    server 127.0.0.1:8050    weight=1  max_fails=1  fail_timeout=20;
    server 127.0.0.1:8060    weight=1;
}
~~~

**负载均衡算法**

> * `轮询+weight`：如上面的演示
> * `ip_hash`：来自同一IP的请求转发给同一个`node`
> 
~~~
upstream some_name {
    ip_hash;
    server 127.0.0.1:8050;
    server 127.0.0.1:8060;
}
~~~
> 
> * `uri_hash`：需要编写一个`nginx module`
> 
> * `least_conn`：请求发给连接数最少的`node`
> 
~~~
upstream tomcats {
    least_conn;
    server 127.0.0.1:8050;
    server 127.0.0.1:8060;
}
~~~
> 
> * `least_time`：

**nginx访问状态监控**

~~~
# 配置访问状态监控
location /conn_status {
	stub_status on;
}
~~~

> 访问`http://${host}/conn_status`，页面显示类似下面

~~~
Active connections: 2 
server accepts handled requests request_time
 33 33 361 5460
Reading: 0 Writing: 1 Waiting: 1 
~~~

**健康检查模块**

> 配置一个status的location

~~~
location /status {
	check_status;
}
~~~

> 在upstream配置如下

~~~
check interval=3000 rise=2 fall=5 timeout=1000 type=http;
check_http_send "HEAD / HTTP/1.0\r\n\r\n";
check_http_expect_alive http_2xx http_3xx;
~~~

> 另外nginx编译时需使用`--add-module=./modules/ngx_http_upstream_check_module`： [https://github.com/alibaba/tengine/issues/1307](https://github.com/alibaba/tengine/issues/1307) 


