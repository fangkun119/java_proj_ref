

# 3 Dockerfile

## 3.1 了解容器内部结构

### 3.1.1 通过`Dockerfile`查看容器内部结构

在`Docker Hub`的镜像页面（例如：[`https://hub.docker.com/_/tomcat`](https://hub.docker.com/_/tomcat)）可以找到镜像的Dockerfile，查看Dockerfile可以看到这个镜像的构建步骤，进而了解其内部结构。Dockfile查找方式如下

> * 查看`Quick Reference` 
>
> * 点击某个tag的链接，查看`Dockerfile`（基准镜像、环境变量、相关命令等），例如 [8.5.59-jdk11-openjdk](https://github.com/docker-library/tomcat/blob/300ac03f4696c761a81fa10afbb893f3368061de/8.5/jdk11/openjdk-buster/Dockerfile) （其底层的Linux是一个最小化的操作系统、小到仅能支撑要安装的应用）

### 3.1.2 在容器中执行命令：`docker exec -it ${cmd}`

> ~~~bash
> # -it: 交互方式执行命令
> # docer exec -it ${container_id} ${command}
> # 例如：
> [root@localhost ~]# docker run -d -p 8000:8080 tomcat
> 2c1a9fdfa9e61e3252f060903275b6594df45ff94ab3029320e70deed3662e2b
> [root@localhost ~]# docker exec -it 2c1a9fdfa9e /bin/bash
> root@2c1a9fdfa9e6:/usr/local/tomcat# pwd
> /usr/local/tomcat
> root@2c1a9fdfa9e6:/usr/local/tomcat# cat /proc/version
> Linux version 3.10.0-1127.19.1.el7.x86_64 (mockbuild@kbuilder.bsys.centos.org) (gcc version 4.8.5 20150623 (Red Hat 4.8.5-39) (GCC) ) #1 SMP Tue Aug 25 17:23:54 UTC 2020
> root@2c1a9fdfa9e6:/usr/local/tomcat# java -version
> openjdk version "11.0.9" 2020-10-20
> OpenJDK Runtime Environment 18.9 (build 11.0.9+11)
> OpenJDK 64-Bit Server VM 18.9 (build 11.0.9+11, mixed mode)
> root@2c1a9fdfa9e6:/usr/local/tomcat# exit
> exit
> [root@localhost ~]#
> ~~~

### 3.1.3 镜像和容器在宿主机的存储位置

> ~~~bash
> [root@localhost ~]# cd /var/lib/docker/
> [root@localhost docker]# ls
> builder   containers  network   plugins   swarm  trust
> buildkit  image       overlay2  runtimes  tmp    volumes
> [root@localhost docker]# ls containers/
> 2c1a9fdfa9e61e3252f060903275b6594df45ff94ab3029320e70deed3662e2b
> 555e2beaf100ea85b713700609020629a93ddac776357ab38e09a945dc76a344
> 896cad4ff9cc9e3d75afe685c058f631cf953b013e1b47cb2c1e2a3cc48a7db4
> c56fc6203cdf90a83f304f18d590414d579eca372d881a3dd2f076a04c903d27
> [root@localhost docker]# ls image/
> overlay2
> ~~~

## 3.2 用Dockerfile构建镜像

### 3.2.1 构建镜像

#### (1) 构建命令`docker build`

> `Docker`通过读取Dockerfile中的指令按步骤自动生成镜像，命令如下
>
> ~~~bash
> # 命令格式
> # docker build -t ${org}/${image_name}<:${tag}> ${docker_file_dir}
> ~~~
#### (2) 例子1：构建tomcat镜像

> 一个简单的例子如下
>
> ~~~dockerfile
> FROM tomcat:latest
> MAINTAINER xxx.xxx
> WORKDIR /usr/local/tomcat/webapps
> ADD docker-web ./docker-web
> ~~~
>
> 其中：
>
> * `From:`：基准镜像
> * `MAINTAINER`：该镜像的维护机构/维护人
> * `WORKDIR`：切换工作目录
> * `ADD`：将指定文件或目录复制到镜像内部的指定目录中

#### (3) 例子2：为tomcat增加index.html

Demo目录：[../03_dockerfile/lab1/](/03_dockerfile/lab1/)

步骤：

> 步骤1：获取基准镜像、并启动基准镜像容器
>
> ~~~bash
> [root@localhost ~]# service docker status #如果不是active(running)、运行service docker start启动docker服务
> [root@localhost ~]# docker pull tomcat: 
> [root@localhost ~]# docker images
> REPOSITORY          TAG                    IMAGE ID            CREATED             SIZE
> docker.io/tomcat    8.5.59-jdk11-openjdk   5ec5c90ea448        5 days ago          647 MB
> ~~~
>
> 步骤2：获得编写Dockerfile所需要的信息
>
> * 用交互式(`-it`)的方式执行容器内的`/bin/bash`，可以获得基准镜像的信息用于帮助编写`Dockerfile`。
>
> * 其中`docker exec`之后所在的目录就是该容器的工作目录（`WORK_DIR`
>
> ~~~bash
> [root@localhost ~]# docker exec -it 4a9d8d88fb /bin/bash
> root@4a9d8d88fb21:/usr/local/tomcat# pwd
> /usr/local/tomcat
> root@4a9d8d88fb21:/usr/local/tomcat# ls
> BUILDING.txt	 NOTICE		RUNNING.txt  lib	     temp	   work
> CONTRIBUTING.md  README.md	bin	     logs	     webapps
> LICENSE		 RELEASE-NOTES	conf	     native-jni-lib  webapps.dist
> ~~~
>
> 步骤3：编写[`../03_dockerfile/docker-web/index.html`](../03_dockerfile/lab1/docker-web/index.html)文件和[`../03_dockerfile/lab1/Dockerfile`](../03_dockerfile/lab1/Dockerfile) 
>
> ~~~bash
> $ mkdir -p /Users/fangkun/Dev/git/java_proj_ref/101_docker/03_docker_file/lab1/
> $ cd /Users/fangkun/Dev/git/java_proj_ref/101_docker/03_docker_file/lab1/
> $ mkdir -p docker-web
> $ vi docker-web/index.html	# 编写html
> $ cat docker-web/index.html  
> $ vi Dockerfile	# 编写Dockerfile
> $ find . -type f
> ./Dockerfile
> ./docker-web/index.html
> ~~~
>
> ~~~dockerfile
> # 基准镜像是tomcat:8.5.59-jdk11-openjdk
> FROM tomcat:8.5.59-jdk11-openjdk
> 
> # 该镜像维护者
> MAINTAINER fangkun119
> 
> # 工作目录，希望把web应用部署在基准镜像工作目录(/usr/local/tomcat/)下面的webapps内
> # 用交互式(docker exec -lt ${container_id} /bin/bash)访问容器时、会自动跳转到该目录
> # 创建容器时、如果不存在也会自动创建该目录
> WORKDIR /usr/local/tomcat/webapps/
> 
> # 第一个docker-web是构建镜像时，与Dockerfile同级的名为docker-web的目录
> # 第二个docker-web是${WORK_DIR}下名为docker-web的目录，如果不存在会创建
> # ADD表示将第一个docker-web目录下所有内容都复制到第二个docker-web目录下
> ADD docker-web ./docker-web
> ~~~
>
> 步骤4：将上面的`lab1`目录远程拷贝到宿主机，执行`docker build -t ${tag} ${docker_file_dir}`来构建，注意`-t`指定的镜像tag有格式要求
>
> ~~~bash
> [root@localhost lab1]# pwd
> /home/work/lab1
> [root@localhost lab1]# find . -type f
> ./docker-web/index.html
> ./Dockerfile
> [root@localhost lab1]# docker build -t fangkun119/webappdemo:0.1 ./ #构建镜像
> Sending build context to Docker daemon 4.096 kB
> Step 1/4 : FROM tomcat:8.5.59-jdk11-openjdk
>  ---> 5ec5c90ea448
> Step 2/4 : MAINTAINER fangkun119
>  ---> Running in 4133541805f6
>  ---> c7b05d432182
> Removing intermediate container 4133541805f6
> Step 3/4 : WORKDIR /usr/local/tomcat/webapps/
>  ---> b15573b565db
> Removing intermediate container 4656d9278c3e
> Step 4/4 : ADD docker-web ./docker-web
>  ---> 1cb2210521e9
> Removing intermediate container 9efe1d133b8d
> Successfully built 1cb2210521e9
> [root@localhost lab1]# docker images #查看镜像
> REPOSITORY              TAG                    IMAGE ID            CREATED             SIZE
> fangkun119/webappdemo   0.1                    1cb2210521e9        2 minutes ago       647 MB
> docker.io/tomcat        8.5.59-jdk11-openjdk   5ec5c90ea448        5 days ago          647 MB
> [root@localhost lab1]# docker run -d -p 8001:8080 fangkun119/webappdemo:0.1 #创建容器
> 73e4eb0578ac004414c2b1b5a8a6b625ba47c10c3f58140527028fe79c676741
> [root@localhost lab1]# docker ps #查看运行中的容器及端口映射
> CONTAINER ID        IMAGE                         COMMAND             CREATED             STATUS              PORTS                    NAMES
> 73e4eb0578ac        fangkun119/webappdemo:0.1     "catalina.sh run"   45 seconds ago      Up 44 seconds       0.0.0.0:8001->8080/tcp   gallant_beaver
> 4a9d8d88fb21        tomcat:8.5.59-jdk11-openjdk   "catalina.sh run"   37 minutes ago      Up 37 minutes       0.0.0.0:8000->8080/tcp   festive_leakey
> [root@localhost lab1]# curl http://localhost:8001/docker-web/index.html #在宿主机本地访问容器中的tomcat，容器的tomcat端口8080被映射为宿主机的8001端口
> <h1>Index Page of Dockerfile Demo</h1>
> [root@localhost lab1]# docker exec -it 73e4eb0578ac /bin/bash #可以看到被添加到容器内的文件
> root@73e4eb0578ac:/usr/local/tomcat/webapps# ls
> docker-web
> root@73e4eb0578ac:/usr/local/tomcat/webapps# find . -type f
> ./docker-web/index.html
> ~~~
>
> 步骤5：使用宿主机的地址，在宿主机外部的浏览器，也可以访问容器内的tomcat。本例是 `http://192.168.1.170:8001/docker-web/index.html`
## 3.3 镜像分层

镜像只读；同一镜像的不同容器在其各自内部可读可写、互不影响

理解镜像构建过程：

> 上面 [`lab1/Dockerfile`](lab1/Dockerfile) 中的4条命令，每一条在构建镜像的日志中，都有一个对应的step，每个step都会生成一个临时容器，对应于当时容器的快照
>
> ~~~bash
> [root@localhost lab1]# docker build -t fangkun119/webappdemo:0.1 ./ #构建镜像
> Sending build context to Docker daemon 4.096 kB
> Step 1/4 : FROM tomcat:8.5.59-jdk11-openjdk
>  ---> 5ec5c90ea448 #执行step1的命令后临时容器id
> Step 2/4 : MAINTAINER fangkun119
>  ---> Running in 4133541805f6
>  ---> c7b05d432182 #执行step2的命令后临时容器id
> Removing intermediate container 4133541805f6
> Step 3/4 : WORKDIR /usr/local/tomcat/webapps/
>  ---> b15573b565db #执行step3的命令后临时容器id
> Removing intermediate container 4656d9278c3e
> Step 4/4 : ADD docker-web ./docker-web
>  ---> 1cb2210521e9 #执行step4的命令后临时容器id
> Removing intermediate container 9efe1d133b8d
> Successfully built 1cb2210521e9
> ~~~
>
> 这些临时容器，是以分层的方式存储和复用的。举例来说，如果另一个镜像构建、前2个step与上面这个镜像相同，那么前两步对应的临时镜像就不需要重复生成、可以直接复用已有的

## 3.4 编写Dockerfile

### 3.4.1 Dockerfile指令

####  (1) 基础指令

#####  `FROM` ： 指定基准镜像

> ~~~dockerfile
> FROM centos 					# 制作基于centos:latest的基准镜像
> FROM scratch 					# 不依赖任何基准镜像
> FROM tomcat:9.0.22-jdk8-openjdk # 制作基于tomcat:9.0.22-jdk8-openjdk的基准镜像
> ~~~
>
> 尽量使用官方提供的Base Image

##### `LABEL` & `MAINTAINER`： 描述信息

> ~~~dockerfile
> MAINTAINER fangkun119
> LABEL version = "1.0"
> LABEL description = "demo for tomcat with an sample website added"
> ~~~

##### `WORKDIR` ：设置工作目录

> ~~~dockerfile
> WORKDIR /usr/local
> WORKDIR /usr/local/newdir #目录不存在时会自动创建
> ~~~
>
> 虽然也可以使用相对路径，但是使用中应当尽量使用绝对路径

##### `ADD` & `COPY`：构建镜像时从本地复制文件到镜像中

> ~~~dockerfile
> ADD hello / 		# 复制到根路径
> ADD test.tar.gz / 	# 添加到根目录并解压
> ~~~
>
> `ADD`除了复制、还可以将远程url的资源下载到镜像内的指定目录中（类似wget和curl），但在实践中不鼓励这么做

#####  `ENV`： 设置环境变量

> ~~~dockerfile
> ENV JAVA_HOME /usr/local/openjdk8
> RUN ${JAVA_HOME}/bin/java -jar test.jar 
> ~~~
>
> 不仅可以设置容器的环境常量，还可以在Dockerfile的命令中引用该环境常量。应当尽量使用环境常量、提高可维护性

##### `EXPOSE` ：让容器暴露端口给外界

> ~~~dockerfile
> EXPOSE 7000			# 让容器对外暴露7000端口
> ~~~

##### `ARG`： 定义变量

> ~~~dockerfile
> ARG JAR_FILE=target/*.jar
> COPY ${JAR_FILE} app.jar
> ~~~

##### `VOLUME`：创建挂载点

> 用镜像创建的容器时会生成挂载点，挂载点指向宿主机内自动生成的目录，当用镜像创建多个挂载点时，挂载点在不同镜像间彼此隔离互不影响。例如：
>
> dockerfile
>
> ~~~dockerfile
> FROM ubuntu
> MAINTAINER hello1
> VOLUME ["/data1","/data2"]
> ~~~
>
> 使用docker inspect可以查看通过该dockerfile创建镜像生成的`容器`，可以看到挂载信息
>
> ~~~json
> "Mounts": [
> 	{
> 		"Name": "d411f6b8f17f4418629d4e5a1ab69679dee369b39e13bb68bed77aa4a0d12d21",
> 		"Source": "/var/lib/docker/volumes/d411f6b8f17f4418629d4e5a1ab69679dee369b39e13bb68bed77aa4a0d12d21/_data",
> 		"Destination": "/data1",
> 		"Driver": "local",
> 		"Mode": "",
> 		"RW": true
> 	},
>         {
>             "Name": "6d3badcf47c4ac5955deda6f6ae56f4aaf1037a871275f46220c14ebd762fc36",
>             "Source": "/var/lib/docker/volumes/6d3badcf47c4ac5955deda6f6ae56f4aaf1037a871275f46220c14ebd762fc36/_data",
>             "Destination": "/data2",
>             "Driver": "local",
>             "Mode": "",
>             "RW": true
>         }
> ],
> ~~~

#### (2) 执行指令

> `Dockerfile`有三个执行指令：
>
> * `RUN`：在构建镜像（`docker build`）时执行、对镜像内部的文件或资源进行调整
> * `ENTRYPOINT`：在创建容器（`docker run`）时执行的命令
> * `CMD`：容器创建（`docker run`）后执行默认的命令或参数

##### `RUN` - 镜像构建时执行的命令

有`Shell命令格式`和`Exec命令格式`两种写法：

> * `Shell命令格式`：会在新创建的子进程中执行脚本，执行完退出子进程回到当前进程
> * `Exec命令格式`：用Exec创建的进程会替代当前进程（同时保持PID不变）来执行脚本
>
> ~~~dockerfile
> RUN yum install -y vim				# Shell命令格式
> RUN ["yum","install", "-y", "vim"]	# Exec命令格式（推荐使用，不会引发父子进程上下文切换）
> ~~~
>
> 一般情况下推荐使用`Exec命令格式`，这样不用考虑父子进程上下文切换

##### `ENTRYPOINT` - 容器创建（启动）时执行命令

> * Dockerfile中只有最后一个`ENTRYPOINT`命令会被执行。
>
> * 同样有`Shell命令格式`和`Exec命令格式`两种，推荐使用`Exec命令格式`
>
> * 与`CMD`命令的区别是：**`ENTRYPOINT`一定会被执行；而`CMD`在出现附加命令时会被替换**
>
> ~~~dockerfile
> ENTRYPOINT ["ps"] # 只有最后一条会被执行
> ~~~

##### `CMD` - 默认命令

> * Dockerfile中只有最后一个`CMD`命令会被执行
> * 同样有`Shell命令格式`和`Exec命令格式`两种，推荐使用`Exec命令格式`
> * 如果容器启动时制定了附加命令（如之前的`docker exec -it ${image_id} /bin/bash`)，则`CMD`会被附加命令替代
>
> ~~~dockerfile
> CMD ["ps", "-ef"]
> ~~~

例子如下：

> Dockerfile
>
> ~~~dockerfile
> FROM centos
> RUN ["echo", "image building!!!"]
> CMD ["echo", "container starting ..."]
> ~~~
>
> 构建镜像，`RUN`命令会在构建时执行（日志输出"image building!!!"）
>
> ~~~bash
> [root@localhost lab2]# docker build -t fangkun119/excmddemo:0.1 ./
> Sending build context to Docker daemon 2.048 kB
> Step 1/3 : FROM centos
> ---> 0d120b6ccaa8
> Step 2/3 : RUN echo image building!!!
> ---> Running in 18613c083ce5
> image building!!!
> ---> b854e4bc7f0e
> Step 3/3 : CMD echo CMD: container starting ...
> ---> Using cache
> ---> 9390b0070a1a
> Successfully built 9390b0070a1a
> ~~~
>
> 创建（启动）容器，`CMD`命令执行
>
> ~~~bash
> [root@localhost lab2]# docker run fangkun119/excmddemo:0.1
> container starting ...
> ~~~
>
> 创建（启动）容器，但是指定了附加命令 `ls | head -n5`，此时只执行附加命令、而不会执行`CMD`命令
>
> ~~~bash
> [root@localhost lab2]# docker run fangkun119/excmddemo:0.1 ls|head -n5
> bin
> dev
> etc
> home
> lib
> ~~~

但是如果把`CMD`改成`ENTRYPOINT`，则**`ENTRYPOINT`不会被附加指令替代，一定会执行**

#### (3) `ENTRYPOINT`（当做命令）与`CMD`（当做默认参数）命令组合使用

两个命令会被组合在一起执行，`CMD`部分用作`ENTRYPOINT`部分的默认参数，例如下面的Dockerfile

> ~~~dockerfile
> FROM centos
> RUN ["echo", "image building!!!"]
> ENTRYPOINT ["ps"]
> CMD ["-ef"]
> ~~~
>
> 没有附加指令时，执行`ps -el`
>
> ~~~bash
> [root@localhost lab2]# docker run fangkun119/excmddemo:0.1
> UID        PID  PPID  C STIME TTY          TIME CMD
> root         1     0  0 12:57 ?        00:00:00 ps -ef
> ~~~
>
> 有附加指令时，用`附加指令部分`替代默认参数`el`
>
> ~~~bash
> [root@localhost lab2]# docker run fangkun119/excmddemo:0.1 -aux
> USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
> root         1  3.0  0.1  47496  1800 ?        Rs   12:57   0:00 ps -aux
> ~~~

完整过程如下

> ~~~bash
> [root@localhost lab2]# docker build -t fangkun119/excmddemo:0.1 ./
> Sending build context to Docker daemon 2.048 kB
> Step 1/4 : FROM centos
>  ---> 0d120b6ccaa8
> Step 2/4 : RUN echo image building!!!
>  ---> Using cache
>  ---> b854e4bc7f0e
> Step 3/4 : ENTRYPOINT ps
>  ---> Running in 39f97f0059b8
>  ---> 5c59ff7ccffe
> Removing intermediate container 39f97f0059b8
> Step 4/4 : CMD -ef
>  ---> Running in 0f464b6ca0cb
>  ---> e70d4f087f16
> Removing intermediate container 0f464b6ca0cb
> Successfully built e70d4f087f16
> [root@localhost lab2]# docker run fangkun119/excmddemo:0.1
> UID        PID  PPID  C STIME TTY          TIME CMD
> root         1     0  0 12:57 ?        00:00:00 ps -ef
> [root@localhost lab2]# docker run fangkun119/excmddemo:0.1 -aux
> USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
> root         1  3.0  0.1  47496  1800 ?        Rs   12:57   0:00 ps -aux
> ~~~

#### (4) `Dockerfile`指令参考

> [https://docs.docker.com/engine/reference/builder/#from](https://docs.docker.com/engine/reference/builder/#from)

## 3.5 Demo：使用`Dockerfile`构建`Redis`镜像

Demo位置：[../03_dockerfile/lab2_redis/](../03_dockerfile/lab2_redis)

文件下载（redis源代码）：https://download.redis.io/releases/redis-4.0.14.tar.gz

步骤

(1) 文件准备

> ~~~bash
> [root@localhost ~]# mkdir -p /usr/image/docker-redis
> [root@localhost ~]# cd /usr/image/docker-redis
> [root@localhost docker-redis]# cp /home/share/redis-4.0.14.tar.gz .
> [root@localhost docker-redis]# ls
> redis-4.0.14.tar.gz
> [root@localhost docker-redis]# vi Dockerfile      # 编写Dockerfile
> [root@localhost docker-redis]# vi redis-7000.conf # 编写redis配置文件
> [root@localhost docker-redis]# pwd
> /usr/image/docker-redis
> [root@localhost docker-redis]# ls
> Dockerfile  redis-4.0.14.tar.gz  redis-7000.conf
> ~~~
>
> Dockerfile内容
>
> ~~~dockerfile
> # 基于cenntos镜像
> FROM centos
> # 安装依赖
> RUN ["yum", "install", "-y", "gcc", "gcc-c++", "net-tools", "make"]
> # 切换工作目录
> WORKDIR /usr/local
> # 拷贝redis源代码压缩包到镜像内，会被ADD命令自动解压
> ADD redis-4.0.14.tar.gz .
> # 切换工作目录
> WORKDIR /usr/local/redis-4.0.14/src
> # 编译和安装redis
> RUN make && make install
> # 切换工作目录
> WORKDIR /usr/local/redis-4.0.14/
> # 拷贝redis配置文件到镜像内，redis对外端口是7000
> ADD redis-7000.conf .
> # 让容器对外暴露7000端口
> EXPOSE 7000
> # 设置容器启动时运行的默认命令
> CMD ["redis-server", "redis-7000.conf"]
> ~~~
>
> redis-7000.conf内容
>
> ~~~txt
> port 7000
> bind 0.0.0.0
> ~~~

(2) 确保Docker Service已经启动

> ~~~bash
> [root@localhost docker-redis]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
>    Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
>    Active: inactive (dead)
>      Docs: http://docs.docker.com
> [root@localhost docker-redis]# service docker start
> Redirecting to /bin/systemctl start docker.service
> [root@localhost docker-redis]# service docker status
> Redirecting to /bin/systemctl status docker.service
> ● docker.service - Docker Application Container Engine
>    Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
>    Active: active (running) since 六 2021-01-30 16:17:24 CST; 43s ago
>    ...
> ~~~

(2) 构建镜像

> ~~~bash
> [root@localhost docker-redis]# docker build -t fangkun119/dockerdemo-redis .
> Sending build context to Docker daemon 1.745 MB
> Step 1/10 : FROM centos
> ...
> Step 10/10 : CMD redis-server redis-7000.conf
> Successfully built b6f8137796d1
> [root@localhost docker-redis]# docker images
> REPOSITORY                    TAG                 IMAGE ID            CREATED             SIZE
> fangkun119/dockerdemo-redis   latest              b6f8137796d1        About an hour ago   494 MB
> docker.io/centos              latest              300e315adb2f        7 weeks ago         209 MB
> ~~~

(3) 创建容器

> ~~~bash
> [root@localhost docker-redis]# docker run -p 7000:7000 fangkun119/dockerdemo-redis
> 1:C 30 Jan 10:38:49.223 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
> 1:C 30 Jan 10:38:49.224 # Redis version=4.0.14, bits=64, commit=00000000, modified=0, pid=1, just started
> 1:C 30 Jan 10:38:49.224 # Configuration loaded
> 1:M 30 Jan 10:38:49.233 * Running mode=standalone, port=7000.
> 1:M 30 Jan 10:38:49.233 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
> 1:M 30 Jan 10:38:49.233 # Server initialized
> 1:M 30 Jan 10:38:49.233 # WARNING overcommit_memory is set to 0! Background save may fail under low memory condition. To fix this issue add 'vm.overcommit_memory = 1' to /etc/sysctl.conf and then reboot or run the command 'sysctl vm.overcommit_memory=1' for this to take effect.
> 1:M 30 Jan 10:38:49.233 # WARNING you have Transparent Huge Pages (THP) support enabled in your kernel. This will create latency and memory usage issues with Redis. To fix this issue run the command 'echo never > /sys/kernel/mm/transparent_hugepage/enabled' as root, and add it to your /etc/rc.local in order to retain the setting after a reboot. Redis must be restarted after THP is disabled.
> 1:M 30 Jan 10:38:49.234 * Ready to accept connections
> ~~~


(4) 检查（另开一个窗口）

> 确认端口7000被监听（CentOS 7）
>
> ~~~bash
> [root@localhost ~]# netstat -lntp
> Active Internet connections (only servers)
> Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
> tcp        0      0 0.0.0.0:111             0.0.0.0:*               LISTEN      682/rpcbind
> tcp        0      0 192.168.122.1:53        0.0.0.0:*               LISTEN      1394/dnsmasq
> tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      1132/sshd
> tcp        0      0 127.0.0.1:631           0.0.0.0:*               LISTEN      1129/cupsd
> tcp        0      0 127.0.0.1:25            0.0.0.0:*               LISTEN      1481/master
> tcp6       0      0 :::111                  :::*                    LISTEN      682/rpcbind
> tcp6       0      0 :::22                   :::*                    LISTEN      1132/sshd
> tcp6       0      0 ::1:631                 :::*                    LISTEN      1129/cupsd
> tcp6       0      0 :::7000                 :::*                    LISTEN      8383/docker-proxy-c
> tcp6       0      0 ::1:25                  :::*                    LISTEN      1481/master
> ~~~
>
> 检查运行中的docker容器，获取容器编号（CONTAINER ID）
>
> ~~~bash
> [root@localhost ~]# docker ps
> CONTAINER ID        IMAGE                         COMMAND                  CREATED             STATUS              PORTS                    NAMES
> 6a8169d83b6e        fangkun119/dockerdemo-redis   "redis-server redi..."   15 minutes ago      Up 15 minutes       0.0.0.0:7000->7000/tcp   peaceful_hamilton
> ~~~
>
> 使用容器编号登录到容器内部查看运行状况
>
> ~~~bash
> [root@localhost ~]# docker exec -it 6a8169d83b6e /bin/bash
> [root@6a8169d83b6e redis-4.0.14]# pwd
> /usr/local/redis-4.0.14
> [root@6a8169d83b6e redis-4.0.14]# ls
> 00-RELEASENOTES  COPYING    Makefile   redis-7000.conf	runtest-cluster   src
> BUGS		 INSTALL    README.md  redis.conf	runtest-sentinel  tests
> CONTRIBUTING	 MANIFESTO  deps       runtest		sentinel.conf	  utils
> [root@6a8169d83b6e redis-4.0.14]# cat redis-7000.conf
> port 7000
> bind 0.0.0.0
> ~~~

(5) 关闭redis容器，关闭Docker Service

> 在运行容器的窗口Ctrl+C，redis会收到信号退出，虽然后关闭Docker Service
>
> ~~~bash
> [root@localhost docker-redis]# service docker stop
> Redirecting to /bin/systemctl stop docker.service
> ~~~

说明：上面只是一个演示，实际工作中可以直接从docker hub上pull官方redis镜像

> ~~~bash
> docker pull redis
> docker run redis
> ~~~







