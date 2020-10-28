# Dockerfile

##1. 了解容器内部结构

(1) 通过镜像的`Dockerfile`查看容器内部结构

> 使用`docker run`可以创建容器并运行，如果想知道这个容器内部有什么组件，可采用如下方法
> 
> 在`Docker Hub`的镜像页面（例如：[`https://hub.docker.com/_/tomcat`](https://hub.docker.com/_/tomcat)）：</br>
> (1) 查看`Quick Reference` </br>
> (2) 点击某个tag的链接，查看`Dockerfile`（基准镜像、环境变量、相关命令等），例如 [8.5.59-jdk11-openjdk](https://github.com/docker-library/tomcat/blob/300ac03f4696c761a81fa10afbb893f3368061de/8.5/jdk11/openjdk-buster/Dockerfile) （其底层的Linux是一个最小化的操作系统、小到仅能支撑要安装的应用）

(2) 在容器中执行命令

~~~bash
# -it: 交互方式执行命令
# docer exec -it ${container_id} ${command}
# 例如：
[root@localhost ~]# docker run -d -p 8000:8080 tomcat
2c1a9fdfa9e61e3252f060903275b6594df45ff94ab3029320e70deed3662e2b
[root@localhost ~]# docker exec -it 2c1a9fdfa9e /bin/bash
root@2c1a9fdfa9e6:/usr/local/tomcat# pwd
/usr/local/tomcat
root@2c1a9fdfa9e6:/usr/local/tomcat# cat /proc/version
Linux version 3.10.0-1127.19.1.el7.x86_64 (mockbuild@kbuilder.bsys.centos.org) (gcc version 4.8.5 20150623 (Red Hat 4.8.5-39) (GCC) ) #1 SMP Tue Aug 25 17:23:54 UTC 2020
root@2c1a9fdfa9e6:/usr/local/tomcat# java -version
openjdk version "11.0.9" 2020-10-20
OpenJDK Runtime Environment 18.9 (build 11.0.9+11)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.9+11, mixed mode)
root@2c1a9fdfa9e6:/usr/local/tomcat# exit
exit
[root@localhost ~]#
~~~

(3) 镜像和容器在宿主机的存储位置

~~~bash
[root@localhost ~]# cd /var/lib/docker/
[root@localhost docker]# ls
builder   containers  network   plugins   swarm  trust
buildkit  image       overlay2  runtimes  tmp    volumes
[root@localhost docker]# ls containers/
2c1a9fdfa9e61e3252f060903275b6594df45ff94ab3029320e70deed3662e2b
555e2beaf100ea85b713700609020629a93ddac776357ab38e09a945dc76a344
896cad4ff9cc9e3d75afe685c058f631cf953b013e1b47cb2c1e2a3cc48a7db4
c56fc6203cdf90a83f304f18d590414d579eca372d881a3dd2f076a04c903d27
[root@localhost docker]# ls image/
overlay2
~~~

# 2. 用Dockerfile构建镜像

(1) 构建镜像的命令

> `Docker`通过读取Dockerfile中的指令按步骤自动生成镜像，命令如下
> 
~~~bash
# 命令格式
# docker build -t ${org}/${image_name}<:${tag}> ${docker_file_dir}
~~~
>

(2) Dockerfile中的基本指令：

> 例子：
> 
~~~bash
FROM tomcat:latest
MAINTAINER mashibing.com
WORKDIR /usr/local/tomcat/webapps
ADD docker-web ./docker-web
~~~
> 
> 其中：
> 
> * `From:`：基准镜像
> * `MAINTAINER`：该镜像的维护机构/维护人
> * `WORKDIR`：切换工作目录
> * `ADD`：将指定文件或目录复制到镜像内部的指定目录中

(3) 例子：为tomcat中加一个index.html

> 获取基准镜像、并启动基准镜像容器
> 
~~~
[root@localhost ~]# service docker status #如果不是active(running)、运行service docker start启动docker服务
[root@localhost ~]# docker pull tomcat: 
[root@localhost ~]# docker images
REPOSITORY          TAG                    IMAGE ID            CREATED             SIZE
docker.io/tomcat    8.5.59-jdk11-openjdk   5ec5c90ea448        5 days ago          647 MB
~~~
>  
> 并用交互式(`-it`)的方式执行容器内的`/bin/bash`，可以获得基准镜像的信息用于帮助编写`Dockerfile`。其中`docker exec`之后所在的目录就是该容器的工作目录（`WORK_DIR`）
>
~~~bash
[root@localhost ~]# docker exec -it 4a9d8d88fb /bin/bash
root@4a9d8d88fb21:/usr/local/tomcat# pwd
/usr/local/tomcat
root@4a9d8d88fb21:/usr/local/tomcat# ls
BUILDING.txt	 NOTICE		RUNNING.txt  lib	     temp	   work
CONTRIBUTING.md  README.md	bin	     logs	     webapps
LICENSE		 RELEASE-NOTES	conf	     native-jni-lib  webapps.dist
~~~
>
> 在笔记本上编写[`./docker-web/index.html`](lab1/docker-web/index.html)文件和[`lab1/Dockerfile`](lab1/Dockerfile) （具体代码点击链接可查看）
> 
~~~bash
$ mkdir -p /Users/fangkun/Dev/git/java_proj_ref/101_docker/03_docker_file/lab1/
$ cd /Users/fangkun/Dev/git/java_proj_ref/101_docker/03_docker_file/lab1/
$ mkdir -p docker-web
$ vi docker-web/index.html	# 编写html
$ cat docker-web/index.html  
$ vi Dockerfile	# 编写Dockerfile
$ find . -type f
./Dockerfile
./docker-web/index.html
~~~
>
> 将上面的`lab1`目录远程拷贝到宿主机，执行`docker build -t ${tag} ${docker_file_dir}`来构建，注意`-t`指定的镜像tag有格式要求
> 
~~~bash
[root@localhost lab1]# pwd
/home/work/lab1
[root@localhost lab1]# find . -type f
./docker-web/index.html
./Dockerfile
[root@localhost lab1]# docker build -t fangkun119/webappdemo:0.1 ./ #构建镜像
Sending build context to Docker daemon 4.096 kB
Step 1/4 : FROM tomcat:8.5.59-jdk11-openjdk
 ---> 5ec5c90ea448
Step 2/4 : MAINTAINER fangkun119
 ---> Running in 4133541805f6
 ---> c7b05d432182
Removing intermediate container 4133541805f6
Step 3/4 : WORKDIR /usr/local/tomcat/webapps/
 ---> b15573b565db
Removing intermediate container 4656d9278c3e
Step 4/4 : ADD docker-web ./docker-web
 ---> 1cb2210521e9
Removing intermediate container 9efe1d133b8d
Successfully built 1cb2210521e9
[root@localhost lab1]# docker images #查看镜像
REPOSITORY              TAG                    IMAGE ID            CREATED             SIZE
fangkun119/webappdemo   0.1                    1cb2210521e9        2 minutes ago       647 MB
docker.io/tomcat        8.5.59-jdk11-openjdk   5ec5c90ea448        5 days ago          647 MB
[root@localhost lab1]# docker run -d -p 8001:8080 fangkun119/webappdemo:0.1 #创建容器
73e4eb0578ac004414c2b1b5a8a6b625ba47c10c3f58140527028fe79c676741
[root@localhost lab1]# docker ps #查看运行中的容器及端口映射
CONTAINER ID        IMAGE                         COMMAND             CREATED             STATUS              PORTS                    NAMES
73e4eb0578ac        fangkun119/webappdemo:0.1     "catalina.sh run"   45 seconds ago      Up 44 seconds       0.0.0.0:8001->8080/tcp   gallant_beaver
4a9d8d88fb21        tomcat:8.5.59-jdk11-openjdk   "catalina.sh run"   37 minutes ago      Up 37 minutes       0.0.0.0:8000->8080/tcp   festive_leakey
[root@localhost lab1]# curl http://localhost:8001/docker-web/index.html #在宿主机本地访问容器中的tomcat，容器的tomcat端口8080被映射为宿主机的8001端口
<h1>Index Page of Dockerfile Demo</h1>
[root@localhost lab1]# docker exec -it 73e4eb0578ac /bin/bash #可以看到被添加到容器内的文件
root@73e4eb0578ac:/usr/local/tomcat/webapps# ls
docker-web
root@73e4eb0578ac:/usr/local/tomcat/webapps# find . -type f
./docker-web/index.html
~~~
> 
> 使用宿主机的地址，在宿主机外部的浏览器，也可以访问容器内的tomcat。本例是 `http://192.168.1.170:8001/docker-web/index.html`

#3. 镜像分层

镜像只读；同一镜像的不同容器在其各自内部可读可写、互不影响

理解镜像构建过程：

> 上面 [`lab1/Dockerfile`](lab1/Dockerfile) 中的4条命令，每一条在构建镜像的日志中，都有一个对应的step，每个step都会生成一个临时容器，对应于当时容器的快照

~~~txt
[root@localhost lab1]# docker build -t fangkun119/webappdemo:0.1 ./ #构建镜像
Sending build context to Docker daemon 4.096 kB
Step 1/4 : FROM tomcat:8.5.59-jdk11-openjdk
 ---> 5ec5c90ea448 #执行step1的命令后临时容器id
Step 2/4 : MAINTAINER fangkun119
 ---> Running in 4133541805f6
 ---> c7b05d432182 #执行step2的命令后临时容器id
Removing intermediate container 4133541805f6
Step 3/4 : WORKDIR /usr/local/tomcat/webapps/
 ---> b15573b565db #执行step3的命令后临时容器id
Removing intermediate container 4656d9278c3e
Step 4/4 : ADD docker-web ./docker-web
 ---> 1cb2210521e9 #执行step4的命令后临时容器id
Removing intermediate container 9efe1d133b8d
Successfully built 1cb2210521e9
~~~

> 这些临时容器，是以分层的方式存储和复用的。举例来说，如果另一个镜像构建、前2个step与上面这个镜像相同，那么前两步对应的临时镜像就不需要重复生成、可以直接复用已有的

##4. Dockerfile基础指令

### From - 基于基准镜像

~~~txt
FROM centos 	# 制作基于centos:latest的基准镜像
FROM scratch 	# 不依赖任何基准镜像
FROM tomcat:9.0.22-jdk8-openjdk #制作基于tomcat:9.0.22-jdk8-openjdk的基准镜像
~~~

> 尽量使用官方提供的Base Image

### LABEL & MAINTAINER - 说明信息

~~~txt
MAINTAINER fangkun119
LABEL version = "1.0"
LABEL description = "demo for tomcat with an sample website added"
~~~

### WORKDIR - 设置工作目录

~~~
WORKDIR /usr/local
WORKDIR /usr/local/newdir #目录不存在时会自动创建
~~~

> 虽然也可以使用相对路径，但是使用中应当尽量使用绝对路径

### ADD & COPY - 构建镜像时从本地复制文件到镜像中

~~~txt
ADD hello / 			# 复制到根路径
ADD test.tar.gz / 	# 添加到根目录并解压
~~~

> `ADD`除了复制、还可以将远程url的资源下载到镜像内的指定目录中（类似wget和curl），但在实践中不鼓励这么做

### ENV - 设置环境常量

~~~txt
ENV JAVA_HOME /usr/local/openjdk8
RUN ${JAVA_HOME}/bin/java -jar test.jar 
~~~

> 不仅可以设置容器的环境常量，还可以在Dockerfile的命令中引用该环境常量。应当尽量使用环境常量、提高可维护性

##5. Dockerfile执行指令

`Dockerfile`有三个执行指令：

* `RUN`：在构建镜像（`docker build`）时执行、对镜像内部的文件或资源进行调整
* `ENTRYPOINT`：在创建容器（`docker run`）时执行的命令
* `CMD`：容器创建（`docker run`）后执行默认的命令或参数

#### RUN-创建时运行

有`Shell命令格式`和`Exec命令格式`两种写法：

> * `Shell命令格式`：会在新创建的子进程中执行脚本，执行完退出子进程回到当前进程
> * `Exec命令格式`：用Exec创建的进程会替代当前进程（同时保持PID不变）来执行脚本

~~~txt
RUN yum install -y vim 			# Shell命令格式
RUN ["yum","install", "-y", "vim"]	# Exec命令格式
~~~

> 一般情况下推荐使用`Exec命令格式`，这样不用考虑父子进程上下文切换

#### ENTRYPOINT-容器创建(启动)时执行命令

> Dockerfile中只有最后一个`ENTRYPOINT`命令会被执行。同样有`Shell命令格式`和`Exec命令格式`两种，推荐使用`Exec命令格式`

~~~txt
ENTRYPOINT ["ps"]
~~~

#### CMD-默认命令

> * Dockerfile中只有最后一个`CMD`命令会被执行
> * 同样有`Shell命令格式`和`Exec命令格式`两种，推荐使用`Exec命令格式`

~~~txt
CMD ["ps", "-ef"]
~~~

> * 如果容器启动时制定了附加命令（如之前的`docker run -lt ${image_id} /bin/bash`)，则`CMD`会被附加命令替代，下面是例子
> 
> Dockerfile
> 
~~~txt
FROM centos
RUN ["echo", "image building!!!"]
CMD ["echo", "container starting ..."]
~~~
> 
> 构建镜像，`RUN`命令会在构建时执行（日志输出"image building!!!"）
> 
~~~bash
[root@localhost lab2]# docker build -t fangkun119/excmddemo:0.1 ./
Sending build context to Docker daemon 2.048 kB
Step 1/3 : FROM centos
---> 0d120b6ccaa8
Step 2/3 : RUN echo image building!!!
---> Running in 18613c083ce5
image building!!!
---> b854e4bc7f0e
Step 3/3 : CMD echo CMD: container starting ...
---> Using cache
---> 9390b0070a1a
Successfully built 9390b0070a1a
~~~
> 
> 创建（启动）容器，`CMD`命令执行
> 
~~~bash
[root@localhost lab2]# docker run fangkun119/excmddemo:0.1
container starting ...
~~~
> 
> 创建（启动）容器，但是指定了附加命令 `ls | head -n5`，此时只执行附加命令、而不会执行`CMD`命令
> 
~~~bash
[root@localhost lab2]# docker run fangkun119/excmddemo:0.1 ls|head -n5
bin
dev
etc
home
lib
~~~
> 
> 假如把`CMD`改成`ENTRYPOINT`，则`ENTRYPOINT`不会被附加指令替代、它一定会执行

