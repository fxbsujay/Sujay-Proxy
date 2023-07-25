package com.susu.proxy.server.service;

import com.susu.proxy.core.netty.NetServer;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractProxyServerFactory implements ProxyServerFactory {

    protected Map<Integer, String> portMapping = new LinkedHashMap<>();


    protected NetServer proxyServer;

    public AbstractProxyServerFactory() {
    }

}
