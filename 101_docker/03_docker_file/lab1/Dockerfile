# 基准镜像是tomcat:8.5.59-jdk11-openjdk
FROM tomcat:8.5.59-jdk11-openjdk

# 该镜像维护者
MAINTAINER fangkun119

# 工作目录，希望把web应用部署在基准镜像工作目录(/usr/local/tomcat/)下面的webapps内
# 用交互式(docker exec -lt ${container_id} /bin/bash)访问容器时、会自动跳转到该目录
# 创建容器时、如果不存在也会自动创建该目录
WORKDIR /usr/local/tomcat/webapps/

# 第一个docker-web是构建镜像时，与Dockerfile同级的名为docker-web的目录
# 第二个docker-web是${WORK_DIR}下名为docker-web的目录，如果不存在会创建
# ADD表示将第一个docker-web目录下所有内容都复制到第二个docker-web目录下
ADD docker-web ./docker-web

