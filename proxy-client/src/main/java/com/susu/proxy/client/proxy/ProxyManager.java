package com.susu.proxy.client.proxy;


import com.susu.proxy.client.MasterClient;
import com.susu.proxy.core.netty.BaseChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <p>Description: 代理管理器 </p>
 *
 * @author sujay
 * @since 17:37 2023/08/28
 * @version 1.0 JDK1.8
 */
public class ProxyManager {


    /**
     *  EventLoopGroup
     */
    private NioEventLoopGroup worker;

    private Bootstrap bootstrap;

    private final MasterClient masterClient;

    public ProxyManager(MasterClient masterClient) {
        this.masterClient = masterClient;
        this.worker = new NioEventLoopGroup();

        this.bootstrap = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ProxyChannelHandle(masterClient));
                    }
                });
    }
}
