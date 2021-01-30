<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Docker安装及配置](#docker%E5%AE%89%E8%A3%85%E5%8F%8A%E9%85%8D%E7%BD%AE)
  - [1.容器化技术介绍](#1%E5%AE%B9%E5%99%A8%E5%8C%96%E6%8A%80%E6%9C%AF%E4%BB%8B%E7%BB%8D)
  - [2. Docker介绍](#2-docker%E4%BB%8B%E7%BB%8D)
  - [3. 安装Docker](#3-%E5%AE%89%E8%A3%85docker)
    - [(1) 在 CentOS 7 安装 Docker](#1-%E5%9C%A8-centos-7-%E5%AE%89%E8%A3%85-docker)
  - [4. 阿里云加速代理](#4-%E9%98%BF%E9%87%8C%E4%BA%91%E5%8A%A0%E9%80%9F%E4%BB%A3%E7%90%86)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Docker安装及配置

## 1.容器化技术介绍

历史演化：

> `物理机` -> `VM` (需要安装Guest OS、浪费了资源) -> `Container`（直接构建在主OS上、没有内置OS、通过Docker的Sand Box机制直接依赖主OS来构建服务，部署快节省资源）
> 
> * `VM`：解决物理资源的隔离
> * `Container`：App层面的隔离

软件协作方式变化：

> * 资源部署打包成容器、研发与运维分工的变化
> * 标准化部署方案、一键发布、集群管理，降低运维人工成本

应用场景：

> * 标准化迁移
> * 统一参数配置
> * 自动化部署
> * 应用集群监控
> * 研发与运维的沟通桥梁

VM与Container的混合应用：

> 例如 EC2(VM）+ MySQL容器

## 2. Docker介绍

Docker

> * 开源、基于Go
> * 使用Sand Box机制，容器开销极低
> * 容器化技术的代名词
> * 也具备一定的虚拟化职能

容器如何做到化标准化部署：容器包含了程序运行的所有必要条件，然后发布到指定的目的地（节点服务器），进行展开

> * 软件运行资源：静态网站、DB、队列信息、……
> * 硬件需求

详细内容：[http://www.docker.com/](http://www.docker.com/)

> * 上手：[https://www.docker.com/get-started](https://www.docker.com/get-started) 
> * 文档：[https://docs.docker.com/](https://docs.docker.com/) 

## 3. 安装Docker 

> * [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/) 
>
> * [https://docs.docker.com/engine/install/](https://docs.docker.com/engine/install/) 

### (1) 在 CentOS 7 安装 Docker

> 以[`https://docs.docker.com/engine/install/centos/`](https://docs.docker.com/engine/install/centos/)为准，下面是简要步骤及一些与官方文档不同、或额外增加的步骤
>
> ~~~bash
> # 确保旧版的Docker已经删除
> [root@localhost ~]# sudo yum remove docker \
>                   docker-client \
>                   docker-client-latest \
>                   docker-common \
>                   docker-latest \
>                   docker-latest-logrotate \
>                   docker-logrotate \
>                   docker-engine
> 
> # 安装yum工具集以便能够使用yum-config-manager，简化安装源配置
> [root@localhost ~]# sudo yum install -y yum-utils 
> 
> # 安装数据存储驱动包、Docker容器进行内部数据存储时，需要这两个驱动包来完成存储 （额外增加步骤）
> [root@localhost ~]# sudo yum install -y device-mapper-persistent-data lvm2
> 
> # 增加安装源，其中ce是community edition的意思，及免费的开源社区版（相对于企业版“ee”）
> # 相比官方文档、增加了aliyun的repo、加快国内访问速度，并设置makecache fast让yum自动选择最快的安装源
> [root@localhost ~]# sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
> [root@localhost ~]# sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
> sudo yum makecache fast
> 
> # 安装docker
> [root@localhost ~]# sudo yum -y install docker-ce
> 
> # 启动docker
> [root@localhost ~]# service docker start
> Redirecting to /bin/systemctl start docker.service
> 
> # 验证，Docker是一个CS架构
> [root@localhost ~]# docker version
> Client: Docker Engine - Community
> Version:          19.03.13
> Server: Docker Engine - Community
> Version:          19.03.13
> 
> [root@localhost ~]# docker pull hello-world
> Using default tag: latest
> latest: Pulling from library/hello-world
> 0e03bdcc26d7: Pull complete
> Digest: sha256:8c5aeeb6a5f3ba4883347d3747a7249f491766ca1caa47e5da5dfcf6b9b717c0
> Status: Downloaded newer image for hello-world:latest
> docker.io/library/hello-world:latest
> 
> [root@localhost ~]# docker run hello-world
> Hello from Docker!
> ~~~

## 4. 阿里云加速代理

> 运行`docker pull`连接外网获取镜像时，会遇到网络不稳定的情况，配置和使用`阿里云镜像加速代理`，可以解决此问题（需要付费）
> 
> 在阿里云官网搜索“容器镜像服务”: [https://www.aliyun.com/](https://www.aliyun.com/)，可以开通，阿里云会提供代理地址和配置文档   


