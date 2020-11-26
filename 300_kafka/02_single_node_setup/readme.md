# Kafka环境搭建

## 1. 环境准备

### (1) 准备`CentOS` (没有用6.10）, `JDK 1.8+`的虚拟机

在Virtual Box上安装CentOS

> 参考: [`999_util/01_centos_on_virtualbox/readme.md`](https://github.com/fangkun119/java_proj_ref/blob/master/999_util/01_centos_on_virtualbox/readme.md)
> 
> 查看操作系统版本
> 
> ~~~bash
> [root@localhost ~]# cat /etc/redhat-release
> CentOS Linux release 7.8.2003 (Core)
> ~~~

为虚拟机配置固定IP（后续要绑定到HostName上、所以需要固定IP）

> (1) `Virtual Box`虚拟机网卡配置：
> 
> * 网卡1：桥接，用来让虚拟机能访问外网，其中“高级”->“控制芯片”一定要选`virtio-net`
> 
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/virtual_box_network_static_ip_cfg_1.jpg)
> 
> * 网卡2：Host Only网络，组建一个从笔记本到虚拟机的子网，用来配置静态IP
> 
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/virtual_box_network_static_ip_cfg_2.jpg)
> 
> (2) 虚拟机静态IP配置：之前的步骤中安装`Virtual Box增强工具`时已经顺带安装了XServer，可以用图形化界面配置；启动虚拟机后、在Virtual Box为虚拟机开启的中断界面输入`startx`命令，进入图形界面
> 
> * `Application` -> `Setting` -> `Network`：点击`Red Hat Ethernet`
> 
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/static_ip_cfg_1.jpg)
> 
> * 填入静态IP、掩码、网关及DNS：
> 
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/static_ip_cfg_2.jpg)

### (2) 设置`JDK`, `JAVA_HOME`，

安装

> ~~~bash
> [root@localhost ~]# rpm -ivh jdk-8u191-linux-x64.rpm
> 警告：jdk-8u191-linux-x64.rpm: 头V3 RSA/SHA256 Signature, 密钥 ID ec551f03: NOKEY
> 准备中...                          ################################# [100%]
> 正在升级/安装...
>    1:jdk1.8-2000:1.8.0_191-fcs        ################################# [100%]
> Unpacking JAR files...
> 	tools.jar...
>	...
> 	localedata.jar...
> [root@localhost ~]# ls -l /usr/java/
> 总用量 0
> lrwxrwxrwx. 1 root root  16 11月 25 16:04 default -> /usr/java/latest
> drwxr-xr-x. 8 root root 258 11月 25 16:04 jdk1.8.0_191-amd64
> lrwxrwxrwx. 1 root root  28 11月 25 16:04 latest -> /usr/java/jdk1.8.0_191-amd64
> [root@localhost ~]# jps
> 23646 Jps
> [root@localhost ~]# ls /usr/java/latest/
> bin  COPYRIGHT  include  javafx-src.zip  jre  lib  LICENSE  man  README.html  release  src.zip  THIRDPARTYLICENSEREADME-JAVAFX.txt  THIRDPARTYLICENSEREADME.txt
> ~~~

配置`JAVA_HOME`和`CLASS_PATH`

> ~~~bash
> [root@localhost ~]# vim ~/.bashrc #添加JAVA_HOME等相关配置
> [root@localhost ~]# cat ~/.bashrc | tail -n 7 #查看新增的6行
> 
> JAVA_HOME=/usr/java/latest
> PATH=$PATH:$JAVA_HOME/bin
> CLASSPATH=.
> export JAVA_HOME
> export PATH
> export CLASSPATH
> [root@localhost ~]# source ~/.bashrc     # 重新加载.bashrc文件
> [root@localhost ~]# echo $JAVA_HOME # 查看JAVA_HOME
> /usr/java/latest
> [root@localhost ~]# echo $CLASSPATH # 查看CLASSPATH
> .
> ~~~

> 相关命令参考
> 
> ~~~bash
> # 查看是否安装了JDK 1.8
> rpm -qa | grep jdk.*1.8
> # 用从oracle网站下载的rpm文件安装
> rpm -ivh jdk-8u191-linux-x64.rpm 
> # 删除某个jdk ，例如jdk1.8-1.8.0_191-fcs.x86_64
> rpm -e `rpm -qa  |  grep jdk1.8-1.8.0_191-fcs.x86_64`
> ~~~

### (3) 配置主机名和IP映射

配置主机名

> ~~~bash
> [root@localhost ~]# cat /etc/sysconfig/network
> # Created by anaconda
> [root@localhost ~]# vi /etc/sysconfig/network
> [root@localhost ~]# cat /etc/sysconfig/network
> NETWORKING=yes
> HOSTNAME=CentOS
> [root@localhost ~]# reboot #重启、让主机名配置生效
> Connection to 192.168.1.104 closed by remote host.
> Connection to 192.168.1.104 closed.
> ~~~

