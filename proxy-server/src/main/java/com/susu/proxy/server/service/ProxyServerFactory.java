package com.susu.proxy.server.service;

import com.susu.proxy.core.common.eum.ProtocolType;

public interface ProxyServerFactory {


    /**
     * 绑定代理端口
     *
     * @param port       服务端端口
     * @param protocol   协议类型 http / tcp
     */
    public boolean bind(int port, ProtocolType protocol);
}