### ENTRYPOINT与CMD命令组合使用

~~~
FROM centos
RUN ["echo", "image building!!!"]
ENTRYPOINT ["ps"]
CMD ["-ef"]
~~~

> 两个命令会被组合在一起执行，`CMD`部分（`-ef`）用作`ENTRYPOINT`部分（`ps`）的默认参数 
> 
> * 如果有附加指令，执行`ps -el`
> 
~~~bash
[root@localhost lab2]# docker run fangkun119/excmddemo:0.1
UID        PID  PPID  C STIME TTY          TIME CMD
root         1     0  0 12:57 ?        00:00:00 ps -ef
~~~
> 
> * 如果有附加指令，用附加指令携带的参数替代默认参数`el`
> 
~~~bash
[root@localhost lab2]# docker run fangkun119/excmddemo:0.1 -aux
USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
root         1  3.0  0.1  47496  1800 ?        Rs   12:57   0:00 ps -aux
~~~
>
> 完整过程
> 
~~~bash
[root@localhost lab2]# docker build -t fangkun119/excmddemo:0.1 ./
Sending build context to Docker daemon 2.048 kB
Step 1/4 : FROM centos
 ---> 0d120b6ccaa8
Step 2/4 : RUN echo image building!!!
 ---> Using cache
 ---> b854e4bc7f0e
