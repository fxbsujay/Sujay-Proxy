package com.susu.proxy.client.proxy;

import com.susu.proxy.client.MasterClient;
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("---------连接成功");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        log.info("---------连接断开");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        log.info(byteBuf.toString());
    }


}
