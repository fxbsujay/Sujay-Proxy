package com.susu.proxy.server.proxy;

import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.task.TaskScheduler;

public abstract class AbstractProxyServerFactory implements ProxyServerFactory {

    private final ProxyChannelHandle channelHandle;

    protected NetServer proxyServer;

    public AbstractProxyServerFactory(TaskScheduler scheduler) {
        channelHandle = new ProxyChannelHandle();
        proxyServer = new NetServer("Proxy-Server", scheduler);
        proxyServer.setBaseChannelHandler(channelHandle);
        proxyServer.startAsync();
    }

    @Override
    public boolean bind(int port) throws InterruptedException {
        proxyServer.bindSync(port);

        return false;
    }
}
