# Session共享 1: 

> Tomcat挂载在一台Nginx后面，配置Nginx和Tomcat，从而让这两台Tomcat能够Session共享

> 这篇文档用的是比较旧的方式，需要手动部署和配置tomcat，不适合Spring Boot用jar包部署的环境，后续会有文档使用`Spring Session`来进行Session共享

## 安装Tomcat

在两台虚拟机上都安装Tomcat，除了原始的Tomcat，还需要用于让Tomcat支持Session共享的Lib。加上[之前](../01_setup_and_config/readme.md)装好的nginx总共有三台虚拟机。

> 在Virtual Box上安装centos
> 
> * 参考: [01\_setup\_and\_config/readme.md](../01_setup_and_config/readme.md)

> tomcat的压缩包和Lib

~~~
[root@localhost ~]# ls /home/share/ | grep -E 'tomcat|lib'
apache-tomcat-7.0.61.tar.gz
lib
~~~	

> 解压tomcat到/usr/local/

~~~
[root@localhost local]# cp /home/share/apache-tomcat-7.0.61.tar.gz /usr/local/
[root@localhost local]# cd /usr/local/
[root@localhost local]# tar xvfz apache-tomcat-7.0.61.tar.gz | tail -2
apache-tomcat-7.0.61/webapps/manager/status.xsd
apache-tomcat-7.0.61/webapps/manager/xform.xsl
~~~

> 启动tomcat

~~~
[root@localhost ROOT]# cd /usr/local/apache-tomcat-7.0.61/bin/
[root@localhost bin]# ./startup.sh
Using CATALINA_BASE:   /usr/local/apache-tomcat-7.0.61
Using CATALINA_HOME:   /usr/local/apache-tomcat-7.0.61
Using CATALINA_TMPDIR: /usr/local/apache-tomcat-7.0.61/temp
Using JRE_HOME:        /usr
Using CLASSPATH:       /usr/local/apache-tomcat-7.0.61/bin/bootstrap.jar:/usr/local/apache-tomcat-7.0.61/bin/tomcat-juli.jar
Tomcat started.
[root@localhost bin]# tail ../logs/catalina.out
十月 22, 2020 1:57:13 下午 org.apache.catalina.startup.HostConfig deployDirectory
信息: Deploying web application directory /usr/local/apache-tomcat-7.0.61/webapps/manager
十月 22, 2020 1:57:13 下午 org.apache.catalina.startup.HostConfig deployDirectory
信息: Deployment of web application directory /usr/local/apache-tomcat-7.0.61/webapps/manager has finished in 86 ms
~~~

> nginx以及两台tomcat所在VM的地址分别是
> 
> * nginx node		: 192.168.1.170
> * tomcat node 1	: 192.168.1.147
> * tomcat node 2	: 192.168.1.127
> 
> 同样需要为这两台centos开放8080端口等（参考: [01\_setup\_and\_config/readme.md](../01_setup_and_config/readme.md)) 

## 配置Nginx连接两台tomcat

