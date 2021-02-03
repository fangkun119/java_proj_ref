<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Docker Compose](#docker-compose)
  - [1. Docker Compose介绍](#1-docker-compose%E4%BB%8B%E7%BB%8D)
    - [1.1 概念](#11-%E6%A6%82%E5%BF%B5)
    - [1.2 安装Docker Compose](#12-%E5%AE%89%E8%A3%85docker-compose)
    - [1.3 例子：使用Docker Compose安装部署wordpress开源博客](#13-%E4%BE%8B%E5%AD%90%E4%BD%BF%E7%94%A8docker-compose%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2wordpress%E5%BC%80%E6%BA%90%E5%8D%9A%E5%AE%A2)
  - [2 Docker Compose实战](#2-docker-compose%E5%AE%9E%E6%88%98)
    - [2.1 准备](#21-%E5%87%86%E5%A4%87)
    - [2.2 Web应用程序镜像](#22-web%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F%E9%95%9C%E5%83%8F)
      - [(1) 基准镜像](#1-%E5%9F%BA%E5%87%86%E9%95%9C%E5%83%8F)
      - [(2) 编写Dockerfile创建镜像](#2-%E7%BC%96%E5%86%99dockerfile%E5%88%9B%E5%BB%BA%E9%95%9C%E5%83%8F)
      - [(3) Dokerfile内容](#3-dokerfile%E5%86%85%E5%AE%B9)
      - [(4) 测试容器创建](#4-%E6%B5%8B%E8%AF%95%E5%AE%B9%E5%99%A8%E5%88%9B%E5%BB%BA)
    - [2.3 数据库镜像](#23-%E6%95%B0%E6%8D%AE%E5%BA%93%E9%95%9C%E5%83%8F)
      - [(1) 基准镜像](#1-%E5%9F%BA%E5%87%86%E9%95%9C%E5%83%8F-1)
      - [(2) 编写Dockerfile创建镜像](#2-%E7%BC%96%E5%86%99dockerfile%E5%88%9B%E5%BB%BA%E9%95%9C%E5%83%8F-1)
      - [(3) Dockerfile内容](#3-dockerfile%E5%86%85%E5%AE%B9)
      - [(4) 测试容器创建](#4-%E6%B5%8B%E8%AF%95%E5%AE%B9%E5%99%A8%E5%88%9B%E5%BB%BA-1)
    - [2.4  使用Docker Compose编排容器](#24--%E4%BD%BF%E7%94%A8docker-compose%E7%BC%96%E6%8E%92%E5%AE%B9%E5%99%A8)
      - [(1) 编写`docker-compose.yml`](#1-%E7%BC%96%E5%86%99docker-composeyml)
      - [(2) 创建并编排容器](#2-%E5%88%9B%E5%BB%BA%E5%B9%B6%E7%BC%96%E6%8E%92%E5%AE%B9%E5%99%A8)
      - [(3) 访问应用](#3-%E8%AE%BF%E9%97%AE%E5%BA%94%E7%94%A8)
      - [(4) 关闭并删除容器](#4-%E5%85%B3%E9%97%AD%E5%B9%B6%E5%88%A0%E9%99%A4%E5%AE%B9%E5%99%A8)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Docker Compose

## 1. Docker Compose介绍

### 1.1 概念

容器编排

> 例如搭建一套互联互通的`Nginx`/` Tomcat`/`MySQL`，编写一个脚本，让他们自动化按照依赖顺序创建容器并且自动配置互联互通

Docker Compose

> (1) 单机多容器部署工具，能力相对有限，只能在一台宿主机上部署（多台宿主机部署需要使用Docker Swam或者Kubernetes来实现）
>
> (2) 通过yml文件定义多容器如何部署
>
> (3) `WIN`/`MAC`上安装Docker时会默认连同Docker Compose一起安装， `Linux`需要单独安装

### 1.2 安装Docker Compose

官网安装说明：https://docs.docker.com/compose/install/

在Linux上安装

> ~~~bash
> [root@localhost ~]# sudo curl -L "https://github.com/docker/compose/releases/download/1.28.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
> [root@localhost ~]# sudo chmod +x /usr/local/bin/docker-compose
> [root@localhost ~]# docker-compose -v
> docker-compose version 1.28.2, build 67630359
> ~~~

### 1.3 例子：使用Docker Compose安装部署wordpress开源博客

教材链接：[https://docs.docker.com/compose/wordpress/](https://docs.docker.com/compose/wordpress/)

准备

> 确认Docker Service已经启动
>
> ~~~bash
> [root@localhost wordpress]# service docker start
> Redirecting to /bin/systemctl start docker.service
> [root@localhost wordpress]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
> Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
> Active: active (running)
> ~~~
>
> 获取出现在docker-compose.yml文件中的依赖镜像（如果跳过这一步 ，在启动docker compose时会自动拉取镜像）
>
> ~~~bash
> [root@localhost wordpress]# # 为了便于演示、提前获取镜像
> [root@localhost wordpress]# docker pull mysql:5.7
> [root@localhost wordpress]# docker pull wordpress:latest
> ~~~

容器编排

> ~~~bash
> [root@localhost ~]# cd /usr/
> [root@localhost usr]# mkdir -p wordpress
> [root@localhost usr]# cd wordpress/
> [root@localhost wordpress]# ls
> [root@localhost wordpress]# vim docker-compose.yml #编写docker-compose.yml文件
> [root@localhost wordpress]# docker-compose up -d
> Building with native build. Learn about native build in Compose here: https://docs.docker.com/go/compose-native-build/
> Creating network "wordpress_default" with the default driver 
> Creating volume "wordpress_db_data" with default driver
> Creating wordpress_db_1 ... done
> Creating wordpress_wordpress_1 ... done
> [root@localhost wordpress]# docker ps
> CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                  NAMES
> dcd492856922        wordpress:latest    "docker-entrypoint..."   4 minutes ago       Up 4 minutes        0.0.0.0:8000->80/tcp   wordpress_wordpress_1
> d19e9b4ec01f        mysql:5.7           "docker-entrypoint..."   4 minutes ago       Up 4 minutes        3306/tcp, 33060/tcp    wordpress_db_1
> ~~~
>
> 可以看到docker compose创建了两个容器，并将wordpress_wordpress_1容器的80端口映射为宿主机的8000端口，用浏览器访问宿主机的8000端口，可以看到worldpress的页面
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/101_docker/docker_wordpress.jpg)

docker-compose.yml内容

> 代码：[../05_dockercompose/wordpress/docker-compose.yml](../05_dockercompose/wordpress/docker-compose.yml)
>
> ~~~yml
> version: '3.3'
> 
> services:
>    db:
>      image: mysql:5.7
>      volumes:
>        - db_data:/var/lib/mysql
>      restart: always
>      environment:
>        MYSQL_ROOT_PASSWORD: somewordpress
>        MYSQL_DATABASE: wordpress
>        MYSQL_USER: wordpress
>        MYSQL_PASSWORD: wordpress
> 
>    wordpress:
>      depends_on:
>        - db
>      image: wordpress:latest
>      ports:
>        - "8000:80"
>      restart: always
>      environment:
>        WORDPRESS_DB_HOST: db:3306
>        WORDPRESS_DB_USER: wordpress
>        WORDPRESS_DB_PASSWORD: wordpress
>        WORDPRESS_DB_NAME: wordpress
> volumes:
>     db_data: {}
> ~~~

## 2 Docker Compose实战

### 2.1 准备

Mac版Docker内置了Docker Compose：[https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)

文件：[../05_dockercompose/lab_bsbdj/bsbdj/](../05_dockercompose/lab_bsbdj/bsbdj/)

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/
> $ find . -type f
> ./bsbdj-db/init-db.sql				# 数据库建表及初始数据导入
> ./bsbdj-app/bsbdj.jar				# Spring Boot Web项目编译的Jar包
> ./bsbdj-app/application-dev.yml		# 开发环境配置
> ./bsbdj-app/application.yml			# 默认配合
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/
> $ docker-compose -v
> docker-compose version 1.27.4, build 40524192
> ~~~

配置文件

> [bsbdj-app/application.yml](../05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/application.yml)
>
> ~~~yml
> mybatis:
> 	 mapper-locations: classpath:/mybatis/mapper/*.xml
> 
> #分页插件
> pagehelper:
> 	helper-dialect: mysql
> 	reasonable: true #查询合理化，如果查询页码小于1 则按第一页查询，查询页码大于最后一页，则查询最后一页
> 
> #自定义参数
> app:
> 	crawler:
> 		enabled: true
> 		cron: 0 * * * * ?
> 		urls: http://c.api.budejie.com/topic/list/jingxuan/1/budejie-android-6.9.4/{np}-20.json?market=xiaomi&ver=6.9.4&visiting=&os=6.0&appname=baisibudejie&client=android&udid=864141036474044&mac=02%3A00%3A00%3A00%3A00%3A00,http://c.api.budejie.com/topic/list/jingxuan/41/budejie-android-6.9.4/{np}-20.json?market=xiaomi&ver=6.9.4&visiting=&os=6.0&appname=baisibudejie&client=android&udid=864141036474044&mac=02%3A00%3A00%3A00%3A00%3A00,http://c.api.budejie.com/topic/list/jingxuan/10/budejie-android-6.9.4/{np}-20.json?market=xiaomi&ver=6.9.4&visiting=&os=6.0&appname=baisibudejie&client=android&udid=864141036474044&mac=02%3A00%3A00%3A00%3A00%3A00,http://c.api.budejie.com/topic/list/jingxuan/29/budejie-android-6.9.4/{np}-20.json?market=xiaomi&ver=6.9.4&visiting=&os=6.0&appname=baisibudejie&client=android&udid=864141036474044&mac=02%3A00%3A00%3A00%3A00%3A00,http://s.budejie.com/topic/list/remen/1/budejie-android-6.9.4/{np}-20.json?market=xiaomi&ver=6.9.4&visiting=&os=6.0&appname=baisibudejie&client=android&udid=864141036474044&mac=02%3A00%3A00%3A00%3A00%3A00
> spring:
> 	profiles:
> 		active: dev
> ~~~
>
> [bsbdj-app/application-dev.yml](../05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/application-dev.yml)
>
> ~~~yml
> spring:
> 	datasource:
> 		driver-class-name: com.mysql.jdbc.Driver
> 		# 连接串中的host为db
> 		url: jdbc:mysql://db:3306/bsbdj?useUnicode=true
> 		username: root
> 		password: root
> 	tomcat:
> 		init-s-q-l: SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci
> 	thymeleaf:
> 		cache: false # 取消thymeleaf缓存，让页面热部署
> logging:
> 	level:
> 		com.itlaoqi.bsbdj.mapper: debug #将mapper包下的日志最低级别调整为debug,输出SQL语句
> 	file: bsbdj.log
> server:
> 	port: 80
> ~~~

支持`emoji`表情字符

> 字符表示（大小为1-4个字节）：1~数字/字母，2~3世界的各种语言，4~特殊符号
>
> * `utf-8`：最多包含3个字节
>
> * `utf8mb4`：包含4个字节，支持特殊符号
>
> 为了支持emoji等特殊字符，需要将MySQL默认的utf8改为utf8mb4
>
> * 设置`spring.tomcat.init-s-q-l=SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci`
>
> * JDBC连接串增加`useUnicode=true`参数

### 2.2 Web应用程序镜像

#### (1) 基准镜像

> 项目编译的java版本是8u222，在Docker Hub查找支持该版本java的镜像
>
> * [https://hub.docker.com/_/openjdk?tab=tags&page=1&ordering=last_updated&name=8u222-jre](https://hub.docker.com/_/openjdk?tab=tags&page=1&ordering=last_updated&name=8u222-jre)
>
> * [https://hub.docker.com/layers/openjdk/library/openjdk/8u222-jre/images/sha256-eccbfade15a98c6cb0711dc1d524a6ea8fba7fb736c8e65cc48e77e86e1bec44?context=explore](https://hub.docker.com/layers/openjdk/library/openjdk/8u222-jre/images/sha256-eccbfade15a98c6cb0711dc1d524a6ea8fba7fb736c8e65cc48e77e86e1bec44?context=explore)
>
> 镜像名以及镜像tag是`openjdk:8u222-jre`

#### (2) 编写Dockerfile创建镜像

> 在之前的bsbdj-app目录下
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/
> $ vim Dockerfile # 编写Dockerfile
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/
> $ ls
> Dockerfile          application-dev.yml application.yml     bsbdj.jar
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/
> $ docker build -t fangkun/bsbdj-app . # 构建Docker镜像
> Sending build context to Docker daemon  24.05MB
> Step 1/7 : FROM openjdk:8u222-jre
>  ---> 25073ded58d2
> Step 2/7 : WORKDIR /usr/local/bsbdj
>  ---> Running in d93685afe306
> Removing intermediate container d93685afe306
>  ---> 122bd5181a0d
> Step 3/7 : ADD bsbdj.jar .
>  ---> c0983bb14381
> Step 4/7 : ADD application.yml .
>  ---> 7deb8f0cc607
> Step 5/7 : ADD application-dev.yml .
>  ---> 63d628fdfa6b
> Step 6/7 : EXPOSE 80
>  ---> Running in e92fed9dcee8
> Removing intermediate container e92fed9dcee8
>  ---> 7d2b2404c04d
> Step 7/7 : CMD ["java", "-jar", "bsbdj.jar"]
>  ---> Running in f7413b54a182
> Removing intermediate container f7413b54a182
>  ---> c0506d4bac5b
> Successfully built c0506d4bac5b
> Successfully tagged fangkun/bsbdj-app:latest
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/
> $ docker images | grep fangkun	   # 查看创建的镜像
> fangkun/bsbdj-app              latest                       c0506d4bac5b   40 seconds ago   270MB
> ~~~

#### (3) Dokerfile内容

> Dockerfile的内容为：[/bsbdj/bsbdj-app/Dockerfile](../05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/Dockerfile)
>
> ~~~dockerfile
> FROM openjdk:8u222-jre				# 基准镜像
> WORKDIR /usr/local/bsbdj			# 创建并切换目录
> ADD bsbdj.jar .						# 将3个项目文件拷贝到镜像中（目录为当前WORKDIR）
> ADD application.yml .
> ADD application-dev.yml .
> EXPOSE 80							# 创建容器时暴露80端口
> CMD ["java", "-jar", "bsbdj.jar"]	# 容器启动后执行的命令
> ~~~

#### (4) 测试容器创建

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/
> $ docker images | grep fangkun	   # 查看创建的镜像
> fangkun/bsbdj-app              latest                       c0506d4bac5b   40 seconds ago   270MB
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/
> $ docker run fangkun/bsbdj-app     # 此时可以创建容器启动tomcat，但是不能访问后台数据库
> ~~~

### 2.3 数据库镜像

#### (1) 基准镜像

> 项目开发使用的MySQL版本的是5.7，在Docker Hub查找对应的镜像
>
> https://hub.docker.com/_/mysql?tab=tags&page=1&ordering=last_updated&name=5.7
>
> [https://hub.docker.com/layers/mysql/library/mysql/5.7/images/sha256-45ad5952e4f304d39aedb02caf7e0afc30a310e66f7ab60af8acf20fd4a0f54c?context=explore](https://hub.docker.com/layers/mysql/library/mysql/5.7/images/sha256-45ad5952e4f304d39aedb02caf7e0afc30a310e66f7ab60af8acf20fd4a0f54c?context=explore)
>
> 镜像名及tag是`mysql:5.7`

#### (2) 编写Dockerfile创建镜像

> 在之前的bsbdj-db目录下
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/
> $ vim Dockerfile			# 编写Dockerfile
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/
> $ ls
> Dockerfile  init-db.sql
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/
> $ docker build -t fangkun/bsbdj-db . # 创建镜像
> Sending build context to Docker daemon  36.46MB
> Step 1/3 : FROM mysql:5.7
> 5.7: Pulling from library/mysql
> a076a628af6f: Pull complete
> f6c208f3f991: Pull complete
> 88a9455a9165: Pull complete
> 406c9b8427c6: Pull complete
> 7c88599c0b25: Pull complete
> 25b5c6debdaf: Pull complete
> 43a5816f1617: Pull complete
> 1831ac1245f4: Pull complete
> 37677b8c1f79: Pull complete
> 27e4ac3b0f6e: Pull complete
> 7227baa8c445: Pull complete
> Digest: sha256:b3d1eff023f698cd433695c9506171f0d08a8f92a0c8063c1a4d9db9a55808df
> Status: Downloaded newer image for mysql:5.7
>  ---> a70d36bc331a
> Step 2/3 : WORKDIR /docker-entrypoint-initdb.d
>  ---> Running in 941bee233e87
> Removing intermediate container 941bee233e87
>  ---> afe325b67d63
> Step 3/3 : ADD init-db.sql .
>  ---> 1809e159bd67
> Successfully built 1809e159bd67
> Successfully tagged fangkun/bsbdj-db:latest
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/
> $ docker images | grep fangkun
> fangkun/bsbdj-db               latest                       1809e159bd67   45 seconds ago   485MB
> fangkun/bsbdj-app              latest                       c0506d4bac5b   34 minutes ago   270MB
> ~~~

#### (3) Dockerfile内容

>Dockfile：[/bsbdj/bsbdj-db/Dockerfile](../05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/Dockerfile)
>
>~~~dockerfile
>FROM mysql:5.7
># 数据库初始化目录
>WORKDIR /docker-entrypoint-initdb.d
>ADD init-db.sql .
>~~~
>
>`/docker-entrypoint-initdb.d`：MySQL 5.7官方镜像提供的数据库初始化脚本存放目录，在[镜像描述页面](https://hub.docker.com/_/mysql?tab=description&page=1&ordering=last_updated)中有说明，此外该说明还包含了启动命令、环境变量等诸多说明
>
>~~~txt
>Starting a MySQL instance is simple:
>$ docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag
>
>Environment Variables
>MYSQL_ROOT_PASSWORD
>This variable is mandatory and specifies the password that will be set for the MySQL root superuser account. In the above example, it was set to my-secret-pw.
>
>MYSQL_DATABASE
>This variable is optional and allows you to specify the name of a database to be created on image startup. If a user/password was supplied (see below) then that user will be granted superuser access (corresponding to GRANT ALL) to this database.
>
>MYSQL_USER, MYSQL_PASSWORD
>These variables are optional, used in conjunction to create a new user and to set that user's password. This user will be granted superuser permissions (see above) for the database specified by the MYSQL_DATABASE variable. Both variables are required for a user to be created.
>
>Do note that there is no need to use this mechanism to create the root superuser, that user gets created by default with the password specified by the MYSQL_ROOT_PASSWORD variable.
>
>MYSQL_ALLOW_EMPTY_PASSWORD
>This is an optional variable. Set to a non-empty value, like yes, to allow the container to be started with a blank password for the root user. NOTE: Setting this variable to yes is not recommended unless you really know what you are doing, since this will leave your MySQL instance completely unprotected, allowing anyone to gain complete superuser access.
>
>MYSQL_RANDOM_ROOT_PASSWORD
>This is an optional variable. Set to a non-empty value, like yes, to generate a random initial password for the root user (using pwgen). The generated root password will be printed to stdout (GENERATED ROOT PASSWORD: .....).
>
>MYSQL_ONETIME_PASSWORD
>Sets root (not the user specified in MYSQL_USER!) user as expired once init is complete, forcing a password change on first login. Any non-empty value will activate this setting. NOTE: This feature is supported on MySQL 5.6+ only. Using this option on MySQL 5.5 will throw an appropriate error during initialization.
>
>MYSQL_INITDB_SKIP_TZINFO
>By default, the entrypoint script automatically loads the timezone data needed for the CONVERT_TZ() function. If it is not needed, any non-empty value disables timezone loading.
>
>Initializing a fresh instance
>When a container is started for the first time, a new database with the specified name will be created and initialized with the provided configuration variables. Furthermore, it will execute files with extensions .sh, .sql and .sql.gz that are found in /docker-entrypoint-initdb.d. Files will be executed in alphabetical order. You can easily populate your mysql services by mounting a SQL dump into that directory and provide custom images with contributed data. SQL files will be imported by default to the database specified by the MYSQL_DATABASE variable.
>~~~

#### (4) 测试容器创建

> 创建容器
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/
> $ docker run -d -e MYSQL_ROOT_PASSWORD=root fangkun/bsbdj-db
> d7631f5ff1da5937c3223e4ea6b96578a36a8baa814853303ff10811b60aaf45
> ~~~
>
> 验证
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/
> $ docker ps
> CONTAINER ID   IMAGE              COMMAND                  CREATED          STATUS          PORTS                 NAMES
> d7631f5ff1da   fangkun/bsbdj-db   "docker-entrypoint.s…"   32 seconds ago   Up 31 seconds   3306/tcp, 33060/tcp   jolly_colden
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/
> $ docker exec -it d7631f5ff1da /bin/bash
> root@d7631f5ff1da:/docker-entrypoint-initdb.d# mysql -uroot -proot
> mysql: [Warning] Using a password on the command line interface can be insecure.
> Welcome to the MySQL monitor.  Commands end with ; or \g.
> Your MySQL connection id is 2
> Server version: 5.7.33 MySQL Community Server (GPL)
> 
> Copyright (c) 2000, 2021, Oracle and/or its affiliates.
> 
> Oracle is a registered trademark of Oracle Corporation and/or its
> affiliates. Other names may be trademarks of their respective
> owners.
> 
> Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
> 
> mysql> use bsbdj
> Reading table information for completion of table and column names
> You can turn off this feature to get a quicker startup with -A
> 
> Database changed
> mysql> show tables;
> +-----------------+
> | Tables_in_bsbdj |
> +-----------------+
> | t_channel       |
> | t_comment       |
> | t_content       |
> | t_forum         |
> | t_image         |
> | t_source        |
> | t_user          |
> | t_video         |
> +-----------------+
> 8 rows in set (0.00 sec)
> mysql> select * from t_user limit 5;
> +---------+----------------------------------------------------------------------------+--------+------+----------+-----------+-----------+----------------------------------------------------------+------------------+
> | uid     | header                                                                     | is_vip | is_v | room_url | room_name | room_role | room_icon                                                | nickname         |
> +---------+----------------------------------------------------------------------------+--------+------+----------+-----------+-----------+----------------------------------------------------------+------------------+
> |  705774 | http://wimg.spriteapp.cn/profile/large/2017/06/19/59472b6e56d33_mini.jpg   |      0 | NULL |          |           |           |                                                          | ????-            |
> | 3435589 | http://wimg.spriteapp.cn/profile/large/2014/06/12/5398f779bebdc_mini.jpg   |      0 | NULL |          |           |           |                                                          | ???              |
> | 5036360 | http://tp2.sinaimg.cn/1740848033/50/22834265309/1                          |      0 | NULL |          |           |           |                                                          | ??Brian          |
> | 5105294 | http://qzapp.qlogo.cn/qzapp/100682801/511E34E3CD200B6011AFF936EB682F26/100 |      0 | NULL |          |           |           |                                                          | 1???             |
> | 5348609 | http://wimg.spriteapp.cn/profile/large/2016/07/26/57974925b34a6_mini.jpg   |      0 |    0 |          | ?????     | ??        | http://wimg.spriteapp.cn/ugc/2016/1101/gang_level_12.png | ???????? [?????] |
> +---------+----------------------------------------------------------------------------+--------+------+----------+-----------+-----------+----------------------------------------------------------+------------------+
> 5 rows in set (0.00 sec)
> mysql> exit
> Bye
> root@d7631f5ff1da:/docker-entrypoint-initdb.d# exit
> exit
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/bsbdj-db/
> $ docker rm -f d7631f5ff1da
> d7631f5ff1da
> ~~~

### 2.4  使用Docker Compose编排容器

#### (1) 编写`docker-compose.yml`

> 在之前的bsbdj目录（bsbdj-app，bsbdj-db的父目录）中
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/
> $ vim docker-compose.yml
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/
> $ ls
> bsbdj-app          bsbdj-db           docker-compose.yml
> ~~~
>
> docker-compose.yml：[/bsbdj/docker-compose.yml](../05_dockercompose/lab_bsbdj/bsbdj/docker-compose.yml)
>
> ~~~yml
> version: '3.3' 	# docker compose文件解析规则是3.3版本的规则
> services: 		# 要部署的容器
> 	# 服务名、用于（1）描述服务的用途（2）创建容器时为容器命名（3）配置容器互联互通时表示网络的主机名
> 	db:
> 		build: ./bsbdj-db/ 	# Dockerfile所在目录
> 		restart: always		# 打开容器宕机自动重启
> 		environment:		# 为容器设置的环境变量
> 			MYSQL_ROOT_PASSWORD: root
> 	# 服务名，不同的服务名要按照依赖顺序进行排列
> 	app:
> 		build: ./bsbdj-app/	
> 		depends_on:			# 设置依赖后，docker compose会自动为有依赖关系的容器设置互联互通（类似网桥）
> 			- db			# 被依赖的服务名，可以当做主机名来使用
> 		ports:				# 端口映射
> 			- "80:80"
> ~~~
>
> 说明：
>
> (1) `yml`文件强制要求`:`与配置值之间加入一个空格
>
> (2) [bsbdj-app/application-dev.yml](../05_dockercompose/lab_bsbdj/bsbdj/bsbdj-app/application-dev.yml)中连接串主机名配置为`db`，它与[/bsbdj/docker-compose.yml](../05_dockercompose/lab_bsbdj/bsbdj/docker-compose.yml)中的服务名`db`相同
>
> ~~~yml
> spring:
> 	datasource:
> 		driver-class-name: com.mysql.jdbc.Driver
> 		# 连接串中的host为db
> 		url: jdbc:mysql://db:3306/bsbdj?useUnicode=true
> ~~~

#### (2) 创建并编排容器

> 执行`docker-compose -up`命令后，会先编译镜像，再创建容器
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/
> $ # docker-compose up	 # 前台运行，键入Ctrl+C时会停止并删除容器
> $ docker-compose up -d	 # 后台运行
> Starting bsbdj_db_1  ... done
> Starting bsbdj_app_1 ... done
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/
> $ # docker-compose logs app 		# 查看容器app的日志
> $ # docker-compose logs db			# 查看容器db的日志
> $ docker-compose logs | head -n5 	# 查看日志前5行
> Attaching to bsbdj_app_1, bsbdj_db_1
> app_1  |
> app_1  |   .   ____          _            __ _ _
> app_1  |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
> app_1  | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/
> $ docker ps
> CONTAINER ID   IMAGE       COMMAND                  CREATED         STATUS              PORTS                 NAMES
> 1893eb9e597b   bsbdj_app   "java -jar bsbdj.jar"    4 minutes ago   Up About a minute   0.0.0.0:80->80/tcp    bsbdj_app_1
> 22bbea18c27c   bsbdj_db    "docker-entrypoint.s…"   4 minutes ago   Up About a minute   3306/tcp, 33060/tcp   bsbdj_db_1
> ~~~

#### (3) 访问应用

> 用浏览器访问：http://localhost:80/
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/101_docker/docker_compose_bsbdj.jpg)

#### (4) 关闭并删除容器

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/101_docker/05_dockercompose/lab_bsbdj/bsbdj/
> $ docker-compose down	# 停止并删除容器
> Stopping bsbdj_app_1 ... done
> Stopping bsbdj_db_1  ... done
> Removing bsbdj_app_1 ... done
> Removing bsbdj_db_1  ... done
> Removing network bsbdj_default
> ~~~