配置IP映射

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ ssh root@192.168.1.104 # 登录到虚拟机
> root@192.168.1.104's password:
> Last login: Wed Nov 25 16:21:41 2020
> [root@localhost ~]# vi /etc/hosts # 增加关于主机名CentOS的配置
> [root@localhost ~]# cat /etc/hosts # 查看配置文件
> 127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
> ::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
> 192.168.1.104 CentOS
> [root@localhost ~]# ping CentOS # 检查
> PING CentOS (192.168.1.104) 56(84) bytes of data.
> 64 bytes from CentOS (192.168.1.104): icmp_seq=1 ttl=64 time=0.042 ms
> 64 bytes from CentOS (192.168.1.104): icmp_seq=2 ttl=64 time=0.043 ms
> ~~~

相关命令

> 查看IP地址：`ifconfig`

### (4) 关闭防火墙&防火墙开机自启动

参考URL：[https://blog.csdn.net/zsgcsdn/article/details/78337564](https://blog.csdn.net/zsgcsdn/article/details/78337564)

> ~~~bash
> [root@localhost ~]# systemctl status firewalld # 查看防火墙状态
> ● firewalld.service - firewalld - dynamic firewall daemon
>    Loaded: loaded (/usr/lib/systemd/system/firewalld.service; enabled; vendor preset: enabled)
>    Active: active (running) since 三 2020-11-25 16:20:30 CST; 19min ago
>      Docs: man:firewalld(1)
>  Main PID: 760 (firewalld)
>     Tasks: 2
>    CGroup: /system.slice/firewalld.service
>            └─760 /usr/bin/python2 -Es /usr/sbin/firewalld --nofork --nopid
> 
> 11月 25 16:20:29 localhost.localdomain systemd[1]: Starting firewalld - dynamic firewall .....
> 11月 25 16:20:30 localhost.localdomain systemd[1]: Started firewalld - dynamic firewall d...n.
> 11月 25 16:20:31 localhost.localdomain firewalld[760]: WARNING: AllowZoneDrifting is enabl....
> Hint: Some lines were ellipsized, use -l to show in full.
> [root@localhost ~]# systemctl stop firewalld # 关闭防火墙
> [root@localhost ~]# systemctl disable firewalld # 防火墙开机不启动
> Removed symlink /etc/systemd/system/multi-user.target.wants/firewalld.service.
> Removed symlink /etc/systemd/system/dbus-org.fedoraproject.FirewallD1.service.
> [root@localhost ~]# systemctl status firewalld # 查看防火墙状态
> ● firewalld.service - firewalld - dynamic firewall daemon
>    Loaded: loaded (/usr/lib/systemd/system/firewalld.service; disabled; vendor preset: enabled)
>    Active: inactive (dead)
>      Docs: man:firewalld(1)
> 
> 11月 25 16:20:29 localhost.localdomain systemd[1]: Starting firewalld - dynamic firewall .....
> 11月 25 16:20:30 localhost.localdomain systemd[1]: Started firewalld - dynamic firewall d...n.
> 11月 25 16:20:31 localhost.localdomain firewalld[760]: WARNING: AllowZoneDrifting is enabl....
> 11月 25 16:39:58 localhost.localdomain systemd[1]: Stopping firewalld - dynamic firewall .....
> 11月 25 16:39:59 localhost.localdomain systemd[1]: Stopped firewalld - dynamic firewall d...n.
> Hint: Some lines were ellipsized, use -l to show in full.
> ~~~

### (5) 同步时钟ntpdate cn.pool.ntp.org | ntp[1-7].aliyun.com 

> 多机部署`kafka`才需要，这里是单机部署，先跳过

### (5) 安装和启动[Zookeeper](http://zookeeper.apache.org)

下载`Zookeeper 3.4.6`：[http://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/](http://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/) 

解压：

> ~~~bash
> [root@localhost ~]# ls ~/share/zookeeper-3.4.6.tar.gz
> /root/share/zookeeper-3.4.6.tar.gz
> [root@localhost ~]# tar -zxf ~/share/zookeeper-3.4.6.tar.gz -C /usr/
> [root@localhost ~]# cd /usr/zookeeper-3.4.6/
> [root@localhost zookeeper-3.4.6]# ls
> bin        CHANGES.txt  contrib     docs             ivy.xml  LICENSE.txt  README_packaging.txt  recipes  zookeeper-3.4.6.jar      zookeeper-3.4.6.jar.md5
> build.xml  conf         dist-maven  ivysettings.xml  lib      NOTICE.txt   README.txt            src      zookeeper-3.4.6.jar.asc  zookeeper-3.4.6.jar.sha
> ~~~

以官方提供的`zoo_sample.cfg`为模板、修改得到配置文件`conf/zoo.cfg`

> ~~~bash
> [root@localhost zookeeper-3.4.6]# ls conf/
> configuration.xsl  log4j.properties  zoo_sample.cfg
> [root@localhost zookeeper-3.4.6]# cp conf/zoo_sample.cfg conf/zoo.cfg
> [root@localhost zookeeper-3.4.6]# vi conf/zoo.cfg #把dataDir改到/tmp/分区以外的目录
> [root@localhost zookeeper-3.4.6]# diff conf/zoo_sample.cfg conf/zoo.cfg #查看修改的部分
> 12c12
> < dataDir=/tmp/zookeeper 
> ---
> > dataDir=/root/zkdata
> [root@localhost zookeeper-3.4.6]# mkdir -p /root/zkdata #创建数据目录
> ~~~

启动`zookeeper`

> ~~~bash
> [root@localhost zookeeper-3.4.6]# cd /usr/zookeeper-3.4.6/
> [root@localhost zookeeper-3.4.6]# ./bin/zkServer.sh
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg # 会提示有哪些命令
> Usage: ./bin/zkServer.sh {start|start-foreground|stop|restart|status|upgrade|print-cmd}
> [root@localhost zookeeper-3.4.6]# ./bin/zkServer.sh start zoo.cfg # 使用之前编写的配置文件启动zookeeper
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
> Starting zookeeper ... STARTED
> [root@localhost zookeeper-3.4.6]# jps # 用jps查看zookeeper进程（QuorumPeerMain）是否已经启动
> 3715 Jps
> 3684 QuorumPeerMain
> [root@localhost zookeeper-3.4.6]# ./bin/zkServer.sh status zoo.cfg # 查看zookeeper状态
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
> Mode: standalone
> ~~~

### (6) 安装和启动及关闭[Kafka](http://Kafka.apache.org)

下载`Scala 2.11 - Kafka_2.11-2.2.0.tgz` ：[http://kafka.apache.org/downloads](http://kafka.apache.org/downloads) 

解压：

> ~~~bash
> [root@localhost zookeeper-3.4.6]# cd ~
> [root@localhost ~]# ls ~/share/kafka_2.11-2.2.0.tgz
> /root/share/kafka_2.11-2.2.0.tgz
> [root@localhost ~]# tar -zxf ~/share/kafka_2.11-2.2.0.tgz -C /usr/
> [root@localhost ~]# cd /usr/kafka_2.11-2.2.0/
> [root@localhost kafka_2.11-2.2.0]# ls
> bin  config  libs  LICENSE  NOTICE  site-docs
> ~~~

修改配置文件：

> 配置文件修改内容：[300_kafka/02_single_node_setup/kafaka/config/server.properties](https://github.com/fangkun119/java_proj_ref/commit/a1c1ee4cfbd52f1d3b5d9edb4ef9080bf814ec95?branch=a1c1ee4cfbd52f1d3b5d9edb4ef9080bf814ec95&diff=split#diff-7efccb1025d1f8b0ba9903944e70cc2d83c74a774edbf265a3cfa158abd183db) 
> 
> 修改过程
> 
> ~~~bash
> [root@localhost kafka_2.11-2.2.0]# ls config/server.properties # 要修改的配置文件
> config/server.properties 
> [root@localhost kafka_2.11-2.2.0]# mkdir -p backup && cp config/server.properties backup/server.properties && ls backup # 备份
> server.properties
> [root@localhost kafka_2.11-2.2.0]# vim config/server.properties # 修改
> [root@localhost kafka_2.11-2.2.0]# diff backup/server.properties config/server.properties # 查看修改的内容
> 20a21
> > # brocker.id：Kafka服务节点的唯一表示，多机部署时需要为不同的节点配置不同的id
> 31c32,34
> < #listeners=PLAINTEXT://:9092
> ---
> > #listeners=PLAINTEXT://:9092
> > #listeners: Service监听地址，要配主机名而不是IP，这个单机Kafka环境在/etc/hosts里面把主机名配成了CentOS
> > listeners=PLAINTEXT://CentOS:9092
> 60c63,64
> < log.dirs=/tmp/kafka-logs
> ---
> > # log.dirs: Kafka Broker节点存储数据的位置，把它改成/tmp/以外分区上的目录，还要保证启动的服务有权限读写这个目录
> > log.dirs=/usr/kafka-logs
> 64a69
> > # num.partitions: topic默认分区数
> 123c128,129
> < zookeeper.connect=localhost:2181
> ---
> > # zookeeper.connect: zooker连接参数
> > zookeeper.connect=CentOS:2181
> ~~~

启动`Kafka`

> ~~~bash
> [root@localhost kafka_2.11-2.2.0]# ./bin/kafka-server-start.sh -daemon config/server.properties # 启动
> [root@localhost kafka_2.11-2.2.0]# jps # 查看java进程
> 8723 Kafka
> 3684 QuorumPeerMain
> 8745 Jps
> [root@localhost kafka_2.11-2.2.0]# ls /usr/kafka-logs/ # 查看Kafka写入的数据文件
> cleaner-offset-checkpoint  log-start-offset-checkpoint  meta.properties  recovery-point-offset-checkpoint  replication-offset-checkpoint
> ~~~

关闭`Kafka`

> ~~~bash
> [root@localhost kafka_2.11-2.2.0]# ./bin/kafka-server-stop.sh # 关闭
> [root@localhost kafka_2.11-2.2.0]# jps # 等待一段时间后执行jps，可以看到Kafka进程已经不存在
> 3684 QuorumPeerMain
> 8911 Jps
> ~~~

## 2. 用生产者、消费者测试`Kafka`

> 需要开启两个终端窗口

(1) 用第一个终端窗口启动`Kafka`

> ~~~bash
> [root@localhost kafka_2.11-2.2.0]# ./bin/kafka-server-start.sh -daemon config/server.properties
> [root@localhost kafka_2.11-2.2.0]# jps
> 9266 Kafka
> 9587 Jps
> 3684 QuorumPeerMain
> ~~~

(2) 用第二个ssh终端窗口，登录并创建`topic`，并启动消费者进程

创建`topic`：topic01

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ ssh root@192.168.1.104
> root@192.168.1.104's password:
> Last login: Wed Nov 25 16:22:34 2020 from 192.168.1.149
> [root@localhost ~]# cd /usr/kafka_2.11-2.2.0/
> [root@localhost kafka_2.11-2.2.0]# pwd
> /usr/kafka_2.11-2.2.0
> [root@localhost kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --help # 查看命令参数
> [root@localhost kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOS:9092 --create --topic topic01 --partitions 1 --replication-factor 1 # 创建分区
> ~~~
> 
> 备注：
> 
> * `2.2`之前修改topic使用的是`--zookeeper`参数，`2.2`之后使用`--bootstrap-server`
> * `--partitions`：topic分区数
> * `--replication-factor`：每个分区的副本数
> * 上面的`CentOS`是`HostName`

启动消费者进程，消费者进程会阻塞并等待消息到来

> ~~~bash
> [root@localhost kafka_2.11-2.2.0]# ./bin/kafka-console-consumer.sh --bootstrap-server CentOS:9092 --topic topic01 --group group1
> 
> ~~~
>
> 备注：
> 
> * `--group`：消费者所属的消费组组名

(3) 用第一个终端窗口发送消息

开启producer进程，可以在`>`提示符之后输入消息

> ~~~bash
> [root@localhost kafka_2.11-2.2.0]# ./bin/kafka-console-producer.sh --broker-list CentOS:9092 --topic topic01
> >this is the first message
> >
> >this is the third message
> ~~~

在第二个窗口可以看到consumer打印出收到的消息

> ~~~bash
> [root@localhost kafka_2.11-2.2.0]# ./bin/kafka-console-consumer.sh --bootstrap-server CentOS:9092 --topic topic01 --group group1
> this is the first message
> 
> this is the third message
> ~~~

(4) 同组多个消费者：消费者负载均衡

如果启动多个消费者、让他们同属于`group1`，可以看到消息会大致均匀地发送给这些消费者，从而做到消费者负载均衡

(5) 多组消费者：消息广播到各个组

如果让多个消费者属于不同的组，可以看到对于同一条消息，每个组都能收到一个拷贝，从而做到像多个组广播消息

(6) 实验结束，关闭`Kafka`服务以及`Zookeeper`，关闭虚拟机

~~~bash
[root@localhost kafka_2.11-2.2.0]# ./bin/kafka-console-consumer.sh --bootstrap-server CentOS:9092 --topic topic01 --group group1
this is the first message

this is the third message
^CProcessed a total of 3 messages
[root@localhost kafka_2.11-2.2.0]# ./bin/kafka-server-stop.sh #关闭kafka
[root@localhost kafka_2.11-2.2.0]# cd /usr/zookeeper-3.4.6/
[root@localhost zookeeper-3.4.6]# ./bin/zkServer.sh stop zoo.cfg #关闭zookeeper
JMX enabled by default
Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
Stopping zookeeper ... STOPPED
[root@localhost zookeeper-3.4.6]# jps #检查
12952 Jps
[root@localhost zookeeper-3.4.6]# shutdown 0 # 关闭虚拟机
Shutdown scheduled for 三 2020-11-25 18:46:30 CST, use 'shutdown -c' to cancel.
[root@localhost zookeeper-3.4.6]# Connection to 192.168.1.104 closed by remote host.
Connection to 192.168.1.104 closed.
~~~
























