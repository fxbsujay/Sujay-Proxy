package com.susu.proxy.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RealServerTest {

    @Test
    public void startTcpRealServer() throws InterruptedException {
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
                .bind(3309).addListener(future -> {
                    System.out.println(future.isSuccess());
                }).sync();

        sync.channel().closeFuture().addListener((ChannelFutureListener) f -> {
            f.channel().close();
        });
        sync.channel().closeFuture().sync();
    }

    @Test
    public void startTcpRealClient() throws InterruptedException {
        ChannelFuture future = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                byte[] bytes = ByteBufUtil.getBytes(byteBuf);
                                log.info("收到消息：{}", new String(bytes, StandardCharsets.UTF_8));
                            }
                        });
                    }
                }).connect("localhost", 8899).sync();
        if (future.isSuccess()) {
            ByteBuf buf = Unpooled.wrappedBuffer("Hello World !!".getBytes());
            future.channel().writeAndFlush(buf);
        }
        future.channel().closeFuture().sync();
    }
}
