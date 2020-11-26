# Kafka集群安装

## 1. CentOS环境准备

### (1) 准备3台装有`Kafka`虚拟机

> 复用了[上一个实验](../02_single_node_setup/readme.md)创建的VM，将其复制出三份（复制VM时选择为所有网卡重新生成MAC地址，链接复制）

![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafaka_vm_copies.jpg) 

### (2) 修改3台机器的静态IP、配置主机名和IP映射

> 方法参考[上一个实验](../02_single_node_setup/readme.md)的配置记录，配置3台VM的`HostName`，`静态IP`如下
> 
> |  VM 			 			| 主机名  	| 静态IP 	         |
> | --------------------	| --------	| ---------- |
> | `kafka_cluster_node_a`	| CentOSA	| 192.168.1.121  |
> | `kafka_cluster_node_b`	| CentOSB	| 192.168.1.122 |
> | `kafka_cluster_node_c`	| CentOSC	| 192.168.1.123 |

### (3) 网络连接检查

> 配置好，重启之后，以第一台`kafaka_cluster_node_a`为例检查如下，另外两台类似
> 
> ~~~bash
> [root@localhost ~]# cat /etc/sysconfig/network  #3台VM的HOSTNAME依次为CentOSA, CentOSB, CentOSC
> NETWORKING=yes
> HOSTNAME=CentOSA
> 
> [root@localhost ~]# cat /etc/hosts #IP到HostName的映射
> 127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
> ::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
> 192.168.1.121 CentOSA
> 192.168.1.122 CentOSB
> 192.168.1.123 CentOSC
> 
> [root@CentOSA ~]# ip add #三台机器的地址依次是192.168.1.121, 122, 123，网关和DNS都是192.168.1.1，子网掩码都是255.255.255.0
> ...
> 3: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
>     link/ether 08:00:27:ba:a7:e9 brd ff:ff:ff:ff:ff:ff
>     inet 192.168.1.121/24 brd 192.168.1.255 scope global noprefixroute eth0
>        valid_lft forever preferred_lft forever
>     inet6 fe80::b6da:a7fa:3df:886b/64 scope link noprefixroute
>        valid_lft forever preferred_lft forever
> ...
> 
> [root@CentOSA ~]# ping www.baidu.com #可以访问外网
> PING www.a.shifen.com (112.80.248.76) 56(84) bytes of data.
> 64 bytes from 112.80.248.76 (112.80.248.76): icmp_seq=1 ttl=56 time=19.6 ms
> 
> ~~~
> 
>启动三台虚拟机，互相之间可以`ping`通
>
>~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/300_kafka/
> $ ssh root@192.168.1.121
> root@192.168.1.121's password:
> Last login: Thu Nov 26 16:41:27 2020
> [root@CentOSA ~]# ping CentOSA
> PING CentOSA (192.168.1.121) 56(84) bytes of data.
> 64 bytes from CentOSA (192.168.1.121): icmp_seq=1 ttl=64 time=0.050 ms
> ^C
> --- CentOSA ping statistics ---
> 1 packets transmitted, 1 received, 0% packet loss, time 999ms
> rtt min/avg/max/mdev = 0.038/0.044/0.050/0.006 ms
> [root@CentOSA ~]# ping CentOSB
> PING CentOSB (192.168.1.122) 56(84) bytes of data.
> 64 bytes from CentOSB (192.168.1.122): icmp_seq=10 ttl=64 time=1.38 ms
> ^C
> --- CentOSB ping statistics ---
> 2 packets transmitted, 1 received, 50% packet loss, time 19012ms
> rtt min/avg/max/mdev = 1.382/1.382/1.382/0.000 ms
> [root@CentOSA ~]# ping CentOSC
> PING CentOSC (192.168.1.123) 56(84) bytes of data.
> 64 bytes from CentOSC (192.168.1.123): icmp_seq=10 ttl=64 time=1.87 ms
> --- CentOSC ping statistics ---
> 1 packets transmitted, 1 received, 0% packet loss, time 999ms
> ^C
> ~~~

### 运行环境配置及`Kafka`启动

> 因为3台虚拟机拷贝自[上一个实验](../02_single_node_setup/readme.md)已经配置好的VM，大部分步骤只需要检查，少数如`时钟同步`、`Zookeeper`、`Kafka`等需要增加配置
> 
> 下面的步骤只贴出来了在`CentOSA`上的操作，这些操作在`CentOSB`、`CentOSC`上也同样需要执行

(1) JDK, JAVA_HOME

> ~~~bash
> [root@CentOSA ~]# rpm -qa | grep jdk1.8.*191
> jdk1.8-1.8.0_191-fcs.x86_64
> [root@CentOSA ~]# cat ~/.bashrc | tail -n 7
> 
> JAVA_HOME=/usr/java/latest
> PATH=$PATH:$JAVA_HOME/bin
> CLASSPATH=.
> export JAVA_HOME
> export PATH
> export CLASSPATH
> [root@CentOSA ~]# echo $JAVA_HOME
> /usr/java/latest
> [root@CentOSA ~]# jps
> 2407 Jps
> ~~~

