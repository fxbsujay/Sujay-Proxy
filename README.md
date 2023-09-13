# Sujay-Proxy
一个简单快捷，开箱即用的内网穿透工具

### 快速开始
启动代理服务器 http://localhost:8890/

```java
ServerApplication application = new ServerApplication();
application.start();
```

Docker
```sh
docker pull registry.cn-hangzhou.aliyuncs.com/susu-space/simple-proxy:1.0

docker run --name simple-proxy \
-p 8899:8899 -p 8890:8890 \
-v /root/proxy-server/config:/mydata/config \
-v /root/proxy-server/data:/mydata/data \
-d registry.cn-hangzhou.aliyuncs.com/susu-space/simple-proxy:1.0
```

启动代理客户端
```java
ClientApplication application = new ClientApplication();
application.start();
```

### 配置文件说明
启动时可指定配置文件
```sh
java -jar proxy-server.jar config=/home/application.yaml
```

```yaml
app:
  # 服务名称
  name: master-server
server:
  # 代理服务器端口
  port: 8899
  # 代理服务器Http端口
  httpPort: 8890
  # 默认启动时的用户名密码
  username: admin
  password: admin
  # 客户端心跳间隔
  heartbeatInterval: 30000
  # 客户端断线超时时间
  heartbeatOutTime: 60000
  # 服务段检测心态时间间隔
  heartbeatCheckInterval: 30000
client:
  # 代理客户端要连接的代理服务器IP端口
  serverIp: localhost
  serverPort: 8899
  # 向服务队发生心态间隔
  heartbeatInterval: 30000
```

### 操作实例
![](https://github.com/fxbsujay/Sujay-Proxy/blob/main/assets/img/img_2.png)

![](https://github.com/fxbsujay/Sujay-Proxy/blob/main/assets/img/img_3.png)

![](https://github.com/fxbsujay/Sujay-Proxy/blob/main/assets/img/img_4.png)