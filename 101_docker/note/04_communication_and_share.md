[TOC]

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
>    Active: active (running) since 日 2021-01-31 20:33:30 CST; 2s ago
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
> ~~~
>
> 这样容器名”database“就可以当做域名，在容器”web“中应用的JDBC中配置，以访问database

#### (7) 环境清理

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
>    Active: inactive (dead) since 日 2021-01-31 21:56:56 CST; 6s ago
> ~~~

## 2. 使用Bridge实现容器间双向通信

> 

## 3. 容器间共享数据

> 



