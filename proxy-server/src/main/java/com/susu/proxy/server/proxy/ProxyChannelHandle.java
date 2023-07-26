package com.susu.proxy.server.proxy;

import com.susu.proxy.core.netty.BaseChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;

@Slf4j
public class ProxyChannelHandle extends BaseChannelHandler {

    @Override
    protected void initChannel(SocketChannel ch) {
       ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
           @Override
           protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
               byte[] bytes = new byte[buf.readableBytes()];
               buf.readBytes(bytes);

               int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
               log.info("proxy port {}", port);
           }
       });
    }
}