Step 3/4 : ENTRYPOINT ps
 ---> Running in 39f97f0059b8
 ---> 5c59ff7ccffe
Removing intermediate container 39f97f0059b8
Step 4/4 : CMD -ef
 ---> Running in 0f464b6ca0cb
 ---> e70d4f087f16
Removing intermediate container 0f464b6ca0cb
Successfully built e70d4f087f16
[root@localhost lab2]# docker run fangkun119/excmddemo:0.1
UID        PID  PPID  C STIME TTY          TIME CMD
root         1     0  0 12:57 ?        00:00:00 ps -ef
[root@localhost lab2]# docker run fangkun119/excmddemo:0.1 -aux
USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
root         1  3.0  0.1  47496  1800 ?        Rs   12:57   0:00 ps -aux
~~~

## 6. 用`Dockerfile`构建`Redis`镜像

> Dockerfile:
> 
~~~txt
FROM centos
RUM ["yum", "install", "-y", "gcc", "gcc-c++", "net-tools", "make"]
WORKDIR /usr/local
ADD redis-4.0.14.tar.gz . 	# redis源代码压缩包，会被ADD命令自动解压；.是当前目录、即WORKDIR
WORKDIR /usr/local/redis-4.0.14/src
RUM make && make install 
WORKDIR /usr/local/redis-4.0.14/
ADD redis-7000.conf .    	# 复制写好的redis配置文件、对外端口是7000
EXPOSE 7000			# 让容器对外暴露7000端口
CMD ["redis-server", "redis-7000.conf"]
~~~
> 
> 构建
> 
~~~bash
docker build -t fangkun119/dockerdemo-redis .
docker images
docker run -p 7000:7000 fangkun119/dockerdemo-redis
~~~
> 
> 测试
> 
~~~bash
netstat 									# 检查端口是否被监听
docker ps 								# 检查容器是否启动
docker exec -it ${container_id} /bin/bash 	# 登进容器内检查redis运行状况
~~~
> 
> 上面只是一个演示，实际工作中可以直接从docker hub上pull官方redis镜像
> 
~~~bash
docker pull redis
docker run redis
~~~







