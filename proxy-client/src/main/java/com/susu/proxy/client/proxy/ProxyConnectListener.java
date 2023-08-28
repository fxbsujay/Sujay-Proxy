package com.susu.proxy.client.proxy;

import com.susu.proxy.core.netty.listener.NetConnectListener;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * <p>Description: 连接状态监听器</p>
 *
 * @author sujay
 * @since 21:10 2023/08/28
 * @version 1.0 JDK1.8
 */
public abstract class ProxyConnectListener implements NetConnectListener, ChannelFutureListener {

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        onConnectStatusChanged(future.isSuccess());
    }
}
