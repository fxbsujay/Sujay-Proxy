FROM openjdk:8-jdk-alpine
#同步时间
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
    apk update && apk add wget unzip vim && apk add -U tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone \
RUN mkdir -p /root/proxy-server

WORKDIR /root/proxy-server

COPY ../application.yaml /root/proxy-server/config/application.yaml
COPY ../proxy-server-1.0.jar /root/proxy-server/proxy-server.jar

ENTRYPOINT ["java","-jar","proxy-server.jar", "config=/root/proxy-server/config/application.yaml"]
#docker run -it -p 9000-9200:9000-9200 -p 8899:8899 -p 8890:8890 -v /root/proxy-server/config:/root/proxy-server/config -d --restart=always --name proxy-server registry.cn-hangzhou.aliyuncs.com/susu-space/simple-proxy:1.0