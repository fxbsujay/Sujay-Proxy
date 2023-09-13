FROM openjdk:8-jdk-alpine

RUN mkdir -p /root/proxy-server

WORKDIR /root/proxy-server

COPY ../application.yaml /root/proxy-server/config/application.yaml
COPY ../proxy-server-1.0.jar /root/proxy-server/proxy-server.jar

ENTRYPOINT ["java","-jar","proxy-server.jar", "config=/root/proxy-server/config/application.yaml"]

#docker run --name simple-proxy -p 8899:8899 -p 8890:8890 -v /root/proxy-server/config:/mydata/config -v /root/proxy-server/data:/mydata/data -d registry.cn-hangzhou.aliyuncs.com/susu-space/simple-proxy:1.0
#docker run --name simple-proxy -p 8899:8899 -p 8890:8890 -v /root/proxy-server/config:/mydata/config -v /root/proxy-server/data:/mydata/data -d simple-proxy:1.0