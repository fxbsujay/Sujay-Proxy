package com.susu.proxy.server.proxy;

import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.task.TaskScheduler;

public abstract class AbstractProxyServerFactory implements ProxyServerFactory {

    protected final ProxyChannelHandle channelHandle;

    protected NetServer proxyServer;

    public AbstractProxyServerFactory(TaskScheduler scheduler) {
        channelHandle = new ProxyChannelHandle();
        proxyServer = new NetServer("proxy-server", scheduler);
        proxyServer.setBaseChannelHandler(channelHandle);
        proxyServer.startAsync();
    }

    @Override
    public boolean bind(int port) throws InterruptedException {
        proxyServer.bindAsync(port);
        return true;
    }
}
