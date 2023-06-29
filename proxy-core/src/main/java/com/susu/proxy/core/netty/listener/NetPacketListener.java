package com.susu.proxy.core.netty.listener;

import com.susu.proxy.core.netty.msg.NetRequest;

/**
 * <p>Description: 网络包响应监听器</p>
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
public interface NetPacketListener {

    /**
     * <p>Description: 收到消息</p>
     * <pre>
     * 注意：
     *      为了保证消息的有序性，这里调用是在EventLoopGroup的线程。
     *      如果在后续处理过程中有什么需要阻塞的场景，需要开启别的线程处理
     *      否则会阻塞后续的网络包接收和处理
     * </pre>
     *
     * @param packet 消息
     * @throws Exception 异常
     */
    void onMessage(NetRequest packet) throws Exception;
}
