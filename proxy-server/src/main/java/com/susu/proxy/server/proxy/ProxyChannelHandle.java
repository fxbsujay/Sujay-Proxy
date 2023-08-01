package com.susu.proxy.server.proxy;

import com.susu.proxy.core.netty.BaseChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 访客消息处理器 </p>
 *
 * @author fxbsujay@gmail.com
 * @since 10:20 2023/08/01
 * @version 1.0 JDK1.8
 */
@Slf4j
public class ProxyChannelHandle extends BaseChannelHandler {

    @Override
    protected void initChannel(SocketChannel ch) {
        for (ChannelInboundHandlerAdapter handler : handlerList) {
            ch.pipeline().addLast(handler);
        }
    }

    public void addHandler(ChannelInboundHandlerAdapter handler) {
        handlerList.add(handler);
    }
}
