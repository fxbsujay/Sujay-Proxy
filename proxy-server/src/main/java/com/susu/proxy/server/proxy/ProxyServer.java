package com.susu.proxy.server.proxy;

import com.susu.proxy.core.config.AppConfig;
import com.susu.proxy.core.config.ServerConfig;
import com.susu.proxy.core.netty.AbstractChannelHandler;
import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.task.TaskScheduler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: Proxy Server NetWork</p>
 * <p>Description: 代理服务器 </p>
 *
 * @author sujay
 * @since 18:13 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class ProxyServer {

    private NetServer netServer;

    /**
     * 消息处理器
     */
    private AbstractChannelHandler handler;

    public ProxyServer(TaskScheduler taskScheduler, AbstractChannelHandler handler) {
        this.netServer = new NetServer(AppConfig.name, taskScheduler);
        this.handler = handler;
    }

    /**
     * 启动服务
     */
    public void start() throws InterruptedException {
        log.info("Start Proxy Server");
        this.netServer.addHandler(handler);
        netServer.start(ServerConfig.port);
    }

    /**
     * 停止服务
     */
    public void shutdown() {
        log.info("Shutdown Proxy Server");
        netServer.shutdown();
    }
}
