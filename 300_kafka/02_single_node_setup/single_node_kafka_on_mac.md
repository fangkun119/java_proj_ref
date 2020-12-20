# 单节点Kafka环境搭建（Mac）

## 1 安装Kafka

> 安装[home brew](https://brew.sh/)，之后直接`brew install kafka`即可，它会自动安装`zookeeper`。重新安装可以使用`brew reinstall kafka`。
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ brew reinstall kafka
> ==> Downloading https://homebrew.bintray.com/bottles/kafka-2.6.0_1.big_sur.bottle.tar.gz
> Already downloaded: /Users/fangkun/Library/Caches/Homebrew/downloads/537fa2f2bf923de228f7ac9bbc2d317d5b46703a21978d916a0b744ed33837aa--kafka-2.6.0_1.big_sur.bottle.tar.gz
> ==> Reinstalling kafka
> ==> Pouring kafka-2.6.0_1.big_sur.bottle.tar.gz
> ==> Caveats
> To have launchd start kafka now and restart at login:
>   brew services start kafka
> Or, if you don't want/need a background service you can just run:
>   zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties & kafka-server-start /usr/local/etc/kafka/server.properties
> ==> Summary
> 🍺  /usr/local/Cellar/kafka/2.6.0_1: 186 files, 62.4MB
> ~~~
>
> 如果安装时brew卡在`update brew`一步，有两种方法可以解决：(1) 按一下`Ctrl + c`等待几秒钟之后会跳过`update brew`继续安装；(2) 使用能提供代理地址的翻墙软件，并为terminal设置代理
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/300_kafka/
> $ export https_proxy=http://127.0.0.1:7890 http_proxy=http://127.0.0.1:7890 all_proxy=socks5://127.0.0.1:7890
> ~~~

## 2 程序及配置文件目录

> 程序目录：`/usr/local/Cellar/*`
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ ls /usr/local/Cellar/zookeeper/
> 3.6.2_1
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ ls /usr/local/Cellar/kafka/
> 2.6.0_1
> ~~~
>
> 配置文件目录：`/user/local/etc/*`
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ ls /usr/local/etc/zookeeper/
> defaults         log4j.properties zoo.cfg          zoo_sample.cfg
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ ls /usr/local/etc/kafka/
> connect-console-sink.properties   consumer.properties
> connect-console-source.properties log4j.properties
> connect-distributed.properties    producer.properties
> connect-file-sink.properties      server.properties
> connect-file-source.properties    tools-log4j.properties
> connect-log4j.properties          trogdor.conf
> connect-mirror-maker.properties   zookeeper.properties
> connect-standalone.properties
> ~~~

## 3 修改配置文件

>修改kafka配置文件：同样不需要修改`log.dirs`，`zookeeper.connect`也不需要修改，增加`listeners`配置即可（zookeeper的配置不用修改）
>
>~~~bash
>__________________________________________________________________
>$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/kafka/
>$ cd /usr/local/etc/kafka/
>__________________________________________________________________
>$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/kafka/
>$ cp server.properties server.properties.backup #备份
>__________________________________________________________________
>$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/kafka/
>$ vim server.properties #打开文件后进行修改
>__________________________________________________________________
>$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/kafka/
>$ diff server.properties server.properties.backup #对比查看修改内容，只增加了一行
>32d31
>< listeners=PLAINTEXT://localhost:9092 
>__________________________________________________________________
>$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:/usr/local/etc/kafka/
>$ cat server.properties | grep ^listeners --color -C2 #配置增加的位置
>#     listeners = PLAINTEXT://your.host.name:9092
>#listeners=PLAINTEXT://:9092
>listeners=PLAINTEXT://localhost:9092
>
># Hostname and port the broker will advertise to producers and consumers. If not set,
>~~~

## 4 启动Zookeeper和Kafka

> 启动Zookeeper，kafka已经提供了一份zookeeper配置，用kafka提供的就行
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ nohup zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties 2>&1 >zk.log &
> [1] 9850
> ~~~
>
> 启动Kafka，日志被重定向到`zk.log`文件中，可以用`tail`、`cat`等命令查看
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ nohup  kafka-server-start /usr/local/etc/kafka/server.properties 2>&1 >kafka.log &
> [2] 10170
> ~~~
>
> 用jps命令可以看到zookeeper的进程，日志被重定向到`kafka.log`文件中，可以用`tail`、`cat`等命令查看
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ jps
> 10489 Jps
> 9850 QuorumPeerMain
> 10170 Kafka
> ~~~

## 5 Kafka命令行工具

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ kafka-
> kafka-acls                        kafka-mirror-maker
> kafka-broker-api-versions         kafka-preferred-replica-election
> kafka-configs                     kafka-producer-perf-test
> kafka-console-consumer            kafka-reassign-partitions
> kafka-console-producer            kafka-replica-verification
> kafka-consumer-groups             kafka-run-class
> kafka-consumer-perf-test          kafka-server-start
> kafka-delegation-tokens           kafka-server-stop
> kafka-delete-records              kafka-streams-application-reset
> kafka-dump-log                    kafka-topics
> kafka-leader-election             kafka-verifiable-consumer
> kafka-log-dirs                    kafka-verifiable-producer
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ kafka-topics --help
> This tool helps to create, delete, describe, or change a topic.
> Option                                   Description
> ------                                   -----------
> --alter                                  Alter the number of partitions,
>                                            replica assignment, and/or
>                                            configuration for the topic.
> --at-min-isr-partitions                  if set when describing topics, only
>                                            show partitions whose isr count is
>                                            equal to the configured minimum. Not
>                                            supported with the --zookeeper
>                                            option.
> ......                                     
> ~~~

## 6 Demo

> 创建主题
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ kafka-topics --bootstrap-server localhost:9092 --list
> 
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ kafka-topics --bootstrap-server localhost:9092 --create --topic topic01 --partitions 1 --replication-factor 1 #单节点只能有一个分区一个replica
> Created topic topic01.
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ kafka-topics --bootstrap-server localhost:9092 --list
> topic01
> ~~~
>
> 在另一个窗口启动消费者，该消费者会阻塞，等待生产者发送消息
>
> ~~~bash 
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ kafka-console-consumer --bootstrap-server localhost:9092 --topic topic01 --group g1
> ~~~
>
> 在原窗口启动生产者，并发送消息（按`Ctrl + C` 可以退出）
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ kafka-console-producer --broker-list localhost:9092 --topic topic01
> >this is the first message
> >this is the second message
> >^c
> ~~~
>
> 在打开消费者的终端窗口，可以看到接收到的消息（按`Ctrl + C` 可以退出）
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ kafka-console-consumer --bootstrap-server localhost:9092 --topic topic01 --group g1
> this is the first message
> this is the second message
> ^c
> ~~~

## 7 关闭Kafka和Zookeeper

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ kafka-server-stop
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ zookeeper-server-stop
> [2]+  Exit 143                nohup kafka-server-start /usr/local/etc/kafka/server.properties 2>&1 > kafka.log
> [1]+  Exit 143                nohup zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties 2>&1 > zk.log
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ jps
> 13721 Jps
> ~~~



