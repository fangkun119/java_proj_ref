# 动静分离

## 1. 命令和配置项补充

让`nginx`开机时自动启动

> 
~~~
[root@localhost ~]# chkconfig --list
netconsole     	0:关	1:关	2:关	3:关	4:关	5:关	6:关
network        	0:关	1:关	2:开	3:开	4:开	5:开	6:关
[root@localhost ~]# chkconfig --add nginx
[root@localhost ~]# chkconfig nginx on
[root@localhost ~]# chkconfig --list
netconsole     	0:关	1:关	2:关	3:关	4:关	5:关	6:关
network        	0:关	1:关	2:开	3:开	4:开	5:开	6:关
nginx          	0:关	1:关	2:开	3:开	4:开	5:开	6:关
~~~
> 

配置虚拟目录

> 在`niginx.conf`中配置（注意root只能配一个，在其他配置项中已经用了，这里只能配alias）
> 
~~~bash
	location /download {
        alias /home/download ;
        autoindex on;
    }
~~~
> 
> 如果nginx部署在开起了SELinux的机器上，需要给目录`/home/download`增加权限
> 
~~~bash
[root@localhost tengine]# getenforce
Enforcing
[root@localhost tengine]# chcon -Rt httpd_sys_content_t /home/download/
[root@localhost tengine]# ls /home/download/
a  b  c
~~~
> 
> 用浏览器访问（nginx部署在192.168.1.175）
>  
>  http://192.168.1.175/download/
> 
~~~txt
Index of /download/
../
a                                                  24-Oct-2020 11:10                   0
b                                                  24-Oct-2020 11:10                   0
c                                                  24-Oct-2020 11:10                   0
~~~

## 2.用Jar包启动Spring Boot项目

> 项目代码：
>
[`../../03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/`](../../03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/)
> 
> 打包
> 
~~~bash
mvn install
~~~
>
> 日志
> 
~~~txt
[INFO] --- maven-jar-plugin:3.1.2:jar (default-jar) @ SpringBootMVC06 ---
[INFO] Building jar: /Users/fangkun/Dev/git/java_proj_ref/03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/target/SpringBootMVC06-0.0.1-SNAPSHOT.jar
[INFO] 
[INFO] --- spring-boot-maven-plugin:2.1.6.RELEASE:repackage (repackage) @ SpringBootMVC06 ---
[INFO] Replacing main artifact with repackaged archive
[INFO] 
[INFO] --- maven-install-plugin:2.5.2:install (default-install) @ SpringBootMVC06 ---
[INFO] Installing /Users/fangkun/Dev/git/java_proj_ref/03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/target/SpringBootMVC06-0.0.1-SNAPSHOT.jar to /Users/fangkun/.m2/repository/com/javaref/springbootMVC/SpringBootMVC06/0.0.1-SNAPSHOT/SpringBootMVC06-0.0.1-SNAPSHOT.jar
[INFO] Installing /Users/fangkun/Dev/git/java_proj_ref/03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/pom.xml to /Users/fangkun/.m2/repository/com/javaref/springbootMVC/SpringBootMVC06/0.0.1-SNAPSHOT/SpringBootMVC06-0.0.1-SNAPSHOT.pom
[INFO] --------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] --------------------------------------------------------------------
~~~
> 
> 启动MySQL数据库（对于Mac上的MySQL 8.0.21，在“系统偏好设置”中搜MySQL，点“Start MySQL Server）
> 
> 拷贝Jar包到某个目录（如果拷贝到其他机器上，要修改application.*.properties中的mysql连接地址，并且在MySQL的"User and Priviledges"中配置"Limit to Hosts Mapping"，以便容许其他机器上的客户端访问该MySQL）
> 
~~~
__________________________________________________________________
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/
$ mkdir -p /Users/fangkun/Dev/run/springboot_oa/
__________________________________________________________________
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/
$ pwd
/Users/fangkun/Dev/git/java_proj_ref/03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures
__________________________________________________________________
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/
$ cp target/SpringBootMVC06-0.0.1-SNAPSHOT.jar /Users/fangkun/Dev/run/springboot_oa/
__________________________________________________________________
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/03_springboot_proj_oa/05_fileupload_restful_relationtable_allfeatures/
$ cd /Users/fangkun/Dev/run/springboot_oa/
__________________________________________________________________
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/run/springboot_oa/
$ ls
SpringBootMVC06-0.0.1-SNAPSHOT.jar
~~~
> 
> 用Jar包启动Spring Boot项目
> 
~~~bash
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/run/springboot_oa/
$ java -jar SpringBootMVC06-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
2020-10-24 11:50:04.692  INFO 22618 --- [           main] c.j.s.SpringBootMvc04Application         : Starting 
2020-10-24 11:50:04.718  INFO 22864 --- [           main] c.j.s.SpringBootMvc04Application         : The following profiles are active: dev
...
...
...
2020-10-24 11:50:09.464  INFO 22618 --- [           main] c.j.s.SpringBootMvc04Application         : Started SpringBootMvc04Application in 5.665 seconds (JVM running for 6.976)
~~~
> 
> 在本地用浏览器访问 http://localhost:803 （spring.profiles.active=dev，dev环境端口配的是803），页面正常打开
> 
> 查看日志
> 
~~~
getSession account:com.javaref.springboot.entity.Account@ba45709c
----filter----/favicon.ico
uri:/account/profile
getSession account:com.javaref.springboot.entity.Account@ba45709c
----filter----/account/profile
uri:/js/bootstrap-table.min.js
uri:/css/bootstrap.min.css
uri:/css/bootstrap-table.min.css
uri:/js/bootstrap.min.js
uri:/js/common/jquery.min.js
uri:/js/bootstrap-table.min.js
uri:/account/list
getSession account:com.javaref.springboot.entity.Account@ba45709c
----filter----/account/list
uri:/css/bootstrap.min.css
uri:/js/common/jquery-3.4.1.min.js
uri:/js/bootstrap.min.js
uri:/account/profile
getSession account:com.javaref.springboot.entity.Account@ba45709c
----filter----/account/profile
uri:/css/bootstrap.min.css
uri:/css/bootstrap-table.min.css
uri:/js/bootstrap.min.js
uri:/js/common/jquery.min.js
uri:/js/bootstrap-table.min.js
uri:/js/bootstrap-table.min.js
~~~
>
> 希望css，js之类的静态文件，能够由Niginx返回，不再占用tomcat的计算资源

