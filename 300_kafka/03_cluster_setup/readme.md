# Kafka集群安装

## 1. CentOS环境准备

### (1) 准备3台装有`Kafka`虚拟机

> 复用了[上一个实验](../02_single_node_setup/readme.md)创建的VM，将其复制出三份（复制VM时选择为所有网卡重新生成MAC地址，链接复制）

![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafaka_vm_copies.jpg) 

### (2) 修改3台机器的静态IP、配置主机名和IP映射

> 为每台机器配置两个网卡：
> 
> * 网卡1：`桥接网卡`，用来让机器能够访问外网，以便下载安装包执行如`rpm`等远程安装命令：
> 	* IP可以使用默认的`DHCP`，以免与其他设备IP冲突；
> 	* 也可以设置成静态IP、方便使用`ssh client`登录（不用每次登录机器都要先在Virtual Box中断执行`ip add`查询IP地址）
> * 网卡2：`Host Only网络`，用来让3台机器组成一个局域网，设置成静态IP
> 
> 配置方法参考文档：[在Virtual Box上安装CentOS](https://github.com/fangkun119/java_proj_ref/blob/master/999_util/01_centos_on_virtualbox/readme.md)中`2.(4) 为虚拟机设置固定IP`小节
> 
> 以下是配置3台VM的`HostName`，`静态IP`如下
> 
> |  VM                             	| 主机名  	| Host Only网络静态IP |  桥接网络 | 
> | --------------------	| --------	| ---------------  | ------- | 
> | `kafka_cluster_node_a`	| CentOSA	| 192.168.56.102         | DHCP     |
> | `kafka_cluster_node_b`	| CentOSB	| 192.168.56.103         | DHCP     |
> | `kafka_cluster_node_c`	| CentOSC	| 192.168.56.104         | DHCP     |

### (3) 网络连接检查

> 修改三台机器的`/etc/sysconfig/network`、`/etc/hosts`文件，修改结果如下
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
> 192.168.56.102 CentOSA
> 192.168.56.103 CentOSB
> 192.168.56.104 CentOSC
> ~~~
> 
> 重启后，检查3台机器用在`Host Only`网络网卡的IP地址，以`CentOSA`为例如下：
> 
> ~~~bash
> [root@CentOSA ~]# ip add #三台机器的地址依次是192.168.56.102、103、104，网关和DNS都是192.168.56.1，子网掩码都是255.255.255.0
> ...
> 3: eth1: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
>     link/ether 08:00:27:1b:24:7f brd ff:ff:ff:ff:ff:ff
>     inet 192.168.56.102/24 brd 192.168.56.255 scope global noprefixroute eth1
>        valid_lft forever preferred_lft forever
>     inet6 fe80::88d5:e1eb:8622:2e7/64 scope link noprefixroute
>        valid_lft forever preferred_lft forever
> ...
> 
> [root@CentOSA ~]# ping www.baidu.com #可以访问外网
> PING www.a.shifen.com (112.80.248.76) 56(84) bytes of data.
> 64 bytes from 112.80.248.76 (112.80.248.76): icmp_seq=1 ttl=56 time=19.6 ms
> ~~~
> 
> 三台虚拟机之间可以通过配置的域名在`Host Only`网络互相访问，基本不会丢包。以`CentOSA`为例、检查结果如下（其中的`192.168.1.124`是在`Virtual Box`虚拟机终端使用`ip add`命令得到）
>
>~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/100_nginx_tengine/01_setup_and_config/
> $ ssh root@192.168.1.124
> root@192.168.1.124's password:
> Last login: Fri Nov 27 15:27:46 2020
> [root@CentOSA ~]# ping CentOSA
> PING CentOSA (192.168.56.102) 56(84) bytes of data.
> 64 bytes from CentOSA (192.168.56.102): icmp_seq=1 ttl=64 time=0.109 ms
> 64 bytes from CentOSA (192.168.56.102): icmp_seq=2 ttl=64 time=0.063 ms
> 64 bytes from CentOSA (192.168.56.102): icmp_seq=3 ttl=64 time=0.069 ms
> 64 bytes from CentOSA (192.168.56.102): icmp_seq=4 ttl=64 time=0.063 ms
> ^C
> --- CentOSA ping statistics ---
> 4 packets transmitted, 4 received, 0% packet loss, time 3002ms
> rtt min/avg/max/mdev = 0.063/0.076/0.109/0.019 ms
> [root@CentOSA ~]# ping CentOSB
> PING CentOSB (192.168.56.103) 56(84) bytes of data.
> 64 bytes from CentOSB (192.168.56.103): icmp_seq=1 ttl=64 time=1.32 ms
> 64 bytes from CentOSB (192.168.56.103): icmp_seq=2 ttl=64 time=0.621 ms
> 64 bytes from CentOSB (192.168.56.103): icmp_seq=3 ttl=64 time=0.692 ms
> 64 bytes from CentOSB (192.168.56.103): icmp_seq=4 ttl=64 time=0.631 ms
> ^C
> --- CentOSB ping statistics ---
> 4 packets transmitted, 4 received, 0% packet loss, time 3005ms
> rtt min/avg/max/mdev = 0.621/0.816/1.322/0.294 ms
> [root@CentOSA ~]# ping CentOSC
> PING CentOSC (192.168.56.104) 56(84) bytes of data.
> 64 bytes from CentOSC (192.168.56.104): icmp_seq=1 ttl=64 time=2.38 ms
> 64 bytes from CentOSC (192.168.56.104): icmp_seq=2 ttl=64 time=0.718 ms
> 64 bytes from CentOSC (192.168.56.104): icmp_seq=3 ttl=64 time=0.603 ms
> 64 bytes from CentOSC (192.168.56.104): icmp_seq=4 ttl=64 time=0.898 ms
> ^C
> --- CentOSC ping statistics ---
> 4 packets transmitted, 4 received, 0% packet loss, time 3007ms
> rtt min/avg/max/mdev = 0.603/1.150/2.381/0.718 ms
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
> ~~~
> 
> 写入节点id标识：与zoo.cfg保持一致，`CentOSA`为1、`CentOSB`为2、`CentOSC`为3
> 
> ~~~bash
> [root@CentOSA zookeeper-3.4.6]# echo 1 > /root/zkdata/myid #“1”来自zoo.cfg，每台机器值都不一样
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
> 
> 检查zookeeper进程，下面的`QuorumPeerMain`就是zookeeper的进程
> 
> ~~~bash
> [root@CentOSA ~]# jps
> 6745 Jps
> 6095 QuorumPeerMain
> ~~~
> 
> 检查zookeeper日志，待3个zookeeper节点都启动后，应当不会再有“java.net.ConnectException: 拒绝连接 (Connection refused)”日志，只剩下下面的正常日志
> 
> ~~~bash
> [root@CentOSA ~]# tail zookeeper.out
> 2020-11-27 16:38:10,556 [myid:1] - INFO  [CentOSA/192.168.56.102:3888:QuorumCnxManager$Listener@511] - Received connection request /192.168.56.104:53134
> 2020-11-27 16:38:10,560 [myid:1] - INFO  [WorkerReceiver[myid=1]:FastLeaderElection@597] - Notification: 1 (message format version), 3 (n.leader), 0x0 (n.zxid), 0x1 (n.round), LOOKING (n.state), 3 (n.sid), 0x0 (n.peerEpoch) FOLLOWING (my state)
> ~~~

(6) `Kafka`配置

> 检查和修改`Kafka`配置
> 
> ~~~bash
> [root@CentOSA ~]# ls /usr/kafka_2.11-2.2.0/
> backup  bin  config  libs  LICENSE  logs  NOTICE  site-docs
> [root@CentOSA ~]# vi /usr/kafka_2.11-2.2.0/config/server.properties # 修改配置
> [root@CentOSA ~]# 以下命令列出了检查或修改的内容
> [root@CentOSA ~]# # 三台虚拟机的brocker.id分别应当是0，1，2
> [root@CentOSA ~]# cat /usr/kafka_2.11-2.2.0/config/server.properties | grep ^broker.id=
> broker.id=0
> [root@CentOSA ~]# # 三台虚拟机监听的Host分别应当是CentOSA:9092, CentOSB:9092, CentOSC:9092
> [root@CentOSA ~]# cat /usr/kafka_2.11-2.2.0/config/server.properties | grep ^listeners= 
> listeners=PLAINTEXT://CentOSA:9092
> [root@CentOSA ~]# cat /usr/kafka_2.11-2.2.0/config/server.properties | grep ^log.dirs=
> log.dirs=/usr/kafka-logs
> [root@CentOSA ~]# cat /usr/kafka_2.11-2.2.0/config/server.properties | grep ^zookeeper.connect=
> zookeeper.connect=CentOSA:2181,CentOSB:2181,CentOSC:2181
> ~~~
> 
> 清理拷贝VM之前、上一次前一个VM遗留的kafka日志
> 
> ~~~bash
> [root@CentOSA ~]# rm -rf /usr/kafka-logs/* #因为是拷贝过来的VM，清理之前遗留的kafka日志
> [root@CentOSA ~]# ls /usr/kafka-logs/
> [root@CentOSA ~]#
> [root@CentOSA ~]# rm -f /usr/kafka_2.11-2.2.0/logs/* 
> ~~~
> 
> 至此`Kafka`集群环境搭建完毕，下一个实验（topic管理）将启动和测试`Kafka`
> 
> 关闭zookeeper节点，关闭虚拟机
> 
> ~~~
> [root@CentOSA zookeeper-3.4.6]# cd /usr/zookeeper-3.4.6/
> [root@CentOSA zookeeper-3.4.6]# ./bin/zkServer.sh stop zoo.cfg #关闭zookeeper
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
> Stopping zookeeper ... STOPPED
> [root@CentOSA zookeeper-3.4.6]# jps #检查
> 12952 Jps
> [root@CentOSA zookeeper-3.4.6]# shutdown 0 # 关闭虚拟机
> ~~~

