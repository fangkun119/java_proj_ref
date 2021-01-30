# 基于cenntos镜像
FROM centos
# 安装依赖
RUN ["yum", "install", "-y", "gcc", "gcc-c++", "net-tools", "make"]
# 切换工作目录
WORKDIR /usr/local
# 拷贝redis源代码压缩包到镜像内，会被ADD命令自动解压
ADD redis-4.0.14.tar.gz .
# 切换工作目录
WORKDIR /usr/local/redis-4.0.14/src
# 编译和安装redis
RUN make && make install
# 切换工作目录
WORKDIR /usr/local/redis-4.0.14/
# 拷贝redis配置文件到镜像内，redis对外端口是7000
ADD redis-7000.conf .
# 让容器对外暴露7000端口
EXPOSE 7000
# 设置容器启动时运行的默认命令
CMD ["redis-server", "redis-7000.conf"]
