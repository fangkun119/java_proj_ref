FROM openjdk:8u222-jre
WORKDIR /usr/local/bsbdj
ADD bsbdj.jar .
ADD application.yml .
ADD application-dev.yml .
EXPOSE 80
CMD ["java", "-jar", "bsbdj.jar"]
