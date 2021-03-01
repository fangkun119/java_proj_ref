<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [容器间通信和数据共享](#%E5%AE%B9%E5%99%A8%E9%97%B4%E9%80%9A%E4%BF%A1%E5%92%8C%E6%95%B0%E6%8D%AE%E5%85%B1%E4%BA%AB)
  - [1. 使用Link实现容器间单向通信](#1-%E4%BD%BF%E7%94%A8link%E5%AE%9E%E7%8E%B0%E5%AE%B9%E5%99%A8%E9%97%B4%E5%8D%95%E5%90%91%E9%80%9A%E4%BF%A1)
    - [1.1 背景](#11-%E8%83%8C%E6%99%AF)
    - [1.2 准备](#12-%E5%87%86%E5%A4%87)
      - [(1) 启动docker service](#1-%E5%90%AF%E5%8A%A8docker-service)
      - [(2) 获取tomcat和mysql镜像](#2-%E8%8E%B7%E5%8F%96tomcat%E5%92%8Cmysql%E9%95%9C%E5%83%8F)
    - [1.3 Demo](#13-demo)
      - [(1) 创建容器web](#1-%E5%88%9B%E5%BB%BA%E5%AE%B9%E5%99%A8web)
      - [(2) 创建容器database](#2-%E5%88%9B%E5%BB%BA%E5%AE%B9%E5%99%A8database)
      - [(3) 获得容器database的虚拟IP地址](#3-%E8%8E%B7%E5%BE%97%E5%AE%B9%E5%99%A8database%E7%9A%84%E8%99%9A%E6%8B%9Fip%E5%9C%B0%E5%9D%80)
      - [(4) 容器间使用虚拟IP可以连通，使用容器name无法连通](#4-%E5%AE%B9%E5%99%A8%E9%97%B4%E4%BD%BF%E7%94%A8%E8%99%9A%E6%8B%9Fip%E5%8F%AF%E4%BB%A5%E8%BF%9E%E9%80%9A%E4%BD%BF%E7%94%A8%E5%AE%B9%E5%99%A8name%E6%97%A0%E6%B3%95%E8%BF%9E%E9%80%9A)
      - [(5) 重建容器web并配置link](#5-%E9%87%8D%E5%BB%BA%E5%AE%B9%E5%99%A8web%E5%B9%B6%E9%85%8D%E7%BD%AElink)
      - [(6) 此时使用name也可以连通容器](#6-%E6%AD%A4%E6%97%B6%E4%BD%BF%E7%94%A8name%E4%B9%9F%E5%8F%AF%E4%BB%A5%E8%BF%9E%E9%80%9A%E5%AE%B9%E5%99%A8)
    - [1.4 环境清理](#14-%E7%8E%AF%E5%A2%83%E6%B8%85%E7%90%86)
  - [2. 使用Bridge实现容器间双向通信](#2-%E4%BD%BF%E7%94%A8bridge%E5%AE%9E%E7%8E%B0%E5%AE%B9%E5%99%A8%E9%97%B4%E5%8F%8C%E5%90%91%E9%80%9A%E4%BF%A1)
    - [2.1 网桥的功能](#21-%E7%BD%91%E6%A1%A5%E7%9A%84%E5%8A%9F%E8%83%BD)
    - [2.2 准备](#22-%E5%87%86%E5%A4%87)
      - [(1) 启动docker service](#1-%E5%90%AF%E5%8A%A8docker-service-1)
      - [(2) 查看已有的镜像](#2-%E6%9F%A5%E7%9C%8B%E5%B7%B2%E6%9C%89%E7%9A%84%E9%95%9C%E5%83%8F)
    - [2.3 Demo](#23-demo)
      - [(1) 创建容器](#1-%E5%88%9B%E5%BB%BA%E5%AE%B9%E5%99%A8)
      - [(2) 查看docker底层网络服务明细](#2-%E6%9F%A5%E7%9C%8Bdocker%E5%BA%95%E5%B1%82%E7%BD%91%E7%BB%9C%E6%9C%8D%E5%8A%A1%E6%98%8E%E7%BB%86)
      - [(3) 创建用于容器通信的网桥](#3-%E5%88%9B%E5%BB%BA%E7%94%A8%E4%BA%8E%E5%AE%B9%E5%99%A8%E9%80%9A%E4%BF%A1%E7%9A%84%E7%BD%91%E6%A1%A5)
      - [(4) 让容器与网桥绑定](#4-%E8%AE%A9%E5%AE%B9%E5%99%A8%E4%B8%8E%E7%BD%91%E6%A1%A5%E7%BB%91%E5%AE%9A)
      - [(5) 验证](#5-%E9%AA%8C%E8%AF%81)
    - [2.4 环境清理](#24-%E7%8E%AF%E5%A2%83%E6%B8%85%E7%90%86)
    - [2.5 网桥可以互联互通的的原理](#25-%E7%BD%91%E6%A1%A5%E5%8F%AF%E4%BB%A5%E4%BA%92%E8%81%94%E4%BA%92%E9%80%9A%E7%9A%84%E7%9A%84%E5%8E%9F%E7%90%86)
  - [3 使用Volumn在容器间共享数据](#3-%E4%BD%BF%E7%94%A8volumn%E5%9C%A8%E5%AE%B9%E5%99%A8%E9%97%B4%E5%85%B1%E4%BA%AB%E6%95%B0%E6%8D%AE)
    - [3.1 背景](#31-%E8%83%8C%E6%99%AF)
    - [3.2 准备](#32-%E5%87%86%E5%A4%87)
    - [3.3 Demo：使用`-v`参数挂载](#33-demo%E4%BD%BF%E7%94%A8-v%E5%8F%82%E6%95%B0%E6%8C%82%E8%BD%BD)
  - [3.4 使用共享容器来简化文件共享](#34-%E4%BD%BF%E7%94%A8%E5%85%B1%E4%BA%AB%E5%AE%B9%E5%99%A8%E6%9D%A5%E7%AE%80%E5%8C%96%E6%96%87%E4%BB%B6%E5%85%B1%E4%BA%AB)
  - [3.5 环境清理](#35-%E7%8E%AF%E5%A2%83%E6%B8%85%E7%90%86)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 容器间通信和数据共享 

## 1. 使用Link实现容器间单向通信

### 1.1 背景

`容器间单向通信`：

> 例如容器A运行tomcat，容器B运行MySQL，需要让容器A能够单向访问容器B，实现tomcat对MySQL的访问

`虚拟IP`：

> 每个Docker容器被创建时，会生成一个虚拟IP，这个IP不能被外部访问，但是可以用在Docker容器间的访问

`问题`：

> 每次使用Image创建一个容器，容器的IP地址都会发生变化，以上面的场景为例，容器B的地址变化了，如何更改容器A中tomcat配置的IP地址呢？

`解决`：

> 应用程序不再配置底层IP地址，而是配置容器的名称

### 1.2 准备

#### (1) 启动docker service

> ~~~bash
> [root@localhost ~]# service docker start
> Redirecting to /bin/systemctl start docker.service
> [root@localhost ~]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
>    Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
>    Active: active (running) 
> ~~~

#### (2) 获取tomcat和mysql镜像

> ~~~bash
> [root@localhost ~]# docker pull tomcat
> Using default tag: latest
> Trying to pull repository docker.io/library/tomcat ...
> latest: Pulling from docker.io/library/tomcat
> Digest: sha256:581a607a1159f25f48b6a022d4a3ba1d612c981e375ca0c27f53527a01eea734
> Status: Image is up to date for docker.io/tomcat:latest
> [root@localhost ~]# docker pull mysql:5.7
> Trying to pull repository docker.io/library/mysql ...
> 5.7: Pulling from docker.io/library/mysql
> Digest: sha256:b3d1eff023f698cd433695c9506171f0d08a8f92a0c8063c1a4d9db9a55808df
> Status: Image is up to date for docker.io/mysql:5.7
> ~~~

### 1.3 Demo

#### (1) 创建容器web

> ~~~bash
> # 创建tomcat容器
> # --name web: 命名为web
> # -d：以deamon的方式创建容器使其后台运行
> [root@localhost ~]# docker run -d --name web tomcat
> 3547a9555b5978406c226f81a961b13ff0f9252443638a5cfff7e21008e65575
> ~~~

#### (2) 创建容器database

> ~~~bash
> # -it：交互式模式，阻止容器执行完/bin/bash命令后退出，而是按照-d的要求在后台运行
> # 镜像使用centos，是因为目前只是建一个容器模拟场景，还不需要启动mysql
> [root@localhost ~]# docker run -d --name database -it centos /bin/bash
> abdcfeba0fd57f03f38986a5290b2508a7149a3eed4f3437603afac6757f140f
> ~~~

#### (3) 获得容器database的虚拟IP地址

> 方法：
>
> * 使用`docker ps`得到`CONTAINER_ID`
>
> * 使用`docker inspect ${CONTAINER_ID}`得到容器信息，其中NetworkSettings部分包含了该容器的虚拟IP
>
> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
> abdcfeba0fd5        centos              "/bin/bash"         53 seconds ago      Up 52 seconds                           database
> 3547a9555b59        tomcat              "catalina.sh run"   4 minutes ago       Up 4 minutes        8080/tcp            web
> [root@localhost ~]# docker inspect abdcfeba0fd5 | grep Networks -A15
>             "Networks": {
>                 "bridge": {
>                     "IPAMConfig": null,
>                     "Links": null,
>                     "Aliases": null,
>                     "NetworkID": "2fefedc2c659edf6b22a0e9be00e3741dda5923a276ac1969918f3f6e0efa891",
>                     "EndpointID": "570aa348a7647e791797e9c1d644ff14dbe517eb362be40b0fea37afde9407fa",
>                     "Gateway": "172.17.0.1",
>                     "IPAddress": "172.17.0.3",
>                     "IPPrefixLen": 16,
>                     "IPv6Gateway": "",
>                     "GlobalIPv6Address": "",
>                     "GlobalIPv6PrefixLen": 0,
>                     "MacAddress": "02:42:ac:11:00:03"
>                 }
>             }
> [root@localhost ~]# docker inspect 3547a9555b59 | grep Networks -A15 | grep IPAddress
>                     "IPAddress": "172.17.0.2",
> ~~~
>
> 上面例子中的172.17.0.3就是容器“database”的虚拟IP，而172.17.0.2是容器“web”的虚拟IP地址

#### (4) 容器间使用虚拟IP可以连通，使用容器name无法连通 

> ~~~bash
> # 获取容器ID
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
> abdcfeba0fd5        centos              "/bin/bash"         11 minutes ago      Up 11 minutes                           database
> 3547a9555b59        tomcat              "catalina.sh run"   15 minutes ago      Up 15 minutes       8080/tcp            web
> # 进入容器”web“
> [root@localhost ~]# docker exec -it 3547a9555b59 /bin/bash
> # 在web容器内使用databas容器的虚拟IP，可以访问成功
> root@3547a9555b59:/usr/local/tomcat# ping 172.17.0.3
> PING 172.17.0.3 (172.17.0.3) 56(84) bytes of data.
> 64 bytes from 172.17.0.3: icmp_seq=1 ttl=64 time=0.122 ms
> 64 bytes from 172.17.0.3: icmp_seq=2 ttl=64 time=0.087 ms
> ^C
> --- 172.17.0.3 ping statistics ---
> 2 packets transmitted, 2 received, 0% packet loss, time 2ms
> rtt min/avg/max/mdev = 0.087/0.104/0.122/0.020 ms
> # 在web容器内使用database容器的容器名，不能访问
> root@3547a9555b59:/usr/local/tomcat# ping database
> ping: database: Name or service not known
> # 关闭容器
> root@3547a9555b59:/usr/local/tomcat# shutdown.sh
> Using CATALINA_BASE:   /usr/local/tomcat
> Using CATALINA_HOME:   /usr/local/tomcat
> Using CATALINA_TMPDIR: /usr/local/tomcat/temp
> Using JRE_HOME:        /usr/local/openjdk-11
> Using CLASSPATH:       /usr/local/tomcat/bin/bootstrap.jar:/usr/local/tomcat/bin/tomcat-juli.jar
> Using CATALINA_OPTS:
> NOTE: Picked up JDK_JAVA_OPTIONS:  --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED
> root@3547a9555b59:/usr/local/tomcat 
> [root@localhost ~]#
> ~~~

#### (5) 重建容器web并配置link

> ~~~bash
> # 删除容器web
> [root@localhost ~]# docker rm 3547a9555b59
> 3547a9555b59
> # 此时已经看不到容器web，只剩下容器database
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
> abdcfeba0fd5        centos              "/bin/bash"         21 minutes ago      Up 21 minutes                           database
> # 重建容器web，--link参数指向容器database
> [root@localhost ~]# docker run -d --name web --link database tomcat
> 8a623a219e05d63a63fba337b6f4b94d4e2cf4251235a53fa95ad5f257029a18
> ~~~

#### (6) 此时使用name也可以连通容器

> 进入容器web，使用`ping database`可以访问容器database
>
> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
> 8a623a219e05        tomcat              "catalina.sh run"   9 seconds ago       Up 8 seconds        8080/tcp            web
> abdcfeba0fd5        centos              "/bin/bash"         22 minutes ago      Up 22 minutes                           database
> [root@localhost ~]# docker exec -it 8a623a219e05 /bin/bash
> root@8a623a219e05:/usr/local/tomcat# ping database
> PING database (172.17.0.3) 56(84) bytes of data.
> 64 bytes from database (172.17.0.3): icmp_seq=1 ttl=64 time=0.102 ms
> 64 bytes from database (172.17.0.3): icmp_seq=2 ttl=64 time=0.070 ms
> 64 bytes from database (172.17.0.3): icmp_seq=3 ttl=64 time=0.080 ms
> ^C
> --- database ping statistics ---
> 3 packets transmitted, 3 received, 0% packet loss, time 1000ms
> rtt min/avg/max/mdev = 0.070/0.084/0.102/0.013 ms
> root@8a623a219e05:/usr/local/tomcat# shutdown.sh
> Using CATALINA_BASE:   /usr/local/tomcat
> Using CATALINA_HOME:   /usr/local/tomcat
> Using CATALINA_TMPDIR: /usr/local/tomcat/temp
> Using JRE_HOME:        /usr/local/openjdk-11
> Using CLASSPATH:       /usr/local/tomcat/bin/bootstrap.jar:/usr/local/tomcat/bin/tomcat-juli.jar
> Using CATALINA_OPTS:
> NOTE: Picked up JDK_JAVA_OPTIONS:  --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED
> root@8a623a219e05:/usr/local/tomcat#
> [root@localhost ~]# docker rm web
> web
> ~~~
>
> 这样容器名”database“就可以当做域名，在容器”web“中应用的JDBC中配置，以访问database

### 1.4 环境清理

> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
> abdcfeba0fd5        centos              "/bin/bash"         29 minutes ago      Up 29 minutes                           database
> [root@localhost ~]# docker rm -f abdcfeba0fd5
> abdcfeba0fd5
> [root@localhost ~]# service docker stop
> Redirecting to /bin/systemctl stop docker.service
> [root@localhost ~]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
>    Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
>    Active: inactive (dead) 
> ~~~

## 2. 使用Bridge实现容器间双向通信

### 2.1 网桥的功能

> * 网桥可以作为容器与外界宿主机的通信桥梁：容器 ↔ 网桥 ↔ 宿主机物理网卡 ↔ 外网
> * 作为容器间双向通信的媒介：多个容器绑定在同一个网桥上

### 2.2 准备

#### (1) 启动docker service

> ~~~bash
> [root@localhost ~]# service docker start
> Redirecting to /bin/systemctl start docker.service
> [root@localhost ~]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
> Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
> Active: active (running) 
> ~~~

#### (2) 查看已有的镜像

> ~~~bash
> [root@localhost ~]# docker images
> REPOSITORY                    TAG                 IMAGE ID            CREATED             SIZE
> docker.io/wordpress           latest              d94129b6e1f4        10 days ago         550 MB
> docker.io/tomcat              latest              345867df0879        11 days ago         649 MB
> docker.io/mysql               5.7                 a70d36bc331a        13 days ago         449 MB
> docker.io/centos              latest              300e315adb2f        7 weeks ago         209 MB
> ~~~

### 2.3 Demo

#### (1) 创建容器

> ~~~bash
> [root@localhost ~]# docker run -d --name web tomcat
> ad04b80548cb19545020b6986d31e06af51a286b8132aa516bb4f053c841cb08
> [root@localhost ~]# docker run -d -it --name database centos /bin/bash
> 8f362519c64bee1f50cd15c8620b311fbb036a9581f09c7bed844c2310911515
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED              STATUS              PORTS               NAMES
> 8f362519c64b        centos              "/bin/bash"         45 seconds ago       Up 44 seconds                           database
> ad04b80548cb        tomcat              "catalina.sh run"   About a minute ago   Up About a minute   8080/tcp            web
> ~~~

#### (2) 查看docker底层网络服务明细

> ~~~bash
> [root@localhost ~]# docker network ls
> NETWORK ID          NAME                DRIVER              SCOPE
> 37cc4cdfc9df        bridge              bridge              local
> 750b50a0783d        host                host                local
> b69a182e2470        none                null                local
> ~~~
>
> 上面的`37cc4cdfc9df bridge`是默认网桥，用于容器和宿主机的通信

#### (3) 创建用于容器通信的网桥

> ~~~bash
> [root@localhost ~]# docker network create -d bridge my-bridge
> 32cb7bb05792dec87cf5d7fe140effe29409a4213dc18d15b972e4ae5f96bcf0
> [root@localhost ~]# docker network ls
> NETWORK ID          NAME                DRIVER              SCOPE
> 37cc4cdfc9df        bridge              bridge              local
> 750b50a0783d        host                host                local
> 32cb7bb05792        my-bridge           bridge              local
> b69a182e2470        none                null                local
> ~~~

#### (4) 让容器与网桥绑定

> ~~~bash
> [root@localhost ~]# docker network connect my-bridge web
> [root@localhost ~]# docker network connect my-bridge database
> ~~~

#### (5) 验证

> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
> 8f362519c64b        centos              "/bin/bash"         6 minutes ago       Up 6 minutes                            database
> ad04b80548cb        tomcat              "catalina.sh run"   6 minutes ago       Up 6 minutes        8080/tcp            web
> [root@localhost ~]# docker exec -it 8f362519c64b /bin/bash
> [root@8f362519c64b /]# ping web
> PING web (172.18.0.2) 56(84) bytes of data.
> 64 bytes from web.my-bridge (172.18.0.2): icmp_seq=1 ttl=64 time=0.134 ms
> 64 bytes from web.my-bridge (172.18.0.2): icmp_seq=2 ttl=64 time=0.147 ms
> ^C
> --- web ping statistics ---
> 2 packets transmitted, 2 received, 0% packet loss, time 3ms
> rtt min/avg/max/mdev = 0.134/0.140/0.147/0.013 ms
> [root@localhost ~]# docker exec -it ad04b80548cb /bin/bash
> root@ad04b80548cb:/usr/local/tomcat# ping database
> PING database (172.18.0.3) 56(84) bytes of data.
> 64 bytes from database.my-bridge (172.18.0.3): icmp_seq=1 ttl=64 time=0.267 ms
> 64 bytes from database.my-bridge (172.18.0.3): icmp_seq=2 ttl=64 time=0.132 ms
> ^C
> --- database ping statistics ---
> 2 packets transmitted, 2 received, 0% packet loss, time 1000ms
> rtt min/avg/max/mdev = 0.132/0.199/0.267/0.068 ms
> root@ad04b80548cb:/usr/local/tomcat# exit
> exit
> ~~~
>
> 两个容器已经可以互联互通了

### 2.4 环境清理

> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
> 8f362519c64b        centos              "/bin/bash"         20 minutes ago      Up 20 minutes                           database
> ad04b80548cb        tomcat              "catalina.sh run"   20 minutes ago      Up 20 minutes       8080/tcp            web
> [root@localhost ~]# docker rm -f 8f362519c64b
> 8f362519c64b
> [root@localhost ~]# docker rm -f ad04b80548cb
> ad04b80548cb
> [root@localhost ~]# service docker stop
> Redirecting to /bin/systemctl stop docker.service
> [root@localhost ~]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
> Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
> Active: inactive (dead) 
> ~~~

### 2.5 网桥可以互联互通的的原理

> 以虚拟网卡（网关）作为媒介，使网关内部的容器可以互联互通，但是如果要与外网通信，仍然需要在做IP转换通过物理网卡来访问外界

## 3 使用Volumn在容器间共享数据

### 3.1 背景

Volumn：数据卷、存储数据的单位

使用场景：

> 例如宿主机上有一个Web应用的镜像，启动多个容器时，希望他们的页面文件是相同的。一个方法是在宿主机上开辟一块存储，让容器共享

挂载方法1：使用`-v`参数

> `docker run --name ${容器名} -v ${宿主机路径}:${容器内挂载路径} ${镜像名}`
>
> ~~~bash
> # 例如
> docker run --name image1 -v /usr/webapps:/usr/local/tomcat/webapps tomcat
> ~~~
>
> 缺点是，输入路径中任何一处错误都会导致挂载失败

挂载方法2：创建共享容器

> ~~~bash
> # 创建共享容器，”create“表示只创建不执行
> docker create --name webpage -v /webapps:/tomcat/webapps tomcat /bin/true
> # 共享容器挂载点，webpage是上面创建的共享同期
> docker run --volumes-from webpage --name image1 -d tomcat
> ~~~
>
> 修改挂载点时，只需要修改共享容器的挂载路径即可

### 3.2 准备

> 宿主机上的共享文件
>
> ~~~bash
> [root@localhost ~]# mkdir /usr/webapps/volumn-test/
> [root@localhost ~]# echo '<h1>I am volumn test page</h1>' > /usr/webapps/volumn-test/index.html
> [root@localhost ~]# cat /usr/webapps/volumn-test/index.html
> <h1>I am volumn test page</h1>
> ~~~
>
> 启动docker service，查看已有镜像
>
> ~~~bash
> [root@localhost ~]# service docker start
> Redirecting to /bin/systemctl start docker.service
> [root@localhost ~]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
> Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
> Active: active (running) 
> [root@localhost ~]# docker images
> REPOSITORY                    TAG                 IMAGE ID            CREATED             SIZE
> fangkun119/dockerdemo-redis   latest              b6f8137796d1        2 days ago          494 MB
> docker.io/wordpress           latest              d94129b6e1f4        10 days ago         550 MB
> docker.io/tomcat              latest              345867df0879        11 days ago         649 MB
> docker.io/mysql               5.7                 a70d36bc331a        13 days ago         449 MB
> docker.io/centos              latest              300e315adb2f        7 weeks ago         209 MB
> ~~~

### 3.3 Demo：使用`-v`参数挂载

> 使用`-v挂载`创建容器`t1`，端口映射到宿主机的8000
>
> ~~~bash
> # 使用-v参数挂载，端口映射到8000
> [root@localhost ~]# docker run --name t1 -p 8000:8080 -d -v /usr/webapps:/usr/local/tomcat/webapps tomcat
> # 查看宿主机的IP，为192.168.1.170
> [root@localhost ~]# ip add | grep -v inet6 | grep inet
>     inet 127.0.0.1/8 scope host lo
>     inet 192.168.1.170/24 brd 192.168.1.255 scope global noprefixroute dynamic enp0s8
>     inet 10.0.2.15/24 brd 10.0.2.255 scope global noprefixroute dynamic eth0
>     inet 192.168.122.1/24 brd 192.168.122.255 scope global virbr0
>     inet 172.17.0.1/16 scope global docker0
>     inet 172.18.0.1/16 scope global br-32cb7bb05792
> ~~~
>
> 根据宿主机IP及端口映射，访问http://192.168.1.170:8000/valumn-test，可以看到共享文件生成的网页
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl http://192.168.1.170:8000/volumn-test/
> <h1>I am volumn test page</h1>
> ~~~
>
> 登入容器，可以查看到共享到容器内部的文件
>
> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                    NAMES
> c867dd9b31bb        tomcat              "catalina.sh run"   7 minutes ago       Up 7 minutes        0.0.0.0:8000->8080/tcp   t1
> [root@localhost ~]# docker exec -it c867dd9b31bb /bin/bash
> root@c867dd9b31bb:/usr/local/tomcat# cd webapps
> root@c867dd9b31bb:/usr/local/tomcat/webapps# ls
> volumn-test
> root@c867dd9b31bb:/usr/local/tomcat/webapps# ls volumn-test/
> index.html
> root@c867dd9b31bb:/usr/local/tomcat/webapps# exit
> exit
> ~~~
>
> 使用`-v挂载`创建容器`t2`，端口映射到宿主机的8001
>
> ~~~bash
> [root@localhost ~]# docker run --name t2 -p 8001:8080 -d -v /usr/webapps:/usr/local/tomcat/webapps tomcat
> 86a895facc55734837ab321541214a6f74575eade9b7ce7e460fd094ac130a48
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                    NAMES
> 86a895facc55        tomcat              "catalina.sh run"   10 seconds ago      Up 9 seconds        0.0.0.0:8001->8080/tcp   t2
> c867dd9b31bb        tomcat              "catalina.sh run"   12 minutes ago      Up 12 minutes       0.0.0.0:8000->8080/tcp   t1
> ~~~
>
> 使用该端口同样可以访问到共享文件
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl http://192.168.1.170:8001/volumn-test/
> <h1>I am volumn test page</h1>
> ~~~
>
> 在宿主机上修改被共享的文件
>
> ~~~bash
> [root@localhost ~]# vim /usr/webapps/volumn-test/index.html
> [root@localhost ~]# cat /usr/webapps/volumn-test/index.html
> <h1>Volumn test page has been modified</h1>
> ~~~
>
> 可以看到修改内容对两个容器都生效了（不需要重启容器）
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl http://192.168.1.170:8000/volumn-test/
> <h1>Volumn test page has been modified</h1>
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl http://192.168.1.170:8001/volumn-test/
> <h1>Volumn test page has been modified</h1>
> ~~~

### 3.4 使用共享容器来简化文件共享

> 创建共享容器：
>
> * 容器名为”webpage“
> * ”create“表示只创建不运行
> * 其中的/bin/true是个占位符
>
> ~~~bash
> [root@localhost ~]# docker create --name webpage -v /usr/webapps/:/usr/local/tomcat/webapps tomcat /bin/true
> e75b49ae7677d62d8bd77a24f068e76bf7c006b06a1e9e4fdf6dcc0d56aec571
> ~~~
>
> 创建容器`t3`、`t4`，它们借助上面的共享容器"webpage"来访问宿主机上的共享容器
>
> * `t3`：端口映射到宿主机的8002
> * `t4`：端口映射到宿主机的8003
>
> ~~~bash
> [root@localhost ~]# docker run -p 8002:8080 --volumes-from webpage --name t3 -d tomcat
> b0043a89a5da0b47e2ead59411ff72339a4ddafa57a9831ea374b96fe547c108
> [root@localhost ~]# docker run -p 8003:8080 --volumes-from webpage --name t4 -d tomcat
> d2d21fc56f3ca22261d9b130d487bd5e350c2f62e96bea2a5e7df31d63ec7728
> ~~~
>
> 可以看到容器共享生效
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl http://192.168.1.170:8002/volumn-test/
> <h1>Volumn test page has been modified</h1>
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl http://192.168.1.170:8003/volumn-test/
> <h1>Volumn test page has been modified</h1>
> ~~~

### 3.5 环境清理

> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE               COMMAND             CREATED              STATUS              PORTS                    NAMES
> d2d21fc56f3c        tomcat              "catalina.sh run"   About a minute ago   Up About a minute   0.0.0.0:8003->8080/tcp   t4
> b0043a89a5da        tomcat              "catalina.sh run"   About a minute ago   Up About a minute   0.0.0.0:8002->8080/tcp   t3
> 86a895facc55        tomcat              "catalina.sh run"   9 minutes ago        Up 9 minutes        0.0.0.0:8001->8080/tcp   t2
> c867dd9b31bb        tomcat              "catalina.sh run"   21 minutes ago       Up 21 minutes       0.0.0.0:8000->8080/tcp   t1
> [root@localhost ~]# docker rm -f t1
> t1
> [root@localhost ~]# docker rm -f t2
> t2
> [root@localhost ~]# docker rm -f t3
> t3
> [root@localhost ~]# docker rm -f t4
> t4
> [root@localhost ~]# service docker stop
> Redirecting to /bin/systemctl stop docker.service
> [root@localhost ~]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
>    Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
>    Active: inactive (dead)
> ~~~