## 在Nginx上配置动静分离

> 获得Spring Jar包所在机器的内网IP地址（与Virtual Box上的虚拟机相同的网段），为`192.168.1.148`
> 
~~~
$ ifconfig | grep 192
	inet 192.168.1.148 netmask 0xffffff00 broadcast 192.168.1.255
~~~
> 
> 修改部署在`192.168.1.170`上nginx的配置
> 

~~~
upstream springboots {
    server 192.168.1.148:803;
}

...

server {
	
	...
	
	location / {
		proxy_pass http://springboots;
	}
	
	# ~: 表示正则表达式匹配
	# 注意只能有一个root
	location ~ .*\.(gif|ico|jpg|jpeg|png|bmp|swf|html|htm|css|js)$ {
		root /var/data/oasys/static/;       
	}
}
~~~
> 
> 将代码中`src/main/resources/static/`所有的子目录都拷贝到nginx机器上，上面所配置的目录（`/var/data/oasys/static/`）中。并且给他们设置访问权限
> 
~~~bash
[root@localhost tengine]# ls  /var/data/oasys/static/
css  images  js  uploads
[root@localhost logs]# chmod +711  /var/data/oasys/static/
[root@localhost logs]# chmod +755 -R /var/data/oasys/static/*
[root@localhost logs]# ls -l /var/data/oasys/
总用量 0
drwxr-xr-x. 6 root root 56 10月 24 14:01 static
[root@localhost logs]# ls -l /var/data/oasys/static/
总用量 4
drwxr-xr-x. 6 root root   89 10月 24 14:01 css
drwxr-xr-x. 2 root root 4096 10月 24 14:01 images
drwxr-xr-x. 5 root root  113 10月 24 14:01 js
drwxr-xr-x. 2 root root   24 10月 24 14:01 uploads
[root@localhost logs]#
~~~
> 
> 让nginx重新加载配置
> 
~~~bash
[root@localhost tengine]# service nginx reload
Reloading nginx configuration (via systemctl):             [  OK  ]
~~~
> 
> 用浏览器访问 http://localhost:803 （spring.profiles.active=dev，dev环境端口配的是803），页面正常打开
> 
> 查看日志，tomcat已经不再打印关于js、css等静态文件的日志，这些文件由nginx来返回
> 
~~~
getSession account:null
uri:/account/login
uri:/account/login
uri:/account/login
uri:/account/login
~~~
