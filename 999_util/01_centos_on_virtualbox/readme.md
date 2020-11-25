# 在`Virtual Box`上安装`CentOS` 

## (1) Mac OS Catalina 10.15.7, Virtual Box 6.1.14, CentOS 7

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

## (2) Mac OS Big Sur 11.0.1, Virtual Box 6.1.14, CentOS 7

> Mac OS升级到11.0.1，原虚拟机无需改动，仍然可以正常使用