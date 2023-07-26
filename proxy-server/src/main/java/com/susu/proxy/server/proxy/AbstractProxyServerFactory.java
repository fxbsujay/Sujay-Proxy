package com.susu.proxy.server.proxy;

import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.task.TaskScheduler;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractProxyServerFactory implements ProxyServerFactory {

    protected Map<Integer, String> portMapping = new LinkedHashMap<>();

    private final ProxyChannelHandle channelHandle;

    protected NetServer proxyServer;

    public AbstractProxyServerFactory(TaskScheduler scheduler) {
        channelHandle = new ProxyChannelHandle();
        proxyServer = new NetServer("Proxy-Server", scheduler);
        proxyServer.setBaseChannelHandler(channelHandle);
        proxyServer.startAsync();
    }

}
