package com.susu.proxy.client.proxy;

import com.susu.proxy.core.task.TaskScheduler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <p>Description: 访客连接调度器 </p>
 *
 * @author sujay
 * @since 16:08 2023/09/05
 * @version 1.0 JDK1.8
 */
public class VisitorConnectionScheduler {

    private final Bootstrap bootstrap;

    /**
     * 代理客户端
     */
    private final MasterClient masterClient;

    /**
     * 线程池
     */
    private final TaskScheduler taskScheduler;

    public VisitorConnectionScheduler(MasterClient masterClient, TaskScheduler taskScheduler) {
        this.masterClient = masterClient;
        this.taskScheduler = taskScheduler;

        this.bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {

                    }
                });
    }
}
