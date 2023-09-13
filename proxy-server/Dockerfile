FROM openjdk:8-jdk-alpine
#同步时间
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
    apk update && apk add wget unzip vim && apk add -U tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone \
RUN mkdir -p /root/proxy-server

WORKDIR /root/proxy-server

COPY ../proxy-server-1.0.jar /root/proxy-server/proxy-server.jar

ENTRYPOINT ["java","-jar","proxy-server.jar"]
#docker run -it -p 9000-9200:9000-9200/tcp -p 8888:8888 -v /root/neutrino-proxy/config:/root/neutrino-proxy/config -d --restart=always --name neutrino registry.cn-hangzhou.aliyuncs.com/asgc/aoshiguchen-docker-images:1.7