> 在安装nginx的虚拟机上，修改并reload nginx配置
>
~~~bash
vi /usr/local/tengine/conf/nginx.conf
[root@localhost conf]# service nginx reload
Reloading nginx configuration (via systemctl):             [  OK  ]
~~~
> 
> 修改内容如下
> 
~~~
upstream tomcats {
	server 192.168.1.147:8080;
	server 192.168.1.127:8080;
}
server {
	# location / {
	#    root   html;
	#    index  index.html index.htm;
	# }
	location / {
		proxy_pass http://tomcats;
	}
	#
	# other configures ...
}
~~~
>
> 在两台安装tomcat的机器上，执行如下命令查看请求发到哪台tomcat上
>
~~~
cd /usr/local/apache-tomcat-7.0.61
tail -f tail -f logs/localhost_access_log.2020-10-22.txt
~~~
>
> 用浏览器访问nginx（在地址后面加上`?`以阻止浏览器端缓存）：`http://192.168.1.170/?`
> 
> 还可以为upstreams中的server配置`weight`、`down`、`backup`、`max_conns`、`max_fails`、`fail_timeout` 等选项，以及`轮询+权重`、`ip_hash`、`uri_hash `、`least_conn`、`least_time`等负载均衡策略。配置方法参考：[01\_setup\_and\_config/readme.md](../01_setup_and_config/readme.md)
> 
> 文档： [https://docs.nginx.com/nginx/deployment-guides/load-balance-third-party/apache-tomcat/](https://docs.nginx.com/nginx/deployment-guides/load-balance-third-party/apache-tomcat/) 

## 缓存

> 在这个例子中，将使用某种缓存来存储session数据从而做到session共享。`nginx`自身提供`http_proxy本地磁盘缓存`，但是该缓存基于磁盘，不适合用于缓存。因此将使用`memcached`用作session共享的缓存。

> 先简要介绍`nginx http_proxy磁盘缓存`

### `nginx http_proxy 本地磁盘缓存`

> nginx本地缓存的缺点是`磁盘IO`导致性能低。
> 
> 一个解决方法是，把静态文件加载到内存中，然后用linux内核工具将这段内存映射为磁盘文件，供`nginx http_proxy`磁盘缓存加载。
> 
> 这样对于css、静态html等总量固定、不会超出内存显示，且不容易发生变化的内容，可以内存映射的方式来缓存。但另一个缺点——运维复杂，仍然无法避免。

~~~
proxy_cache_path /path/to/cache levels=1:2 keys_zone=my_cache:10m max_size=10g inactive=60m use_temp_path=off;
server {
     set $upstream http://ip:port
          location / {
                   proxy_cache my_cache;
                   proxy_pass $upstream;
                }
}
~~~

> 配置项说明：
>  
> * `/path/to/cache`: 本地路径，用来设置Nginx缓存资源的存放地址
> * `levels`: 默认所有缓存文件都放在同一个`/path/to/cache`下，但是会影响缓存的性能，因此通常会在`/path/to/cache`下面建立子目录用来分别存放不同的文件。假设`levels=1:2`，Nginx为将要缓存的资源生成的key为`f4cd0fbc769e94925ec5540b6a4136d0`，那么key的最后一位0，以及倒数第2-3位6d作为两级的子目录，也就是该资源最终会被缓存到`/path/to/cache/0/6d`目录中
> * `key_zone`: 在共享内存中设置一块存储区域来存放缓存的key和metadata（类似使用次数），这样nginx可以快速判断一个request是否命中或者未命中缓存，1m可以存储8000个key，10m可以存储80000个key
> * `max_size`: 最大cache空间，如果不指定，会使用掉所有disk space，当达到配额后，会删除最少使用的cache文件
> * `inactive`: 未被访问文件在缓存中保留时间，本配置中如果60分钟未被访问则不论状态是否为expired，缓存控制程序会删掉文件。inactive默认是10分钟。需要注意的是，inactive和expired配置项的含义是不同的，expired只是缓存过期，但不会被删除，inactive是删除指定时间内未被访问的缓存文件
> * `use_temp_path`: 如果为off，则nginx会将缓存文件直接写入指定的cache文件中，而不是使用temp_path存储，官方建议为off，避免文件在不同文件系统中不必要的拷贝
> * `proxy_cache`: 启用`proxy cache`，并指定`key_zone`。另外，如果`proxy_cache off`表示关闭掉缓存。

### Memcached

> 安装`libevent`和`memcached`：
>
>~~~
>yum –y install memcached
>~~~

##### 启动memcached

~~~
# 启动memcached
# 参数解释：
# -d	：后台启动服务
# -m	：缓存大小（默认64MB）
# -p 	：端口，让memcached监听哪个端口（默认11211）
# -l 	：IP，启动memcached机器可能有多个IP，让memcached监听哪个IP
# -P	：服务器启动后的系统进程ID，存储的文件
# -u	：服务器启动是以哪个用户名作为管理用户
# -c	：最多同时连接多少个connection（默认1024）
[root@localhost ~]# memcached -d -m 128 -u root -l 192.168.1.147 -p 11211 -c 256 -P /tmp/memcached.pid

# 检查memcached是否启动
[root@localhost ~]# ps aux | grep memcached | grep -v grep
root     31668  0.0  0.1 344100  1188 ?        Ssl  19:14   0:00 memcached -d -m 128 -u root -l 192.168.1.147 -p 11211 -c 256 -P /tmp/memcached.pid
[root@localhost ~]# memcached-tool 192.168.1.147:11211
  #  Item_Size  Max_age   Pages   Count   Full?  Evicted Evict_Time OOM
~~~

> 注意：要给centos防火墙设置容许该端口开放

~~~
[root@localhost ~]# systemctl status firewalld # 检查firewall是否已经打开，如果firewall关闭，需要先打开firewall
   ...
   Active: active (running) since 四 2020-10-22 13:42:41 CST; 1 day 5h ago
   ...
[root@localhost ~]# firewall-cmd --list-ports
80/tcp 8080/tcp 8088/tcp 3306/tcp
[root@localhost ~]# firewall-cmd --zone=public --add-port=11211/tcp --permanent
success
[root@localhost ~]# firewall-cmd --reload
success
[root@localhost ~]# firewall-cmd --list-ports
80/tcp 8080/tcp 8088/tcp 3306/tcp 11211/tcp
~~~

## Session共享配置

> 有了memched，就可以配置tomcat和nginx，让他们从memched获取session从而做到session共享

### 展示页面

> 将`tomcat 1`的` webapps/ROOT/index.jsp`改为如下内容、方便演示

~~~
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<h1>node 1</h1>
<p><%=session.getId()%></p>
~~~

> `tomcat 2`也类似，只是"node 1"改为"node 2"

### tomcat配置

> 拷贝一组用于session共享的jar包到tomcat的lib目录

~~~bash
[root@localhost lib]# ls /home/share/lib/
asm-3.2.jar                              minlog-1.2.jar
kryo-1.04.jar                            msm-kryo-serializer-1.7.0.jar
kryo-serializers-0.11.jar                reflectasm-1.01.jar
memcached-session-manager-1.7.0.jar      spymemcached-2.7.3.jar
memcached-session-manager-tc7-1.8.1.jar
[root@localhost lib]# cp /home/share/lib/* /usr/local/apache-tomcat-7.0.61/lib/
~~~

> 修改tomcat的context.xml配置，让tomcat从memcached获取session，其中`n1`表示第一台机器，后接memcached的IP和端口

~~~
    <!-- Uncomment this to disable session persistence across Tomcat restarts -->
    <!-- <Manager pathname="" /> -->
    <Manager
        className="de.javakaffee.web.msm.MemcachedBackupSessionManager"
        memcachedNodes="n1:192.168.1.147:11211"
        sticky="false"
        lockingMode="auto"
        sessionBackupAsync="false"
        requestUriIgnorePattern=".*\.(ico|png|gif|jpg|css|js)$"
        sessionBackupTimeout="1000"
        transcoderFactoryClass="de.javakaffee.web.msm.serializer.kryo.KryoTranscoderFactory"
    />
~~~

> 重启tomcat

~~~bash
[root@localhost bin]# cd  /usr/local/apache-tomcat-7.0.61/bin
[root@localhost bin]# ./shutdown.sh
Using CATALINA_BASE:   /usr/local/apache-tomcat-7.0.61
Using CATALINA_HOME:   /usr/local/apache-tomcat-7.0.61
Using CATALINA_TMPDIR: /usr/local/apache-tomcat-7.0.61/temp
Using JRE_HOME:        /usr
Using CLASSPATH:       /usr/local/apache-tomcat-7.0.61/bin/bootstrap.jar:/usr/local/apache-tomcat-7.0.61/bin/tomcat-juli.jar
[root@localhost bin]# ./startup.sh
Using CATALINA_BASE:   /usr/local/apache-tomcat-7.0.61
Using CATALINA_HOME:   /usr/local/apache-tomcat-7.0.61
Using CATALINA_TMPDIR: /usr/local/apache-tomcat-7.0.61/temp
Using JRE_HOME:        /usr
Using CLASSPATH:       /usr/local/apache-tomcat-7.0.61/bin/bootstrap.jar:/usr/local/apache-tomcat-7.0.61/bin/tomcat-juli.jar
Tomcat started.
~~~

### Nginx配置

> Nginx配置不变，仍然让一台nginx连接两台tomcat
> 
~~~
upstream tomcats {
    server 192.168.1.147:8080;
    server 192.168.1.127:8080;
}
server {
    # location / {
    #    root   html;
    #    index  index.html index.htm;
    # }
    location / {
        proxy_pass http://tomcats;
    }
    #
    # other configures ...
}
~~~
> 
> 多次访问nginx：http://192.168.1.175/? 
> 
> 页面显示如下：
> 
~~~html
node 2
905E77EC741638012586201EB6F7382C-n1
~~~
> 
> 或者是
> 
~~~html
node 1
905E77EC741638012586201EB6F7382C-n1
~~~
> 
> 不论请求发落哪一台tomcat上，打印出的session id都相同，实现了session共享
> 
> 再次查看memcached，发现memcached已经存储了session数据
>  
~~~
[root@localhost bin]# memcached-tool 192.168.1.147:11211
  #  Item_Size  Max_age   Pages   Count   Full?  Evicted Evict_Time OOM
  2     120B         0s       1       0     yes        0        0    0
  3     152B       119s       1       1     yes        0        0    0
  5     240B       131s       1       1     yes        0        0    0
~~~

### 可能会遇到的问题

session失效问题
 
> `tomcat`访问`memcached`获取session时会更新session时间戳，如果两台`tomcat`的时间设置不一样（如`tomcat 1`时间为2019年、`tomcat 2`时间为2020年），`tomcat 2`访问时会将session时间戳设置为2019年，`tomcat 2`访问时因为本地时间是2020年，会认为session失效，删除并创建新的session
> 
> 解决办法：让各台机器都启动`ntpd`服务来同步时间
> 
~~~bash
[root@localhost ~]# service ntpd status
Redirecting to /bin/systemctl status ntpd.service
● ntpd.service - Network Time Service
   Loaded: loaded (/usr/lib/systemd/system/ntpd.service; disabled; vendor preset: disabled)
   Active: inactive (dead)
[root@localhost ~]# service ntpd start
Redirecting to /bin/systemctl start ntpd.service
[root@localhost ~]# service ntpd status
Redirecting to /bin/systemctl status ntpd.service
● ntpd.service - Network Time Service
   Loaded: loaded (/usr/lib/systemd/system/ntpd.service; disabled; vendor preset: disabled)
   Active: active (running) since 六 2020-10-24 10:48:46 CST; 3s ago
  Process: 15699 ExecStart=/usr/sbin/ntpd -u ntp:ntp $OPTIONS (code=exited, status=0/SUCCESS)
 Main PID: 15700 (ntpd)
    Tasks: 1
   CGroup: /system.slice/ntpd.service
           └─15700 /usr/sbin/ntpd -u ntp:ntp -g
~~~



