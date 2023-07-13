package com.susu.proxy.core.config;

import com.susu.proxy.core.common.Constants;
import com.susu.proxy.core.stereotype.Configuration;
import lombok.Data;

@Data
@Configuration("client")
public class ClientConfig {

    /**
     * 服务端IP地址
     */
    public static String serverIp = "localhost";

    /**
     * 服务端Tcp端口
     */
    public static int serverPort = 8899;

    /**
     * 默认客户端发送心跳间隔
     */
    public static int heartbeatInterval = Constants.HEARTBEAT_INTERVAL;
}
