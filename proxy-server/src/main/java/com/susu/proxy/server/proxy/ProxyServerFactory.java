package com.susu.proxy.server.proxy;

import java.util.List;

public interface ProxyServerFactory {

    /**
     * 绑定代理端口
     *
     * @param port       服务端端口
     */
    public boolean bind(int port) throws InterruptedException;

    /**
     * 删除代理
     * @param port      服务端端口
     */
    public boolean close(int port);

    /**
     * 端口是否存在
     */
    public boolean isExist(int port);

    /**
     * 获取所有代理端口
     */
    public List<Integer> getAllPort();
}
