package com.susu.proxy.server.proxy;

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

    /**
     * 服务端口
     */
    private final int port;

    private NetServer netServer;

    /**
     * 消息处理器
     */
    private AbstractChannelHandler handler;

    public ProxyServer(int port, TaskScheduler taskScheduler, AbstractChannelHandler handler) {
        this.netServer = new NetServer("PROXY_SERVER",taskScheduler);
        this.handler = handler;
        this.port = port;
    }

    /**
     * 启动服务
     */
    public void start() {
        log.info("Start Proxy Server");
        this.netServer.addHandler(handler);
        netServer.startAsync(port);
    }

    /**
     * 停止服务
     */
    public void shutdown() {
        log.info("Shutdown Proxy Server");
        netServer.shutdown();
    }
}
