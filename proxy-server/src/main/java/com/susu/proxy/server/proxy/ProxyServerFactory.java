package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.eum.ProtocolType;
import java.util.List;

public interface ProxyServerFactory {

    /**
     * 绑定代理端口
     *
     * @param port       服务端端口
     * @param protocol   协议类型 http / tcp
     */
    public boolean bind(int port) throws InterruptedException;

    /**
     * 删除代理
     * @param port      服务端端口
     */
    public boolean close(int port);

    /**
     * 获取所有代理端口
     */
    public List<Integer> getAllPort();
}
