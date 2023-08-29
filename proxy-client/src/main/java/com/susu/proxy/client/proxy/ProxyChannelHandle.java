package com.susu.proxy.client.proxy;

import com.susu.proxy.client.transmit.MasterClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyChannelHandle extends SimpleChannelInboundHandler<ByteBuf> {

    public final MasterClient client;

    public ProxyChannelHandle(MasterClient client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        log.info(byteBuf.toString());
    }


}
