package com.susu.proxy.server.client;

import com.susu.proxy.core.config.AppConfig;
import com.susu.proxy.core.config.ServerConfig;
import com.susu.proxy.core.netty.AbstractChannelHandler;
import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.task.TaskScheduler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: Server NetWork</p>
 * <p>Description: 代理客户端服务器 </p>
 *
 * @author sujay
 * @since 18:13 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class MasterServer {

    private NetServer netServer;

    /**
     * 消息处理器
     */
    private AbstractChannelHandler handler;

    public MasterServer(TaskScheduler taskScheduler, AbstractChannelHandler handler) {
        this.netServer = new NetServer(AppConfig.name, taskScheduler);
        this.handler = handler;
    }

    /**
     * 启动服务
     */
    public void start() throws InterruptedException {
        this.netServer.addHandler(handler);
        netServer.startAsync(ServerConfig.port);
    }

    /**
     * 停止服务
     */
    public void shutdown() {
        netServer.shutdown();
    }
}