(2) 主机名和IP映射

> 上一小节已经检查

(3) 防火墙关闭，且不会开机自启动（`CentOS 7`）

> ~~~bash
> [root@CentOSA ~]# systemctl status firewalld
> ● firewalld.service - firewalld - dynamic firewall daemon
>    Loaded: loaded (/usr/lib/systemd/system/firewalld.service; disabled; vendor preset: enabled)
>    Active: inactive (dead)
>      Docs: man:firewalld(1)
> ~~~
> 
> 对于`CentOS 6`是检查`service iptables status`以及`chkconfig --list | grep iptables`

(4) 配置时钟同步

> 确保`ntp`时钟服务已经安装
> 
> ~~~bash
> [root@CentOSA ~]# yum install ntp -y
> 已加载插件：fastestmirror, langpacks
> Loading mirror speeds from cached hostfile
>  * base: mirrors.cn99.com
>  * extras: mirrors.aliyun.com
>  * updates: mirrors.cn99.com
> 软件包 ntp-4.2.6p5-29.el7.centos.2.x86_64 已安装并且是最新版本
> 无须任何处理
> ~~~
> 
> 时钟同步，同步源可以是`cn.pool.ntp.org`或者`ntp[1-7].aliyun.com`
> 
> ~~~bash
> [root@CentOSA ~]# ntpdate ntp1.aliyun.com
> 26 Nov 16:59:11 ntpdate[2868]: adjust time server 120.25.115.20 offset 0.001538 sec
> ~~~

(5) Zookeeper配置及启动

> 检查data目录配置
> 
> ~~~bash
> [root@CentOSA ~]# cat /usr/zookeeper-3.4.6/conf/zoo.cfg  | grep 'dataDir='
> dataDir=/root/zkdata
> [root@CentOSA ~]# ls /root/zkdata/
> version-2
> [root@CentOSA ~]# rm -rf /root/zkdata/* # 清空zkdata目录 （三台VM都清空）
> [root@CentOSA ~]# ls /root/zkdata/
> ~~~
> 
> 增加zookeeper节点配置（三台虚拟机都配置）
> 
> ~~~bash
> [root@CentOSA ~]# vi /usr/zookeeper-3.4.6/conf/zoo.cfg # 增加3行zookeeper节点配置
> [root@CentOSA ~]# tail -4 /usr/zookeeper-3.4.6/conf/zoo.cfg  # 检查新增的配置
> #autopurge.purgeInterval=1
> server.1=CentOSA:2888:3888
> server.2=CentOSB:2888:3888
> server.3=CentOSC:2888:3888
> 
> ~~~
> 
> 启动zookeeper节点（三台虚拟机上的节点都启动）
> 
> ~~~bash
> [root@CentOSA ~]# /usr/zookeeper-3.4.6/bin/zkServer.sh start zoo.cfg
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
> Starting zookeeper ... STARTED
> ~~~

(6) `Kafka`配置及启动

> 检查和修改`Kafka`配置
> 
> ~~~bash
> [root@CentOSA ~]# ls /usr/kafka_2.11-2.2.0/
> backup  bin  config  libs  LICENSE  logs  NOTICE  site-docs
> [root@CentOSA ~]# vi /usr/kafka_2.11-2.2.0/config/server.properties # 修改配置
> [root@CentOSA ~]# 以下命令列出了检查或修改的内容
> [root@CentOSA ~]# # 三台虚拟机的brocker.id分别应当是0，1，2
> [root@CentOSA ~]# cat /usr/kafka_2.11-2.2.0/config/server.properties | grep ^broker.id=
broker.id=0
> [root@CentOSA ~]# # 三台虚拟机监听的Host分别应当是CentOSA:9092, CentOSB:9092, CentOSC:9092
> [root@CentOSA ~]# cat /usr/kafka_2.11-2.2.0/config/server.properties | grep ^listeners= 
> listeners=PLAINTEXT://CentOSA:9092
> [root@CentOSA ~]# cat /usr/kafka_2.11-2.2.0/config/server.properties | grep ^log.dirs=
> log.dirs=/usr/kafka-logs
> [root@CentOSA ~]# cat /usr/kafka_2.11-2.2.0/config/server.properties | grep ^zookeeper.connect=
> zookeeper.connect=CentOSA:2181,CentOSB:2181,CentOSC:2181
> ~~~
> 
> 清理拷贝VM之前、上一次遗留的kafka日志
> 
> ~~~bash
> [root@CentOSA ~]# rm -rf /usr/kafka-logs/* #因为是拷贝过来的VM，清理之前遗留的kafka日志
> [root@CentOSA ~]# ls /usr/kafka-logs/
> [root@CentOSA ~]#
> ~~~
> 
> 至此`Kafka`集群环境搭建完毕，可以启动`Kafka`
> 
> 