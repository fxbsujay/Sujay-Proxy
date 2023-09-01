package com.susu.proxy.client;

import com.susu.proxy.core.netty.AbstractChannelHandler;
import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.netty.msg.NetPacket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
public class RealServerTest {

    @Test
    public void startRealServer() throws InterruptedException {
        ChannelFuture sync = new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.info(buf.toString(StandardCharsets.UTF_8));
                                buf.clear();
                                buf.writeBytes("Hello World !!".getBytes());
                                ctx.writeAndFlush(buf);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                log.info("连接成功");
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                log.info("连接端口");
                            }
                        });
                    }
                })
                .bind(3309).sync();

        sync.channel().closeFuture().addListener((ChannelFutureListener) f -> f.channel().close());
        sync.channel().closeFuture().sync();

    }

}
