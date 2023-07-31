package com.susu.proxy.server.proxy;

import com.susu.proxy.core.netty.BaseChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

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