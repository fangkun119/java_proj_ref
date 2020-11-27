# 在`Virtual Box`上安装`CentOS` 

## 1.安装记录

### (1) Mac OS Catalina 10.15.7, Virtual Box 6.1.14, CentOS 7

> * centos镜像下载：[http://mirrors.163.com/centos/7/](http://mirrors.163.com/centos/7/)
> * centos虚拟机安装：用运行在Mac OS Catalina 10.15.7上的Virtual Box 6.1上安装和运行Centos 7虚拟机
> * 需要在Virtual Box中关闭虚拟机声卡功能来避免虚拟机启动时Virtual Box崩溃
> * 将非root账号添加到sudoer列表：[参考url](https://blog.csdn.net/myself00/article/details/9112817) 
> * 让VM可以连接外网：[参考url](https://blog.csdn.net/qq_23286071/article/details/80871352?utm_medium=distribute.pc_relevant.none-task-bloag-BlogCommendFromBaidu-4.channel_param&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-4.channel_param) 
> 	* 网卡1配置：连接方式为“NAT”；控制芯片为“准虚拟化网络（virtio-net）”
>	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/virtual_box_net_cfg_1.jpg)
>	* 网卡2配置：桥接网络
>	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/virtual_box_net_cfg_2.jpg)
> * 在VM中查看该VM的内网IP：执行命令 `ip add` （不再是ifconfig）
> * 安装Virtual Box增强工具，用来设置共享目录从主机传递文件：参考url [1](https://www.jianshu.com/p/1ccff5a7d750) [2](https://blog.csdn.net/kswkly/article/details/83690565) [3](https://blog.csdn.net/Scythe666/article/details/88624279)

### (2) Mac OS Big Sur 11.0.1, Virtual Box 6.1.16, CentOS 7

> Mac OS升级到11.0.1，原虚拟机无法启动，下载最新的`Virtual Box 6.1.16`，安装过程中提示需要“安全与隐私”授权时按提示跳转到MacOS设置中为其设置权限，启动`Virtual Box`时按提示更新`Virtual Box Extension`，原虚拟机可以正常启动

## 2. `CentOS 7`相关操作

### (1) 开启某个端口

参考url：[https://blog.csdn.net/weixin_38750084/article/details/90387056](https://blog.csdn.net/weixin_38750084/article/details/90387056)

> 例子：开启80、8080、3306、8088端口：
> 
> ~~~shell
> [root@localhost tengine]# systemctl status firewalld  #如果是关闭就执行systemctl start firewalld
> ● firewalld.service - firewalld - dynamic firewall daemon
>    Loaded: loaded (/usr/lib/systemd/system/firewalld.service; enabled; vendor preset: enabled)
>    Active: active (running) since 一 2020-10-19 20:00:05 CST; 28min ago
>      Docs: man:firewalld(1)
>  Main PID: 754 (firewalld)
>     Tasks: 2
>    CGroup: /system.slice/firewalld.service
>            └─754 /usr/bin/python2 -Es /usr/sbin/firewalld --nofork --nopid
> 
> 10月 19 20:00:03 localhost.localdomain systemd[1]: Starting firewalld - dynamic fi....
> 10月 19 20:00:05 localhost.localdomain systemd[1]: Started firewalld - dynamic fir....
> 10月 19 20:00:05 localhost.localdomain firewalld[754]: WARNING: AllowZoneDrifting ....
> Hint: Some lines were ellipsized, use -l to show in full.
> [root@localhost tengine]# firewall-cmd --list-ports
> 
> [root@localhost tengine]# firewall-cmd --zone=public --add-port=80/tcp --permanent
> success
> [root@localhost tengine]# firewall-cmd --zone=public --add-port=8080/tcp --permanent
> success
> [root@localhost tengine]# firewall-cmd --zone=public --add-port=8088/tcp --permanent
> success
> [root@localhost tengine]# firewall-cmd --zone=public --add-port=3306/tcp --permanent
> success
> [root@localhost tengine]# firewall-cmd --reload
> success
> [root@localhost tengine]# firewall-cmd --list-ports
> 80/tcp 8080/tcp 8088/tcp 3306/tcp
> ~~~

### (2) 查看虚拟机IP地址

> `ifconfig`还没有安装时、可以使用`ip add`
> 
> ~~~shell
> [root@localhost tengine]# ip add | grep inet
>     inet 127.0.0.1/8 scope host lo
>     inet6 ::1/128 scope host
>     inet 192.168.1.169/24 brd 192.168.1.255 scope global noprefixroute dynamic enp0s8
>     inet6 fe80::fc2e:c8d8:879e:f698/64 scope link noprefixroute
>     inet 192.168.122.1/24 brd 192.168.122.255 scope global virbr0
> ~~~

### (3) 关闭`防火墙`

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

### (4)为虚拟机设置固定IP

> (1) `Virtual Box`虚拟机网卡配置：
> 
> * 网卡1：桥接，用来让虚拟机能访问外网，“控制芯片”选`virtio-net`
> 
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/virtual_box_network_static_ip_cfg_1.jpg)
> 
> * 网卡2：Host Only网络，组建一个从笔记本到虚拟机的子网，“控制芯片”选`virtio-net`
> 
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/virtual_box_network_static_ip_cfg_2.jpg)
> 
> (2) 虚拟机静态IP配置：之前步骤安装`Virtual Box增强工具`时已经顺带安装了XServer，可在终端输入`startx`命令进入图形化配置
> 
> * `Application` -> `Setting` -> `Network`：图中的`eth0`对应上一步的`网卡1`（桥接）、`eth1`对应上一步的`网卡2`（`Host Only`网络)
> 
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/static_ip_cfg_1.jpg) 
> 
> * 修改`eth`网络的IP，在标签`Detail`中可以看到虚拟机在`Host Only`网络中的IP，本例是`192.168.56.102`，将这个IP以静态IP的方式配置在`IPv4`标签中，同时填入掩码/DNS/网关
> 
> 	![](https://raw.githubusercontent.com/kenfang119/pics/main/999_util/static_ip_cfg_2.jpg) 
> 
> 重启虚拟机，使用`ip add`或`ifconfig`命令，可以看到`eth1`的IP被固定在`192.168.56.102`
