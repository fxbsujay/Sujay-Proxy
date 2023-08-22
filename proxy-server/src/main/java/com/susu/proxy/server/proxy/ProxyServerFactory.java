package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.server.entity.PortMapping;

import java.util.List;

/**
 * <p>Description: 访客服务工厂</p>
 *
 * @author fxbsujay@gmail.com
 * @since 10:20 2023/08/01
 * @version 1.0 JDK1.8
 */
public interface ProxyServerFactory {

    /**
     * 绑定代理端口
     *
     * @param port       服务端端口
     */
    boolean bind(int port) throws InterruptedException;

    /**
     * 删除代理
     * @param port      服务端端口
     */
    boolean close(int port);

    /**
     * 端口是否存在
     */
    boolean isExist(int port);

    /**
     * 获取所有代理端口
     */
    List<Integer> getAllPort();

    /**
     * 获取端口协议
     * @param port 端口号
     */
    ProtocolType getProtocol(int port);

    /**
     * 获取所有映射
     */
    List<PortMapping> getAllMapping();
}
