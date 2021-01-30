<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Docker基本概念和常用命令](#docker%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5%E5%92%8C%E5%B8%B8%E7%94%A8%E5%91%BD%E4%BB%A4)
  - [1. 基本概念](#1-%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5)
  - [2. 执行流程](#2-%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B)
  - [3. 常用命令](#3-%E5%B8%B8%E7%94%A8%E5%91%BD%E4%BB%A4)
  - [4. 例子：快速部署tomcat](#4-%E4%BE%8B%E5%AD%90%E5%BF%AB%E9%80%9F%E9%83%A8%E7%BD%B2tomcat)
    - [(1) 查找镜像](#1-%E6%9F%A5%E6%89%BE%E9%95%9C%E5%83%8F)
    - [(2) 抽取镜像](#2-%E6%8A%BD%E5%8F%96%E9%95%9C%E5%83%8F)
    - [(3) 创建对应的容器](#3-%E5%88%9B%E5%BB%BA%E5%AF%B9%E5%BA%94%E7%9A%84%E5%AE%B9%E5%99%A8)
    - [(5) 移除某个镜像](#5-%E7%A7%BB%E9%99%A4%E6%9F%90%E4%B8%AA%E9%95%9C%E5%83%8F)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Docker基本概念和常用命令

## 1. 基本概念

> (1) Docker是容器化平台：应用程序需 <-- Docker引擎 <-- 资源（物理机、虚拟机）
>
> (2) CS体系结构： 
>
> * Client（`docker CLI`）--Rest API--> Server(`docker deamon`)  
> * 可以用一个Client访问多个Server
>
> (3) `镜像`：只读文件、提供程序运行的完整资源（集装箱）；`容器`：由Docker创建、容器之间彼此隔离

## 2. 执行流程

> * 客户端：`docker build`, `docker pull`, `docker run`, ……
> * 服务端：`docker deamon`，`containers`，`images`，……
> * Docker镜像注册中心

## 3. 常用命令

> * `docker pull ${image_name} <:tags>`：从远程仓库抽取镜像，tags表示docker的版本，没提供tags时下载最新镜像
> * `docker images`：查看本地镜像
> * `docker run ${image_name} <:tags>`：创建容器启动应用，如果本地没有该镜像时、会先执行docker pull抽取镜像
> * `docker ps`：查看正在运行的镜像
> * `docker rm <-f> ${image_id}`：删除容器，`-f`参数强制删除正在运行的容器
> * `docker rmi <-f> ${image_name}:<tags>`：删除指定版本的镜像，`-f`参数强制删除存在对应容器的镜像

## 4. 例子：快速部署tomcat

### (1) 查找镜像

> * 搜索镜像：在`hub.docker.com`可以进行搜索 ，例如下面的两个"tomcat"的搜索链接
> 	* [`https://hub.docker.com/search?q=tomcat&type=image`](https://hub.docker.com/search?q=tomcat&type=image) 
> 	* [`https://hub.docker.com/_/tomcat`](https://hub.docker.com/_/tomcat)
> 	
> * 镜像发布方：
>
>     * 尽量选择官方发布的docker（往往带有Logo），个人发布的docker可能会有问题
>     * 另外关注下载数量、Stars。
>
> * 镜像的平台和CPU架构：
> 	
> 	* 在过滤框中选择基于那种操作系统和CPU架构，例如下面的tomcat搜索结果勾选了Linux和X86-64
> 	
> 	  https://hub.docker.com/search?q=tomcat&type=image&operating_system=linux&architecture=amd64
> 	
> 	  ![](https://raw.githubusercontent.com/kenfang119/pics/main/upload/101_docker/docker_img_searh_result_page.jpg)
> 	
> 	* 对应的的tag，点击其中一个可以看到详细描述和源代码，例如：</br>
> 	  [10.0.0-M9-jdk15-openjdk-oraclelinux7, 10.0.0-jdk15-openjdk-oraclelinux7, 10.0-jdk15-openjdk-oraclelinux7, 10-jdk15-openjdk-oraclelinux7, 10.0.0-M9-jdk15-openjdk-oracle, 10.0.0-jdk15-openjdk-oracle, 10.0-jdk15-openjdk-oracle, 10-jdk15-openjdk-oracle](https://github.com/docker-library/tomcat/blob/061a912e90b210f9cd7c5b631d1b5e666678d50f/10.0/jdk15/openjdk-oraclelinux7/Dockerfile)

### (2) 抽取镜像

抽取镜像

> ~~~bash
> [root@localhost ~]# docker pull tomcat
> Using default tag: latest
> latest: Pulling from library/tomcat
> e4c3d3e4f7b0: Pull complete
> ...
> 9eac66e32ef5: Pull complete
> Digest: sha256:30dd6da4bc6b290da345cd8a90212f358b6a094f6197a59fe7f2ba9b8a261b4f
> Status: Downloaded newer image for tomcat:latest
> docker.io/library/tomcat:latest
> ~~~

查看镜像，其中可以看到TAG和镜像ID

> ~~~bash
> [root@localhost ~]# docker images
> REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
> tomcat              latest              625b734f984e        3 days ago          648MB
> hello-world         latest              bf756fb1ae65        9 months ago        13.3kB
> ~~~

抽取指定版本的镜像，从前面的docker hub页面找到tag，追加到docker pull命令后面

> ~~~bash
> [root@localhost ~]# docker pull tomcat:8.5.59-jdk8-openjdk
> 8.5.59-jdk8-openjdk: Pulling from library/tomcat
> e4c3d3e4f7b0: Already exists
> ...
> 56ed8aed535b: Pull complete
> Digest: sha256:0cd1574a9de689da1e98297f8dfa5de7821eb391e2e6922dcf05433bfe680558
> Status: Downloaded newer image for tomcat:8.5.59-jdk8-openjdk
> docker.io/library/tomcat:8.5.59-jdk8-openjdk
> ~~~

再次查看镜像，其中tag不能唯一表示镜像（latest有可能就是某个tag），Image ID可以唯一标识（latest往往指向使用最多的版本、作为默认的安装下载）

> ~~~bash
> [root@localhost ~]# docker images
> REPOSITORY          TAG                   IMAGE ID            CREATED             SIZE
> tomcat              latest                625b734f984e        3 days ago          648MB
> tomcat              8.5.59-jdk8-openjdk   02d718ca90fd        12 days ago         530MB
> hello-world         latest                bf756fb1ae65        9 months ago        13.3kB
> ~~~

### (3) 创建对应的容器

`docker run tomcat:8.5.59-jdk8-openjdk` ，如果不指定，默认tag为latest

> ~~~bash
> [root@localhost ~]# docker run tomcat #如果不指定，默认tag为latest
> NOTE: Picked up JDK_JAVA_OPTIONS:  --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED
> ...
> 26-Oct-2020 09:07:29.758 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["http-nio-8080"]
> 26-Oct-2020 09:07:29.877 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [352] milliseconds
> ~~~
>
> 日志中可以看到、Tomcat使用了Docker容器的8080端口，但是目前，还不能从外部的浏览器访问该Tomcat，需要做端口映射。例如：
>
> * 将宿主机（本例为安装Docker的那台VM）的8000端口映射到Docker的8080端口
> * 这时外部系统/浏览器访问宿主机的8000端口，就可以通过端口映射，访问到Docker的8080端口
> 这种端口映射也带来一个好处，可以对运行在Docker内的Web容器无缝迁移（例如：将安装了Tomcat的Docker容器、换成安装了Jetty的Docker容器，只需要更改端口映射配置即可，而无需改动其他访问该Web服务的系统）
>
为宿主机开通8000端口，为docker端口映射做准备

> CentOS 7的Linux SE防火墙默认关闭这些端口
>
> ~~~bash
> [root@localhost ~]# systemctl status firewalld  #如果是关闭就执行systemctl start firewalld
> ● firewalld.service - firewalld - dynamic firewall daemon
>    Loaded: loaded (/usr/lib/systemd/system/firewalld.service; enabled; vendor preset: enabled)
>    Active: active (running) since 一 2020-10-26 13:54:28 CST; 3h 26min ago
>      Docs: man:firewalld(1)
>  Main PID: 754 (firewalld)
>     Tasks: 2
>    Memory: 920.0K
>    CGroup: /system.slice/firewalld.service
>            └─754 /usr/bin/python2 -Es /usr/sbin/firewalld --nofork --nopid
> [root@localhost ~]# firewall-cmd --list-ports
> [root@localhost ~]# firewall-cmd --zone=public --add-port=8000/tcp --permanent
> success
> [root@localhost ~]# firewall-cmd --reload
> success
> [root@localhost ~]# firewall-cmd --list-ports
> 8000/tcp
> ~~~

以端口映射+Deamon的方式启动容器

> 启动容器时指定端口映射，将宿主机的8000端口映射到容器的8080端口，同时这次指定`-d`参数，让Docker容器在后台运行（deamon模式），并用`netstat -tulpn`查看端口映射
>
> ~~~bash
> [root@localhost ~]# docker run -p 8000:8080 -d tomcat
> 2c358316e90ddb7f261bb69d62aceec7219b0fd1a7ca1ddae184310cabc16497
> [root@localhost ~]# netstat -tulpn
> Active Internet connections (only servers)
> Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
> tcp6       0      0 :::8000                 :::*                    LISTEN      25620/docker-proxy
> ...
> udp6       0      0 :::851                  :::*                                679/rpcbind
> ~~~
### (4) 移除容器

> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                    NAMES
> 2c358316e90d        tomcat              "catalina.sh run"   4 minutes ago       Up 4 minutes        0.0.0.0:8000->8080/tcp   youthful_joliot
> [root@localhost ~]# docker rm 2c358316e90d #不能直接移除运行中的容器，除非指定-f参数强制移除
> Error response from daemon: You cannot remove a running container 2c358316e90ddb7f261bb69d62aceec7219b0fd1a7ca1ddae184310cabc16497. Stop the container before attempting removal or force remove
> [root@localhost ~]# docker stop 2c358316e90d && docker rm 2c358316e90d #先停止、再移除
> 2c358316e90d
> 2c358316e90d
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
> ~~~

### (5) 移除某个镜像

> ~~~bash
> [root@localhost ~]# docker images #如果有对应容器在运行、会移除失败，除非指定-f参数但不推荐
> REPOSITORY          TAG                   IMAGE ID            CREATED             SIZE
> tomcat              latest                625b734f984e        3 days ago          648MB
> tomcat              8.5.59-jdk8-openjdk   02d718ca90fd        12 days ago         530MB
> hello-world         latest                bf756fb1ae65        9 months ago        13.3kB
> [root@localhost ~]# docker rmi tomcat:8.5.59-jdk8-openjdk
> Untagged: tomcat:8.5.59-jdk8-openjdk
> Untagged: tomcat@sha256:0cd1574a9de689da1e98297f8dfa5de7821eb391e2e6922dcf05433bfe680558
> Deleted: 
> ...
> sha256:f40370342e4aedb1e1623924089468a7c0db1d99916759e0f981928399478fb5
> Deleted: sha256:254cf71da09bbc16e304f925dee4270ac9069649dee8576423a6a77186ee0061
> [root@localhost ~]# docker images
> REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
> tomcat              latest              625b734f984e        3 days ago          648MB
> hello-world         latest              bf756fb1ae65        9 months ago        13.3kB
> ~~~