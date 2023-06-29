package com.susu.proxy.core.netty.listener;

/**
 * <p>Description: 网络包响应监听器</p>
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
public interface NetConnectListener {

    /**
     * <p>Description: 网络状态监听</p>
     * <pre>
     * 注意：
     *      为了保证消息的有序性，这里调用是在EventLoopGroup的线程。
     *      如果在后续处理过程中有什么需要阻塞的场景，需要开启别的线程处理
     *      否则会阻塞后续的网络包接收和处理
     * </pre>
     *
     * @param isConnected 是否连接
     */
    void onConnectStatusChanged(boolean isConnected) throws Exception;
}
