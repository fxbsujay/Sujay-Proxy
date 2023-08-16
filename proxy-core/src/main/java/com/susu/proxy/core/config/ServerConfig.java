package com.susu.proxy.core.config;

import com.susu.proxy.core.common.Constants;
import com.susu.proxy.core.stereotype.Configuration;
import lombok.Data;

@Data
@Configuration("server")
public class ServerConfig {

    public static Integer port = 8899;

    public static Integer httpPort = 8890;

    public static String username = "admin";

    public static String password = "admin";

    /**
     * 默认客户端发送心跳间隔
     */
    public static int heartbeatInterval = Constants.HEARTBEAT_INTERVAL;

    /**
     * 默认客户端存活时间
     */
    public static int heartbeatOutTime = Constants.HEARTBEAT_OUT_TIME;

    /**
     * 默认呵客户端存活检测时间间隔
     */
    public static int heartbeatCheckInterval = Constants.HEARTBEAT_INTERVAL;
}
